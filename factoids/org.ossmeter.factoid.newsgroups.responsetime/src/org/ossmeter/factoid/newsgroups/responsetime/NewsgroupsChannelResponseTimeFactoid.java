/*******************************************************************************
 * Copyright (c) 2014 OSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Yannis Korkontzelos - Implementation.
 *******************************************************************************/
package org.ossmeter.factoid.newsgroups.responsetime;

import java.util.Arrays;
import java.util.List;

import org.ossmeter.metricprovider.historic.newsgroups.responsetime.ResponseTimeHistoricMetricProvider;
import org.ossmeter.metricprovider.historic.newsgroups.responsetime.model.NewsgroupsResponseTimeHistoricMetric;
import org.ossmeter.platform.AbstractFactoidMetricProvider;
import org.ossmeter.platform.Date;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.delta.ProjectDelta;
import org.ossmeter.platform.factoids.Factoid;
import org.ossmeter.platform.factoids.StarRating;
import org.ossmeter.repository.model.CommunicationChannel;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.cc.nntp.NntpNewsGroup;

import com.googlecode.pongo.runtime.Pongo;

public class NewsgroupsChannelResponseTimeFactoid extends AbstractFactoidMetricProvider{

	protected List<IMetricProvider> uses;
	
	@Override
	public String getShortIdentifier() {
		return "NewsgroupChannelResponseTime";
	}

	@Override
	public String getFriendlyName() {
		return "Newsgroup Channel Response Time";
		// This method will NOT be removed in a later version.
	}

	@Override
	public String getSummaryInformation() {
		return "summaryblah"; // This method will NOT be removed in a later version.
	}

	@Override
	public boolean appliesTo(Project project) {
		for (CommunicationChannel communicationChannel: project.getCommunicationChannels()) {
			if (communicationChannel instanceof NntpNewsGroup) return true;
		}
		return false;
	}

	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(ResponseTimeHistoricMetricProvider.IDENTIFIER);
	}

	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}

	@Override
	public void measureImpl(Project project, ProjectDelta delta, Factoid factoid) {
//		factoid.setCategory(FactoidCategory.BUGS);
		factoid.setName(getFriendlyName());

		ResponseTimeHistoricMetricProvider responseTimeProvider = null;

		for (IMetricProvider m : this.uses) {
			if (m instanceof ResponseTimeHistoricMetricProvider) {
				responseTimeProvider = (ResponseTimeHistoricMetricProvider) m;
				continue;
			}
		}
		
		int eightHoursMilliSeconds = 8 * 60 * 60 * 1000, 
			dayMilliSeconds = 3 * eightHoursMilliSeconds,
			weekMilliSeconds = 7 * dayMilliSeconds;
		
		Date end = new Date();
		Date start = (new Date()).addDays(-365);
//		Date start=null, end=null;
//		try {
//			start = new Date("20040801");
//			end = new Date("20050801");
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		List<Pongo> responseTimeList = responseTimeProvider.getHistoricalMeasurements(context, project, start, end);
//					severityResponseTimeList = severityResponseTimeProvider.getHistoricalMeasurements(context, project, start, end);
		
		long cumulativeAvgResponseTime = 0,
				yearlyAvgResponseTime = 0;

		if (responseTimeList.size() > 0) {
			NewsgroupsResponseTimeHistoricMetric responseTimeMetric = 
					(NewsgroupsResponseTimeHistoricMetric) responseTimeList.get(responseTimeList.size() - 1);
			cumulativeAvgResponseTime = responseTimeMetric.getCumulativeAvgResponseTime();
			yearlyAvgResponseTime = getYearlyAvgResponseTime(responseTimeList);
		}

		if ( ( yearlyAvgResponseTime > 0 ) && ( yearlyAvgResponseTime < eightHoursMilliSeconds ) ) {
			factoid.setStars(StarRating.FOUR);
		} else if ( ( yearlyAvgResponseTime > 0 ) && ( yearlyAvgResponseTime < dayMilliSeconds ) ) {
			factoid.setStars(StarRating.THREE);
		} else if ( ( yearlyAvgResponseTime > 0 ) && ( yearlyAvgResponseTime < weekMilliSeconds ) ) {
			factoid.setStars(StarRating.TWO);
		} else {
			factoid.setStars(StarRating.ONE);
		}
		
		StringBuffer stringBuffer = new StringBuffer();
		
		stringBuffer.append("Considering the whole duration of the project, " +
							"requests receive a first response ");
		if ( cumulativeAvgResponseTime < eightHoursMilliSeconds ) {
			stringBuffer.append("very");
		} else if ( cumulativeAvgResponseTime < dayMilliSeconds ) {
			stringBuffer.append("");
		} else if ( cumulativeAvgResponseTime < weekMilliSeconds ) {
			stringBuffer.append("fairly");
		} else
			stringBuffer.append("not so");
		stringBuffer.append(" quickly.\n");

		stringBuffer.append("Lately, requests receive a first response ");
		if ( yearlyAvgResponseTime < eightHoursMilliSeconds ) {
			stringBuffer.append("very");
		} else if ( yearlyAvgResponseTime < dayMilliSeconds ) {
			stringBuffer.append("");
		} else if ( yearlyAvgResponseTime < weekMilliSeconds ) {
			stringBuffer.append("fairly");
		} else
			stringBuffer.append("not so");
		stringBuffer.append(" quickly.\n");
		
		stringBuffer.append("Response speed is lately ");
		if ( Math.abs(cumulativeAvgResponseTime-yearlyAvgResponseTime) < eightHoursMilliSeconds )
			stringBuffer.append("approximately equal");
		else if ( cumulativeAvgResponseTime > yearlyAvgResponseTime )
			stringBuffer.append("better");
		else 
			stringBuffer.append("worse");
		stringBuffer.append(" than the overall average for the project.\n");

		factoid.setFactoid(stringBuffer.toString());

	}
	
	private long getYearlyAvgResponseTime(List<Pongo> responseTimeList) {
		long totalResponseTime = 0;
		int totalThreadsConsidered = 0;
		for (Pongo pongo: responseTimeList) {
			NewsgroupsResponseTimeHistoricMetric responseTimePongo = 
					(NewsgroupsResponseTimeHistoricMetric) pongo;
			totalThreadsConsidered += responseTimePongo.getThreadsConsidered();
			totalResponseTime += responseTimePongo.getAvgResponseTime();
		}
		if (totalThreadsConsidered>0)
			return totalResponseTime / totalThreadsConsidered;
		else
			return 0;
	}

}
