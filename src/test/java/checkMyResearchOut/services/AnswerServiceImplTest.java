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
import checkMyResearchOut.mongoModel.CMROUserNamesOnly;
import checkMyResearchOut.mongoModel.CMROUserRepository;
import checkMyResearchOut.mongoModel.Question;
import checkMyResearchOut.mongoModel.Quiz;
import checkMyResearchOut.mongoModel.TestInstanceGenerationUtil;
import checkMyResearchOut.mongoModel.UserIdQuizRank;
import checkMyResearchOut.services.exceptions.OtherAnswerGivenEarlierException;
import checkMyResearchOut.services.exceptions.SuccessfulAnswerException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.AdditionalAnswers;
import static org.mockito.BDDMockito.given;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author Rémi Venant
 */
public class AnswerServiceImplTest {

    private AutoCloseable mocks;

    @Mock
    private CMROUserAnswerRepository answerRepo;

    @Mock
    private CMROUserRepository userRepo;

    private AnswerServiceImpl testedSvc;

    public AnswerServiceImplTest() {
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
        this.testedSvc = new AnswerServiceImpl(answerRepo, userRepo, 5);
    }

    @AfterEach
    public void tearDown() throws Exception {
        this.mocks.close();
    }

    /**
     * Test of answerAQuestion method, of class AnswerServiceImpl.
     */
    @Test
    public void testAnswerAQuestionFirstTime() throws OtherAnswerGivenEarlierException, SuccessfulAnswerException {
        System.out.println("answerAQuestion first time");
        CMROUser user = new CMROUser("user@mail", "lname", "fname", "encpwd");
        Quiz quiz = new Quiz("qi", "quiz", "quizDesc");
        List<AnswerProposition> propositions = List.of(
                new AnswerProposition("Q1-P1", true),
                new AnswerProposition("Q1-P2", false),
                new AnswerProposition("Q1-P3", true),
                new AnswerProposition("Q1-P4", false),
                new AnswerProposition("Q1-P5", false));
        Question question = TestInstanceGenerationUtil.withId(
                new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"),
                "qID");

        given(this.answerRepo.findByQuestionIdAndUser("qID", user)).willReturn(Optional.empty());
        given(this.answerRepo.save(Mockito.any(CMROUserAnswer.class))).will(AdditionalAnswers.returnsFirstArg());

        //Correct answer
        CMROUserAnswer res1 = this.testedSvc.answerAQuestion(user, question, Set.of(0, 2));
        //Uncomplete answer
        CMROUserAnswer res2 = this.testedSvc.answerAQuestion(user, question, Set.of(0));
        //Wrong propositions
        CMROUserAnswer res3 = this.testedSvc.answerAQuestion(user, question, Set.of(0, 2, 3));

        Mockito.verify(this.answerRepo, Mockito.times(3)).findByQuestionIdAndUser("qID", user);
        Mockito.verify(this.answerRepo, Mockito.times(3)).save(Mockito.any(CMROUserAnswer.class));

        assertThat(res1.isSuccess()).isTrue();
        assertThat(res1.getAttempts()).isEqualTo(1);
        assertThat(res2.isSuccess()).isFalse();
        assertThat(res2.getAttempts()).isEqualTo(1);
        assertThat(res3.isSuccess()).isFalse();
        assertThat(res3.getAttempts()).isEqualTo(1);
    }

    @Test
    public void testAnswerAQuestionNextTimeOk() throws OtherAnswerGivenEarlierException, SuccessfulAnswerException {
        System.out.println("answerAQuestion second time");
        CMROUser user = new CMROUser("user@mail", "lname", "fname", "encpwd");
        Quiz quiz = new Quiz("qi", "quiz", "quizDesc");
        List<AnswerProposition> propositions = List.of(
                new AnswerProposition("Q1-P1", true),
                new AnswerProposition("Q1-P2", false),
                new AnswerProposition("Q1-P3", true),
                new AnswerProposition("Q1-P4", false),
                new AnswerProposition("Q1-P5", false));
        Question question = TestInstanceGenerationUtil.withId(
                new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"),
                "qID");
        CMROUserAnswer firstAnswer = new CMROUserAnswer(question, user, false);
        LocalDateTime lastAttemptDt = LocalDateTime.now().minusHours(3);
        firstAnswer = TestInstanceGenerationUtil.withAttemptInfo(firstAnswer, 2, lastAttemptDt.toString());

        given(this.answerRepo.findByQuestionIdAndUser("qID", user)).willReturn(Optional.of(firstAnswer));
        given(this.answerRepo.save(Mockito.any(CMROUserAnswer.class))).will(AdditionalAnswers.returnsFirstArg());

        //Correct answer
        CMROUserAnswer res = this.testedSvc.answerAQuestion(user, question, Set.of(0, 2));

        Mockito.verify(this.answerRepo, Mockito.times(1)).findByQuestionIdAndUser("qID", user);
        Mockito.verify(this.answerRepo, Mockito.times(1)).save(Mockito.any(CMROUserAnswer.class));

        assertThat(res.isSuccess()).isTrue();
        assertThat(res.getAttempts()).isEqualTo(3);
        assertThat(res.getLastAttemptDateTime()).isAfter(lastAttemptDt);
    }

