/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.crossmeter.plugin.recommendation;

import java.util.List;

import org.crossmeter.plugin.context.sourcecodestatus.SourceCodeStatusDetector;
import org.crossmeter.plugin.context.sourcecodestatus.SourceCodeStatusException;
import org.crossmeter.plugin.knowledgebase.access.KnowledgeBaseAccessManager;
import org.crossmeter.plugin.recommendation.applier.RecommendationSetApplier;
import org.crossmeter.commons.context.sourcecode.SourceCodeContext;
import org.crossmeter.commons.library.Library;
import org.crossmeter.commons.libraryapi.LibraryAPIElement;
import org.crossmeter.commons.recommendation.RecommendationSet;
import org.crossmeter.commons.transaction.APIUsageInContext;
import org.crossmeter.commons.transaction.ParsedSourceCodeContext;
import org.crossmeter.commons.transaction.UpdatableAPIUsage;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;

public class RecommendationManager implements ILibraryUpdateRecommendationProvider {
	private final KnowledgeBaseAccessManager knowledgeBaseAccessManager;
	
	public RecommendationManager(KnowledgeBaseAccessManager knowledgeBaseAccessManager) {
		this.knowledgeBaseAccessManager = knowledgeBaseAccessManager;
	}
	
	public void applyRecommendations(IProject project, RecommendationSet recommendations) {
		RecommendationSetApplier applier = new RecommendationSetApplier(project, recommendations);
		applier.apply();
	}
	
	/* (non-Javadoc)
	 * @see org.crossmeter.plugin.recommendation.ILibraryUpdateRecommendationProvider#requestRecommendationsToUpdateLibraryInProject(org.eclipse.jdt.core.IJavaProject, org.crossmeter.commons.library.Library, org.crossmeter.commons.library.Library)
	 */
	@Override
	public RecommendationSet requestRecommendationsToUpdateLibraryInProject(IJavaProject project, Library updateFrom,
			Library updateTo) throws SourceCodeStatusException {
		List<LibraryAPIElement> apiElements = knowledgeBaseAccessManager.requestLibraryAPIChangesBetweenVersions(
				updateFrom, updateTo);
		
		List<APIUsageInContext> apiUsages = SourceCodeStatusDetector.findAPIUsagesInProject(project, apiElements);
		
		UpdatableAPIUsage updatableAPIUsage = new UpdatableAPIUsage(updateFrom, updateTo, apiUsages);
		
		RecommendationSet recommendationSet = knowledgeBaseAccessManager.requestRecommendationsToUpdateAPIUsage(
				updatableAPIUsage);
		return recommendationSet;
	}
	
	public RecommendationSet requestRecommendationsToGeneralImprovements(SourceCodeContext context) {
		ParsedSourceCodeContext parsedSourceCodeContext = new ParsedSourceCodeContext(context);
		RecommendationSet recommendationSet = knowledgeBaseAccessManager.requestRecommendationsToGeneralImprovements(
				parsedSourceCodeContext);
		
		return recommendationSet;
	}
}
