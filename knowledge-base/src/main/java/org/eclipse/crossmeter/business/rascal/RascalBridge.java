package org.eclipse.crossmeter.business.rascal;

import io.usethesource.vallang.ISourceLocation;
import io.usethesource.vallang.IValue;

public interface RascalBridge {
	public Object callFunction(ISourceLocation moduleRoot, String module, String function, IValue ... args);
}
