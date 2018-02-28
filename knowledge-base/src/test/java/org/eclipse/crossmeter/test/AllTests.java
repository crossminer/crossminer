/*******************************************************************************
 * Copyright (C) 2017 University of L'Aquila
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.crossmeter.test;

import org.eclipse.crossmeter.test.crossrec.CROSSRECTest;
import org.eclipse.crossmeter.test.importer.GithubImporterTest;
import org.eclipse.crossmeter.test.manager.ClusterManagerTest;
import org.eclipse.crossmeter.test.manager.SimilarityManagerTest;
import org.eclipse.crossmeter.test.recommendation.providers.AlternativeLibrariesRecommendationProviderTest;
import org.eclipse.crossmeter.test.rest.RecommenderRestTest;
import org.eclipse.crossmeter.test.similarity.CROSSSimSimilarityCalculatorTest;
import org.eclipse.crossmeter.test.similarity.CompoundSimilarityCalculatorTest;
import org.eclipse.crossmeter.test.similarity.DependencySimilarityCalculatorTest;
import org.eclipse.crossmeter.test.similarity.ReadmeSimilarityCalculatorTest;
import org.eclipse.crossmeter.test.similarity.RepoPalCompoundSimilarityCalculatorTest;
import org.eclipse.crossmeter.test.similarity.RepoPalStarSimilarityCalculatorTest;
import org.eclipse.crossmeter.test.similarity.RepoPalTimeSimilarityCalculatorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CompoundSimilarityCalculatorTest.class, CROSSSimSimilarityCalculatorTest.class,
		DependencySimilarityCalculatorTest.class, ReadmeSimilarityCalculatorTest.class,
		RepoPalCompoundSimilarityCalculatorTest.class, RepoPalStarSimilarityCalculatorTest.class,
		RepoPalTimeSimilarityCalculatorTest.class, SimilarityManagerTest.class, GithubImporterTest.class,
		ClusterManagerTest.class, DependencyServiceTest.class, RecommenderRestTest.class, CROSSRECTest.class,
		AlternativeLibrariesRecommendationProviderTest.class, AlternativeLibrariesRecommendationProviderTest.class})
public class AllTests {

}
