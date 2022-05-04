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

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.UnsetOperation;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 *
 * @author Rémi Venant
 */
public class CMROUserAnswerRepositoryImpl implements CMROUserAnswerRepositoryCustom {

    private static final Log LOG = LogFactory.getLog(CMROUserAnswerRepositoryImpl.class);

    private final MongoOperations mongoOps;

    @Autowired
    public CMROUserAnswerRepositoryImpl(MongoOperations mongoOps) {
        this.mongoOps = mongoOps;
    }

    @Override
    public List<UserIdQuizRank> getRankingsByQuizName(String quizName) {
        final Aggregation aggPipeline = Aggregation.newAggregation(
                // Match documents of the quizName and where answer was correct
                Aggregation.match(Criteria.where("quizName").is(quizName)
                        .and("success").is(Boolean.TRUE)),
                // Remove _id, questionId and success fields
                new UnsetOperation(List.of("_id", "questionId", "success")),
                // Group by userId and count document, sum attemps and retrieve max lastAttemptDateTime
                Aggregation.group("userId")
                        .count().as("numSuccessfulAnswers")
                        .sum("attempts").as("numAttempts")
                        .max("lastAttemptDateTime").as("lastAttempt"),
                // set userId field as _id.userId and remove _id
                Aggregation.project("numSuccessfulAnswers", "numAttempts", "lastAttempt").and("_id").as("userId").andExclude("_id"),
                //new SetOperation("userId", "$_id"),
                // remove _id
                //new UnsetOperation(List.of("_id")),
                // sort by numSuccessfulAnswers DESC, numAttempts ASC and lastAttemptDateTime ASC
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "numSuccessfulAnswers"))
                        .and(Sort.by(Sort.Direction.ASC, "numAttempts"))
                        .and(Sort.by(Sort.Direction.ASC, "lastAttempt"))
        );
        final AggregationResults<UserIdQuizRank> aggResult = this.mongoOps.aggregate(
                aggPipeline, CMROUserAnswer.class, UserIdQuizRank.class);
        return aggResult.getMappedResults();
    }

}
