package org.eclipse.crossmeter.business.rascal.impl;

import org.apache.log4j.Logger;
import org.eclipse.crossmeter.business.rascal.RascalBridge;
import org.rascalmpl.interpreter.Evaluator;
import org.rascalmpl.interpreter.control_exceptions.Throw;
import org.rascalmpl.interpreter.load.StandardLibraryContributor;
import org.rascalmpl.interpreter.load.URIContributor;

import io.usethesource.vallang.ISourceLocation;
import io.usethesource.vallang.IValue;

public class JavaRascalBridge implements RascalBridge {
	private Logger logger;
	
	public JavaRascalBridge(Logger logger) {
		this.logger = logger;
	}

	@Override
	public Object callFunction(ISourceLocation moduleRoot, String module, ClassLoader[] classLoaders, String function, IValue ... args) {
		try { 
			// Create Rascal intepreter
			Evaluator evaluator = JavaRascalContext.getEvaluator();
			
			// Add project (with Rascal modules) to search path and import module
			evaluator.addRascalSearchPathContributor(StandardLibraryContributor.getInstance());
			evaluator.addRascalSearchPathContributor(new URIContributor(moduleRoot));
			for(ClassLoader classLoader : classLoaders) {
				evaluator.addClassLoader(classLoader);
			}
			evaluator.doImport(null, module);
			
			// Call function (if the evaluator is shared it must be synchronized).
			synchronized (evaluator) {
				return evaluator.call(function, args);
			}
		} 
		catch (Throw e) {
			// Managing Rascal exceptions
			throw new RuntimeException("RascalBridge call to " + module + "::" + function +"  failed: " + e.getMessage(), e);
		}
	}
}
