/*
 * Copyright 2011-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.neo4j.integration.shared.common;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

/**
 * @author Gerrit Meier
 */
@Node
public class Person {

	@Id @GeneratedValue private Long id;
	private String firstName;
	private String lastName;

	@Relationship("LIVES_AT") private Address address;

	/**
	 * Address of a person.
	 */
	@Node
	public static class Address {
		@Id @GeneratedValue private Long id;
		private String zipCode;
		private String city;
		private String street;

		public Long getId() {
			return id;
		}

		public String getZipCode() {
			return zipCode;
		}

		public String getCity() {
			return city;
		}

		public String getStreet() {
			return street;
		}
	}

	// The getters are needed for Spring Expression Language in `NamesOnly`
	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Address getAddress() {
		return address;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "Person{" + "id=" + id + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", address="
				+ address + '}';
	}
}
