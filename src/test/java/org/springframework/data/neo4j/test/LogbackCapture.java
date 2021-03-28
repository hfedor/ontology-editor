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
package org.springframework.data.neo4j.test;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Provides access to the formatted message captured from Logback during test run.
 *
 * @author Michael J. Simons
 * @soundtrack Various - Just The Best 90s
 */
public final class LogbackCapture implements ExtensionContext.Store.CloseableResource {

	private final ListAppender<ILoggingEvent> listAppender;
	private final Logger logger;

	LogbackCapture() {
		this.listAppender = new ListAppender<>();
		// While forbidden by our checkstyle, we must go that route to get the logback root logger.
		this.logger = (Logger) org.slf4j.LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	}

	public List<String> getFormattedMessages() {
		return listAppender.list.stream().map(e -> e.getFormattedMessage()).collect(Collectors.toList());
	}

	void start() {
		this.logger.addAppender(listAppender);
		this.listAppender.start();
	}

	void clear() {
		this.listAppender.list.clear();
	}

	@Override
	public void close() {
		this.listAppender.stop();
		this.logger.detachAppender(listAppender);
	}
}
