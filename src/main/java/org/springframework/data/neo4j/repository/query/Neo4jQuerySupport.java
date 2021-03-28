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
package org.springframework.data.neo4j.repository.query;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.logging.LogFactory;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.neo4j.driver.types.MapAccessor;
import org.neo4j.driver.types.TypeSystem;
import org.springframework.core.log.LogAccessor;
import org.springframework.data.domain.Range;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.neo4j.core.convert.Neo4jSimpleTypes;
import org.springframework.data.neo4j.core.mapping.CypherGenerator;
import org.springframework.data.neo4j.core.mapping.Neo4jMappingContext;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.ResultProcessor;
import org.springframework.data.repository.query.ReturnedType;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Some conversions used by both reactive and imperative Neo4j queries. While we try to separate reactive and imperative
 * flows, it is cumbersome to repeat those conversions all over the place.
 *
 * @author Gerrit Meier
 * @author Michael J. Simons
 * @since 6.0
 */
abstract class Neo4jQuerySupport {

	protected final Neo4jMappingContext mappingContext;
	protected final Neo4jQueryMethod queryMethod;
	/**
	 * The query type.
	 */
	protected final Neo4jQueryType queryType;

	static final LogAccessor REPOSITORY_QUERY_LOG = new LogAccessor(LogFactory.getLog(Neo4jQuerySupport.class));

	/**
	 * Centralizes inquiry of the domain type to use the result processor of the query method as the point of truth.
	 * While this could be exposed on the query method itself, we would risk working with another type if at some point
	 * we osk the result processor only.
	 *
	 * @param queryMethod The query method whose domain type is requested
	 * @return The domain type of the given query method.
	 */
	static Class<?> getDomainType(QueryMethod queryMethod) {
		return queryMethod.getResultProcessor().getReturnedType().getDomainType();
	}

	Neo4jQuerySupport(Neo4jMappingContext mappingContext, Neo4jQueryMethod queryMethod, Neo4jQueryType queryType) {

		Assert.notNull(mappingContext, "The mapping context is required.");
		Assert.notNull(queryMethod, "Query method must not be null!");
		Assert.notNull(queryType, "Query type must not be null!");

		this.mappingContext = mappingContext;
		this.queryMethod = queryMethod;
		this.queryType = queryType;
	}

	protected final BiFunction<TypeSystem, MapAccessor, ?> getMappingFunction(final ResultProcessor resultProcessor) {

		final ReturnedType returnedTypeMetadata = resultProcessor.getReturnedType();
		final Class<?> returnedType = returnedTypeMetadata.getReturnedType();
		final Class<?> domainType = returnedTypeMetadata.getDomainType();

		final BiFunction<TypeSystem, MapAccessor, ?> mappingFunction;

		if (Neo4jSimpleTypes.HOLDER.isSimpleType(returnedType)) {
			// Clients automatically selects a single value mapping function.
			// It will thrown an error if the query contains more than one column.
			mappingFunction = null;
		} else if (returnedTypeMetadata.isProjecting()) {
			BiFunction<TypeSystem, MapAccessor, ?> target = this.mappingContext.getRequiredMappingFunctionFor(domainType);
			mappingFunction = (t, r) -> new EntityInstanceWithSource(target.apply(t, r), t, r);
		} else {
			mappingFunction = this.mappingContext.getRequiredMappingFunctionFor(domainType);
		}
		return mappingFunction;
	}

	protected final List<String> getInputProperties(final ResultProcessor resultProcessor) {

		ReturnedType returnedType = resultProcessor.getReturnedType();
		return returnedType.isProjecting() ? returnedType.getInputProperties() : Collections.emptyList();
	}

	/**
	 * Converts parameter as needed by the query generated, which is not covered by standard conversion services.
	 *
	 * @param parameter The parameter to fit into the generated query.
	 * @return A parameter that fits the place holders of a generated query
	 */
	final Object convertParameter(Object parameter) {
		return this.convertParameter(parameter, null);
	}

	/**
	 * Converts parameter as needed by the query generated, which is not covered by standard conversion services.
	 *
	 * @param parameter The parameter to fit into the generated query.
	 * @param conversionOverride Passed to the entity converter if present.
	 * @return A parameter that fits the place holders of a generated query
	 */
	final Object convertParameter(Object parameter, @Nullable Function<Object, Value> conversionOverride) {

		if (parameter == null) {
			// According to https://neo4j.com/docs/cypher-manual/current/syntax/working-with-null/#cypher-null-intro
			// it does not make any sense to continue if a `null` value gets into a comparison
			// but we just warn the users and do not throw an exception on `null`.
			REPOSITORY_QUERY_LOG.warn("Do not use `null` as a property value for comparison."
					+ " It will always be false and return an empty result.");

			return Values.NULL;
		}

		// Maybe move all of those into Neo4jConverter at some point.
		if (parameter instanceof Range) {
			return convertRange((Range) parameter);
		} else if (parameter instanceof Distance) {
			return calculateDistanceInMeter((Distance) parameter);
		} else if (parameter instanceof Circle) {
			return convertCircle((Circle) parameter);
		} else if (parameter instanceof Instant) {
			return ((Instant) parameter).atOffset(ZoneOffset.UTC);
		} else if (parameter instanceof Box) {
			return convertBox((Box) parameter);
		} else if (parameter instanceof BoundingBox) {
			return convertBoundingBox((BoundingBox) parameter);
		}

		if (mappingContext.hasPersistentEntityFor(parameter.getClass())) {

			Map<String, Object> result = new HashMap<>();
			Neo4jNestedMapEntityWriter.forContext(mappingContext).write(parameter, result);
			return result;
		}

		return mappingContext.getConversionService().writeValue(parameter,
				ClassTypeInformation.from(parameter.getClass()), conversionOverride);
	}

