package org.ossmeter.metricprovider.generic.numberofrequestsrepliesperday;

import java.util.Arrays;
import java.util.List;

import org.ossmeter.metricprovider.generic.numberofrequestsrepliesperday.model.DailyNewsgroupData;
import org.ossmeter.metricprovider.generic.numberofrequestsrepliesperday.model.DailyNorr;
import org.ossmeter.metricprovider.requestreplyclassification.RequestReplyClassificationMetricProvider;
import org.ossmeter.metricprovider.requestreplyclassification.model.NewsgroupArticlesData;
import org.ossmeter.metricprovider.requestreplyclassification.model.Rrc;
import org.ossmeter.platform.IHistoricalMetricProvider;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.repository.model.CommunicationChannel;
import org.ossmeter.repository.model.cc.nntp.NntpNewsGroup;
import org.ossmeter.repository.model.Project;

import com.googlecode.pongo.runtime.Pongo;

public class NumberOfRequestsRepliesPerDayProvider  implements IHistoricalMetricProvider{
	public final static String IDENTIFIER = 
			"org.ossmeter.metricprovider.generic.numberofrequestsrepliesperday";

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

		DailyNorr dailyNorr = new DailyNorr();
		int requests = 0, 
			 replies = 0;
		for (IMetricProvider used : uses) {
			Rrc usedRrc = ((RequestReplyClassificationMetricProvider)used).
							adapt(context.getProjectDB(project));
			for (NewsgroupArticlesData naData: usedRrc.getNewsgroupArticles()) {
				if (naData.getClassificationResult().equals("Request")) 
					requests++;
				else if (naData.getClassificationResult().equals("Reply")) 
					replies++;
				else {
					System.err.println("Classification result ( " + 
							naData.getClassificationResult() + 
									" ) should be either Request or Reply!");
				}
			}
			DailyNewsgroupData dailyNewsgroupData = new DailyNewsgroupData();
			dailyNewsgroupData.setNumberOfRequests(requests);
			dailyNewsgroupData.setNumberOfReplies(replies);
			dailyNorr.getNewsgroups().add(dailyNewsgroupData);
		}
		return dailyNorr;
	}
			
	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}
	
	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(RequestReplyClassificationMetricProvider.class.getCanonicalName());
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context = context;
	}

	@Override
	public String getShortIdentifier() {
		return "nrrpd";
	}

	@Override
	public String getFriendlyName() {
		return "Number Of Requests Replies Per Day";
	}

	@Override
	public String getSummaryInformation() {
		return "This class computes the number of request and reply newsgroup articles " +
				"per day.";
	}

}