/*
 * Copyright (C) 2022 ATIEF.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package checkMyResearchOut.services;

import checkMyResearchOut.mongoModel.AnswerProposition;
import checkMyResearchOut.mongoModel.CMROUser;
import checkMyResearchOut.mongoModel.CMROUserAnswer;
import checkMyResearchOut.mongoModel.CMROUserAnswerRepository;
import checkMyResearchOut.mongoModel.Question;
import checkMyResearchOut.mongoModel.QuestionRepository;
import checkMyResearchOut.mongoModel.Quiz;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import checkMyResearchOut.mongoModel.QuizRepository;
import checkMyResearchOut.mongoModel.QuizSimpleInformations;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Rémi Venant
 */
@Service
public class QuizServiceImpl implements QuizService {

    private static final Log LOG = LogFactory.getLog(QuizServiceImpl.class);
    
    private final QuizRepository quizRepo;

    private final QuestionRepository questionRepo;

    private final CMROUserAnswerRepository answerRepo;

    private final int answerAttemptTimeoutMinutes;

    @Autowired
    public QuizServiceImpl(QuizRepository quizRepo, QuestionRepository questionRepo,
            CMROUserAnswerRepository answerRepo,
            @Value("${checkMyResearchOut.game.answer-attempt-timeout:5}") int answerAttemptTimeoutMinutes) {
        this.quizRepo = quizRepo;
        this.questionRepo = questionRepo;
        this.answerRepo = answerRepo;
        this.answerAttemptTimeoutMinutes = answerAttemptTimeoutMinutes;
    }

    @Override
    public Stream<QuizSimpleInformations> getQuizzesWithSimpleInformations() {
        return this.quizRepo.findSimpleInfoBy();
    }

    @Override
    public Quiz createQuiz(String name, String fullName, String description) throws IllegalArgumentException, DuplicateKeyException {
        if (!StringUtils.hasText(name) || !StringUtils.hasText(fullName)) {
            throw new IllegalArgumentException("Quiz name nor fullName cannot be blank.");
        }
        try {
            Quiz qi = new Quiz(name.trim(), fullName.trim(),
                    StringUtils.hasText(description) ? description.trim() : null);
            return this.quizRepo.save(qi);
        } catch (ConstraintViolationException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public List<Question> getQuizQuestions(Quiz quiz) {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz cannot be null.");
        }
        return this.questionRepo.findByQuizName(quiz.getName()).collect(Collectors.toList());
    }

    @Override
    public Quiz getQuizByName(String name) throws IllegalArgumentException, NoSuchElementException {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Quiz name cannot be blank.");
        }
        return this.quizRepo.findByName(name).orElseThrow(() -> new NoSuchElementException("Unknown quiz name"));
    }

    @Override
    public Quiz updateQuiz(Quiz quiz, String fullName, String description) throws IllegalArgumentException {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz cannot be null.");
        }
        if (!StringUtils.hasText(fullName)) {
            throw new IllegalArgumentException("Quiz full name cannot be blank.");
        }
        quiz.setFullName(fullName.trim());
        quiz.setDescription(StringUtils.hasText(description) ? description.trim() : null);
        return this.quizRepo.save(quiz);
    }

    @Override
    public void deleteQuiz(Quiz quiz) throws IllegalArgumentException {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz  cannot be null.");
        }
        this.answerRepo.deleteByQuizName(quiz.getName());
        this.questionRepo.deleteByQuizName(quiz.getName());
        this.quizRepo.delete(quiz);
    }

    @Override
    public Question createQuestion(Quiz quiz, String title, List<AnswerProposition> answerPropositions, String author, String publication) throws IllegalArgumentException {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz cannot be null.");
        }
        if (answerPropositions == null || answerPropositions.isEmpty()) {
            throw new IllegalArgumentException("Answer propositions cannot be null nor empty.");
        }
        if (answerPropositions.stream().anyMatch(ap -> !StringUtils.hasText(ap.getTitle()))) {
            throw new IllegalArgumentException("Answer propositions must all have a title.");
        }
        if (!StringUtils.hasText(title) || !StringUtils.hasText(author) || !StringUtils.hasText(publication)) {
            throw new IllegalArgumentException("Author nor publication cannot be blank.");
        }
        Question question = new Question(quiz.getName(), title, answerPropositions, author.trim(), publication.trim());
        return this.questionRepo.save(question);
    }

    @Override
    public Question getQuestionById(String quizName, String questionId) throws IllegalArgumentException, NoSuchElementException {
        if (!StringUtils.hasText(quizName) || !StringUtils.hasText(questionId)) {
            throw new IllegalArgumentException("QuizName nor question id cannot be blank.");
        }
        Question question = this.questionRepo.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException("Unknown question id."));
        if (!question.getQuizName().equals(quizName)) {
            throw new NoSuchElementException("Unknown question id for the given quiz.");
        }
        return question;
    }

    @Override
    public Question updateQuestion(Question question, String title, List<AnswerProposition> answerPropositions, String author, String publication) throws IllegalArgumentException {
        if (answerPropositions == null || answerPropositions.isEmpty()) {
            throw new IllegalArgumentException("Answer propositions cannot be null nor empty.");
        }
        if (answerPropositions.stream().anyMatch(ap -> !StringUtils.hasText(ap.getTitle()))) {
            throw new IllegalArgumentException("Answer propositions must all have a title.");
        }
        if (!StringUtils.hasText(title) || !StringUtils.hasText(author) || !StringUtils.hasText(publication)) {
            throw new IllegalArgumentException("Author nor publication cannot be blank.");
        }
        question.setTitle(title);
        question.setAnswerPropositions(answerPropositions);
        question.setAuthor(author);
        question.setPublication(publication);
        return this.questionRepo.save(question);
    }

    @Override
    public void deleteQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null.");
        }
        this.answerRepo.deleteByQuestionId(question.getId());
        this.questionRepo.delete(question);
    }

    @Override
    public Long getQuizQuestionsNumber(Quiz quiz) throws IllegalArgumentException {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz cannot be null.");
        }
        return this.questionRepo.countByQuizName(quiz.getName());
    }

