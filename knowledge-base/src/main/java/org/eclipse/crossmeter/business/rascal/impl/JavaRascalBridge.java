package org.eclipse.crossmeter.business.rascal.impl;

import java.io.PrintWriter;
import java.net.URISyntaxException;

import org.eclipse.crossmeter.business.rascal.RascalBridge;
import org.rascalmpl.interpreter.Evaluator;
import org.rascalmpl.interpreter.NullRascalMonitor;
import org.rascalmpl.interpreter.control_exceptions.Throw;
import org.rascalmpl.interpreter.env.GlobalEnvironment;
import org.rascalmpl.interpreter.env.ModuleEnvironment;
import org.rascalmpl.interpreter.load.StandardLibraryContributor;
import org.rascalmpl.interpreter.load.URIContributor;
import org.rascalmpl.values.uptr.IRascalValueFactory;

import io.usethesource.vallang.ISourceLocation;
import io.usethesource.vallang.IValue;

public class JavaRascalBridge implements RascalBridge {

	@Override
	public Object callFunction(ISourceLocation moduleRoot, String module, String function, IValue ... args) {
		try { 
			// Create Rascal intepreter
			GlobalEnvironment heap = new GlobalEnvironment();
			Evaluator evaluator = new Evaluator(IRascalValueFactory.getInstance(), 
					new PrintWriter(System.err, true), new PrintWriter(System.out), 
					new ModuleEnvironment("$crossminer$", heap), heap);
			
			// Add project (with Rascal modules) to search path and import module
			evaluator.addRascalSearchPathContributor(StandardLibraryContributor.getInstance());
			evaluator.addRascalSearchPathContributor(new URIContributor(moduleRoot));
			evaluator.doImport(new NullRascalMonitor(), module);
			
			// Call function
			return evaluator.call(function, args);
		} 
		catch (Throw e) {
			// Managing Rascal exceptions
			throw new RuntimeException("JavaBridge call to " + module + "::" + function +"  failed", e);
		}
	}
}
