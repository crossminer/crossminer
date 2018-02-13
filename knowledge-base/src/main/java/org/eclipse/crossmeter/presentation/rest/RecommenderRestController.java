/*******************************************************************************
 * Copyright (C) 2017 University of L'Aquila
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.crossmeter.presentation.rest;

import java.util.List;

import org.eclipse.crossmeter.business.IRecommenderManager;
import org.eclipse.crossmeter.business.ISimilarityCalculator;
import org.eclipse.crossmeter.business.dto.Query;
import org.eclipse.crossmeter.business.dto.Recommendation;
import org.eclipse.crossmeter.business.integration.ArtifactRepository;
import org.eclipse.crossmeter.business.model.Artifact;
import org.eclipse.crossmeter.business.model.Cluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Juri Di Rocco
 *
 */
@RestController
@RequestMapping("/api/recommendation")
public class RecommenderRestController {
	@Autowired
	@Qualifier("Dependency")
	private ISimilarityCalculator dependency;
	@Autowired
	@Qualifier("Readme")
	private ISimilarityCalculator readme;
	@Autowired
	private IRecommenderManager recommenderManager;
	@Autowired
	private ArtifactRepository artifactRepository;
	
	
	@RequestMapping(value="artifacts", produces = "application/json")
    public Page<Artifact> getArtifacts(Pageable pageable) {
		return artifactRepository.findAll(pageable);
    }
	
	@RequestMapping(value="cluster/{sim_method}", produces = "application/json")
    public List<Cluster> getClusters(@PathVariable("sim_method") String simFunction) {
		return recommenderManager.getClusters(simFunction);
    }

	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Recommendation getRecommendation(@RequestBody Query query) throws Exception {
		return recommenderManager.getRecommendation(query);
    }
	
	@RequestMapping(value="similar/p/{id}/m/{sim_method}/n/{num}", produces = "application/json")
    public List<Artifact> getSimilarProject(@PathVariable("sim_method") String simFunction,
    		@PathVariable("id") String id,
    		@PathVariable("num") int num) {
		
		return recommenderManager.getSimilarProjects(id, simFunction, num);
    }
	
	@RequestMapping(value="search/{artifact_query}", produces = "application/json")
    public List<Artifact> getProject(@PathVariable("artifact_query") String projectQuery) {
		return recommenderManager.getArtifactsByQuery(projectQuery);
    }
}
