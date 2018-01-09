/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.crossmeter.plugin.knowledgebase.access.requestsender;

import org.crossmeter.demoserver.DemoServer;

public class LocalRequestSender implements IRequestSender{

	@Override
	public String sendRequest(String address, String content) {
		return DemoServer.receive(address, content);
	}
	
}