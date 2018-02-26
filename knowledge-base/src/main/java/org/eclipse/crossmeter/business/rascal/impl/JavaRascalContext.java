package org.eclipse.crossmeter.business.rascal.impl;

import java.io.PrintWriter;

import org.rascalmpl.interpreter.Evaluator;
import org.rascalmpl.interpreter.env.GlobalEnvironment;
import org.rascalmpl.interpreter.env.ModuleEnvironment;
import org.rascalmpl.values.uptr.IRascalValueFactory;

public class JavaRascalContext {
	
	private static Evaluator evaluator;
	private static GlobalEnvironment heap;
	
	public static Evaluator getEvaluator() {
		if(evaluator == null) {
			evaluator = new Evaluator(IRascalValueFactory.getInstance(), 
					new PrintWriter(System.err, true), new PrintWriter(System.out), 
					new ModuleEnvironment("$crossminer$", getHeap()), getHeap());
		}
		return evaluator;
	}
	
	public static GlobalEnvironment getHeap() {
		if(heap == null) {
			heap = new GlobalEnvironment();
		}
		return heap;
	}
}
