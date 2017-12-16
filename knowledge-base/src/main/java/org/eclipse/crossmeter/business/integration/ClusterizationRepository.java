/*******************************************************************************
 * Copyright (C) 2017 University of L'Aquila
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.crossmeter.business.integration;

import org.eclipse.crossmeter.business.model.Clusterization;
import org.eclipse.crossmeter.business.model.SimilarityMethod;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Juri Di Rocco
 *
 */
public interface ClusterizationRepository extends MongoRepository<Clusterization, String> {
	Clusterization findOneBySimilarityMethodOrderByClusterizationDateDesc(String name);
	Clusterization findTopByOrderByClusterizationDate();
	Clusterization findTopBySimilarityMethodOrderByClusterizationDate(String similarityMethod);
}
