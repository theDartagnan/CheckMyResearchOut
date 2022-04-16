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
import checkMyResearchOut.mongoModel.CMROUserAnswer;
import checkMyResearchOut.mongoModel.CMROUserAnswerRepository;
import checkMyResearchOut.mongoModel.Question;
import checkMyResearchOut.mongoModel.QuestionRepository;
import checkMyResearchOut.mongoModel.Quiz;
import checkMyResearchOut.mongoModel.TestInstanceGenerationUtil;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import checkMyResearchOut.mongoModel.QuizRepository;

/**
 *
 * @author Rémi Venant
 */
public class QuizServiceImplTest {

    private AutoCloseable mocks;

    @Mock
    private QuizRepository quizRepo;

    @Mock
    private QuestionRepository questionRepo;

    @Mock
    private CMROUserAnswerRepository answerRepo;

    private QuizServiceImpl testedSvc;

    public QuizServiceImplTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        this.mocks = MockitoAnnotations.openMocks(this);
        this.testedSvc = new QuizServiceImpl(quizRepo, questionRepo, answerRepo, 5);
    }

    @AfterEach
    public void tearDown() throws Exception {
        this.mocks.close();
    }

    /**
     * Test of createQuiz method, of class QuizServiceImpl.
     */
    //@Test
    public void testCreateQuiz() {
        System.out.println("createQuiz");
        fail("The test case is a prototype.");
    }

    /**
     * Test of getQuizByName method, of class QuizServiceImpl.
     */
    //@Test
    public void testGetQuizByName() {
        System.out.println("getQuizByName");
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateQuiz method, of class QuizServiceImpl.
     */
    //@Test
    public void testUpdateQuiz() {
        System.out.println("updateQuiz");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteQuiz method, of class QuizServiceImpl.
     */
    //@Test
    public void testDeleteQuiz() {
        System.out.println("deleteQuiz");
        fail("The test case is a prototype.");
    }

    /**
     * Test of createQuestion method, of class QuizServiceImpl.
     */
    //@Test
    public void testCreateQuestion() {
        System.out.println("createQuestion");
        fail("The test case is a prototype.");
    }

    /**
     * Test of getQuestionById method, of class QuizServiceImpl.
     */
    //@Test
    public void testGetQuestionById() {
        System.out.println("getQuestionById");
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateQuestion method, of class QuizServiceImpl.
     */
    //@Test
    public void testUpdateQuestion() {
        System.out.println("updateQuestion");
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteQuestion method, of class QuizServiceImpl.
     */
    //@Test
    public void testDeleteQuestion() {
        System.out.println("deleteQuestion");
        fail("The test case is a prototype.");
    }

    /**
     * Test of getQuizQuestionsNumber method, of class QuizServiceImpl.
     */
    @Test
    public void testGetQuizQuestionsNumber() {
        System.out.println("getQuizQuestionsNumber");
        Quiz quiz = new Quiz("qi", "quiz", "quizDesc");
        List<AnswerProposition> propositions = List.of(
                new AnswerProposition("Q1-P1", true),
                new AnswerProposition("Q1-P2", false),
                new AnswerProposition("Q1-P3", true),
                new AnswerProposition("Q1-P4", false),
                new AnswerProposition("Q1-P5", false));
        List<Question> questions = List.of(
                new Question(quiz.getName(), "Q1", propositions, "aQ1", "pQ1"),
                new Question(quiz.getName(), "Q2", propositions, "aQ2", "pQ2"),
                new Question(quiz.getName(), "Q3", propositions, "aQ3", "pQ3"));

        given(this.questionRepo.countByQuizName(quiz.getName())).willReturn(3L);
        Long nbQuestions = this.testedSvc.getQuizQuestionsNumber(quiz);
        assertThat(nbQuestions).isEqualTo(3);
    }

    /**
     * Test of getUnansweredOrUnsuccessfulAnsweredQuestionsNumber method, of class QuizServiceImpl.
     */
    @Test
    public void testGetUnansweredOrUnsuccessfulAnsweredQuestionsNumber() {
        System.out.println("getUnsuccessfulAnsweredQuestionsNumber");
        Quiz quiz = new Quiz("qi", "quiz", "quizDesc");
        CMROUser user = new CMROUser("user@mail", "lname", "fname", "encpwd");

        given(this.questionRepo.countByQuizName(quiz.getName())).willReturn(3L);
        given(this.answerRepo.countByQuizNameAndUserAndSuccessIsTrue(quiz.getName(), user)).willReturn(2L);

        Long n = this.testedSvc.getUnansweredOrUnsuccessfulAnsweredQuestionsNumber(quiz, user);
        assertThat(n).isEqualTo(1L);
        Mockito.verify(this.questionRepo, Mockito.times(1)).countByQuizName(quiz.getName());
        Mockito.verify(this.answerRepo, Mockito.times(1)).countByQuizNameAndUserAndSuccessIsTrue(quiz.getName(), user);
    }

    /**
     * Test of getAnswerableQuestions method, of class QuizServiceImpl.
     */
    @Test
    public void testGetAnswerableQuestions() {
        System.out.println("getUnsuccessfulAnsweredAndAnswerableQuestions");
        CMROUser user = new CMROUser("user@mail", "lname", "fname", "encpwd");
        Quiz quiz = new Quiz("qi", "quiz", "quizDesc");
        List<AnswerProposition> propositions = List.of(new AnswerProposition("Q1-P1", true),
                new AnswerProposition("Q1-P2", false));
        List<Question> questions = List.of(
                TestInstanceGenerationUtil.withId(new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"), "qID1"),
                TestInstanceGenerationUtil.withId(new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"), "qID2"),
                TestInstanceGenerationUtil.withId(new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"), "qID3"),
                TestInstanceGenerationUtil.withId(new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"), "qID4")
        );
        //Q1: answered correctly, Q2: answered badly, and early, Q3: answered badly long time ago, Q4: not answered
        List<CMROUserAnswer> answers = List.of(
                TestInstanceGenerationUtil.withAttemptInfo(new CMROUserAnswer(questions.get(0), user, true), 1, LocalDateTime.now().minusMinutes(30).toString()),
                TestInstanceGenerationUtil.withAttemptInfo(new CMROUserAnswer(questions.get(1), user, false), 1, LocalDateTime.now().minusMinutes(1).toString()),
                TestInstanceGenerationUtil.withAttemptInfo(new CMROUserAnswer(questions.get(2), user, false), 1, LocalDateTime.now().minusMinutes(30).toString())
        );
        given(this.answerRepo.findByQuizNameAndUser(quiz.getName(), user)).will(iom -> answers.stream());
        given(this.questionRepo.findByQuizName(quiz.getName())).will(iom -> questions.stream());

        List<Question> availableQuestions = this.testedSvc.getAnswerableQuestions(quiz, user);

        Mockito.verify(this.answerRepo, Mockito.times(1)).findByQuizNameAndUser(quiz.getName(), user);
        Mockito.verify(this.questionRepo, Mockito.times(1)).findByQuizName(quiz.getName());
        assertThat(availableQuestions).hasSize(2);
        assertThat(availableQuestions).map(Question::getId).containsExactlyInAnyOrder("qID3", "qID4");
    }

    @Test
    public void testGetAnswerableQuestionsNoQuestion() {
        System.out.println("getUnsuccessfulAnsweredAndAnswerableQuestions");
        CMROUser user = new CMROUser("user@mail", "lname", "fname", "encpwd");
        Quiz quiz = new Quiz("qi", "quiz", "quizDesc");
        List<AnswerProposition> propositions = List.of(new AnswerProposition("Q1-P1", true),
                new AnswerProposition("Q1-P2", false));
        List<Question> questions = List.of(
                TestInstanceGenerationUtil.withId(new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"), "qID1"),
                TestInstanceGenerationUtil.withId(new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"), "qID2"),
                TestInstanceGenerationUtil.withId(new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"), "qID3"),
                TestInstanceGenerationUtil.withId(new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"), "qID4")
        );
        //Q1: answered correctly, Q2: answered badly, and early, Q3: answered badly long time ago, Q4: not answered
        List<CMROUserAnswer> answers = List.of(
                TestInstanceGenerationUtil.withAttemptInfo(new CMROUserAnswer(questions.get(0), user, true), 1, LocalDateTime.now().minusMinutes(30).toString()),
                TestInstanceGenerationUtil.withAttemptInfo(new CMROUserAnswer(questions.get(1), user, false), 1, LocalDateTime.now().minusMinutes(1).toString()),
                TestInstanceGenerationUtil.withAttemptInfo(new CMROUserAnswer(questions.get(2), user, false), 1, LocalDateTime.now().minusMinutes(1).toString()),
                TestInstanceGenerationUtil.withAttemptInfo(new CMROUserAnswer(questions.get(3), user, true), 1, LocalDateTime.now().minusMinutes(30).toString())
        );
        given(this.answerRepo.findByQuizNameAndUser(quiz.getName(), user)).will(iom -> answers.stream());
        given(this.questionRepo.findByQuizName(quiz.getName())).will(iom -> questions.stream());

        List<Question> availableQuestions = this.testedSvc.getAnswerableQuestions(quiz, user);

        Mockito.verify(this.answerRepo, Mockito.times(1)).findByQuizNameAndUser(quiz.getName(), user);
        Mockito.verify(this.questionRepo, Mockito.times(1)).findByQuizName(quiz.getName());
        assertThat(availableQuestions).isEmpty();
    }

    /**
     * Test of getRandomAnswerableQuestion method, of class QuizServiceImpl.
     */
    @Test
    public void testGetRandomUnsuccessfulAnsweredAndAnswerableQuestion() {
        System.out.println("getRandomUnsuccessfulAnsweredAndAnswerableQuestion");
        CMROUser user = new CMROUser("user@mail", "lname", "fname", "encpwd");
        Quiz quiz = new Quiz("qi", "quiz", "quizDesc");
        List<AnswerProposition> propositions = List.of(new AnswerProposition("Q1-P1", true),
                new AnswerProposition("Q1-P2", false));
        List<Question> questions = List.of(
                TestInstanceGenerationUtil.withId(new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"), "qID1"),
                TestInstanceGenerationUtil.withId(new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"), "qID2"),
                TestInstanceGenerationUtil.withId(new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"), "qID3"),
                TestInstanceGenerationUtil.withId(new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"), "qID4")
        );
        //Q1: answered correctly, Q2: answered badly, and early, Q3: answered badly long time ago, Q4: not answered
        List<CMROUserAnswer> answers = List.of(
                TestInstanceGenerationUtil.withAttemptInfo(new CMROUserAnswer(questions.get(0), user, true), 1, LocalDateTime.now().minusMinutes(30).toString()),
                TestInstanceGenerationUtil.withAttemptInfo(new CMROUserAnswer(questions.get(1), user, false), 1, LocalDateTime.now().minusMinutes(1).toString()),
                TestInstanceGenerationUtil.withAttemptInfo(new CMROUserAnswer(questions.get(2), user, false), 1, LocalDateTime.now().minusMinutes(30).toString())
        );
        given(this.answerRepo.findByQuizNameAndUser(quiz.getName(), user)).will(iom -> answers.stream());
        given(this.questionRepo.findByQuizName(quiz.getName())).will(iom -> questions.stream());

        Question selectedQuestion = this.testedSvc.getRandomAnswerableQuestion(quiz, user);

        Mockito.verify(this.answerRepo, Mockito.times(1)).findByQuizNameAndUser(quiz.getName(), user);
        Mockito.verify(this.questionRepo, Mockito.times(1)).findByQuizName(quiz.getName());
        assertThat(selectedQuestion).isNotNull();
        assertThat(selectedQuestion.getId()).isIn("qID3", "qID4");
    }

    @Test
    public void testGetRandomUnsuccessfulAnsweredAndAnswerableQuestionNoQuestionLeft() {
        System.out.println("getRandomUnsuccessfulAnsweredAndAnswerableQuestion");
        CMROUser user = new CMROUser("user@mail", "lname", "fname", "encpwd");
        Quiz quiz = new Quiz("qi", "quiz", "quizDesc");
        List<AnswerProposition> propositions = List.of(new AnswerProposition("Q1-P1", true),
                new AnswerProposition("Q1-P2", false));
        List<Question> questions = List.of(
                TestInstanceGenerationUtil.withId(new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"), "qID1"),
                TestInstanceGenerationUtil.withId(new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"), "qID2"),
                TestInstanceGenerationUtil.withId(new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"), "qID3"),
                TestInstanceGenerationUtil.withId(new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"), "qID4")
        );
        //Q1: answered correctly, Q2: answered badly, and early, Q3: answered badly and early, Q4: answered properly
        List<CMROUserAnswer> answers = List.of(
                TestInstanceGenerationUtil.withAttemptInfo(new CMROUserAnswer(questions.get(0), user, true), 1, LocalDateTime.now().minusMinutes(30).toString()),
                TestInstanceGenerationUtil.withAttemptInfo(new CMROUserAnswer(questions.get(1), user, false), 1, LocalDateTime.now().minusMinutes(1).toString()),
                TestInstanceGenerationUtil.withAttemptInfo(new CMROUserAnswer(questions.get(2), user, false), 1, LocalDateTime.now().minusMinutes(1).toString()),
                TestInstanceGenerationUtil.withAttemptInfo(new CMROUserAnswer(questions.get(3), user, true), 1, LocalDateTime.now().minusMinutes(30).toString())
        );
        given(this.answerRepo.findByQuizNameAndUser(quiz.getName(), user)).will(iom -> answers.stream());
        given(this.questionRepo.findByQuizName(quiz.getName())).will(iom -> questions.stream());

        Question selectedQuestion = this.testedSvc.getRandomAnswerableQuestion(quiz, user);

        Mockito.verify(this.answerRepo, Mockito.times(1)).findByQuizNameAndUser(quiz.getName(), user);
        Mockito.verify(this.questionRepo, Mockito.times(1)).findByQuizName(quiz.getName());
        assertThat(selectedQuestion).isNull();
    }
}
