/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.crossmeter.plugin.ui.recommendationaccept;

import java.util.List;
import java.util.stream.Collectors;

import org.crossmeter.plugin.event.informative.IInformativeEvent;
import org.crossmeter.plugin.event.informative.IInformativeEventSubscriber;
import org.crossmeter.plugin.event.informative.InformativeEvent;
import org.crossmeter.plugin.event.notifier.INotifierEvent;
import org.crossmeter.plugin.event.notifier.INotifierEventSubscriber;
import org.crossmeter.plugin.event.notifier.NotifierEvent;
import org.crossmeter.plugin.recommendation.highlight.SourceCodeHighlighterController;
import org.crossmeter.plugin.recommendation.highlight.SourceCodeHighlighterModel;
import org.crossmeter.plugin.recommendation.highlight.SourceCodeHighlighterView;
import org.crossmeter.plugin.ui.abstractmvc.AbstractController;
import org.crossmeter.plugin.utils.Utils;
import org.crossmeter.commons.highlight.IHighlightable;
import org.crossmeter.commons.recommendation.RecommendationKind;
import org.crossmeter.commons.recommendation.RecommendationSet;
import org.crossmeter.commons.recommendation.source.SourceRecommendation;
import org.eclipse.core.resources.IProject;

public class RecommendationAcceptController extends
		AbstractController<RecommendationAcceptModel, RecommendationAcceptView> {
	private final IInformativeEvent<RecommendationSet> recommendationsAccepted = registerEvent(
			new InformativeEvent<>());
	private final INotifierEvent recommendationsCancelled = registerEvent(new NotifierEvent());
	
	private SourceCodeHighlighterController sourceCodeHighlighterController;
	
	public RecommendationAcceptController(RecommendationAcceptModel model, RecommendationAcceptView view,
			IProject project) {
		super(model, view);
		
		getView().getRecommendationsAccepted().subscribe(this::onRecommendationsAccepted);
		getView().getRecommendationsCancelled().subscribe(() -> recommendationsCancelled.invoke());
		
		getView().showRecommendations(getModel().getRecommendations().asList());
		
		initSourceCodeHighlighter(project);
	}
	
	public IInformativeEventSubscriber<RecommendationSet> getRecommendationsAccepted() {
		return recommendationsAccepted;
	}
	
	public INotifierEventSubscriber getRecommendationsCancelled() {
		return recommendationsCancelled;
	}
	
	private void onRecommendationsAccepted() {
		RecommendationSet recommendations = getModel().getRecommendations();
		recommendationsAccepted.invoke(recommendations);
	}
	
	private void initSourceCodeHighlighter(IProject project) {
		List<SourceRecommendation> sourceRecommendations = Utils.filterType(getModel().getRecommendations().asList(),
				RecommendationKind.SOURCE_REPLACE);
		List<IHighlightable> highlightables = sourceRecommendations.stream().map(IHighlightable.class::cast).collect(
				Collectors.toList());
		
		SourceCodeHighlighterModel sourceCodeHighlighterModel = new SourceCodeHighlighterModel(highlightables);
		SourceCodeHighlighterView sourceCodeHighlighterView = new SourceCodeHighlighterView(project);
		sourceCodeHighlighterController = new SourceCodeHighlighterController(sourceCodeHighlighterModel,
				sourceCodeHighlighterView);
		addSubController(sourceCodeHighlighterController);
	}
}
