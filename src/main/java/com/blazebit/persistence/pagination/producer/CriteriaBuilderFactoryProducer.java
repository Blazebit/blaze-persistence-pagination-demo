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
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;

/**
 * @author Moritz Becker (moritz.becker@gmx.at)
 * @since 1.2
 */
@Singleton // From javax.ejb
@Startup   // From javax.ejb
public class CriteriaBuilderFactoryProducer {

	// inject your entity manager factory
	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;

	private CriteriaBuilderFactory criteriaBuilderFactory;

	@PostConstruct
	public void init() {
		CriteriaBuilderConfiguration config = Criteria.getDefault();
		// do some configuration
		this.criteriaBuilderFactory = config.createCriteriaBuilderFactory(entityManagerFactory);
	}

	@Produces
	@ApplicationScoped
	public CriteriaBuilderFactory createCriteriaBuilderFactory() {
		return criteriaBuilderFactory;
	}
}