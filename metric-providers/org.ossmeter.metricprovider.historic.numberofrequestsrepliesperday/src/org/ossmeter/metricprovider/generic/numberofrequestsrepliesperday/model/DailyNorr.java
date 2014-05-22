package org.ossmeter.metricprovider.generic.numberofrequestsrepliesperday.model;

import com.mongodb.*;
import java.util.*;
import com.googlecode.pongo.runtime.*;


public class DailyNorr extends Pongo {
	
	protected List<DailyNewsgroupRequestsData> requests = null;
	protected List<DailyNewsgroupRepliesData> replies = null;
	
	
	public DailyNorr() { 
		super();
		dbObject.put("requests", new BasicDBList());
		dbObject.put("replies", new BasicDBList());
	}
	
	
	
	
	
	public List<DailyNewsgroupRequestsData> getRequests() {
		if (requests == null) {
			requests = new PongoList<DailyNewsgroupRequestsData>(this, "requests", true);
		}
		return requests;
	}
	public List<DailyNewsgroupRepliesData> getReplies() {
		if (replies == null) {
			replies = new PongoList<DailyNewsgroupRepliesData>(this, "replies", true);
		}
		return replies;
	}
	
	
}