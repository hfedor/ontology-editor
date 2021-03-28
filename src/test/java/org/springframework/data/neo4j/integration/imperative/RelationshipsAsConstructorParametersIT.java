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
package org.springframework.data.neo4j.integration.imperative;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.AbstractNeo4jConfig;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.data.neo4j.integration.shared.common.RelationshipsAsConstructorParametersEntities;
import org.springframework.data.neo4j.test.Neo4jExtension;
import org.springframework.data.neo4j.test.Neo4jIntegrationTest;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Neo4jIntegrationTest
class RelationshipsAsConstructorParametersIT {

	protected static Neo4jExtension.Neo4jConnectionSupport neo4jConnectionSupport;

	protected final Driver driver;

	protected RelationshipsAsConstructorParametersIT(@Autowired Driver driver) {
		this.driver = driver;
	}

	@BeforeEach
	protected void setupData() {

		try (Session session = driver.session(); Transaction transaction = session.beginTransaction()) {
			transaction.run("MATCH (n) detach delete n").consume();
			transaction
					.run("CREATE (b:NodeTypeB {name: 'detail'}) - [:BELONGS_TO] -> (a:NodeTypeA {name: 'master'}) RETURN a, b")
					.consume();
			transaction.commit();
		}
	}

	/**
	 * Partially immutable entity with association filled during construction. Failed originally due to the fact that we
	 * did not check if the association was a constructor property.
	 *
	 * @param template Needed for executing the query.
	 */
	@Test
	void shouldCreateMasterDetailRelationshipViaConstructor(@Autowired Neo4jTemplate template) {

		List<RelationshipsAsConstructorParametersEntities.NodeTypeB> details = template
				.findAll(RelationshipsAsConstructorParametersEntities.NodeTypeB.class);
		assertThat(details).hasSize(1).element(0).satisfies(content -> {
			assertThat(content.getName()).isEqualTo("detail");
			assertThat(content.getNodeTypeA()).isNotNull()
					.extracting(RelationshipsAsConstructorParametersEntities.NodeTypeA::getName).isEqualTo("master");
		});
	}

	@Configuration
	@EnableTransactionManagement
	static class Config extends AbstractNeo4jConfig {

		@Bean
		public Driver driver() {
			return neo4jConnectionSupport.getDriver();
		}

	}
}
