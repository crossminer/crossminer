/*******************************************************************************
 * Copyright (C) 2017 University of L'Aquila
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.crossmeter.business.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.crossmeter.business.IClusterManager;
import org.eclipse.crossmeter.business.IRecommendationProvider;
import org.eclipse.crossmeter.business.IRecommenderManager;
import org.eclipse.crossmeter.business.ISimilarityCalculator;
import org.eclipse.crossmeter.business.ISimilarityManager;
import org.eclipse.crossmeter.business.dto.Query;
import org.eclipse.crossmeter.business.dto.Recommendation;
import org.eclipse.crossmeter.business.integration.ArtifactRepository;
import org.eclipse.crossmeter.business.model.Artifact;
import org.eclipse.crossmeter.business.model.Cluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * @author Juri Di Rocco
 *
 */
@Service
public class RecommenderManager implements IRecommenderManager{

	private static final Logger logger = Logger.getLogger(RecommenderManager.class);
	@Autowired
	private IRecommendationProvider recommendationProvider;
	@Autowired
	private ArtifactRepository artifactRepository;

	@Autowired
	List<ISimilarityCalculator> similarityFunction;
	@Autowired
	private IClusterManager clusterManager;
	@Autowired
	private ISimilarityManager similarityManager;
	@Override
	public Recommendation getRecommendation(Query query) throws Exception {
		HashMap<String, Object> params = new HashMap<>();
		return recommendationProvider.getRecommendation(query, params);			
	}
	@Override
	public List<Cluster> getClusters(String similarityName) {
		try {
			return clusterManager.getClusters(similarityFunction.get(0));
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}
	
	@Override
	public List<Artifact> getSimilarProjects(String projectId, String similarityFunction, int numOfResult){
		Artifact p1 = artifactRepository.findOne(projectId);
		try {
			return similarityManager.getSimilarProjects(p1, getSimilarityCalculator(similarityFunction), numOfResult);
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	@Override
	public Recommendation getRecommendation(Query query, List<IRecommendationProvider> providers) {
		// TODO Auto-generated method stub
		return null;
	}

	private ISimilarityCalculator getSimilarityCalculator(String similarityMethod) throws Exception {
		Optional<ISimilarityCalculator> a = similarityFunction.stream().filter(z -> z.getSimilarityName().equals(similarityMethod)).findFirst();
		if (a.isPresent())
			return a.get();
		else throw new Exception("similarity method " + similarityMethod + " is unavailable");
	}
}
