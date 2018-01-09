/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.crossmeter.plugin.recommendation.highlight;

import org.crossmeter.plugin.ui.abstractmvc.AbstractController;

public class SourceCodeHighlighterController extends
		AbstractController<SourceCodeHighlighterModel, SourceCodeHighlighterView> {
	
	public SourceCodeHighlighterController(SourceCodeHighlighterModel model, SourceCodeHighlighterView view) {
		super(model, view);
		
		getView().showMarkers(getModel().getHighlightables());
	}
}