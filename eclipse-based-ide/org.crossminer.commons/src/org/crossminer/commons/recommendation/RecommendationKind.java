/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*    Zsolt J�nos Szamosv�lgyi
*    Endre Tam�s V�radi
*    Gerg� Balogh
**********************************************************************/
package org.crossminer.commons.recommendation;

import java.lang.reflect.Type;

import org.crossminer.commons.ITypeRepresentation;
import org.crossminer.commons.recommendation.library.AddAntLibraryRecommendation;
import org.crossminer.commons.recommendation.library.AddMavenLibraryRecommendation;
import org.crossminer.commons.recommendation.library.AddSimpleLibraryRecommendation;
import org.crossminer.commons.recommendation.library.RemoveLibraryRecommendation;
import org.crossminer.commons.recommendation.source.SourceReplaceRecommendation;

/**
 * Provides an enumeration-safe set of kinds of {@link Recommendation}.
 *
 */
public enum RecommendationKind implements ITypeRepresentation {
	SOURCE_REPLACE(SourceReplaceRecommendation.class),
	ADD_MAVEN_LIBRARY(AddMavenLibraryRecommendation.class),
	ADD_ANT_LIBRARY(AddAntLibraryRecommendation.class),
	ADD_SIMPLE_LIBRARY(AddSimpleLibraryRecommendation.class),
	REMOVE_LIBRARY(RemoveLibraryRecommendation.class);

	private final Type type;
	
	private RecommendationKind(Type type) {
		this.type = type;
	}
	
	@Override
	public Type getType() {
		return type;
	}
}
