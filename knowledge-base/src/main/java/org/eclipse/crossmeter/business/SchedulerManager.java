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

import org.apache.log4j.Logger;
import org.eclipse.crossmeter.business.impl.CROSSRecServiceImpl;
import org.eclipse.crossmeter.business.impl.ClusterManager;
import org.eclipse.crossmeter.business.impl.SimilarityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SchedulerManager {

	private static final Logger logger = Logger.getLogger(SchedulerManager.class);
	@Autowired
	private List<ISimilarityCalculator> simCalcs;

	@Autowired
	private SimilarityManager simManager;

	@Autowired
	private ClusterManager clusterManager;

	@Autowired
	@Qualifier("Ossmeter")
	private IImporter importer;
	
	@Autowired
	private CROSSRecServiceImpl crossRecervice;

	// TODO DE-COMMENT ROW BELOW TO ENABLE TASK EXECUTION
	// @Scheduled(cron = "0 53 12 * * *")
	public void test() {
		try {
			logger.info("Importing projects from crossminer platform");
			importer.importAll();
			crossRecervice.createCROSSRecGraph();
			logger.info("Imported projects from crossminer platform");
		} catch (Exception e) {
		}

		for (ISimilarityCalculator iSimilarityCalculator : simCalcs)
			try {
				logger.info("Generating: " + iSimilarityCalculator.getSimilarityName() + " distance matrix");
				simManager.deleteRelations(iSimilarityCalculator);
				simManager.createAndStoreDistanceMatrix(iSimilarityCalculator);
				logger.info("Generated: " + iSimilarityCalculator.getSimilarityName() + " distance matrix");
			} catch (Exception e) {
			}
		for (ISimilarityCalculator iSimilarityCalculator : simCalcs)
			try {
				logger.info("Generating: " + iSimilarityCalculator.getSimilarityName() + " clusterization");
				clusterManager.calculateAndStoreClusterization(iSimilarityCalculator);
				logger.info("Generated: " + iSimilarityCalculator.getSimilarityName() + " clusterization");
			} catch (Exception e) {
			}
	}
}
