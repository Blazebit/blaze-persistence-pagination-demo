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

import javax.faces.context.FacesContext;
import java.util.*;

import com.blazebit.persistence.KeysetPage;
import com.blazebit.persistence.PagedList;
import com.blazebit.persistence.PaginatedCriteriaBuilder;
import com.blazebit.persistence.pagination.web.view.IdHolderView;
import com.blazebit.persistence.view.EntityViewSetting;
import com.blazebit.persistence.view.Sorter;
import com.blazebit.persistence.view.Sorters;
import com.blazebit.reflection.ReflectionUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

/**
 * @author Moritz Becker (moritz.becker@gmx.at)
 * @since 1.2
 */
public abstract class AbstractLazyDataModel<T extends IdHolderView<?>> extends LazyDataModel<T> {

	protected final Class<T> viewClass;
	private KeysetPage keysetPage;

	// Request caching
	private PagedList<T> entityData;
	private Object requestContext;

	private List<SortMeta> oldSortMeta = Collections.emptyList();

	@SuppressWarnings("unchecked")
	public AbstractLazyDataModel() {
		this.viewClass = (Class<T>) ReflectionUtils.resolveTypeVariable(getClass(), AbstractLazyDataModel.class.getTypeParameters()[0]);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		// Notice that sort functions will only work with multisort enabled on
		// the datatable
		final PagedList<T> list;
		if (sortField == null) {
			list = load(first, pageSize, Collections.EMPTY_LIST, filters);
		} else {
			list = load(first, pageSize, Arrays.asList(new SortMeta(null, sortField, sortOrder, null)), filters);
		}

		return list;
	}

	@Override
	public PagedList<T> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
		Object currentRequestContext = FacesContext.getCurrentInstance();
		// prevent duplicate data fetching from the database within the same request
		if (requestContext == currentRequestContext && entityData != null && entityData.getMaxResults() == pageSize && multiSortMeta.isEmpty() && filters.isEmpty()) {
			return entityData;
		}

		requestContext = currentRequestContext;
		if(hasSortingChanged(multiSortMeta)){
			// we have to reset the keyset if the sorting changes
			keysetPage = null;
		}
		oldSortMeta = new ArrayList<>(multiSortMeta);
		entityData = getEntityData(first, pageSize, multiSortMeta, filters);

		setRowCount((int) entityData.getTotalSize());
		keysetPage = entityData.getKeysetPage();

		return entityData;
	}

	protected PagedList<T> getEntityData(int startRow, int rowsPerPage, List<SortMeta> multiSortMeta, Map<String, Object> filterMap) {
		return getEntityData(createSettings(viewClass, startRow, rowsPerPage, multiSortMeta, filterMap));
	}

	/**
	 * Implement this method to create a database query using the supplied entity view settings.
	 *
	 * @param setting the entity view settings that should be used for the query
	 * @return the query results
	 */
	protected abstract PagedList<T> getEntityData(EntityViewSetting<T, PaginatedCriteriaBuilder<T>> setting);

	/**
	 * This method creates entity view settings according to the current state of pagination, filtering and sorting in
	 * the datatable.
	 *
	 * This implementation transparently uses keyset pagination if possible.
	 *
	 * @param modelClass entity view class
	 * @param startRow page start row
	 * @param rowsPerPage page size
	 * @param multiSortMeta current sort metadata
	 * @param filterMap current filter settings
	 * @return
	 */
	protected EntityViewSetting<T, PaginatedCriteriaBuilder<T>> createSettings(Class<T> modelClass, int startRow, int rowsPerPage, List<SortMeta> multiSortMeta, Map<String, Object> filterMap) {
		EntityViewSetting<T, PaginatedCriteriaBuilder<T>> setting = EntityViewSetting.create(modelClass, startRow, rowsPerPage);

		if (startRow > 0) {
			setting.withKeysetPage(keysetPage);
		}

		applyFilters(setting, filterMap);

		applySorters(setting, multiSortMeta);

		return setting;
	}

	protected void applyFilters(EntityViewSetting<?, ?> setting, Map<String, Object> filterMap) {
		for (Map.Entry<String, Object> entry : filterMap.entrySet()) {
			String attributeName = getAttributeName(entry.getKey());
			Object filterValue = entry.getValue();
			String filterString;

			if (filterValue == null) {
				filterString = null;
			} else if (filterValue instanceof String) {
				filterString = filterValue.toString();
			} else {
				throw new IllegalArgumentException("Unsupported filter value [" + filterValue + "], only strings are supported!");
			}

			setting.addAttributeFilter(attributeName, filterString);
		}
	}

	protected void applySorters(EntityViewSetting<?, ?> setting, List<SortMeta> multiSortMeta) {
		if (multiSortMeta != null && !multiSortMeta.isEmpty()) {
			for (SortMeta meta : multiSortMeta) {
				if (meta.getSortOrder() == SortOrder.UNSORTED) {
					continue;
				}

				String attributeName = getAttributeName(meta.getSortField());
				Sorter sorter = meta.getSortOrder() == SortOrder.ASCENDING ? Sorters.ascending() : Sorters.descending();
				setting.addAttributeSorter(attributeName, sorter);
			}
		}
	}

	/**
	 * Maps the filter field supplied by the datatable definition to a entity view property name.
	 *
	 * @param expression the filter field expression from the datatable definition
	 * @return the entity view property name
	 */
	protected String getAttributeName(String expression) {
		return expression;
	}

	private boolean hasSortingChanged(List<SortMeta> newSortMeta) {
		if (newSortMeta.size() != oldSortMeta.size()) {
			return true;
		} else {
			for (int i = 0; i < newSortMeta.size(); i++) {
				if (!equals(newSortMeta.get(i), oldSortMeta.get(i))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean equals(SortMeta sort1, SortMeta sort2) {
		return Objects.equals(sort1.getSortField(), sort2.getSortField()) &&
				Objects.equals(sort1.getSortFunction(), sort2.getSortFunction()) &&
				Objects.equals(sort1.getSortOrder(), sort2.getSortOrder());
	}
}
