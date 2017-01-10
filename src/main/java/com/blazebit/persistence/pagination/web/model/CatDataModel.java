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

package com.blazebit.persistence.pagination.web.model;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.PagedList;
import com.blazebit.persistence.PaginatedCriteriaBuilder;
import com.blazebit.persistence.pagination.entity.Cat;
import com.blazebit.persistence.pagination.web.view.CatTableRowView;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;

/**
 * @author Moritz Becker (moritz.becker@gmx.at)
 * @since 1.2
 */
@ViewScoped
@Named
public class CatDataModel extends AbstractLazyDataModel<CatTableRowView> {

	@Inject
	private CriteriaBuilderFactory cbf;

	@Inject
	private EntityViewManager evm;

	@Inject
	private EntityManager em;

	@Override
	protected PagedList<CatTableRowView> getEntityData(EntityViewSetting<CatTableRowView, PaginatedCriteriaBuilder<CatTableRowView>> setting) {
		return evm.applySetting(setting, cbf.create(em, Cat.class))
				.orderByAsc("id")
				.getResultList();
	}
}
