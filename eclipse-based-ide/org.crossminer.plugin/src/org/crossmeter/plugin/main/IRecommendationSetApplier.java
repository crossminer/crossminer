/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.crossmeter.plugin.main;

import org.crossmeter.commons.recommendation.RecommendationSet;
import org.eclipse.core.resources.IProject;

public interface IRecommendationSetApplier {
	void applyRecommendationSetOnProject(IProject project, RecommendationSet recommendations);
}
