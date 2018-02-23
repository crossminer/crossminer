package org.eclipse.crossmeter.business.rascal.impl;

import java.io.PrintWriter;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.crossmeter.business.rascal.RascalBridge;
import org.rascalmpl.interpreter.Evaluator;
import org.rascalmpl.interpreter.control_exceptions.Throw;
import org.rascalmpl.interpreter.env.GlobalEnvironment;
import org.rascalmpl.interpreter.env.ModuleEnvironment;
import org.rascalmpl.interpreter.load.StandardLibraryContributor;
import org.rascalmpl.interpreter.load.URIContributor;
import org.rascalmpl.library.Prelude;
import org.rascalmpl.values.uptr.IRascalValueFactory;

import io.usethesource.vallang.ISourceLocation;
import io.usethesource.vallang.IValue;

public class JavaRascalBridge implements RascalBridge {
	
	private static Logger logger;
	
	public JavaRascalBridge(Logger logger) {
		JavaRascalBridge.logger = logger;
	}
	
	// TODO: create the evaluator in a separate method (for the sake of efficiency).
	@Override
	public Object callFunction(ISourceLocation moduleRoot, String module, Set<ClassLoader> classLoaders, String function, IValue ... args) {
		try { 
			// Create Rascal intepreter
			GlobalEnvironment heap = new GlobalEnvironment();
			Evaluator evaluator = new Evaluator(IRascalValueFactory.getInstance(), 
					new PrintWriter(System.err, true), new PrintWriter(System.out), 
					new ModuleEnvironment("$crossminer$", heap), heap);
			
			// Add project (with Rascal modules) to search path and import module
			evaluator.addRascalSearchPathContributor(StandardLibraryContributor.getInstance());
			evaluator.addRascalSearchPathContributor(new URIContributor(moduleRoot));
			if (classLoaders !=  null) {
				classLoaders.forEach(evaluator::addClassLoader);
			}
			evaluator.addClassLoader(DependenciesAnalyzer.class.getClassLoader());
			evaluator.addClassLoader(Prelude.class.getClassLoader());
			
			ModuleEnvironment env = new ModuleEnvironment(module, heap);
			for(String imp : env.getImportsTransitive()) {
				evaluator.doImport(null, imp);
			}
			evaluator.doImport(null, module);

			// Call function (if the evaluator is shared it must be synchronized).
			return evaluator.call(function, args);
		} 
		catch (Throw e) {
			// Managing Rascal exceptions
			throw new RuntimeException("RascalBridge call to " + module + "::" + function +"  failed: " + e.getMessage(), e);
		}
	}
}
