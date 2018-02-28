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

import java.util.List;

import org.eclipse.crossmeter.business.dto.Query;
import org.eclipse.crossmeter.business.dto.Recommendation;
import org.eclipse.crossmeter.business.dto.RecommendationType;
import org.eclipse.crossmeter.business.model.Artifact;
import org.eclipse.crossmeter.business.model.Cluster;

/**
 * @author Juri Di Rocco
 *
 */
public interface IRecommenderManager {
	Recommendation getRecommendation(Query query, RecommendationType rt) throws Exception;
	List<Cluster> getClusters(String similarityName);
	List<Artifact> getSimilarProjects(String projectId, String similarityFunction, int numOfResult);
	List<Artifact> getArtifactsByQuery(String projectQuery);
	
	
}
