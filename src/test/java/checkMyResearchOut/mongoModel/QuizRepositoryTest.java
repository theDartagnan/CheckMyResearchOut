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
import java.util.Optional;
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
public class QuizRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private QuizRepository testedRepo;

    public QuizRepositoryTest() {
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
        this.mongoTemplate.remove(new BasicQuery("{}"), Quiz.class);
    }

    private Quiz generateQuiz(int idx) {
        return new Quiz("quiz_" + idx, "Quiz " + idx, "A quiz " + idx);
    }

    /**
     * Test of findByName method, of class QuizRepository.
     */
    @Test
    public void testFindByName() {
        System.out.println("findByName");

        String[] quizIds = new String[4];
        for (int i = 0; i < 4; i++) {
            Quiz qi = this.mongoTemplate.save(this.generateQuiz(i));
            quizIds[i] = qi.getId();
        }

        Optional<Quiz> optQuiz = this.testedRepo.findByName("quiz_2");
        assertThat(optQuiz).isNotEmpty();
        assertThat(optQuiz.get().getId()).isEqualTo(quizIds[2]);
    }

}
