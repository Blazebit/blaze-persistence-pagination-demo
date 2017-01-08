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

package com.blazebit.persistence.pagination.startup;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.blazebit.persistence.pagination.entity.Cat;

/**
 * @author Moritz Becker (moritz.becker@gmx.at)
 * @since 1.2
 */
@Singleton
@Startup
public class StartupBean {

	@Inject
	private EntityManager em;

	@PostConstruct
	public void onStartup() {
		final Cat c1 = persist(createCat("Cat1", 12L, null));
		final Cat c2 = persist(createCat("Cat2", 11L, null));
		final Cat c3 = persist(createCat("Cat3", 1L, null));
		final Cat c4 = persist(createCat("Cat4", 5L, c3));
		final Cat c5 = persist(createCat("Cat5", 6L, null));
		final Cat c6 = persist(createCat("Cat6", 9L, null));
		final Cat c7 = persist(createCat("Cat7", 29L, null));
		final Cat c8 = persist(createCat("Cat8", 23L, c6));
		final Cat c9 = persist(createCat("Cat9", 14L, null));
		final Cat c10 = persist(createCat("Cat10", 16L, null));
		final Cat c11 = persist(createCat("Cat11", 10L, c9));
		final Cat c12 = persist(createCat("Cat12", 54L, null));
		final Cat c13 = persist(createCat("Cat13", 64L, null));
		final Cat c14 = persist(createCat("Cat14", 42L, c12));
		final Cat c15 = persist(createCat("Cat15", 40L, null));
		final Cat c16 = persist(createCat("Cat16", 20L, null));
		final Cat c17 = persist(createCat("Cat17", 89L, c7));
		final Cat c18 = persist(createCat("Cat18", 43L, null));
		final Cat c19 = persist(createCat("Cat19", 25L, null));
		final Cat c20 = persist(createCat("Cat20", 33L, c1));
		final Cat c21 = persist(createCat("Cat21", 39L, null));
	}

	private Cat createCat(String name, Long age, Cat parent) {
		Cat cat = new Cat();
		cat.setName(name);
		cat.setAge(age);
		cat.setParent(parent);
		return cat;
	}

	private <T> T persist(T t) {
		em.persist(t);
		return t;
	}
}
