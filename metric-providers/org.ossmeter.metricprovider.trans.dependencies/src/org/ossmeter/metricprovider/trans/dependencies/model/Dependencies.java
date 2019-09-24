package org.ossmeter.metricprovider.trans.dependencies.model;

import com.mongodb.*;
import java.util.*;
import com.googlecode.pongo.runtime.*;
import com.googlecode.pongo.runtime.querying.*;


public class Dependencies extends Pongo {
	
	
	
	public Dependencies() { 
		super();
		DEPENDENCY.setOwningType("org.ossmeter.metricprovider.trans.dependencies.model.Dependencies");
		DISCOVERFILE.setOwningType("org.ossmeter.metricprovider.trans.dependencies.model.Dependencies");
	}
	
	public static StringQueryProducer DEPENDENCY = new StringQueryProducer("dependency"); 
	public static StringQueryProducer DISCOVERFILE = new StringQueryProducer("discoverFile"); 
	
	
	public String getDependency() {
		return parseString(dbObject.get("dependency")+"", "");
	}
	
	public Dependencies setDependency(String dependency) {
		dbObject.put("dependency", dependency);
		notifyChanged();
		return this;
	}
	public String getDiscoverFile() {
		return parseString(dbObject.get("discoverFile")+"", "");
	}
	
	public Dependencies setDiscoverFile(String discoverFile) {
		dbObject.put("discoverFile", discoverFile);
		notifyChanged();
		return this;
	}
	
	
	
	
}