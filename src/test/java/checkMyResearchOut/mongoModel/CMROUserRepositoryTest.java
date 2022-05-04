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
import java.util.Map;
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
public class CMROUserRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CMROUserRepository testedRepo;

    public CMROUserRepositoryTest() {
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

    private CMROUser generateSampleUser(int idx) {
        CMROUser user = new CMROUser(String.format("user%d@mail.com", idx), "lname_" + idx,
                "fname_" + idx, "encPass_" + idx);
        user.setValidated(Boolean.TRUE);
        return user;
    }

    @AfterEach
    public void tearDown() {
        this.mongoTemplate.remove(new BasicQuery("{}"), CMROUser.class);
    }

    /**
     * Test of findByMail method, of class CMROUserRepository.
     */
    @Test
    public void testFindByMail() {
        System.out.println("findByMail");
        String[] userIds = new String[4];
        for (int i = 0; i < 4; i++) {
            CMROUser u = this.mongoTemplate.save(this.generateSampleUser(i));
            userIds[i] = u.getId();
        }

        Optional<CMROUser> optUser = this.testedRepo.findByMail("user2@mail.com");
        assertThat(optUser).isNotEmpty();
        assertThat(optUser.get().getId()).isEqualTo(userIds[2]);
    }

    /**
     * Test of findPublicInfoByIdIn method, of class CMROUserRepository.
     */
    @Test
    public void testFindPublicInfoByIdIn() {
        System.out.println("findPublicInfoFromIdIn");
        CMROUser[] users = new CMROUser[6];
        for (int i = 0; i < 6; i++) {
            users[i] = this.mongoTemplate.save(this.generateSampleUser(i));
        }

        final Map<String, CMROUser> selectedUsersById = Map.of(
                users[1].getId(), users[1],
                users[2].getId(), users[2],
                users[3].getId(), users[3]);

        List<CMROUserNamesOnly> unames = this.testedRepo.findPublicInfoByIdIn(selectedUsersById.keySet()).collect(Collectors.toList());

        assertThat(unames).hasSize(selectedUsersById.size());
        assertThat(unames).allSatisfy((uname) -> {
            CMROUser relatedUser = selectedUsersById.get(uname.getId());
            assertThat(relatedUser).isNotNull();
            assertThat(uname.getFirstname()).isEqualTo(relatedUser.getFirstname());
            assertThat(uname.getLastname()).isEqualTo(relatedUser.getLastname());
        });
    }

}
