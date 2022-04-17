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
package checkMyResearchOut.controllers;

import checkMyResearchOut.mongoModel.CMROUser;
import checkMyResearchOut.mongoModel.CMROUserAnswer;
import checkMyResearchOut.mongoModel.Question;
import checkMyResearchOut.mongoModel.Quiz;
import checkMyResearchOut.mongoModel.views.QuestionViews;
import checkMyResearchOut.mongoModel.views.QuizViews;
import checkMyResearchOut.services.AnswerService;
import checkMyResearchOut.services.CMROUserRanking;
import checkMyResearchOut.services.CMROUserService;
import checkMyResearchOut.services.QuizService;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Rémi Venant
 */
@RestController
@RequestMapping("/api/v1/rest/quizzes")
public class QuizController {

    private final QuizService quizSvc;

    private final CMROUserService userSvc;

    private final AnswerService answerSvc;

    @Autowired
    public QuizController(QuizService quizSvc, CMROUserService userSvc, AnswerService answerSvc) {
        this.quizSvc = quizSvc;
        this.userSvc = userSvc;
        this.answerSvc = answerSvc;
    }

    @PostMapping
    @JsonView(QuizViews.Normal.class)
    public Quiz createQuiz(@RequestBody Quiz quizToCreate) {
        return this.quizSvc.createQuiz(quizToCreate.getName(), quizToCreate.getFullName(),
                quizToCreate.getDescription());
    }

    @GetMapping("{quizName}")
    @JsonView(QuizViews.Normal.class)
    public Quiz getQuiz(@PathVariable String quizName) {
        return this.quizSvc.getQuizByName(quizName);
    }

    @PutMapping("{quizName}")
    @JsonView(QuizViews.Normal.class)
    public Quiz updateQuiz(@PathVariable String quizName, @RequestBody Quiz quizToUpdate) {
        final Quiz quiz = this.quizSvc.getQuizByName(quizName);
        return this.quizSvc.updateQuiz(quiz, quizToUpdate.getFullName(), quizToUpdate.getDescription());
    }

    @DeleteMapping("{quizName}")
    public ResponseEntity<?> deleteQuiz(@PathVariable String quizName) {
        final Quiz quiz = this.quizSvc.getQuizByName(quizName);
        this.quizSvc.deleteQuiz(quiz);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{quizName}/admin/questions")
    @JsonView(QuestionViews.Normal.class)
    public List<Question> getQuizQuestions(@PathVariable String quizName) {
        final Quiz quiz = this.quizSvc.getQuizByName(quizName);
        return this.quizSvc.getQuizQuestions(quiz);
    }

    @PostMapping("{quizName}/admin/questions")
    @JsonView(QuestionViews.WithAnswers.class)
    public Question createQuizQuestion(@PathVariable String quizName,
            @RequestBody Question questionToCreate) {
        final Quiz quiz = this.quizSvc.getQuizByName(quizName);
        return this.quizSvc.createQuestion(quiz,
                questionToCreate.getTitle(),
                questionToCreate.getAnswerPropositions(),
                questionToCreate.getAuthor(),
                questionToCreate.getPublication());
    }

    @GetMapping("{quizName}/admin/questions/{questionId}")
    @JsonView(QuestionViews.WithAnswers.class)
    public Question getQuizQuestion(@PathVariable String quizName,
            @PathVariable String questionId) {
        return this.quizSvc.getQuestionById(quizName, questionId);
    }

    @GetMapping("{quizName}/questions-number")
    public Long getQuizQuestionsNumber(@PathVariable String quizName) {
        final Quiz quiz = this.quizSvc.getQuizByName(quizName);
        return this.quizSvc.getQuizQuestionsNumber(quiz);
    }

    @GetMapping("{quizName}/unsucceeded-questions-number/{userId}")
    public Long getQuizUnsucceededQuestionsNumberForUser(
            @PathVariable String quizName,
            @PathVariable String userId) {
        final CMROUser user = "myself".equals(userId) ? null : this.userSvc.getUserById(userId);
        final Quiz quiz = this.quizSvc.getQuizByName(quizName);
        return this.quizSvc.getUnansweredOrUnsuccessfulAnsweredQuestionsNumber(quiz, user);
    }

    @GetMapping("{quizName}/random-question-to-answer/{userId}")
    @JsonView(QuestionViews.Normal.class)
    public Question getRandomQuestionsToAnswerForUser(
            @PathVariable String quizName,
            @PathVariable String userId) {
        final CMROUser user = "myself".equals(userId) ? null : this.userSvc.getUserById(userId);
        final Quiz quiz = this.quizSvc.getQuizByName(quizName);
        return this.quizSvc.getRandomAnswerableQuestion(quiz, user);
    }

    @GetMapping("{quizName}/questions/{questionId}/answers/{userId}")
    public Boolean answerQuestion(
            @PathVariable String quizName,
            @PathVariable String questionId,
            @PathVariable String userId,
            @RequestBody List<Integer> propositionIndices) {
        final Set<Integer> propIndicesSet = new HashSet<>(propositionIndices);
        if (propIndicesSet.size() != propositionIndices.size()) {
            throw new IllegalArgumentException("Duplicated proposition indices");
        }
        final CMROUser user = "myself".equals(userId) ? null : this.userSvc.getUserById(userId);
        final Question question = this.quizSvc.getQuestionById(quizName, questionId);
        final CMROUserAnswer answer = this.answerSvc.answerAQuestion(user, question, propIndicesSet);
        return answer.isSuccess();
    }

    @GetMapping("{quizName}/ranking/{userId}")
    public CMROUserRanking getQuizRanking(@PathVariable String quizName, @PathVariable String userId) {
        final CMROUser user = "myself".equals(userId) ? null : this.userSvc.getUserById(userId);
        final Quiz quiz = this.quizSvc.getQuizByName(quizName);
        return this.answerSvc.getQuizRanking(quiz, user);
    }
}
