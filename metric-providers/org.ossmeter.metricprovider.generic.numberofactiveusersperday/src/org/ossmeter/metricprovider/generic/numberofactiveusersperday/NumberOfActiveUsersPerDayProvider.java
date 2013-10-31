package org.ossmeter.metricprovider.generic.numberofactiveusersperday;

import java.util.Arrays;
import java.util.List;

import org.ossmeter.metricprovider.activeusers.ActiveUsersMetricProvider;
import org.ossmeter.metricprovider.activeusers.model.ActiveUsers;
import org.ossmeter.metricprovider.activeusers.model.NewsgroupData;
import org.ossmeter.metricprovider.generic.numberofactiveusersperday.model.DailyActiveUsers;
import org.ossmeter.metricprovider.generic.numberofactiveusersperday.model.DailyNewsgroupData;
import org.ossmeter.platform.IHistoricalMetricProvider;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.repository.model.CommunicationChannel;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.cc.nntp.NntpNewsGroup;

import com.googlecode.pongo.runtime.Pongo;

public class NumberOfActiveUsersPerDayProvider implements IHistoricalMetricProvider{

	public final static String IDENTIFIER = 
			"org.ossmeter.metricprovider.generic.numberofactiveusersperday";

	protected MetricProviderContext context;
	
	/**
	 * List of MPs that are used by this MP. These are MPs who have specified that 
	 * they 'provide' data for this MP.
	 */
	protected List<IMetricProvider> uses;
	
	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}
	
	@Override
	public boolean appliesTo(Project project) {
		for (CommunicationChannel communicationChannel: project.getCommunicationChannels()) {
			if (communicationChannel instanceof NntpNewsGroup) return true;
		}
		return false;
	}

	@Override
	public Pongo measure(Project project) {

		 DailyActiveUsers dailyActiveUsers = new DailyActiveUsers();
		for (IMetricProvider used : uses) {
			ActiveUsers ActiveUsers = ((ActiveUsersMetricProvider)used).adapt(context.getProjectDB(project));
			int activeUsers = 0;
			for (NewsgroupData newsgroup: ActiveUsers.getNewsgroups())
				activeUsers += newsgroup.getNumberOfActiveUsers();
			if (activeUsers > 0) {
				DailyNewsgroupData dailyNewsgroupData = new DailyNewsgroupData();
				dailyNewsgroupData.setNumberOfActiveUsers(activeUsers);
				dailyActiveUsers.getNewsgroups().add(dailyNewsgroupData);
			}
		}
		return dailyActiveUsers;
	}
			
	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}
	
	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(ActiveUsersMetricProvider.class.getCanonicalName());
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context = context;
	}

	@Override
	public String getShortIdentifier() {
		return "activeusers";
	}

	@Override
	public String getFriendlyName() {
		return "Number Of ActiveUsers Per Day Provider";
	}

	@Override
	public String getSummaryInformation() {
		return "This metric computes the number of active users per day.";
	}
}
