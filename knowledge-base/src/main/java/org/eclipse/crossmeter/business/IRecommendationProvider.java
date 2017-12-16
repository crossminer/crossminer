/*******************************************************************************
 * Copyright (C) 2017 University of L'Aquila
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.crossmeter.business;

import java.util.Map;

import org.eclipse.crossmeter.business.dto.Query;
import org.eclipse.crossmeter.business.dto.Recommendation;
import org.eclipse.crossmeter.business.model.ArtifactType;

/**
 * @author Juri Di Rocco
 *
 */
public interface IRecommendationProvider {
	ArtifactType getRecommendationType();
	Recommendation getRecommendation(Query query, Map<String, Object> parameters) throws Exception;
}
