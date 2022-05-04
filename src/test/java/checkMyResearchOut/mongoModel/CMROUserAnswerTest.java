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

import checkMyResearchOut.configuration.MongoConfiguration;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author Rémi Venant
 */
@DataMongoTest
@Import(MongoConfiguration.class)
@ActiveProfiles("mongo-test")
public class CMROUserAnswerTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    public CMROUserAnswerTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testQuestionQuestionIdMapping() {
        System.out.println("testQuestionQuestionIdMapping");

        final Quiz quiz = this.mongoTemplate.save(new Quiz("quizz", "quizz", "quizz desc"));
        final Question question = this.mongoTemplate.save(new Question(quiz.getName(), "question",
                List.of(
                        new AnswerProposition("Q1-P1", true),
                        new AnswerProposition("Q1-P5", false)
                ), "author", "publi"));
        final CMROUser user = this.mongoTemplate.save(new CMROUser("user@mail", "lname", "fname", "encpwd"));

        // Create an answer
        CMROUserAnswer createdAnswer = new CMROUserAnswer(question, user, true);
        assertThat(createdAnswer.getQuestionId()).as("Question id of created answer is not null and is equal to the question id")
                .isNotNull().isEqualTo(question.getId());
        assertThat(createdAnswer.getQuestion()).as("Question of created answer is not null and its id is equals to the question id of the answer")
                .isNotNull().extracting("id").isEqualTo(question.getId());

        // save answer and retrieve only its id
        final String answerId = this.mongoTemplate.save(new CMROUserAnswer(question, user, true)).getId();
        // Retrieve the answer from mongo
        CMROUserAnswer retrievedAnswer = this.mongoTemplate.findById(answerId, CMROUserAnswer.class);
        assertThat(retrievedAnswer).as("Retrieved answer is not null").isNotNull();
        assert retrievedAnswer != null;
        System.out.println("So far, retrieved answer is ok");

        CMROUser retrievedUser = retrievedAnswer.getUser();
        System.out.println("User retrieved?");
        assertThat(retrievedUser).isNotNull();
        assert retrievedUser != null;
        assertThat(retrievedUser.getId()).isEqualTo(user.getId());

        assertThat(retrievedAnswer.getQuestionId()).as("Question id of retrieved answer is not null and is equal to the question id")
                .isNotNull().isEqualTo(question.getId());
        Question retrievedQuestion = retrievedAnswer.getQuestion();
        System.out.println("Question retrieved?");
        assertThat(retrievedQuestion).isNotNull();
        assert retrievedQuestion != null;
        assertThat(retrievedQuestion.getId()).as("Question of retrieved answer is not null and its id is equals to the question id of the answer")
                .isEqualTo(question.getId());
    }

    /**
     * Test of getId method, of class CMROUserAnswer.
     */
    //@Test
    public void testGetId() {
        System.out.println("getId");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setId method, of class CMROUserAnswer.
     */
    //@Test
    public void testSetId() {
        System.out.println("setId");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getQuestionId method, of class CMROUserAnswer.
     */
    //@Test
    public void testGetQuestionId() {
        System.out.println("getQuestionId");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setQuestionId method, of class CMROUserAnswer.
     */
    //@Test
    public void testSetQuestionId() {
        System.out.println("setQuestionId");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getQuizName method, of class CMROUserAnswer.
     */
    //@Test
    public void testGetQuizName() {
        System.out.println("getQuizName");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setQuizName method, of class CMROUserAnswer.
     */
    //@Test
    public void testSetQuizName() {
        System.out.println("setQuizName");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getQuestion method, of class CMROUserAnswer.
     */
    //@Test
    public void testGetQuestion() {
        System.out.println("getQuestion");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setQuestion method, of class CMROUserAnswer.
     */
    //@Test
    public void testSetQuestion() {
        System.out.println("setQuestion");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUser method, of class CMROUserAnswer.
     */
    //@Test
    public void testGetUser() {
        System.out.println("getUser");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setUser method, of class CMROUserAnswer.
     */
    //@Test
    public void testSetUser() {
        System.out.println("setUser");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAttempts method, of class CMROUserAnswer.
     */
    //@Test
    public void testGetAttempts() {
        System.out.println("getAttempts");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setAttempts method, of class CMROUserAnswer.
     */
    //@Test
    public void testSetAttempts() {
        System.out.println("setAttempts");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLastAttemptDateTime method, of class CMROUserAnswer.
     */
    //@Test
    public void testGetLastAttemptDateTime() {
        System.out.println("getLastAttemptDateTime");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLastAttemptDateTime method, of class CMROUserAnswer.
     */
    //@Test
    public void testSetLastAttemptDateTime() {
        System.out.println("setLastAttemptDateTime");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isSuccess method, of class CMROUserAnswer.
     */
    //@Test
    public void testIsSuccess() {
        System.out.println("isSuccess");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSuccess method, of class CMROUserAnswer.
     */
    //@Test
    public void testSetSuccess() {
        System.out.println("setSuccess");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setNewAnswer method, of class CMROUserAnswer.
     */
    //@Test
    public void testSetNewAnswer() {
        System.out.println("setNewAnswer");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class CMROUserAnswer.
     */
    //@Test
    public void testToString() {
        System.out.println("toString");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createSampleAnswer method, of class CMROUserAnswer.
     */
    //@Test
    public void testCreateSampleAnswer() {
        System.out.println("createSampleAnswer");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
