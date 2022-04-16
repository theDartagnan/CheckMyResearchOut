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
package checkMyResearchOut.security.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author Rémi Venant
 */
public class PasswordEncodingServiceImplTest {

    public PasswordEncodingServiceImplTest() {
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
    }

    /**
     * Test of encodePassword method, of class PasswordEncodingServiceImpl.
     */
    @Test
    public void testEncodePassword() {
        String clearPassword = "to$to ha3éhj  ";
        PasswordEncodingServiceImpl instance = new PasswordEncodingServiceImpl();
        String result = instance.encodePassword(clearPassword);
        System.out.println("Encoded password: ->" + result + "<-");
        assertThat(result).isNotBlank().as("Encoded password is not blank");
    }

    /**
     * Test of verifyPassword method, of class PasswordEncodingServiceImpl.
     */
    @Test
    public void testVerifyPassword() {
        String encodedPassword = "$2b$12$JxMaDq9RUV.s.p0DeRsb/OEgZHNjBlcD5Vb80QbrZTkh0x3H36xZu";
        PasswordEncodingServiceImpl instance = new PasswordEncodingServiceImpl();
        boolean result = instance.verifyPassword("to$to ha3éhj  ", encodedPassword);
        assertThat(result).isTrue().as("Good password match");
        result = instance.verifyPassword("autre pass", encodedPassword);
        assertThat(result).isFalse().as("Good password match");
    }

}
