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
package org.springframework.data.neo4j.core.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.StringValue;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mapping.Association;
import org.springframework.data.neo4j.config.Neo4jEntityScanner;
import org.springframework.data.neo4j.core.convert.Neo4jConversionService;
import org.springframework.data.neo4j.core.convert.Neo4jConversions;
import org.springframework.data.neo4j.core.convert.Neo4jPersistentPropertyToMapConverter;
import org.springframework.data.neo4j.core.mapping.datagraph1446.B;
import org.springframework.data.neo4j.core.mapping.datagraph1446.C;
import org.springframework.data.neo4j.core.mapping.datagraph1446.P;
import org.springframework.data.neo4j.core.mapping.datagraph1446.R1;
import org.springframework.data.neo4j.core.mapping.datagraph1446.R2;
import org.springframework.data.neo4j.core.mapping.datagraph1448.A_S3;
import org.springframework.data.neo4j.core.mapping.datagraph1448.RelatedThing;
import org.springframework.data.neo4j.core.schema.CompositeProperty;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.IdGenerator;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.data.neo4j.integration.shared.common.FriendshipRelationship;
import org.springframework.data.neo4j.test.LogbackCapture;
import org.springframework.data.neo4j.test.LogbackCapturingExtension;

/**
 * @author Michael J. Simons
 */
class Neo4jMappingContextTest {

	@ExtendWith(LogbackCapturingExtension.class)
	@Nested
	class InvalidRelationshipProperties {

		@Test // GH-2118
		void startupShouldFail() {

			Neo4jMappingContext schema = new Neo4jMappingContext();
			assertThatIllegalStateException().isThrownBy(() -> {
						schema.setInitialEntitySet(new HashSet<>(
								Arrays.asList(IrrelevantSourceContainer.class, InvalidRelationshipPropertyContainer.class,
										IrrelevantTargetContainer.class)));
						schema.initialize();
					}).withMessage("The target class `org.springframework.data.neo4j.core.mapping.Neo4jMappingContextTest$InvalidRelationshipPropertyContainer` for the properties of the relationship `RELATIONSHIP_PROPERTY_CONTAINER` is missing a property for the generated, internal ID (`@Id @GeneratedValue Long id`) which is needed for safely updating properties.");
		}

		@Test // GH-2118
		void noWarningShouldBeLogged(LogbackCapture logbackCapture) {

			Neo4jMappingContext schema = new Neo4jMappingContext();
			schema.setInitialEntitySet(new HashSet<>(Arrays.asList(IrrelevantSourceContainer2.class, FriendshipRelationship.class, IrrelevantTargetContainer2.class)));
			schema.initialize();
			assertThat(logbackCapture.getFormattedMessages()).isEmpty();
		}
	}

	@Test
	void initializationOfSchemaShouldWork() {

		Neo4jMappingContext schema = new Neo4jMappingContext();
		schema.setInitialEntitySet(new HashSet<>(Arrays.asList(BikeNode.class, UserNode.class, TripNode.class)));
		schema.initialize();

		NodeDescription<?> optionalUserNodeDescription = schema.getNodeDescription("User");
		assertThat(optionalUserNodeDescription).isNotNull().satisfies(description -> {
			assertThat(description.getUnderlyingClass()).isEqualTo(UserNode.class);

			assertThat(description.getIdDescription().isInternallyGeneratedId()).isTrue();

			assertThat(description.getGraphProperties()).extracting(GraphPropertyDescription::getFieldName)
					.containsExactlyInAnyOrder("id", "name", "first_name");

			assertThat(description.getGraphProperties()).extracting(GraphPropertyDescription::getPropertyName)
					.containsExactlyInAnyOrder("id", "name", "firstName");

			Collection<String> expectedRelationships = Arrays.asList("[:OWNS] -> (:BikeNode)");
			Collection<RelationshipDescription> relationships = description.getRelationships();
			assertThat(relationships.stream().filter(r -> !r.isDynamic())).allMatch(d -> expectedRelationships
					.contains(String.format("[:%s] -> (:%s)", d.getType(), d.getTarget().getPrimaryLabel())));
		});

		NodeDescription<?> optionalBikeNodeDescription = schema.getNodeDescription("BikeNode");
		assertThat(optionalBikeNodeDescription).isNotNull().satisfies(description -> {
			assertThat(description.getUnderlyingClass()).isEqualTo(BikeNode.class);

			assertThat(description.getIdDescription().isAssignedId()).isTrue();

			Collection<String> expectedRelationships = Arrays.asList("[:OWNER] -> (:User)", "[:RENTER] -> (:User)");
			Collection<RelationshipDescription> relationships = description.getRelationships();
			assertThat(relationships.stream().filter(r -> !r.isDynamic())).allMatch(d -> expectedRelationships
					.contains(String.format("[:%s] -> (:%s)", d.getType(), d.getTarget().getPrimaryLabel())));
		});

		Neo4jPersistentEntity<?> bikeNodeEntity = schema.getPersistentEntity(BikeNode.class);

		assertThat(bikeNodeEntity.getPersistentProperty("owner").isAssociation()).isTrue();
		assertThat(bikeNodeEntity.getPersistentProperty("renter").isAssociation()).isTrue();
		assertThat(bikeNodeEntity.getPersistentProperty("dynamicRelationships").isAssociation()).isTrue();
		assertThat(bikeNodeEntity.getPersistentProperty("someValues").isAssociation()).isFalse();
		assertThat(bikeNodeEntity.getPersistentProperty("someMoreValues").isAssociation()).isFalse();
		assertThat(bikeNodeEntity.getPersistentProperty("evenMoreValues").isAssociation()).isFalse();
		assertThat(bikeNodeEntity.getPersistentProperty("funnyDynamicProperties").isAssociation()).isFalse();
	}

