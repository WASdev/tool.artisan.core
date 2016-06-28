/*******************************************************************************
 * Copyright (c) 2016 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/ 
package application.springboot.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JDBCController {
	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping("/springbootjdbc")
	public String executeJDBC() {
		try {
			String result = "<b>Welcome to Springboot JDBC running on Liberty!</b><br><br";
			result += "<b>Creating tables:</b><br><br>";

			jdbcTemplate.execute("CREATE SCHEMA sa");

			jdbcTemplate.execute("CREATE TABLE customers(id INT, first_name VARCHAR(255), last_name VARCHAR(255))");

			List<Object[]> splitUpNames = new ArrayList<Object[]>();
			String[] names = new String[] { "John Woo", "Jeff Dean", "Josh Bloch", "Josh Long" };

			for (String name : names) {
				splitUpNames.add(name.split(" "));
			}

			for (Object[] obj : splitUpNames) {
				result += String.format("Inserting customer record for %s %s <br>", obj[0], obj[1]);
			}

			// Uses JdbcTemplate's batchUpdate operation to bulk load data
			jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

			result += "<br><b>Querying for customer records where first_name = 'Josh':</b><br>";

			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<Customer> cusList = (List<Customer>) jdbcTemplate.query(
					"SELECT id, first_name, last_name FROM customers WHERE first_name = ?", new Object[] { "Josh" },
					new RowMapperResultSetExtractor(new RowMapper() {
						public Object mapRow(ResultSet rs, int index) throws SQLException {
							Customer c = new Customer(rs.getLong("id"), rs.getString("first_name"),
									rs.getString("last_name"));

							return c;
						}
					}));

			for (Customer cus : cusList) {
				result += cus.toString() + "<br>";
			}

			// Clear environment for next execution
			jdbcTemplate.execute("DROP TABLE customers");
			jdbcTemplate.execute("DROP SCHEMA sa RESTRICT");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "Error happened during JDBC execution";
		}
	}
}