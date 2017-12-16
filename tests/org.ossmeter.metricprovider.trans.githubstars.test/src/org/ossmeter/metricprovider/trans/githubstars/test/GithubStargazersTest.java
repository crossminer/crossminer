package org.ossmeter.metricprovider.trans.githubstars.test;

import static org.junit.Assert.*;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ossmeter.metricprovider.trans.githubstars.StarsTransientMetricProvider;
import org.ossmeter.metricprovider.trans.githubstars.model.Stars;
import org.ossmeter.platform.Date;
import org.ossmeter.platform.Platform;
import org.ossmeter.platform.osgi.executors.ProjectExecutor;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.github.GitHubRepository;

import com.googlecode.pongo.runtime.PongoFactory;
import com.googlecode.pongo.runtime.osgi.OsgiPongoFactoryContributor;
import com.mongodb.Mongo;

public class GithubStargazersTest {
	
	protected static Platform platform;
	protected static Mongo mongo;
	
	@BeforeClass
	public static void setup() throws Exception {
		PongoFactory.getInstance().getContributors().add(new OsgiPongoFactoryContributor());
		
		mongo = new Mongo();
		platform = new Platform(mongo);
		GitHubRepository project = new GitHubRepository();
		project.setName("hierarchical-clustering-java");
		project.setShortName("hierarchical-clustering-java");
		project.setFull_name("md2manoppello/hierarchical-clustering-java");
		String startDate = new Date().addDays(-2).toString();
		project.getExecutionInformation().setLastExecuted(startDate);
		
		platform.getProjectRepositoryManager().getProjectRepository().getProjects().add(project);
		platform.getProjectRepositoryManager().getProjectRepository().getProjects().sync();
	}
	
	@AfterClass
	public static void closedown() {
		
		Project project = platform.getProjectRepositoryManager().getProjectRepository().getProjects().findOneByName("hierarchical-clustering-java");
		platform.getProjectRepositoryManager().getProjectRepository().getProjects().remove(project);
		platform.getProjectRepositoryManager().getProjectRepository().getProjects().sync();
		
		mongo.close();
	}
	
	
	@Test
	public void testLastExecutedDate() throws Exception {
		
		ManualRegistrationMetricProviderManager metricProviderManager = new ManualRegistrationMetricProviderManager();
		StarsTransientMetricProvider smp = new StarsTransientMetricProvider();
		metricProviderManager.addMetricProvider(smp);
		platform.setMetricProviderManager(metricProviderManager);
		assertEquals(1,platform.getMetricProviderManager().getMetricProviders().size());
		
		Project project = platform.getProjectRepositoryManager().getProjectRepository().getProjects().findOneByName("hierarchical-clustering-java");
		assertNotNull(project);
		Stars starOrm = smp.adapt(platform.getMetricsRepository(project).getDb());
		ProjectExecutor pe = new ProjectExecutor(platform, project);
		pe.run();
		assertEquals(starOrm.getStargazers().size(), 1);
		 
	}
	
	
}
