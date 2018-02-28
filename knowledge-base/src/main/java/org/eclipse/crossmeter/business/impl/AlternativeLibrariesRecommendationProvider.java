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
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.crossmeter.business.IRecommendationProvider;
import org.eclipse.crossmeter.business.ISimilarityCalculator;
import org.eclipse.crossmeter.business.ISimilarityManager;
import org.eclipse.crossmeter.business.dto.Dependency;
import org.eclipse.crossmeter.business.dto.Query;
import org.eclipse.crossmeter.business.dto.Recommendation;
import org.eclipse.crossmeter.business.dto.RecommendationItem;
import org.eclipse.crossmeter.business.integration.ArtifactRepository;
import org.eclipse.crossmeter.business.integration.RelationRepository;
import org.eclipse.crossmeter.business.model.Artifact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Juri Di Rocco
 *
 */
@Service
@Qualifier("AlternativeLibraries")
public class AlternativeLibrariesRecommendationProvider implements IRecommendationProvider {

	private static final Logger logger = Logger.getLogger(AlternativeLibrariesRecommendationProvider.class);
	private static final int NUMBER_OF_RESULT = 5;
	@Autowired
	RelationRepository relationRepository;
	@Autowired
	private ArtifactRepository artifactRepository;

	@Autowired
	List<ISimilarityCalculator> similarities;

	@Autowired
	private ISimilarityManager similarityManager;
	
	//		RecommendationEngine engine = new RecommendationEngine(this.srcDir, inputProject);
//	int numberOfNeighbours = 20;
//	engine.UserBasedRecommendation(numberOfNeighbours);
	

	@Override
	public Recommendation getRecommendation(Query query) throws Exception {
		Recommendation rec = new Recommendation();
		rec.setRecommendationItems(new ArrayList<RecommendationItem>());
		ISimilarityCalculator simCalc = getSimilarityCalculator(query.getSimilarityMethod());
		for (Dependency dependency : query.getProjectDependencies()) {
			try{
				Artifact current = artifactRepository.findOne(dependency.getArtifactID());
				if(current != null){
					List<Artifact> similarProjects = similarityManager.getSimilarProjects(
							current, simCalc, 
							NUMBER_OF_RESULT);
					for (Artifact artifact : similarProjects) {
						RecommendationItem ri = new RecommendationItem();
						ri.setRelatedTo(dependency);
						ri.setArtifact(artifact);
						ri.setRecommendationType("Alternative");
						rec.getRecommendationItems().add(ri);
						
					}
	//				Clusterization clusterization = clusterizationRepository.findTopBySimilarityMethodOrderByClusterizationDate(query.getSimilarityMethod());
	//				Cluster cluster = clusterRepository.findOneByArtifactsIdAndClusterizationId(new ObjectId(dependency.getArtifactID()), new ObjectId(clusterization.getId()));
	//				for (Artifact artifact : cluster.getArtifacts()) {
	//					RecommendationItem ri = new RecommendationItem();
	//					ri.setRelatedTo(dependency);
	//					ri.setArtifact(artifact);
	//					rec.getRecommendationItems().add(ri);
	//				}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return rec;
	}

	
	
	private ISimilarityCalculator getSimilarityCalculator(String similarityMethod) throws Exception {
		Optional<ISimilarityCalculator> a = similarities.stream().filter(z -> z.getSimilarityName().equals(similarityMethod)).findFirst();
		if (a.isPresent())
			return a.get();
		else throw new Exception("similarity method " + similarityMethod + " is unavailable");
	}

}