	static class QueryContext {

		final String repositoryMethodName;

		final String template;

		final Map<String, Object> boundParameters;

		String query;

		private boolean hasLiteralReplacementForSort = false;

		QueryContext(String repositoryMethodName, String template, Map<String, Object> boundParameters) {
			this.repositoryMethodName = repositoryMethodName;
			this.template = template;
			this.query = this.template;
			this.boundParameters = boundParameters;
		}
	}

	void replaceLiteralsIn(QueryContext queryContext) {

		String cypherQuery = queryContext.template;
		Iterator<Map.Entry<String, Object>> iterator = queryContext.boundParameters.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();
			Object value = entry.getValue();
			if (!(value instanceof Neo4jSpelSupport.LiteralReplacement)) {
				continue;
			}
			iterator.remove();

			String key = entry.getKey();
			cypherQuery = cypherQuery.replace("$" + key, ((Neo4jSpelSupport.LiteralReplacement) value).getValue());
			queryContext.hasLiteralReplacementForSort =
					queryContext.hasLiteralReplacementForSort ||
					((Neo4jSpelSupport.LiteralReplacement) value).getTarget() == Neo4jSpelSupport.LiteralReplacement.Target.SORT;
		}
		queryContext.query = cypherQuery;
	}

	void logWarningsIfNecessary(QueryContext queryContext, Neo4jParameterAccessor parameterAccessor) {

		// Log warning if necessary
		if (!(queryContext.hasLiteralReplacementForSort || parameterAccessor.getSort().isUnsorted())) {

			Neo4jQuerySupport.REPOSITORY_QUERY_LOG.warn(() ->
					String.format(
							"You passed a sorted request to the custom query for '%s'. SDN won't apply any sort information from that object to the query. "
							+ "Please specify the order in the query itself and use an unsorted request or use the SpEL extension `:#{orderBy(#sort)}`.",
							queryContext.repositoryMethodName));

			String fragment = CypherGenerator.INSTANCE.createOrderByFragment(parameterAccessor.getSort());
			if (fragment != null) {
				Neo4jQuerySupport.REPOSITORY_QUERY_LOG.warn(() ->
						String.format(
								"One possible order clause matching your page reguest would be the following fragment:%n%s",
								fragment));
			}
		}
	}

	private Map<String, Object> convertRange(Range range) {
		Map<String, Object> map = new HashMap<>();
		range.getLowerBound().getValue().map(this::convertParameter).ifPresent(v -> map.put("lb", v));
		range.getUpperBound().getValue().map(this::convertParameter).ifPresent(v -> map.put("ub", v));
		return map;
	}

	private Map<String, Object> convertCircle(Circle circle) {
		Map<String, Object> map = new HashMap<>();
		map.put("x", convertParameter(circle.getCenter().getX()));
		map.put("y", convertParameter(circle.getCenter().getY()));
		map.put("radius", convertParameter(calculateDistanceInMeter(circle.getRadius())));
		return map;
	}

	private Map<String, Object> convertBox(Box box) {

		BoundingBox boundingBox = BoundingBox.of(box);
		return convertBoundingBox(boundingBox);
	}

	private Map<String, Object> convertBoundingBox(BoundingBox boundingBox) {

		Map<String, Object> map = new HashMap<>();

		map.put("llx", convertParameter(boundingBox.getLowerLeft().getX()));
		map.put("lly", convertParameter(boundingBox.getLowerLeft().getY()));
		map.put("urx", convertParameter(boundingBox.getUpperRight().getX()));
		map.put("ury", convertParameter(boundingBox.getUpperRight().getY()));

		return map;
	}

	private static double calculateDistanceInMeter(Distance distance) {

		if (distance.getMetric() == Metrics.KILOMETERS) {
			double kilometersDivisor = 0.001d;
			return distance.getValue() / kilometersDivisor;

		} else if (distance.getMetric() == Metrics.MILES) {
			double milesDivisor = 0.00062137d;
			return distance.getValue() / milesDivisor;

		} else {
			return distance.getValue();
		}
	}
}
