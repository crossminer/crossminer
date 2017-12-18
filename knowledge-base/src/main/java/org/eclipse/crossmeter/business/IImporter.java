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

import java.io.IOException;
import java.util.List;

import org.eclipse.crossmeter.business.model.Artifact;
import org.eclipse.crossmeter.business.model.GithubUser;
import org.eclipse.crossmeter.business.model.Stargazers;

/**
 * @author Juri Di Rocco
 *
 */
public interface IImporter {
	Artifact importProject(String artId) 
			throws IOException;
	void importAll();
	void storeGithubUserCommitter(List<GithubUser> committers, String repoName);
	void storeGithubUser(List<Stargazers> starred, String repoName);
}
