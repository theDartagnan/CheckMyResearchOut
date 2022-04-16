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
import checkMyResearchOut.mongoModel.UserIdQuizRank;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author Rémi Venant
 */
@Service
public class AnswerServiceImpl implements AnswerService {

    private final CMROUserAnswerRepository answerRepo;

    private final CMROUserRepository userRepo;

    private final int answerAttemptTimeoutMinutes;

    @Autowired
    public AnswerServiceImpl(CMROUserAnswerRepository answerRepo, CMROUserRepository userRepo,
            @Value("${checkMyResearchOut.game.answer-attempt-timeout:5}") int answerAttemptTimeoutMinutes) {
        this.answerRepo = answerRepo;
        this.userRepo = userRepo;
        this.answerAttemptTimeoutMinutes = answerAttemptTimeoutMinutes;
    }

    @Override
    public CMROUserAnswer answerAQuestion(CMROUser user, Question question, Set<Integer> propositionIndices) throws IllegalArgumentException {
        if (user == null || question == null || propositionIndices == null) {
            throw new IllegalArgumentException("User nor question nor propositions cannot be null.");
        }
        // Check if their was a previous too early attempt or an existing successful attemp.
        CMROUserAnswer answer = this.answerRepo.findByQuestionIdAndUser(question.getId(), user).orElse(null);
        if (answer != null) {
            if (answer.isSuccess()) {
                throw new IllegalArgumentException("Question already successfully answered.");
            }
            if (answer.getAttempts() > 0 && answer.getLastAttemptDateTime()
                    .plusMinutes(this.answerAttemptTimeoutMinutes).isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException(String.format("Answer attempt already given within the last %d minutes.", this.answerAttemptTimeoutMinutes));
            }
        }
        // Define if the question is properly answered or not: 
        // (i) all answer proposition of the question marked as correct should be present in the proposition indices set
        boolean correctAnswer = true;
        int nbCorrectAnswerPropositions = 0;
        for (int i = 0; i < question.getAnswerPropositions().size(); i++) {
            final AnswerProposition ap = question.getAnswerPropositions().get(i);
            if (ap.isCorrect()) {
                if (!propositionIndices.contains(i)) {
                    correctAnswer = false;
                    break;
                }
                nbCorrectAnswerPropositions++;
            }
        }
        // (ii) The set size must match the correct answer proposition set size
        if (correctAnswer && nbCorrectAnswerPropositions != propositionIndices.size()) {
            correctAnswer = false;
        }

        // Update or create the answer
        if (answer != null) {
            answer.setNewAnswer(correctAnswer);
        } else {
            answer = new CMROUserAnswer(question, user, correctAnswer);
        }
        return this.answerRepo.save(answer);
    }

//    @Override
//    public List<CMROUserAnswer> getUsersAnswers(Quiz quiz, CMROUser user) throws IllegalArgumentException {
//        if (quiz == null || user == null) {
//            throw new IllegalArgumentException("Quiz nor user cannot be null.");
//        }
//        return this.answerRepo.findByQuizAndUser(quiz.getName(), user).collect(Collectors.toList());
//    }
    @Override
    public CMROUserRanking getQuizRanking(Quiz quiz, CMROUser user) throws IllegalArgumentException {
        if (quiz == null || user == null) {
            throw new IllegalArgumentException("Quiz nor user cannot be null.");
        }
        // Retrieve ranking with userIds
        List<UserIdQuizRank> userIdsRanking = this.answerRepo.getRankingsByQuizName(quiz.getName());
        // Create a list of userIds used in the next two steps
        final List<String> userIds = userIdsRanking.stream().map(UserIdQuizRank::getUserId).collect(Collectors.toList());
        // Compute current user rank
        int userRank = 1;
        for (String userId : userIds) {
            if (userId.equals(user.getId())) {
                break;
            }
            userRank++;
        }
        // Retrieve all users public info from the rank by userId
        final Map<String, CMROUserNamesOnly> usernamesByUserId = this.buildUserNamesByuserIdFromIds(userIds);
        // Build a public user info ranking
        final List<PublicQuizRank> publicRanking = userIdsRanking
                .stream().map(rank -> {
                    final CMROUserNamesOnly userNames = usernamesByUserId.get(rank.getUserId());
                    return PublicQuizRank.createFromUserInfoAndUserIdRank(userNames.getFirstname(), userNames.getLastname(), rank);
                })
                .collect(Collectors.toList());
        return new CMROUserRanking(userRank, publicRanking);
    }

    private Map<String, CMROUserNamesOnly> buildUserNamesByuserIdFromIds(List<String> userIds) {
        HashMap<String, CMROUserNamesOnly> usernamesByUserId = new HashMap<>();
        // Batch request for each 500 user ids;
        final int BATCH_SIZE = 500;
        final int USER_IDS_SIZE = userIds.size();
        for (int fromIndex = 0; fromIndex < USER_IDS_SIZE; fromIndex += BATCH_SIZE) {
            int toIndex = fromIndex + BATCH_SIZE;
            if (toIndex > USER_IDS_SIZE) {
                toIndex = USER_IDS_SIZE;
            }
            this.userRepo.findPublicInfoByIdIn(userIds.subList(fromIndex, toIndex))
                    .forEach(pi -> usernamesByUserId.put(pi.getId(), pi));
        }
        return usernamesByUserId;
    }

}
