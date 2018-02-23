package org.eclipse.crossmeter.business.rascal;

import java.util.Set;

import io.usethesource.vallang.ISourceLocation;
import io.usethesource.vallang.IValue;

// Consider moving the whole package to another project.
public interface RascalBridge {
	
	/**
	 * Calls a function in a Rascal project.
	 * @param moduleRoot: Rascal location to the source folder of a given module.
	 * @param module: qualified name of the module (e.g. org::eclipse::crossmeter::business::rascal::Module)
	 * @param classLoaders: set of classloaders of Java classes used by the Rascal project.
	 * @param function: name of the function to be called.
	 * @param args: set of arguments required by the Rascal function.
	 */
	public Object callFunction(ISourceLocation moduleRoot, String module, Set<ClassLoader> classLoaders, String function, IValue ... args);
}
