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
package checkMyResearchOut;

import checkMyResearchOut.mongoModel.AnswerProposition;
import checkMyResearchOut.mongoModel.CMROUser;
import checkMyResearchOut.mongoModel.CMROUserAnswer;
import checkMyResearchOut.mongoModel.CMROUserAnswerRepository;
import checkMyResearchOut.mongoModel.CMROUserRepository;
import checkMyResearchOut.mongoModel.Question;
import checkMyResearchOut.mongoModel.QuestionRepository;
import checkMyResearchOut.mongoModel.Quiz;
import checkMyResearchOut.security.services.PasswordEncodingService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import checkMyResearchOut.mongoModel.QuizRepository;

/**
 *
 * @author Rémi Venant
 */
@Profile("sample-data")
@Component
public class SampleDataLoader implements CommandLineRunner {

    private static final Log LOG = LogFactory.getLog(SampleDataLoader.class);

    private final CMROUserRepository userRepo;

    private final QuizRepository quizRepo;

    private final QuestionRepository questionRepo;

    private final CMROUserAnswerRepository answerRepo;

    private final PasswordEncodingService passwordEncodingSvc;

    private final boolean alwayResetData;

    @Autowired
    public SampleDataLoader(CMROUserRepository userRepo, QuizRepository quizRepo,
            QuestionRepository questionRepo, CMROUserAnswerRepository answerRepo,
            PasswordEncodingService passwordEncodingSvc,
            @Value("${mmiLibraryServer.sampleData.alwayResetData:false}") boolean alwayResetData) {
        this.userRepo = userRepo;
        this.quizRepo = quizRepo;
        this.questionRepo = questionRepo;
        this.answerRepo = answerRepo;
        this.passwordEncodingSvc = passwordEncodingSvc;
        this.alwayResetData = alwayResetData;
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("START sampling data...");

        if (this.hasData()) {
            if (!this.alwayResetData) {
                LOG.info("Database contains data. do not sample data.");
                return;
            }
            LOG.info("Database contains data. Reset data from db.");
            this.resetData();
        }

        LOG.info("Create and save sample users...");
        final List<CMROUser> users = this.createUsers();
        LOG.info(String.format("%d users created", users.size()));

        LOG.info("Create and save sample quiz...");
        final Quiz quiz = this.createQuiz();
        LOG.info(String.format("1 quiz created", users.size()));

        LOG.info("Create and save sample questions...");
        final List<Question> questions = this.createQuestions(quiz);
        LOG.info(String.format("%d questions created", questions.size()));

        LOG.info("Create and save sample answers...");
        final List<CMROUserAnswer> answers = this.createAnswers(quiz, questions, users);
        LOG.info(String.format("%d answers created", answers.size()));

        LOG.info("END sampling data...");
    }

    private boolean hasData() {
        long dataCount = this.userRepo.count() + this.quizRepo.count()
                + this.questionRepo.count() + this.answerRepo.count();
        return dataCount > 0;
    }

    private void resetData() {
        this.userRepo.deleteAll();
        this.quizRepo.deleteAll();
        this.questionRepo.deleteAll();
        this.answerRepo.deleteAll();
    }

    /**
     * Create 3 users
     *
     * @return
     */
    private List<CMROUser> createUsers() {
        final CMROUser user1 = new CMROUser("user1@mail.com", "luengo", "vanda", this.passwordEncodingSvc.encodePassword("user1pass"));
        user1.setValidated(true);
        final CMROUser user2 = new CMROUser("user2@mail.com", "broisin", "julien", this.passwordEncodingSvc.encodePassword("user2pass"));
        user2.setValidated(true);
        final CMROUser user3 = new CMROUser("user3@mail.com", "marty", "jean-charles", this.passwordEncodingSvc.encodePassword("user3pass"));
        user3.setValidated(true);
        return StreamSupport.stream(this.userRepo.saveAll(List.of(user1, user2, user3)).spliterator(), false)
                .collect(Collectors.toList());
    }

