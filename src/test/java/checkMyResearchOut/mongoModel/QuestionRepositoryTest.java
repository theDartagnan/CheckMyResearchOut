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
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
public class QuestionRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private QuestionRepository testedRepo;

    private Quiz[] quizzes = new Quiz[2];

    public QuestionRepositoryTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        this.quizzes[0] = this.mongoTemplate.save(new Quiz("quiz1", "Quiz 1",
                "A quiz desc 1"));
        this.quizzes[1] = this.mongoTemplate.save(new Quiz("quiz2", "Quiz 2",
                "A quiz desc 2"));
    }

    @AfterEach
    public void tearDown() {
        this.mongoTemplate.remove(new BasicQuery("{}"), Question.class);
        this.mongoTemplate.remove(new BasicQuery("{}"), Quiz.class);
    }

    public Question generateQuestion(int quizIdx, int questionIdx) {
        List<AnswerProposition> propositions = Stream.of(1, 2, 3, 4, 5)
                .map(i -> new AnswerProposition(
                String.format("Proposition %d of question %d of quiz %d", i, questionIdx + 1, quizIdx + 1),
                i % 2 == questionIdx % 2))
                .collect(Collectors.toList());
        String author = String.format("Author of question %d of quiz %d.", questionIdx + 1, quizIdx + 1);
        String publication = String.format("Publication of question %d of quiz %d.", questionIdx + 1, quizIdx + 1);
        return new Question(this.quizzes[quizIdx].getName(), "Q" + questionIdx, propositions, author, publication);
    }

    /**
     * Test of findByQuizName method, of class QuestionRepository.
     */
    @Test
    public void testFindByQuizName() {
        System.out.println("findByQuizName");

        String[][] questionsIds = new String[2][5];
        for (int quizIdx = 0; quizIdx < 2; quizIdx++) {
            for (int questionIdx = 0; questionIdx < 5; questionIdx++) {
                Question q = this.mongoTemplate.save(this.generateQuestion(quizIdx, questionIdx));
                questionsIds[quizIdx][questionIdx] = q.getId();
            }
        }

        List<Question> questions = this.testedRepo.findByQuizName(this.quizzes[0].getName())
                .collect(Collectors.toList());
        assertThat(questions).hasSize(5);
        assertThat(questions).map(Question::getId).containsExactlyInAnyOrder(questionsIds[0]);
        questions = this.testedRepo.findByQuizName(this.quizzes[1].getName())
                .collect(Collectors.toList());
        assertThat(questions).hasSize(5);
        assertThat(questions).map(Question::getId).containsExactlyInAnyOrder(questionsIds[1]);
    }

    /**
     * Test of countByQuizName method, of class QuestionRepository.
     */
    @Test
    public void testCountByQuizName() {
        System.out.println("countByQuizName");
        String[][] questionsIds = new String[2][5];
        for (int quizIdx = 0; quizIdx < 2; quizIdx++) {
            for (int questionIdx = 0; questionIdx < 5; questionIdx++) {
                Question q = this.mongoTemplate.save(this.generateQuestion(quizIdx, questionIdx));
                questionsIds[quizIdx][questionIdx] = q.getId();
            }
        }
        long nbQuestions = this.testedRepo.countByQuizName(this.quizzes[0].getName());
        assertThat(nbQuestions).isEqualTo(5);
        nbQuestions = this.testedRepo.countByQuizName(this.quizzes[1].getName());
        assertThat(nbQuestions).isEqualTo(5);
    }

    /**
     * Test of deleteByQuizName method, of class QuestionRepository.
     */
    @Test
    public void testDeleteByQuizName() {
        System.out.println("deleteByQuizName");
        String[][] questionsIds = new String[2][5];
        for (int quizIdx = 0; quizIdx < 2; quizIdx++) {
            for (int questionIdx = 0; questionIdx < 5; questionIdx++) {
                Question q = this.mongoTemplate.save(this.generateQuestion(quizIdx, questionIdx));
                questionsIds[quizIdx][questionIdx] = q.getId();
            }
        }
        long nbTotalQuestions = this.testedRepo.count();
        assertThat(nbTotalQuestions).isEqualTo(2 * 5);
        this.testedRepo.deleteByQuizName(this.quizzes[0].getName());
        nbTotalQuestions = this.testedRepo.count();
        assertThat(nbTotalQuestions).isEqualTo(5);
        this.testedRepo.deleteByQuizName(this.quizzes[1].getName());
        nbTotalQuestions = this.testedRepo.count();
        assertThat(nbTotalQuestions).isEqualTo(0);
    }

}