	@Test
	void shouldPreventIllegalIdAnnotations() {

		Neo4jMappingContext schema = new Neo4jMappingContext();
		schema.setInitialEntitySet(new HashSet<>(Arrays.asList(InvalidId.class)));
		assertThatIllegalArgumentException().isThrownBy(() -> schema.initialize())
				.withMessageMatching("Cannot use internal id strategy with custom property getMappingFunctionFor on entity .*");
	}

	@Test
	void shouldPreventIllegalIdTypes() {

		Neo4jMappingContext schema = new Neo4jMappingContext();
		schema.setInitialEntitySet(new HashSet<>(Arrays.asList(InvalidIdType.class)));
		assertThatIllegalArgumentException().isThrownBy(() -> schema.initialize())
				.withMessageMatching("Internally generated ids can only be assigned to one of .*");
	}

	@Test
	void missingIdDefinitionShouldRaiseError() {

		Neo4jMappingContext schema = new Neo4jMappingContext();
		assertThatIllegalStateException().isThrownBy(() -> schema.getPersistentEntity(MissingId.class))
				.withMessage("Missing id property on " + MissingId.class + ".");
	}

	@Test
	void targetTypeOfAssociationsShouldBeKnownToTheMappingContext() {

		Neo4jMappingContext schema = new Neo4jMappingContext();
		Neo4jPersistentEntity<?> bikeNodeEntity = schema.getPersistentEntity(BikeNode.class);
		bikeNodeEntity.doWithAssociations((Association<Neo4jPersistentProperty> association) -> Assertions
				.assertThat(schema.getRequiredMappingFunctionFor(association.getInverse().getAssociationTargetType()))
				.isNotNull());
	}

	@Test
	void shouldDeriveARelationshipType() {

		Neo4jMappingContext schema = new Neo4jMappingContext();
		Neo4jPersistentEntity<?> bikeNodeEntity = schema.getPersistentEntity(BikeNode.class);
		assertThat(bikeNodeEntity.getRequiredPersistentProperty("renter").getAssociation()).isNotNull()
				.satisfies(association -> {
					assertThat(association).isInstanceOf(RelationshipDescription.class);
					RelationshipDescription relationshipDescription = (RelationshipDescription) association;
					assertThat(relationshipDescription.getType()).isEqualTo("RENTER");
				});
	}

	@Test
	void shouldCacheIdGenerators() {

		Neo4jMappingContext schema = new Neo4jMappingContext();
		IdGenerator<?> dummyIdGenerator1 = schema.getOrCreateIdGeneratorOfType(DummyIdGenerator.class);
		IdGenerator<?> dummyIdGenerator2 = schema.getOrCreateIdGeneratorOfType(DummyIdGenerator.class);

		assertThat(dummyIdGenerator1).isSameAs(dummyIdGenerator2);
	}

