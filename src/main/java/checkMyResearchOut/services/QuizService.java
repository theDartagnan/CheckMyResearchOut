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
import checkMyResearchOut.mongoModel.Question;
import checkMyResearchOut.mongoModel.Quiz;
import checkMyResearchOut.mongoModel.QuizSimpleInformations;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 *
 * @author Rémi Venant
 */
public interface QuizService {

    /**
     *
     * @return
     */
    @PreAuthorize("hasRole('USER')")
    Stream<QuizSimpleInformations> getQuizzesWithSimpleInformations();

    /**
     * Create a quiz
     *
     * @param name
     * @param fullName
     * @param description
     * @return
     * @throws IllegalArgumentException
     * @throws DuplicateKeyException
     */
    @PreAuthorize("hasRole('ADMIN')")
    Quiz createQuiz(String name, String fullName, String description) throws IllegalArgumentException, DuplicateKeyException;

    /**
     *
     * @param name
     * @return
     */
    @PreAuthorize("hasRole('USER')")
    Quiz getQuizByName(String name) throws IllegalArgumentException, NoSuchElementException;

    /**
     * Update a quiz
     *
     * @param quiz
     * @param fullName
     * @param description
     * @return
     * @throws IllegalArgumentException
     */
    @PreAuthorize("hasRole('ADMIN')")
    Quiz updateQuiz(Quiz quiz, String fullName, String description) throws IllegalArgumentException;

    /**
     * Delete a quiz and all its questions with their answers
     *
     * @param quiz
     * @throws IllegalArgumentException
     */
    @PreAuthorize("hasRole('ADMIN')")
    void deleteQuiz(Quiz quiz) throws IllegalArgumentException;

    /**
     * Create a question
     *
     * @param quiz
     * @param title
     * @param answerPropositions
     * @param author
     * @param publication
     * @return
     * @throws IllegalArgumentException
     */
    @PreAuthorize("hasRole('ADMIN')")
    Question createQuestion(Quiz quiz, String title, List<AnswerProposition> answerPropositions, String author, String publication) throws IllegalArgumentException;

    /**
     * Get questions of a quiz
     *
     * @param quiz
     * @return
     */
    @PreAuthorize("hasRole('ADMIN')")
    List<Question> getQuizQuestions(Quiz quiz);

    /**
     *
     * @param quizName
     * @param questionId
     * @return
     */
    @PreAuthorize("hasRole('USER')") // Warning, for a better security enforcment, only admin should access this service. However it is used to answer a question
    Question getQuestionById(String quizName, String questionId) throws IllegalArgumentException, NoSuchElementException;

    /**
     * Update a question
     *
     * @param question
     * @param title
     * @param answerPropositions
     * @param author
     * @param publication
     * @return
     * @throws IllegalArgumentException
     */
    @PreAuthorize("hasRole('ADMIN')")
    Question updateQuestion(Question question, String title, List<AnswerProposition> answerPropositions, String author, String publication) throws IllegalArgumentException;

    /**
     * Delete a question and all its answers
     *
     * @param question
     */
    @PreAuthorize("hasRole('ADMIN')")
    void deleteQuestion(Question question);

    /**
     *
     * @param quiz
     * @return
     * @throws IllegalArgumentException
     */
    @PreAuthorize("hasRole('USER')")
    Long getQuizQuestionsNumber(Quiz quiz) throws IllegalArgumentException;

    /**
     * return the number of unanswered or unsuccessfully answered questions of a user
     *
     * @param quiz
     * @param user
     * @return
     * @throws IllegalArgumentException if quiz or user is null
     */
//    @PreAuthorize("(hasRole('USER') and #user.id == principal.userId) or hasRole('ADMIN')")
//    Long getUnansweredOrUnsuccessfulAnsweredQuestionsNumber(Quiz quiz, CMROUser user) throws IllegalArgumentException;
    /**
     * Return the user info about a quiz
     *
     * @param quizName
     * @param user
     * @return
     * @throws IllegalArgumentException if quiz or user is null
     */
    @PreAuthorize("(hasRole('USER') and #user.id == principal.userId) or hasRole('ADMIN')")
    QuizUserInfo getQuizUserInfo(String quizName, CMROUser user) throws IllegalArgumentException;

    /**
     * return a list of unanswered or unsuccessfully answered questions
     *
     * @param quiz
     * @param user
     * @return
     */
    // List<Question> getAnswerableQuestions(Quiz quiz, CMROUser user);
    /**
     * Return a question unanswered or unsuccessfully answered that can be answered or null if all
     * questions have already been successfully answered or cannot been answered
     *
     * @param quiz
     * @param user
     * @return
     * @throws IllegalArgumentException if quiz or user is null
     */
    @PreAuthorize("(hasRole('USER') and #user.id == principal.userId) or hasRole('ADMIN')")
    Question getRandomAnswerableQuestion(Quiz quiz, CMROUser user) throws IllegalArgumentException;
}
