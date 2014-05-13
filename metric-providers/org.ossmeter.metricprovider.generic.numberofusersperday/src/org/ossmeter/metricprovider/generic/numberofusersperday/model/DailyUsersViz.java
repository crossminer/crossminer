 
package org.ossmeter.metricprovider.generic.numberofusersperday.model;

import com.googlecode.pongo.runtime.viz.PongoViz;
import com.mongodb.DB;
import com.mongodb.Mongo;

public class DailyUsersViz extends PongoViz {

	public DailyUsersViz() {
		super();
	}
	
	public void setProjectDB(DB projectDB) {
		this.collection = projectDB.getCollection("org.ossmeter.metricprovider.generic.numberofusersperday");
	}


	@Override
	public String getViz(String type) {
		switch (type) {
			case "json":
				return ("{ 'id' : 'usersperday', 'name' : 'Users', 'type' : 'line', " +
						"'description' : 'A cumulative count of the number of unique users of the newsgroup. Uniqueness was determined by the author line of the newsgroup article.', " +
						"'xtext' : 'Date', 'ytext':'Users', 'orderRule' : 'Date', 'datatable' : " + createD3DataTable("newsgroups", "_id", "__date", "numberOfUsers", "Date", "Users", DateFilter.MONTH) + "," +
						"'isTimeSeries':true, 'lastValue': '"+getLastValue()+"' }").replaceAll("'", "\"");
			case "csv":
				return createCSVDataTable("newsgroups", "_id", "__date", "numberOfUsers", "Date", "NumberOfUsers", DateFilter.DAY);
			}	
		return null;
	}
	public static void main(String[] args) throws Exception {
		Mongo mongo = new Mongo();
		DB db = mongo.getDB("Subversion");
			
		DailyUsersViz viz = new DailyUsersViz();
		viz.setProjectDB(db);
		System.err.println(viz.getViz("d3"));
	}
}
