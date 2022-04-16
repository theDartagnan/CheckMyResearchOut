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
package checkMyResearchOut.mongoModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

/**
 *
 * @author Rémi Venant
 */
@Document(collection = "answers")
public class CMROUserAnswer implements Serializable {

    @Id
    private String id;

    @NotNull
    @Indexed(unique = false)
    private String quizName;

    @NotNull
    @Field(targetType = FieldType.OBJECT_ID)
    private String questionId;

    @NotNull
    @ReadOnlyProperty
    @DocumentReference(lookup = "{ '_id': ?#{#self.questionId} }", lazy = true)
    private Question question;

    @NotNull
    @Field(name = "userId") // Just for consistency with questionId field
    @DocumentReference(lazy = true)
    private CMROUser user;

    @NotNull
    private int attempts = 0;

    @NotNull
    private LocalDateTime lastAttemptDateTime;

    private boolean success;

    protected CMROUserAnswer() {
    }

    public CMROUserAnswer(Question question, CMROUser user, boolean success) {
        this.quizName = question.getQuizName();
        this.questionId = question.getId();
        this.question = question;
        this.user = user;
        this.attempts = 1;
        this.lastAttemptDateTime = LocalDateTime.now();
        this.success = success;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionId() {
        return questionId;
    }

    protected void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuizName() {
        return quizName;
    }

    protected void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public Question getQuestion() {
        return question;
    }

    protected void setQuestion(Question question) {
        this.question = question;
        this.questionId = question.getId();
        this.quizName = question.getQuizName();
    }

    public CMROUser getUser() {
        return user;
    }

    protected void setUser(CMROUser user) {
        this.user = user;
    }

    public int getAttempts() {
        return attempts;
    }

    protected void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public LocalDateTime getLastAttemptDateTime() {
        return lastAttemptDateTime;
    }

    protected void setLastAttemptDateTime(LocalDateTime lastAttemptDateTime) {
        this.lastAttemptDateTime = lastAttemptDateTime;
    }

    public boolean isSuccess() {
        return success;
    }

    protected void setSuccess(boolean success) {
        this.success = success;
    }

    public void setNewAnswer(boolean success) {
        this.attempts++;
        this.lastAttemptDateTime = LocalDateTime.now();
        this.success = success;
    }

    @Override
    public String toString() {
        return "CMROUserAnswer{" + "id=" + id + ", attempts=" + attempts + ", lastAttemptDateTime=" + lastAttemptDateTime + ", success=" + success + '}';
    }

    public static CMROUserAnswer createSampleAnswer(Question question, CMROUser user,
            boolean success, int attempts, LocalDateTime lastAttemptDateTime) {
        final CMROUserAnswer answer = new CMROUserAnswer(question, user, success);
        answer.setAttempts(attempts);
        answer.setLastAttemptDateTime(lastAttemptDateTime);
        return answer;
    }
}
