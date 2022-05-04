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

import java.io.Serializable;

/**
 *
 * @author Rémi Venant
 */
public class QuizUserInfo implements Serializable {

    private long successfullyAnsweredQuestions;
    private boolean canAnswerAQuestion;
    private Long waitingMinutesBeforeNextAnswer;
    private Boolean lastAnswerSuccess;

    public QuizUserInfo() {
        this.successfullyAnsweredQuestions = 0;
        this.canAnswerAQuestion = false;
        this.lastAnswerSuccess = null;
    }

    public QuizUserInfo(long successfullyAnsweredQuestions, boolean canAnswerAQuestion) {
        this.successfullyAnsweredQuestions = successfullyAnsweredQuestions;
        this.canAnswerAQuestion = canAnswerAQuestion;
        this.waitingMinutesBeforeNextAnswer = null;
        this.lastAnswerSuccess = null;
    }

    public long getSuccessfullyAnsweredQuestions() {
        return successfullyAnsweredQuestions;
    }

    public void setSuccessfullyAnsweredQuestions(long successfullyAnsweredQuestions) {
        this.successfullyAnsweredQuestions = successfullyAnsweredQuestions;
    }

    public boolean isCanAnswerAQuestion() {
        return canAnswerAQuestion;
    }

    public void setCanAnswerAQuestion(boolean canAnswerAQuestion) {
        this.canAnswerAQuestion = canAnswerAQuestion;
    }

    public Long getWaitingMinutesBeforeNextAnswer() {
        return waitingMinutesBeforeNextAnswer;
    }

    public void setWaitingMinutesBeforeNextAnswer(Long waitingMinutesBeforeNextAnswer) {
        this.waitingMinutesBeforeNextAnswer = waitingMinutesBeforeNextAnswer;
    }

    public Boolean getLastAnswerSuccess() {
        return lastAnswerSuccess;
    }

    public void setLastAnswerSuccess(Boolean lastAnswerSuccess) {
        this.lastAnswerSuccess = lastAnswerSuccess;
    }

}
