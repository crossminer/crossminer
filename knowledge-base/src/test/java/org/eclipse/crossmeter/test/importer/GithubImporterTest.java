/*******************************************************************************
 * Copyright (C) 2017 University of L'Aquila
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.crossmeter.test.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.crossmeter.Application;
import org.eclipse.crossmeter.business.impl.GithubImporter;
import org.eclipse.crossmeter.business.integration.ArtifactRepository;
import org.eclipse.crossmeter.business.model.Artifact;
import org.eclipse.crossmeter.business.model.GithubUser;
import org.eclipse.crossmeter.business.rascal.RascalBridge;
import org.eclipse.crossmeter.business.rascal.impl.JavaRascalBridge;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.usethesource.vallang.IString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class})
@TestPropertySource(locations="classpath:application.properties")
public class GithubImporterTest {
	@Autowired
	private GithubImporter importer;
	
	@Autowired
	private ArtifactRepository artifactRepository;
	
	private static final Logger logger = Logger.getLogger(GithubImporterTest.class);

	@Before
	public void init(){
		artifactRepository.deleteAll();
	}
	@After
	public void dispose(){
		artifactRepository.deleteAll();
	}
	@Test
	public void importProjectTest() throws IOException {
		importer.importProject("MDEGroup/MDEProfile");
		assertEquals(artifactRepository.count(), 1);
	}
	@Test
	public void importer() throws IOException, XmlPullParserException{
		Artifact art = importer.importProject("pylerSM/Xinstaller");
		assertNotNull(art);
	}
	@Test
	public void testRascalBridge() {
		RascalBridge bridge = new JavaRascalBridge();
		String path = "file:///" + System.getProperty("user.dir") + "/src/";
		IString hoi = (IString) bridge.callFunction(path, 
				"test::java::org::eclipse::crossmeter::test::importer::RascalModuleTest", 
				"testBridge");
		assertEquals("Hello world!", hoi.getValue());
	}
}