    @Test
    public void testAnswerAQuestionNextTimeTooEarly() {
        System.out.println("answerAQuestion second time but too eearly");
        CMROUser user = new CMROUser("user@mail", "lname", "fname", "encpwd");
        Quiz quiz = new Quiz("qi", "quiz", "quizDesc");
        List<AnswerProposition> propositions = List.of(
                new AnswerProposition("Q1-P1", true),
                new AnswerProposition("Q1-P2", false),
                new AnswerProposition("Q1-P3", true),
                new AnswerProposition("Q1-P4", false),
                new AnswerProposition("Q1-P5", false));
        Question question = TestInstanceGenerationUtil.withId(
                new Question(quiz.getName(), "Q", propositions, "aQ", "pQ"),
                "qID");
        CMROUserAnswer firstAnswer = new CMROUserAnswer(question, user, false);
        LocalDateTime lastAttemptDt = LocalDateTime.now().minusMinutes(1);
        firstAnswer = TestInstanceGenerationUtil.withAttemptInfo(firstAnswer, 2, lastAttemptDt.toString());

        given(this.answerRepo.findByQuestionIdAndUser("qID", user)).willReturn(Optional.of(firstAnswer));
        given(this.answerRepo.save(Mockito.any(CMROUserAnswer.class))).will(AdditionalAnswers.returnsFirstArg());

        Assertions.assertThrows(OtherAnswerGivenEarlierException.class, () -> {
            this.testedSvc.answerAQuestion(user, question, Set.of(0, 2));
        });
    }

    /**
     * Test of getQuizRanking method, of class AnswerServiceImpl.
     */
    @Test
    public void testGetQuizRanking() {
        System.out.println("getQuizRanking");
        CMROUser user1 = TestInstanceGenerationUtil.withId(new CMROUser("user1@mail", "lname1", "fname1", "encpwd"), "u1ID");
        CMROUser user2 = TestInstanceGenerationUtil.withId(new CMROUser("user2@mail", "lname2", "fname2", "encpwd"), "u2ID");
        CMROUser user3 = TestInstanceGenerationUtil.withId(new CMROUser("user3@mail", "lname3", "fname3", "encpwd"), "u3ID");
        Quiz quiz = new Quiz("qi", "quiz", "quizDesc");
        List<UserIdQuizRank> rawRanking = List.of(new UserIdQuizRank("u1ID", 3, 3, LocalDateTime.parse("2022-02-01T15:00:00")),
                new UserIdQuizRank("u2ID", 3, 4, LocalDateTime.parse("2022-02-01T14:00:00")),
                new UserIdQuizRank("u3ID", 2, 3, LocalDateTime.parse("2022-02-01T16:00:00"))
        );
        final List<CMROUserNamesOnly> publicInfo = List.of(new CMROUserNamesOnlyImpl(user1),
                new CMROUserNamesOnlyImpl(user2), new CMROUserNamesOnlyImpl(user3));

        given(this.answerRepo.getRankingsByQuizName(quiz.getName())).willReturn(rawRanking);
        given(this.userRepo.findPublicInfoByIdIn(Mockito.anyList())).will(iom -> publicInfo.stream());

        CMROUserRanking ranking = this.testedSvc.getQuizRanking(quiz, user2);
        Mockito.verify(this.answerRepo, Mockito.times(1)).getRankingsByQuizName(quiz.getName());
        Mockito.verify(this.userRepo, Mockito.times(1)).findPublicInfoByIdIn(Mockito.anyList());
        assertThat(ranking.getRank()).isEqualTo(2);
        assertThat(ranking.getPublicRanking().get(0).getFirstname()).isEqualTo("fname1");
        assertThat(ranking.getPublicRanking().get(0).getLastname()).isEqualTo("lname1");
        assertThat(ranking.getPublicRanking().get(0).getNumSuccessfulAnswers()).isEqualTo(3);
        assertThat(ranking.getPublicRanking().get(0).getNumAttempts()).isEqualTo(3);
    }

    public static class CMROUserNamesOnlyImpl implements CMROUserNamesOnly {

        private final CMROUser user;

        public CMROUserNamesOnlyImpl(CMROUser user) {
            this.user = user;
        }

        @Override
        public String getId() {
            return this.user.getId();
        }

        @Override
        public String getLastname() {
            return this.user.getLastname();
        }

        @Override
        public String getFirstname() {
            return this.user.getFirstname();
        }

    }
}
