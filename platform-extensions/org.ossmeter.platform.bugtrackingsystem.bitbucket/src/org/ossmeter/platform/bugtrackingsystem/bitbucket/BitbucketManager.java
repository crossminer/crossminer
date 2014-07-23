package org.ossmeter.platform.bugtrackingsystem.bitbucket;

import org.apache.commons.lang.time.DateUtils;
import org.ossmeter.platform.Date;
import org.ossmeter.platform.bugtrackingsystem.bitbucket.api.BitbucketIssue;
import org.ossmeter.platform.bugtrackingsystem.bitbucket.api.BitbucketIssueComment;
import org.ossmeter.platform.bugtrackingsystem.bitbucket.api.BitbucketIssueQuery;
import org.ossmeter.platform.bugtrackingsystem.bitbucket.api.BitbucketPullRequest;
import org.ossmeter.platform.bugtrackingsystem.bitbucket.api.BitbucketRestClient;
import org.ossmeter.platform.bugtrackingsystem.bitbucket.api.BitbucketSearchResult;
import org.ossmeter.platform.bugtrackingsystem.cache.Cache;
import org.ossmeter.platform.bugtrackingsystem.cache.Caches;
import org.ossmeter.platform.delta.bugtrackingsystem.BugTrackingSystemBug;
import org.ossmeter.platform.delta.bugtrackingsystem.BugTrackingSystemComment;
import org.ossmeter.platform.delta.bugtrackingsystem.BugTrackingSystemDelta;
import org.ossmeter.platform.delta.bugtrackingsystem.IBugTrackingSystemManager;
import org.ossmeter.repository.model.BugTrackingSystem;
import org.ossmeter.repository.model.bitbucket.BitbucketBugTrackingSystem;

public class BitbucketManager implements
		IBugTrackingSystemManager<BitbucketBugTrackingSystem> {

	private Caches<BitbucketIssue, String> issueCaches = new Caches<BitbucketIssue, String>(
			new IssueCacheProvider());

	private Caches<BitbucketPullRequest, Long> pullRequestCaches = new Caches<BitbucketPullRequest, Long>(
			new PullRequestCacheProvider());

	@Override
	public boolean appliesTo(BugTrackingSystem bugTracker) {
		return bugTracker instanceof BitbucketBugTrackingSystem;
	}

	@Override
	public BugTrackingSystemDelta getDelta(
			BitbucketBugTrackingSystem bugTracker, Date date) throws Exception {

		java.util.Date day = date.toJavaDate();

		Cache<BitbucketIssue, String> issuesCache = issueCaches.getCache(
				bugTracker, true);
		Iterable<BitbucketIssue> issues = issuesCache.getItemsAfterDate(day);

		BitbucketBugTrackingSystemDelta delta = new BitbucketBugTrackingSystemDelta();

		// Process issues and comments
		for (BitbucketIssue issue : issues) {

			if (DateUtils.isSameDay(issue.getUpdateDate(), day)) {
				delta.getUpdatedBugs().add(issue);
			} else if (DateUtils.isSameDay(issue.getCreationTime(), day)) {
				delta.getNewBugs().add(issue);
			}

			// Store updated comments in delta
			for (BugTrackingSystemComment comment : issue.getComments()) {
				BitbucketIssueComment jiraComment = (BitbucketIssueComment) comment;

				java.util.Date updated = jiraComment.getUpdateDate();
				java.util.Date created = jiraComment.getCreationTime();

				if (DateUtils.isSameDay(created, day)) {
					delta.getComments().add(comment);
				} else if (updated != null && DateUtils.isSameDay(updated, day)) {
					delta.getComments().add(comment);
				}
			}
		}
		
		// Process pull requests
		Cache<BitbucketPullRequest, Long> pullRequestsCache = pullRequestCaches.getCache(bugTracker, true);
		Iterable<BitbucketPullRequest> pullRequests = pullRequestsCache.getItemsOnDate(day);
		for (BitbucketPullRequest pullRequest: pullRequests) {
			delta.getPullRequests().add(pullRequest);
		}

		return delta;
	}

	@Override
	public Date getFirstDate(BitbucketBugTrackingSystem bugTracker)
			throws Exception {
		BitbucketIssueQuery query = new BitbucketIssueQuery(
				bugTracker.getUser(), bugTracker.getRepository());
		query = query.setSort("utc_created_on");
		BitbucketRestClient bitbucket = getBitbucketRestClient(bugTracker);
		BitbucketSearchResult result = bitbucket.search(query, false, 0, 1);
		if (result.getIssues().size() > 0) {
			return new Date(result.getIssues().get(0).getCreationTime());
		}
		return null;
	}

	@Override
	public String getContents(BitbucketBugTrackingSystem bugTracker,
			BugTrackingSystemBug bug) throws Exception {
		BitbucketRestClient bitbucket = getBitbucketRestClient(bugTracker);
		BitbucketIssue issue = bitbucket.getIssue(bugTracker.getUser(),
				bugTracker.getRepository(), bug.getBugId(), false);
		if (null != issue) {
			return issue.getContent();
		}
		return null;
	}

	@Override
	public String getContents(BitbucketBugTrackingSystem bugTracker,
			BugTrackingSystemComment comment) throws Exception {
		BitbucketRestClient bitbucket = getBitbucketRestClient(bugTracker);
		BitbucketIssueComment bitbucketComment = bitbucket.getIssueComment(
				bugTracker.getUser(), bugTracker.getRepository(),
				comment.getBugId(), comment.getCommentId());
		if (null != bitbucketComment) {
			return bitbucketComment.getText();
		}
		return null;
	}

	protected static BitbucketRestClient getBitbucketRestClient(
			BitbucketBugTrackingSystem bugTracker) {
		BitbucketRestClient client = new BitbucketRestClient();
		String login = bugTracker.getLogin();
		if (login != null && login.trim().length() > 0 && !"null".equals(login)) {
			client.setCredentials(login, bugTracker.getPassword());
		}
		return client;
	}

	public static void main(String[] args) throws Exception {
		BitbucketBugTrackingSystem bts = new BitbucketBugTrackingSystem();
		bts.setUser("jmurty");
		bts.setRepository("jets3t");
		BitbucketManager manager = new BitbucketManager();

		Date firstDate = manager.getFirstDate(bts);
		System.out.println(firstDate);

		BitbucketIssue issue = new BitbucketIssue();
		issue.setBugId("189");
		System.out.println(manager.getContents(bts, issue));

		BitbucketIssueComment comment = new BitbucketIssueComment();
		comment.setBugId("189");
		comment.setCommentId("11288353");
		System.out.println(manager.getContents(bts, comment));

		BugTrackingSystemDelta delta = manager.getDelta(bts, new Date(
				"20140626"));
		System.out.println(delta.getUpdatedBugs().size());
	}

}
