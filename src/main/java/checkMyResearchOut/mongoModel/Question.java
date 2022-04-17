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

import checkMyResearchOut.mongoModel.views.QuestionViews;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 *
 * @author Rémi Venant
 */
@Document(collection = "questions")
public class Question implements Serializable {

    @JsonView(QuestionViews.Normal.class)
    @Id
    private String id;

    @JsonView(QuestionViews.Normal.class)
    @NotNull
    @Indexed(unique = false)
    private String quizName;

    @JsonView(QuestionViews.Normal.class)
    @NotBlank
    private String title;

    @JsonView(QuestionViews.Normal.class)
    @NotEmpty
    private List<AnswerProposition> answerPropositions = new ArrayList<>();

    @JsonView(QuestionViews.Normal.class)
    @NotBlank
    private String author;

    @JsonView(QuestionViews.Normal.class)
    @NotBlank
    private String publication;

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'question':?#{#self._id} }", lazy = true)
    private List<CMROUserAnswer> answers;

    protected Question() {
    }

    public Question(String quizName, String title, List<AnswerProposition> answerPropositions, String author, String publication) {
        this.quizName = quizName;
        this.title = title;
        if (answerPropositions != null) {
            this.answerPropositions = answerPropositions;
        }
        this.author = author;
        this.publication = publication;
        this.answers = List.of();
    }

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<AnswerProposition> getAnswerPropositions() {
        return answerPropositions;
    }

    public void setAnswerPropositions(List<AnswerProposition> answerPropositions) {
        this.answerPropositions = answerPropositions;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public List<CMROUserAnswer> getAnswers() {
        return answers;
    }

    protected void setAnswers(List<CMROUserAnswer> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "Question{" + "id=" + id + ", quizName=" + quizName + ", title=" + title + ", author=" + author + ", publication=" + publication + '}';
    }

}
