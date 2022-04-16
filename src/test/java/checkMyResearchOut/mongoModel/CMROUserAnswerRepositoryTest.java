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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author Rémi Venant
 */
@DataMongoTest
@Import(MongoConfiguration.class)
@ActiveProfiles("mongo-test")
public class CMROUserAnswerRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CMROUserAnswerRepository testedRepo;

    private Quiz quiz;

    private List<Question> questions;

    private List<CMROUser> users;

    public CMROUserAnswerRepositoryTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        // Create 5 users, 1 quiz with 4 questions
        // 5 users
        this.users = List.of(
                this.mongoTemplate.save(new CMROUser("user1@mail", "lname1", "fname1", "encpass1")),
                this.mongoTemplate.save(new CMROUser("user2@mail", "lname2", "fname2", "encpass2")),
                this.mongoTemplate.save(new CMROUser("user3@mail", "lname3", "fname3", "encpass3")),
                this.mongoTemplate.save(new CMROUser("user4@mail", "lname4", "fname4", "encpass4")),
                this.mongoTemplate.save(new CMROUser("user5@mail", "lname5", "fname5", "encpass5"))
        );
        // 1 quiz
        this.quiz = this.mongoTemplate.save(new Quiz("Quiz", "Quiz", "Quiz Desc"));
        // Question 1 : 5 choice, 1 and 3 correct
        List<AnswerProposition> propositions = List.of(
                new AnswerProposition("Q1-P1", true),
                new AnswerProposition("Q1-P2", false),
                new AnswerProposition("Q1-P3", true),
                new AnswerProposition("Q1-P4", false),
                new AnswerProposition("Q1-P5", false));
        Question q1 = this.mongoTemplate.save(new Question(quiz.getName(), "Q1", propositions, "aQ1", "aP1"));
        // Question 2: 5 choices, 4 correct
        propositions = List.of(
                new AnswerProposition("Q2-P1", false),
                new AnswerProposition("Q2-P2", false),
                new AnswerProposition("Q2-P3", false),
                new AnswerProposition("Q2-P4", true),
                new AnswerProposition("Q2-P5", false));
        Question q2 = this.mongoTemplate.save(new Question(quiz.getName(), "Q2", propositions, "aQ2", "aP2"));
        // Question 3: 5 choices, non correct
        propositions = List.of(
                new AnswerProposition("Q3-P1", false),
                new AnswerProposition("Q3-P2", false),
                new AnswerProposition("Q3-P3", false),
                new AnswerProposition("Q3-P4", false),
                new AnswerProposition("Q3-P5", false));
        Question q3 = this.mongoTemplate.save(new Question(quiz.getName(), "Q3", propositions, "aQ3", "aP3"));
        // Question 4: 5 choices, all correct
        propositions = List.of(
                new AnswerProposition("Q4-P1", true),
                new AnswerProposition("Q4-P2", true),
                new AnswerProposition("Q4-P3", true),
                new AnswerProposition("Q4-P4", true),
                new AnswerProposition("Q4-P5", true));
        Question q4 = this.mongoTemplate.save(new Question(quiz.getName(), "Q4", propositions, "aQ4", "aP4"));
        this.questions = List.of(q1, q2, q3, q4);
        //U1 answers all question correct with 1 attempt each
        this.mongoTemplate.save(this.generateAnswer(q1, this.users.get(0), true, 1, "2022-02-01T15:00:00"));
        this.mongoTemplate.save(this.generateAnswer(q2, this.users.get(0), true, 1, "2022-02-01T16:00:00"));
        this.mongoTemplate.save(this.generateAnswer(q3, this.users.get(0), true, 1, "2022-02-01T17:00:00"));
        this.mongoTemplate.save(this.generateAnswer(q4, this.users.get(0), true, 1, "2022-02-01T18:00:00"));
        //U2 answers all questions, all correct with 1 attempt each, but later than U1
        this.mongoTemplate.save(this.generateAnswer(q1, this.users.get(1), true, 1, "2022-02-01T17:00:00"));
        this.mongoTemplate.save(this.generateAnswer(q2, this.users.get(1), true, 1, "2022-02-01T17:30:00"));
        this.mongoTemplate.save(this.generateAnswer(q3, this.users.get(1), true, 1, "2022-02-01T18:00:00"));
        this.mongoTemplate.save(this.generateAnswer(q4, this.users.get(1), true, 1, "2022-02-01T19:00:00"));
        //U3 answers all questions, all correct with more than 1 attempt each
        this.mongoTemplate.save(this.generateAnswer(q1, this.users.get(2), true, 1, "2022-02-01T15:00:00"));
        this.mongoTemplate.save(this.generateAnswer(q2, this.users.get(2), true, 2, "2022-02-01T16:00:00"));
        this.mongoTemplate.save(this.generateAnswer(q3, this.users.get(2), true, 1, "2022-02-01T17:00:00"));
        this.mongoTemplate.save(this.generateAnswer(q4, this.users.get(2), true, 3, "2022-02-01T18:00:00"));
        //U4 answers all questions, 3 correct with 1 attempt each
        this.mongoTemplate.save(this.generateAnswer(q1, this.users.get(3), true, 1, "2022-02-01T15:00:00"));
        this.mongoTemplate.save(this.generateAnswer(q2, this.users.get(3), true, 1, "2022-02-01T16:00:00"));
        this.mongoTemplate.save(this.generateAnswer(q3, this.users.get(3), false, 1, "2022-02-01T17:00:00"));
        this.mongoTemplate.save(this.generateAnswer(q4, this.users.get(3), true, 1, "2022-02-01T18:00:00"));
        //U5 answers only 3 questions, all correct with 1 attempt each
        this.mongoTemplate.save(this.generateAnswer(q1, this.users.get(4), true, 1, "2022-02-01T15:00:00"));
        this.mongoTemplate.save(this.generateAnswer(q2, this.users.get(4), true, 1, "2022-02-01T16:00:00"));
        this.mongoTemplate.save(this.generateAnswer(q4, this.users.get(4), true, 1, "2022-02-01T18:00:00"));
    }

    private CMROUserAnswer generateAnswer(Question q, CMROUser u, boolean correct, int nbAttempts, String lastAttempt) {
        CMROUserAnswer answer = new CMROUserAnswer(q, u, correct);
        if (nbAttempts > 0) {
            answer.setAttempts(nbAttempts);
        }
        if (lastAttempt != null) {
            LocalDateTime la = LocalDateTime.parse(lastAttempt);
            answer.setLastAttemptDateTime(la);
        }
        return answer;
    }

    @AfterEach
    public void tearDown() {
        this.mongoTemplate.remove(new BasicQuery("{}"), CMROUserAnswer.class);
        this.mongoTemplate.remove(new BasicQuery("{}"), Question.class);
        this.mongoTemplate.remove(new BasicQuery("{}"), Quiz.class);
        this.mongoTemplate.remove(new BasicQuery("{}"), CMROUser.class);
    }

    /**
     * Test of findByQuizNameAndUser method, of class CMROUserAnswerRepository.
     */
    @Test
    public void testFindByQuizNameAndUser() {
        System.out.println("findByQuizNameAndUser");

        List<CMROUserAnswer> answers = this.testedRepo.findByQuizNameAndUser(
                quiz.getName(), this.users.get(1)).collect(Collectors.toList());

        assertThat(answers).hasSize(4);
    }

    /**
     * Test of findByQuestionIdAndUser method, of class CMROUserAnswerRepository.
     */
    @Test
    public void testFindByQuestionIdAndUser() {
        System.out.println("findByQuestionIdAndUser");
        Optional<CMROUserAnswer> ans = this.testedRepo.findByQuestionIdAndUser(this.questions.get(2).getId(), this.users.get(3));
        assertThat(ans).isNotEmpty();
        assertThat(ans.get().isSuccess()).isFalse();
    }

    /**
     * Test of countByQuizNameAndUserAndSuccessIsTrue method, of class CMROUserAnswerRepository.
     */
    @Test
    public void testCountByQuizNameAndUserAndSuccessIsTrue() {
        System.out.println("countByQuizNameAndUserAndSuccessIsTrue");
        long c = this.testedRepo.countByQuizNameAndUserAndSuccessIsTrue(this.quiz.getName(), this.users.get(3));
        assertThat(c).isEqualTo(3);
    }

    /**
     * Test of deleteByUser method, of class CMROUserAnswerRepository.
     */
    @Test
    public void testDeleteByUser() {
        System.out.println("deleteByUser");
        assertThat(this.testedRepo.count()).isEqualTo(19);
        this.testedRepo.deleteByUser(this.users.get(4));
        assertThat(this.testedRepo.count()).isEqualTo(16);
    }

    /**
     * Test of deleteByQuestion method, of class CMROUserAnswerRepository.
     */
    @Test
    public void testDeleteByQuestion() {
        System.out.println("deleteByQuestion");
        assertThat(this.testedRepo.count()).isEqualTo(19);
        this.testedRepo.deleteByQuestionId(this.questions.get(0).getId());
        assertThat(this.testedRepo.count()).isEqualTo(14);
    }

    /**
     * Test of deleteByQuizName method, of class CMROUserAnswerRepository.
     */
    @Test
    public void testDeleteByQuizName() {
        System.out.println("deleteByQuizName");
        assertThat(this.testedRepo.count()).isEqualTo(19);
        this.testedRepo.deleteByQuizName(this.quiz.getName());
        assertThat(this.testedRepo.count()).isEqualTo(0);
    }

    @Test
    public void testGetRankingsByQuizName() {
        System.out.println("getRankingsByQuizName");
        List<UserIdQuizRank> ranking = this.testedRepo.getRankingsByQuizName(this.quiz.getName());
        assertThat(ranking).hasSize(5);
        System.out.println("RANKING");
        System.out.println("------");
        ranking.forEach(rank -> {
            System.out.println(
                    String.format("- %s | numSuccess: %d | numAttempts: %d | lastAttempt: %s",
                            rank.getUserId(), rank.getNumSuccessfulAnswers(), rank.getNumAttempts(),
                            rank.getLastAttempt().toString()
                    ));
        });
        System.out.println("------");
        for (int i = 0; i < 5; i++) {
            assertThat(ranking.get(i).getUserId()).as(String.format("User %d is %d", i, i)).isEqualTo(this.users.get(i).getId());
        }
    }
}