    private Quiz createQuiz() {
        final Quiz quiz = new Quiz("testquiz", "Test Quiz", "A test quiz.");
        return this.quizRepo.save(quiz);
    }

    private List<Question> createQuestions(Quiz quiz) {
        final String quizName = quiz.getName();
        //Question 1: some propositions are correct, some other are not
        List<AnswerProposition> propositions = List.of(
                new AnswerProposition("Cette réponse est vraie.", true),
                new AnswerProposition("Cette réponse est fausse.", false),
                new AnswerProposition("Cette réponse est fausse.", false),
                new AnswerProposition("Cette réponse est fausse.", false),
                new AnswerProposition("Cette réponse est vraie.", true));
        final Question q1 = new Question(quizName, "Question 1: props 1 et 5 correctes ?", propositions, "Author Q1", "Publication Q1");
        //Question 2: only one proposition is correct
        propositions = List.of(
                new AnswerProposition("Cette réponse est fausse.", false),
                new AnswerProposition("Cette réponse est fausse.", false),
                new AnswerProposition("Cette réponse est vraie.", true),
                new AnswerProposition("Cette réponse est fausse.", false),
                new AnswerProposition("Cette réponse est fausse.", false));
        final Question q2 = new Question(quizName, "Question 2: prop 3 correcte ?", propositions, "Author Q2", "Publication Q2");
        //Question 3: all propositions are correct
        propositions = List.of(
                new AnswerProposition("Cette réponse est vraie.", true),
                new AnswerProposition("Cette réponse est vraie.", true),
                new AnswerProposition("Cette réponse est vraie.", true),
                new AnswerProposition("Cette réponse est vraie.", true),
                new AnswerProposition("Cette réponse est vraie.", true));
        final Question q3 = new Question(quizName, "Question 3: toutes les props correctes ?", propositions, "Author Q3", "Publication Q3");
        //Question 4: none of the propositions is correct
        propositions = List.of(
                new AnswerProposition("Cette réponse est fausse.", false),
                new AnswerProposition("Cette réponse est fausse.", false),
                new AnswerProposition("Cette réponse est fausse.", false),
                new AnswerProposition("Cette réponse est fausse.", false),
                new AnswerProposition("Cette réponse est fausse.", false));
        final Question q4 = new Question(quizName, "Question 4: aucune des props correcte ?", propositions, "Author Q4", "Publication Q4");

        return StreamSupport.stream(this.questionRepo.saveAll(List.of(q1, q2, q3, q4)).spliterator(), false)
                .collect(Collectors.toList());
    }

    private List<CMROUserAnswer> createAnswers(Quiz quiz, List<Question> questions,
            List<CMROUser> users) {
        List<CMROUserAnswer> answers = new ArrayList<>(7);
        // all answered are all within the last 1 hour
        final LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        //user 1 answered Q1, Q2, Q3 questions correctly, and Q4 badly with 2 attemps for the Q2
        answers.add(CMROUserAnswer.createSampleAnswer(questions.get(0), users.get(0), true, 1, startTime));
        answers.add(CMROUserAnswer.createSampleAnswer(questions.get(1), users.get(0), true, 2, startTime.plusMinutes(5)));
        answers.add(CMROUserAnswer.createSampleAnswer(questions.get(2), users.get(0), true, 1, startTime.plusMinutes(10)));
        answers.add(CMROUserAnswer.createSampleAnswer(questions.get(3), users.get(0), false, 1, startTime.plusMinutes(15)));
        //User 2 answered 2 of the questions (Q1, Q4), Q1 correctly, Q4 badly, with one attempt each and the last one tried now
        answers.add(CMROUserAnswer.createSampleAnswer(questions.get(0), users.get(0), true, 1, startTime));
        answers.add(CMROUserAnswer.createSampleAnswer(questions.get(3), users.get(0), false, 1, startTime.plusHours(1)));
        //User 3 did not answer any question
        return StreamSupport.stream(this.answerRepo.saveAll(answers).spliterator(), false)
                .collect(Collectors.toList());
    }
}
