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
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.dao.DuplicateKeyException;

/**
 *
 * @author Rémi Venant
 */
public interface QuizService {

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
    Quiz createQuiz(String name, String fullName, String description) throws IllegalArgumentException, DuplicateKeyException;

    /**
     *
     * @param name
     * @return
     */
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
    Quiz updateQuiz(Quiz quiz, String fullName, String description) throws IllegalArgumentException;

    /**
     * Delete a quiz and all its questions with their answers
     *
     * @param quiz
     * @throws IllegalArgumentException
     */
    void deleteQuiz(Quiz quiz) throws IllegalArgumentException;

    /**
     * Create a question
     *
     * @param quiz
     * @param answerPropositions
     * @param author
     * @param publication
     * @return
     * @throws IllegalArgumentException
     */
    Question createQuestion(Quiz quiz, String title, List<AnswerProposition> answerPropositions, String author, String publication) throws IllegalArgumentException;

    /**
     *
     * @param questionId
     * @return
     */
    Question getQuestionById(String questionId) throws IllegalArgumentException, NoSuchElementException;

    /**
     * Update a question
     *
     * @param question
     * @param answerPropositions
     * @param author
     * @param publication
     * @return
     * @throws IllegalArgumentException
     */
    Question updateQuestion(Question question, String title, List<AnswerProposition> answerPropositions, String author, String publication) throws IllegalArgumentException;

    /**
     * Delete a question and all its answers
     *
     * @param question
     */
    void deleteQuestion(Question question);

    /**
     *
     * @param quiz
     * @return
     * @throws IllegalArgumentException
     */
    Long getQuizQuestionsNumber(Quiz quiz) throws IllegalArgumentException;

    /**
     * return the number of unanswered or unsuccessfully answered questions of a user
     *
     * @param quiz
     * @param user
     * @return
     * @throws IllegalArgumentException
     */
    Long getUnansweredOrUnsuccessfulAnsweredQuestionsNumber(Quiz quiz, CMROUser user) throws IllegalArgumentException;

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
     * @throws IllegalArgumentException
     */
    Question getRandomAnswerableQuestion(Quiz quiz, CMROUser user) throws IllegalArgumentException;
}
