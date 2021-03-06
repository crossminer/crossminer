/*******************************************************************************
 * Copyright (C) 2017 University of L'Aquila
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.crossmeter.test.similarity;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.crossmeter.Application;
import org.eclipse.crossmeter.business.IAggregatedSimilarityCalculator;
import org.eclipse.crossmeter.business.ISingleSimilarityCalculator;
import org.eclipse.crossmeter.business.impl.OssmeterImporter;
import org.eclipse.crossmeter.business.integration.ArtifactRepository;
import org.eclipse.crossmeter.business.integration.GithubUserRepository;
import org.eclipse.crossmeter.business.model.Artifact;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Table;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class})
@TestPropertySource(locations="classpath:application.properties")
public class RepoPalTimeSimilarityCalculatorTest {
	@Autowired
	private ArtifactRepository artifactRepository;
	@Autowired
	@Qualifier("RepoPalTime")
	private ISingleSimilarityCalculator repoPalTimeSim;
	private static final Logger logger = Logger.getLogger(RepoPalTimeSimilarityCalculatorTest.class);
	private Artifact artifact1;
	private Artifact artifact2;
	@Autowired
	OssmeterImporter ossmeterImporter;
	
	@Autowired
	private GithubUserRepository githubUserRepository;
	@Before
	public void init(){
		artifactRepository.deleteAll();
		githubUserRepository.deleteAll();
		try {
			ObjectMapper mapper = new ObjectMapper();
			Resource resource = new ClassPathResource("artifacts.json");
			InputStream resourceInputStream = resource.getInputStream();
			List<Artifact> myObjects = mapper.readValue(resourceInputStream, new TypeReference<List<Artifact>>(){});
			artifactRepository.save(myObjects);
			for (Artifact artifact : myObjects) {
				ossmeterImporter.storeGithubUser(artifact.getStarred(), artifact.getFullName());
				ossmeterImporter.storeGithubUserCommitter(artifact.getCommitteers(), artifact.getFullName());
			}
			resourceInputStream.close();
			artifact1 = myObjects.get(0);
			artifact2 = myObjects.get(1);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	@After
	public void dispose(){
		artifactRepository.deleteAll();
		githubUserRepository.deleteAll();
	}
	@Test
	public void crossSimCommutativeTest() {
		double val1 = repoPalTimeSim.calculateSimilarity(artifact1, artifact2);
		double val2 = repoPalTimeSim.calculateSimilarity(artifact2, artifact1);
		logger.info(val1 + " " + val2);
		assertEquals(val1,val2, 0.0);
	}
	@Test
	public void crossSimIdentityTest() {
		List<Artifact> artifacts = artifactRepository.findFirst10ByOrderByIdDesc();
		double val1 = repoPalTimeSim.calculateSimilarity(artifacts.get(0), artifacts.get(0));
		assertEquals(val1,1, 0.000001);
	}
}
