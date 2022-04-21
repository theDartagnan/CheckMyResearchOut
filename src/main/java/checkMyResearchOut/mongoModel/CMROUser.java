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

import checkMyResearchOut.mongoModel.views.CMROUserViews;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Rémi Venant
 */
@Document(collection = "users")
public class CMROUser implements Serializable {

    @JsonView(CMROUserViews.WithInfo.class)
    @Id
    private String id;

    @JsonView(CMROUserViews.Normal.class)
    @NotBlank
    @Indexed(unique = true)
    private String mail;

    @JsonView(CMROUserViews.Normal.class)
    @NotBlank
    private String lastname;

    @JsonView(CMROUserViews.Normal.class)
    @NotBlank
    private String firstname;

    @NotBlank
    private String password;

    private String validationToken;

    private LocalDateTime tokenEmissionDateTime;

    @JsonView(CMROUserViews.WithInfo.class)
    private Boolean validated;

    @JsonView(CMROUserViews.WithInfo.class)
    private Boolean admin;

    protected CMROUser() {
    }

    public CMROUser(String mail, String lastname, String firstname, String password) {
        this.mail = mail;
        this.lastname = lastname;
        this.firstname = firstname;
        this.password = password;
        this.validationToken = null;
        this.tokenEmissionDateTime = null;
        this.validated = false;
        this.admin = false;
    }

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getValidationToken() {
        return validationToken;
    }

    public void setValidationToken(String validationToken) {
        this.validationToken = validationToken;
    }

    public LocalDateTime getTokenEmissionDateTime() {
        return tokenEmissionDateTime;
    }

    public void setTokenEmissionDateTime(LocalDateTime tokenEmissionDateTime) {
        this.tokenEmissionDateTime = tokenEmissionDateTime;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "CMROUser{" + "id=" + id + ", mail=" + mail + ", lastname=" + lastname + ", firstname=" + firstname + ", validated=" + validated + ", admin=" + admin + '}';
    }

}
