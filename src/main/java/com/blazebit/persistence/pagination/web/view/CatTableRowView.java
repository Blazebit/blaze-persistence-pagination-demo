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

package com.blazebit.persistence.pagination.web.view;

import com.blazebit.persistence.SubqueryInitiator;
import com.blazebit.persistence.pagination.entity.Cat;
import com.blazebit.persistence.view.*;
import com.blazebit.persistence.view.filter.ContainsFilter;

/**
 * @author Moritz Becker (moritz.becker@gmx.at)
 * @since 1.2
 */
@EntityView(Cat.class)
public interface CatTableRowView extends IdHolderView<Long> {

	@AttributeFilter(ContainsFilter.class)
	String getName();

	Long getAge();

	@AttributeFilter(ContainsFilter.class)
	@Mapping("parent.name")
	String getParentName();

	@MappingSubquery(NumChildrenSubqueryProvider.class)
	Long getNumChildren();

	class NumChildrenSubqueryProvider implements SubqueryProvider {

		@Override
		public <T> T createSubquery(SubqueryInitiator<T> subqueryInitiator) {
			return subqueryInitiator.from(Cat.class)
					.select("COUNT(*)")
					.where("parent.id").eqExpression("OUTER(id)")
					.end();
		}
	}

}
