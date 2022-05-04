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
package checkMyResearchOut.controllers;

import checkMyResearchOut.mongoModel.CMROUser;
import checkMyResearchOut.mongoModel.views.CMROUserViews;
import checkMyResearchOut.services.CMROUserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Rémi Venant
 */
@RestController
@RequestMapping("/api/v1/rest/users")
public class UserController {

    private final CMROUserService userSvc;

    @Autowired
    public UserController(CMROUserService userSvc) {
        this.userSvc = userSvc;
    }

    @PostMapping
    @JsonView(CMROUserViews.Normal.class)
    public CMROUser createUser(@RequestBody CMROUser userToCreate) {
        final CMROUser createdUser = this.userSvc.createUser(userToCreate.getMail(),
                userToCreate.getLastname(), userToCreate.getFirstname(), userToCreate.getPassword());
        return createdUser;
    }

    @GetMapping("{userId}")
    @JsonView(CMROUserViews.WithInfo.class)
    public CMROUser getUser(@PathVariable String userId) {
        final CMROUser user = "myself".equals(userId) ? this.userSvc.getCurrentUser() : this.userSvc.getUserById(userId);
        return user;
    }

    @PatchMapping("{userId}")
    @JsonView(CMROUserViews.WithInfo.class)
    public CMROUser patchUser(@PathVariable String userId, @RequestBody CMROUser userToUpdate) {
        final CMROUser user = "myself".equals(userId) ? this.userSvc.getCurrentUser() : this.userSvc.getUserById(userId);
        CMROUser updatedUser = this.userSvc.patchUser(user, userToUpdate.getLastname(),
                userToUpdate.getFirstname(), userToUpdate.getPassword());
        return updatedUser;
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        final CMROUser user = "myself".equals(userId) ? this.userSvc.getCurrentUser() : this.userSvc.getUserById(userId);
        this.userSvc.deleteUser(user);
        // delete cookie
        return ResponseEntity.noContent().build();
    }

}
