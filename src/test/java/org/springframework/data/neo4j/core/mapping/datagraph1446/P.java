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
package org.springframework.data.neo4j.core.mapping.datagraph1446;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

/**
 * @author Michael J. Simons
 */
@Node
public class P {

	@Id @GeneratedValue
	private Long id;

	private String name;

	@Relationship(value = "R")
	R1 b;

	@Relationship(value = "R")
	R2 c;

	public P(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public R1 getB() {
		return b;
	}

	public void setB(R1 b) {
		this.b = b;
	}

	public R2 getC() {
		return c;
	}

	public void setC(R2 c) {
		this.c = c;
	}

	@Override public String toString() {
		return "A{" +
			   "id=" + id +
			   ", name='" + name + '\'' +
			   ", b=" + b +
			   ", c=" + c +
			   '}';
	}
}
