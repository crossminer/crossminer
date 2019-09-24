package org.ossmeter.metricprovider.trans.dependencies.model;

import com.googlecode.pongo.runtime.*;
import java.util.*;
import com.mongodb.*;

public class DependenciesCollection extends PongoCollection<Dependencies> {
	
	public DependenciesCollection(DBCollection dbCollection) {
		super(dbCollection);
	}
	
	public Iterable<Dependencies> findById(String id) {
		return new IteratorIterable<Dependencies>(new PongoCursorIterator<Dependencies>(this, dbCollection.find(new BasicDBObject("_id", id))));
	}
	
	
	@Override
	public Iterator<Dependencies> iterator() {
		return new PongoCursorIterator<Dependencies>(this, dbCollection.find());
	}
	
	public void add(Dependencies dependencies) {
		super.add(dependencies);
	}
	
	public void remove(Dependencies dependencies) {
		super.remove(dependencies);
	}
	
}