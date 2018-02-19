package org.eclipse.crossmeter.business.rascal.impl;

import org.rascalmpl.interpreter.Evaluator;
import org.rascalmpl.interpreter.NullRascalMonitor;
import org.rascalmpl.interpreter.env.GlobalEnvironment;
import org.rascalmpl.interpreter.env.ModuleEnvironment;
import org.rascalmpl.interpreter.load.URIContributor;
import org.rascalmpl.values.ValueFactoryFactory;

import java.io.PrintWriter;
import java.net.URISyntaxException;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.crossmeter.business.rascal.RascalBridge;

public class JavaRascalBridge implements RascalBridge {

	@Override
	public Object callFunction(String project, String module, String function) {
		return t(project,module,function);
	}
	
	private Object t (String project, String module, String function) {
		GlobalEnvironment heap = new GlobalEnvironment();
		Evaluator evaluator = new Evaluator(ValueFactoryFactory.getValueFactory(), 
				new PrintWriter(System.err), 
				new PrintWriter(System.out), 
				new ModuleEnvironment("$crossminer$",heap), 
				heap);
		try {
			evaluator.addRascalSearchPathContributor(new URIContributor(org.rascalmpl.uri.URIUtil.createFromURI(project)));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		evaluator.doImport(new NullRascalMonitor(), module);
		Object result = evaluator.call(function);
		return result;
	}
}