//    @Override
//    public Long getUnansweredOrUnsuccessfulAnsweredQuestionsNumber(Quiz quiz, CMROUser user) throws IllegalArgumentException {
//        if (quiz == null || user == null) {
//            throw new IllegalArgumentException("Quiz nor user cannot be null.");
//        }
//        final long nbQuestions = this.questionRepo.countByQuizName(quiz.getName());
//        final long nbSuccessfulAnswers = this.answerRepo.countByQuizNameAndUserAndSuccessIsTrue(quiz.getName(), user);
//        return nbQuestions - nbSuccessfulAnswers;
//    }
    public List<Question> getAnswerableQuestions(Quiz quiz, CMROUser user) throws IllegalArgumentException {
        if (quiz == null || user == null) {
            throw new IllegalArgumentException("Quiz nor user cannot be null.");
        }
        // Compute a set of question id the user has already successfully answered or the user's last attempt is too early
        final Set<String> questionIdsToRemove = this.answerRepo.findByQuizNameAndUser(quiz.getName(), user)
                .filter(a -> a.isSuccess()
                || a.getLastAttemptDateTime().plusMinutes(this.answerAttemptTimeoutMinutes).isAfter(LocalDateTime.now()))
                .map(CMROUserAnswer::getQuestionId)
                .collect(Collectors.toSet());
        // Get all questions of the quiz that can be answered by the user (i.e. : whose id does not belong in the previous set)
        return this.questionRepo.findByQuizName(quiz.getName())
                .filter(q -> !questionIdsToRemove.contains(q.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public QuizUserInfo getQuizUserInfo(String quizName, CMROUser user) throws IllegalArgumentException {
        if (quizName == null || user == null) {
            throw new IllegalArgumentException("Quiz name nor user cannot be null.");
        }
        // From the user's answer : compute the number of successful question, get the time of
        // the oldest "too early" badly answered question and a set of question id
        // the user has already successfully answered or the user's last attempt is too early
        LocalDateTime now = LocalDateTime.now();
        // Retrieve the list of question the user has already successfully answered or the user's last attempt is too early
        final List<CMROUserAnswer> answers = this.answerRepo.findByQuizNameAndUser(quizName, user)
                .filter(a -> a.isSuccess()
                || a.getLastAttemptDateTime().plusMinutes(this.answerAttemptTimeoutMinutes).isAfter(now))
                .collect(Collectors.toList());
        // compute number of successfull question and the last "too early" badly answered question 
        long successfullyAnsweredQuestions = 0;
        LocalDateTime oldestTooEarlyAnsweredQuestion = null;
        for (CMROUserAnswer answer : answers) {
            if (answer.isSuccess()) { // answer is a success
                successfullyAnsweredQuestions++;
            } else if (oldestTooEarlyAnsweredQuestion == null 
                    || oldestTooEarlyAnsweredQuestion.isAfter(answer.getLastAttemptDateTime())) { 
                oldestTooEarlyAnsweredQuestion = answer.getLastAttemptDateTime();
            }
        }
        // Compute a set of question id the user has already successfully answered or the user's last attempt is too early
        final Set<String> questionIdsToRemove = answers.stream()
                .map(CMROUserAnswer::getQuestionId).collect(Collectors.toSet());
        // count questions the user can answer
        final long answerableQuestionsNum = this.questionRepo.countByQuizNameAndIdNotIn(quizName, questionIdsToRemove);

        final QuizUserInfo quizUserInfo = new QuizUserInfo(successfullyAnsweredQuestions, answerableQuestionsNum > 0);
        if (answerableQuestionsNum <= 0 && oldestTooEarlyAnsweredQuestion != null) {
            //compute waiting time in minutes
            final long waitingTimeMinute = this.answerAttemptTimeoutMinutes - ChronoUnit.MINUTES.between(oldestTooEarlyAnsweredQuestion, now);
            quizUserInfo.setWaitingMinutesBeforeNextAnswer(waitingTimeMinute > 0 ? waitingTimeMinute : 0);
        }
        return quizUserInfo;
    }

    @Override
    public Question getRandomAnswerableQuestion(Quiz quiz, CMROUser user) throws IllegalArgumentException {
        // Get all questions of the quiz that can be answered by the user
        final List<Question> answerableQuestions = this.getAnswerableQuestions(quiz, user);
        if (answerableQuestions.isEmpty()) {
            return null;
        } else {
            // Compute a random question
            int idx = (int) Math.floor(Math.random() * answerableQuestions.size());
            return answerableQuestions.get(idx);
        }
    }

}
