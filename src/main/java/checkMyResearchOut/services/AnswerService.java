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

import checkMyResearchOut.mongoModel.CMROUser;
import checkMyResearchOut.mongoModel.CMROUserAnswer;
import checkMyResearchOut.mongoModel.Question;
import checkMyResearchOut.mongoModel.Quiz;
import checkMyResearchOut.services.exceptions.OtherAnswerGivenEarlierException;
import checkMyResearchOut.services.exceptions.SuccessfulAnswerException;
import java.util.Set;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 *
 * @author Rémi Venant
 */
public interface AnswerService {

    /**
     * Answer a question.Will increment attempts count, update lastAttemptDateTime
     *
     * @param user
     * @param question
     * @param propositionIndices
     * @return
     * @throws IllegalArgumentException if user or question is null
     * @throws checkMyResearchOut.services.exceptions.OtherAnswerGivenEarlierException if question
     * was answered eralier before the timeout
     * @throws checkMyResearchOut.services.exceptions.SuccessfulAnswerException if question was
     * already successfully answere
     */
    @PreAuthorize("(hasRole('USER') and #user.id == principal.userId) or hasRole('ADMIN')")
    CMROUserAnswer answerAQuestion(CMROUser user, Question question, Set<Integer> propositionIndices)
            throws IllegalArgumentException, OtherAnswerGivenEarlierException, SuccessfulAnswerException;

    //List<CMROUserAnswer> getUsersAnswers(Quiz quiz, CMROUser user) throws IllegalArgumentException;
    /**
     *
     * @param quiz
     * @param user
     * @return
     * @throws IllegalArgumentException
     */
    @PreAuthorize("(hasRole('USER') and #user.id == principal.userId) or hasRole('ADMIN')")
    CMROUserRanking getQuizRanking(Quiz quiz, CMROUser user) throws IllegalArgumentException;
}