	@Test
	void complexPropertyWithConverterShouldNotBeConsideredAsAssociation() {

		class ConvertibleTypeConverter implements GenericConverter {
			@Override
			public Set<ConvertiblePair> getConvertibleTypes() {
				// in the real world this should also define the opposite way
				return Collections.singleton(new ConvertiblePair(ConvertibleType.class, StringValue.class));
			}

			@Override
			public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
				// no implementation needed for this test
				return null;
			}
		}

		Neo4jMappingContext schema = new Neo4jMappingContext(
				new Neo4jConversions(Collections.singleton(new ConvertibleTypeConverter())));
		Neo4jPersistentEntity<?> entity = schema.getPersistentEntity(EntityWithConvertibleProperty.class);

		Assertions.assertThat(entity.getPersistentProperty("convertibleType").isRelationship()).isFalse();
	}

	@Test
	void complexPropertyWithoutConverterShouldBeConsideredAsAssociation() {

		Neo4jMappingContext schema = new Neo4jMappingContext(new Neo4jConversions());
		Neo4jPersistentEntity<?> entity = schema.getPersistentEntity(EntityWithConvertibleProperty.class);

		Assertions.assertThat(entity.getPersistentProperty("convertibleType").isRelationship()).isTrue();
	}

	@Test
	void shouldHonourTransientAnnotation() {

		Neo4jMappingContext schema = new Neo4jMappingContext();
		Neo4jPersistentEntity<?> userNodeEntity = schema.getPersistentEntity(UserNode.class);

		assertThat(userNodeEntity.getPersistentProperty("anAnnotatedTransientProperty")).isNull();

		List<String> associations = new ArrayList<>();
		userNodeEntity.doWithAssociations((Association<Neo4jPersistentProperty> a) -> {
			associations.add(a.getInverse().getFieldName());
		});

		assertThat(associations).containsOnly("bikes");
	}

	@Test
	void enumMapKeys() {

		Neo4jMappingContext schema = new Neo4jMappingContext();
		Neo4jPersistentEntity<?> enumRelNodeEntity = schema.getPersistentEntity(EnumRelNode.class);

		List<Neo4jPersistentProperty> associations = new ArrayList<>();
		enumRelNodeEntity.doWithAssociations((Association<Neo4jPersistentProperty> a) -> associations.add(a.getInverse()));

		assertThat(associations).hasSize(2);
	}

	@Test
	void shouldPreventIllegalCompositeUsageOnScalars() {
		Neo4jMappingContext schema = new Neo4jMappingContext();
		schema.setInitialEntitySet(new HashSet<>(Arrays.asList(WithInvalidCompositeUsage.class)));
		Neo4jPersistentEntity<?> entity = schema.getPersistentEntity(WithInvalidCompositeUsage.class);
		Neo4jPersistentProperty property = entity.getRequiredPersistentProperty("doesntWorkOnScalar");

		assertThatIllegalArgumentException()
				.isThrownBy(() -> schema.getOptionalCustomConversionsFor(property))
				.withMessageMatching("@CompositeProperty can only be used on Map properties without additional configuration. Was used on `.*` in `.*`");
	}

	@Test
	void shouldNotPreventlegalCompositeUsageOnScalars() {
		Neo4jMappingContext schema = new Neo4jMappingContext();
		Neo4jPersistentEntity<?> entity = schema.getPersistentEntity(WithValidCompositeUsage.class);
		Neo4jPersistentProperty property = entity.getRequiredPersistentProperty("worksWithExplictConverter");

		schema.getOptionalCustomConversionsFor(property);
	}

	@Test
	void shouldPreventIllegalCompositeUsageOnCollections() {
		Neo4jMappingContext schema = new Neo4jMappingContext();
		Neo4jPersistentEntity<?> entity = schema.getPersistentEntity(WithInvalidCompositeUsage.class);
		Neo4jPersistentProperty property = entity.getRequiredPersistentProperty("doesntWorkOnCollection");

		assertThatIllegalArgumentException()
				.isThrownBy(() -> schema.getOptionalCustomConversionsFor(property))
				.withMessageMatching("@CompositeProperty can only be used on Map properties without additional configuration. Was used on `.*` in `.*`");
	}

	@Test
	void shouldPreventIllegalCompositeUsageWithCustomMapConverters() {
		Neo4jMappingContext schema = new Neo4jMappingContext();
		Neo4jPersistentEntity<?> entity = schema.getPersistentEntity(WithInvalidCompositeUsage.class);
		Neo4jPersistentProperty property = entity.getRequiredPersistentProperty("mismatch");

		assertThatIllegalArgumentException()
				.isThrownBy(() -> schema.getOptionalCustomConversionsFor(property))
				.withMessageMatching("The property type `.*` created by `.*` used on `.*` in `.*` doesn't match the actual property type.");
	}

	@Test
	void shouldPreventIllegalCompositeUsageOnUnsupportedMapKeys() {
		Neo4jMappingContext schema = new Neo4jMappingContext();
		Neo4jPersistentEntity<?> entity = schema.getPersistentEntity(WithInvalidCompositeUsage.class);
		Neo4jPersistentProperty property = entity.getRequiredPersistentProperty("doesntWorkOnWrongMapType");

		assertThatIllegalArgumentException()
				.isThrownBy(() -> schema.getOptionalCustomConversionsFor(property))
				.withMessageMatching("@CompositeProperty can only be used on Map properties with a key type of String or enum. Was used on `.*` in `.*`");
	}

	@Test // DATAGRAPH-1446
	void relationshipPropertiesShouldBeAbleToContainGenerics() {
		Neo4jMappingContext schema = new Neo4jMappingContext();

		schema.initialize();
		Neo4jPersistentEntity<?> entity = schema.getPersistentEntity(P.class);
		assertThat(
				entity.getRelationships().stream().sorted(Comparator.comparing(RelationshipDescription::getFieldName)))
				.hasSize(2)
				.satisfies(l -> assertThat(l)
						.extracting(RelationshipDescription::getRelationshipPropertiesEntity)
						.extracting(e -> (Class) e.getUnderlyingClass())
						.containsExactly(R1.class, R2.class)
				)
				.extracting(r -> (Class) r.getTarget().getUnderlyingClass())
				.containsExactly(B.class, C.class);
	}

	@Test // DATAGRAPH-1448
	void abstractClassesInRelationshipPropertiesShouldWork() {

		Neo4jMappingContext schema = new Neo4jMappingContext();
		schema.initialize();
		Neo4jPersistentEntity<?> entity = schema.getPersistentEntity(A_S3.class);
		assertThat(entity).isNotNull();
	}

	@Test // DATAGRAPH-1448
	void abstractClassesInRelationshipPropertiesShouldWorkInStrictMode() throws ClassNotFoundException {

		Neo4jMappingContext schema = new Neo4jMappingContext();
		schema.setStrict(true);
		schema.setInitialEntitySet(Neo4jEntityScanner.get().scan(A_S3.class.getPackage().getName()));
		schema.initialize();
		Neo4jPersistentEntity<?> entity = schema.getPersistentEntity(A_S3.class);
		assertThat(entity).isNotNull();
	}

	@Test // DATAGRAPH-1448
	void shouldNotOverwriteDiscoveredBaseClassWhenSeeingClassAsGenericPropertyAgain() throws ClassNotFoundException {

		Neo4jMappingContext schema = new Neo4jMappingContext();
		schema.setInitialEntitySet(Neo4jEntityScanner.get().scan(A_S3.class.getPackage().getName()));
		schema.initialize();
		Neo4jPersistentEntity<?> entity = schema.getPersistentEntity(RelatedThing.class);
		assertThat(entity.getChildNodeDescriptionsInHierarchy()).hasSize(2);
		assertThat(entity).isNotNull();
	}

	@ParameterizedTest // DATAGRAPH-1459
	@ValueSource(classes = {InvalidMultiDynamics1.class, InvalidMultiDynamics2.class, InvalidMultiDynamics3.class, InvalidMultiDynamics4.class})
	void shouldDetectAllVariantsOfMultipleDynamicRelationships(Class<?> thingWithRelations)  {

		assertThatIllegalStateException().isThrownBy(() -> {
			Neo4jMappingContext schema = new Neo4jMappingContext();
			schema.setInitialEntitySet(new HashSet<>(Arrays.asList(TripNode.class, thingWithRelations)));
			schema.initialize();
		}).withMessageMatching(".*Only one dynamic relationship between to entities is permitted.");
	}

	static class DummyIdGenerator implements IdGenerator<Void> {

		@Override
		public Void generateId(String primaryLabel, Object entity) {
			return null;
		}
	}

	@Node("User")
	static class UserNode {

		@org.springframework.data.annotation.Id @GeneratedValue private long id;

		@Relationship(type = "OWNS") List<BikeNode> bikes;

		String name;

		@Transient String anAnnotatedTransientProperty;

		@Transient List<SomeOtherClass> someOtherTransientThings;

		@Property(name = "firstName") String first_name;
	}

	@Node
	static class SomeOtherClass {

	}

	enum A {
		A1, A2
	}

	enum ExtendedA {

		EA1, EA2 {
			@Override
			public void doNothing() {}
		};

		public void doNothing() {

		}
	}

	@Node
	static class BikeNode {

		@Id private String id;

		UserNode owner;

		List<UserNode> renter;

		Map<String, UserNode> dynamicRelationships;

		List<String> someValues;
		String[] someMoreValues;
		byte[] evenMoreValues;
		Map<String, Object> funnyDynamicProperties;
	}

	@Node
	static class EnumRelNode {

		@Id private String id;

		Map<A, UserNode> relA;

		Map<ExtendedA, BikeNode> relEA;
	}

	@Node
	static class TripNode {

		@Id private String id;

		String name;
	}

	@Node
	static class InvalidMultiDynamics1 {

		@Id private String id;

		String name;

		Map<ExtendedA, BikeNode> relEA;

		Map<ExtendedA, BikeNode> relEB;
	}

	@Node
	static class InvalidMultiDynamics2 {

		@Id private String id;

		String name;

		Map<ExtendedA, BikeNode> relEA;

		@Relationship
		Map<ExtendedA, BikeNode> relEB;
	}

	@Node
	static class InvalidMultiDynamics3 {

		@Id private String id;

		String name;

		@Relationship
		Map<ExtendedA, BikeNode> relEA;

		@Relationship
		Map<ExtendedA, BikeNode> relEB;
	}

	@Node
	static class InvalidMultiDynamics4 {

		@Id private String id;

		String name;

		@Relationship
		Map<ExtendedA, BikeNode> relEA;

		Map<ExtendedA, BikeNode> relEB;
	}


	@Node
	static class InvalidId {

		@Id @GeneratedValue @Property("getMappingFunctionFor") private String id;
	}

	@Node
	static class InvalidIdType {

		@Id @GeneratedValue private String id;
	}

	@Node
	static class MissingId {}

	@Node
	static class EntityWithConvertibleProperty {

		@Id @GeneratedValue private Long id;

		private ConvertibleType convertibleType;
	}

	static class ConvertibleType {}

	@Node
	static class WithInvalidCompositeUsage {

		@Id @GeneratedValue private Long id;

		@CompositeProperty
		String doesntWorkOnScalar;

		@CompositeProperty
		Map<Long, Object> doesntWorkOnWrongMapType;

		@CompositeProperty
		List<String> doesntWorkOnCollection;

		@CompositeProperty(converter = MissingIdToMapConverter.class)
		String mismatch;
	}

	@Node
	static class WithValidCompositeUsage {

		@Id @GeneratedValue private Long id;

		@CompositeProperty(converter = MissingIdToMapConverter.class)
		MissingId worksWithExplictConverter;
	}

	@Node
	static class IrrelevantSourceContainer {
		@Id @GeneratedValue
		private Long id;

		@Relationship(type = "RELATIONSHIP_PROPERTY_CONTAINER")
		InvalidRelationshipPropertyContainer relationshipPropertyContainer;
	}

	@RelationshipProperties
	static class InvalidRelationshipPropertyContainer {
		@TargetNode
		private IrrelevantTargetContainer irrelevantTargetContainer;
	}

	@Node
	static class IrrelevantTargetContainer {
		@Id @GeneratedValue
		private Long id;
	}

	@Node
	static class IrrelevantSourceContainer2 {
		@Id @GeneratedValue
		private Long id;

		@Relationship(type = "RELATIONSHIP_PROPERTY_CONTAINER")
		List<RelationshipPropertyContainer> relationshipPropertyContainer;
	}

	@RelationshipProperties
	static class RelationshipPropertyContainer {
		@Id @GeneratedValue
		private Long id;

		@TargetNode
		private IrrelevantTargetContainer irrelevantTargetContainer;
	}

	@Node
	static class IrrelevantTargetContainer2 {
		@Id @GeneratedValue
		private Long id;
	}

	static class MissingIdToMapConverter implements Neo4jPersistentPropertyToMapConverter<String, MissingId> {

		@Override public Map<String, Value> decompose(MissingId property, Neo4jConversionService conversionService) {
			return null;
		}

		@Override public MissingId compose(Map<String, Value> source,  Neo4jConversionService conversionService) {
			return null;
		}
	}
}
