/*
 * Copyright 2014 - 2016 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blazebit.persistence.pagination.producer;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.spi.EntityViewConfiguration;

/**
 * @author Moritz Becker (moritz.becker@gmx.at)
 * @since 1.2
 */
@Singleton // from javax.ejb
@Startup   // from javax.ejb
public class EntityViewManagerProducer {

	// inject the configuration provided by the cdi integration
	@Inject
	private EntityViewConfiguration config;

	// inject the criteria builder factory which will be used along with the entity view manager
	@Inject
	private CriteriaBuilderFactory criteriaBuilderFactory;

	// inject your entity manager factory
	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;

	private EntityViewManager evm;

	@PostConstruct
	public void init() {
		// do some configuration
		evm = config.createEntityViewManager(criteriaBuilderFactory, entityManagerFactory);
	}

	@Produces
	@ApplicationScoped
	public EntityViewManager createEntityViewManager() {
		return evm;
	}
}
