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
package checkMyResearchOut.controllers.model;

import java.io.Serializable;

/**
 *
 * @author Rémi Venant
 */
public class ValidAccPassRenewInput implements Serializable {

    private String encodedMail;
    private String token;
    private String password;

    public ValidAccPassRenewInput() {
    }

    public String getEncodedMail() {
        return encodedMail;
    }

    public void setEncodedMail(String encodedMail) {
        this.encodedMail = encodedMail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
