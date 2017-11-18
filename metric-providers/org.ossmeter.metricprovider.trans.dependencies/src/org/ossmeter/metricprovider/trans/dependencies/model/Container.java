package org.ossmeter.metricprovider.trans.dependencies.model;

import com.googlecode.pongo.runtime.*;
import com.mongodb.*;

public class Container extends PongoDB {
	
	public Container() {}
	
	public Container(DB db) {
		setDb(db);
	}
	
	protected DependenciesCollection dependencies = null;
	
	
	
	public DependenciesCollection getDependencies() {
		return dependencies;
	}
	
	
	@Override
	public void setDb(DB db) {
		super.setDb(db);
		dependencies = new DependenciesCollection(db.getCollection("Container.dependencies"));
		pongoCollections.add(dependencies);
	}
}