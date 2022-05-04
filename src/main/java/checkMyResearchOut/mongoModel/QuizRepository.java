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

import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author Rémi Venant
 */
public interface QuizRepository extends PagingAndSortingRepository<Quiz, String> {

    Stream<QuizSimpleInformations> findSimpleInfoBy();

    Optional<Quiz> findByName(String name);
}
