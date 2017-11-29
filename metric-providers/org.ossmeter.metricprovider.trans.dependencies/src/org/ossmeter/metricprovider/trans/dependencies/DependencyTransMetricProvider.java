package org.ossmeter.metricprovider.trans.dependencies;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.repository.DefaultMirrorSelector;
import org.eclipse.aether.util.repository.DefaultProxySelector;
import org.ossmeter.metricprovider.trans.dependencies.model.Container;
import org.ossmeter.metricprovider.trans.dependencies.model.Dependencies;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.ITransientMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.platform.Platform;
import org.ossmeter.platform.delta.ProjectDelta;
import org.ossmeter.platform.delta.vcs.IVcsManager;
import org.ossmeter.platform.logging.OssmeterLogger;
import org.ossmeter.platform.vcs.workingcopy.manager.WorkingCopyCheckoutException;
import org.ossmeter.platform.vcs.workingcopy.manager.WorkingCopyFactory;
import org.ossmeter.platform.vcs.workingcopy.manager.WorkingCopyManagerUnavailable;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.VcsRepository;

import com.mongodb.DB;

public class DependencyTransMetricProvider implements ITransientMetricProvider<Container> {

	protected OssmeterLogger logger;
	protected MetricProviderContext context;
	private static String GRADLE_FILENAME = "build.gradle";
	private static final String POM_FILENAME = "pom.xml";
	
	public DependencyTransMetricProvider() {
		logger = (OssmeterLogger) OssmeterLogger.getLogger("metricprovider.trans.depedenciies.DependecyTransMetricProvider");
	}
	@Override
	public String getIdentifier() {
		return DependencyTransMetricProvider.class.getCanonicalName();
	}

	@Override
	public String getShortIdentifier() {
		return "dependencies";
	}

	@Override
	public String getFriendlyName() {
		return "dependencies";
	}

	@Override
	public String getSummaryInformation() {
		return null;
	}

	@Override
	public boolean appliesTo(Project project) {
		if(project.getVcsRepositories().isEmpty())
			return false;
		else return true;
	}

	@Override
	public void setUses(List<IMetricProvider> uses) {

	}

	@Override
	public List<String> getIdentifiersOfUses() {
		return Collections.emptyList();
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		
	}

	@Override
	public Container adapt(DB db) {
		return new Container(db);
	}

	@Override
	public void measure(Project project, ProjectDelta delta, Container db) {
		try {
			db.getDependencies().getDbCollection().drop();
			db.sync();
			
			List<IVcsManager> d = Platform.getInstance().getVcsManager().getVcsManagers();
			for (IVcsManager iVcsManager : d) {
				for (VcsRepository vcsRepo : project.getVcsRepositories()) {
					if(iVcsManager.appliesTo(vcsRepo)){
						String s = iVcsManager.getCurrentRevision(vcsRepo);
						HashMap<String, File> wc = new HashMap<>();
						HashMap<String, File> sc = new HashMap<>();
						WorkingCopyFactory.getInstance().checkout(project, s, wc, sc);
						File f = wc.get(vcsRepo.getUrl());
						try{
							List<File> gradleFiles = getFilesByFileName(f, GRADLE_FILENAME);
							List<String> gradleDependencies = getGradleDependencies(gradleFiles);
							for (String gradleDepemdecy : gradleDependencies) {
								Dependencies dependency = new Dependencies();
								dependency.setDependency(gradleDepemdecy);
								dependency.setDiscoverFile(GRADLE_FILENAME);
								db.getDependencies().add(dependency);
							}
						} catch(Exception e){
							logger.error("importing gradle dependencies");
						}
						try {
							List<File> pomFiles = getFilesByFileName(f, POM_FILENAME);
							List<String> pomDependencies = getMavenDependencies(pomFiles);
							for (String pomDepemdecy : pomDependencies) {
								Dependencies dependency = new Dependencies();
								dependency.setDependency(pomDepemdecy);
								dependency.setDiscoverFile(POM_FILENAME);
								db.getDependencies().add(dependency);
							}
						} catch (Exception e) {
							logger.error("importing pom dependencies");
						}
						db.sync();
					}
				}
			}
		} catch (WorkingCopyManagerUnavailable | WorkingCopyCheckoutException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}

	
	private List<String> getGradleDependencies(List<File> gradlePath)  {
		List<String> result = new ArrayList<>();
		for (File gradleFile : gradlePath) {
			try {
				String gradleFileContents = new String(Files.readAllBytes(Paths.get(gradleFile.getPath())));
				
				AstBuilder builder = new AstBuilder();
			    List<ASTNode> nodes = builder.buildFromString(gradleFileContents);
			    DependencyVisitor visitor = new DependencyVisitor();
		        walkScript(visitor, nodes );
		        
		        List<String> partialResult = visitor.getDependencies();
		        if(partialResult!=null)
		        	result.addAll(partialResult);
			} catch (Exception e) {logger.error(e.getMessage());}
			
		}
		return result;
	}
	
	private void walkScript(GroovyCodeVisitor visitor, List<ASTNode> nodes)
    {
        for( ASTNode node : nodes )
        {
            node.visit( visitor );
        }
    }
	
	private  List<File> getFilesByFileName(File rootFolder, String fileName) {
		List<File> result = new ArrayList<File>();
		if (rootFolder.isDirectory())
			result.addAll(Arrays.asList(rootFolder.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					File f = new File(dir.getAbsolutePath()+ "/" + filename);
					return filename.toLowerCase().equals(fileName) && !f.isDirectory();
				}
			})));
		List<File> listFolder = Arrays.asList(rootFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				File f = new File(dir.getAbsolutePath() + File.separator + filename);
				return f.isDirectory();
			}
		}));
		for (File file : listFolder) {
			result.addAll(getFilesByFileName(file, fileName));
		}
		return result;
	}
	
	/*
	 * getDependencies takes several path to pom files
	 */
	private List<String> getMavenDependencies(List<File> pomFiles) {
		List<String> result = new ArrayList<>();
		for (File pomFile : pomFiles) {
			try {

				String pomFileContents = new String(Files.readAllBytes(Paths.get(pomFile.getPath())));
				MavenXpp3Reader reader = new MavenXpp3Reader();
				Model model = reader.read(new StringReader(pomFileContents));

				for (Dependency repositoryContents : model.getDependencies()) {
					result.add(repositoryContents.getGroupId() + ":" + repositoryContents.getArtifactId());

				}
				if (model.getDependencyManagement() != null)
					for (Dependency repositoryContents : model.getDependencyManagement().getDependencies()) {
						result.add(repositoryContents.getGroupId() + ":" + repositoryContents.getArtifactId());
					}
				if (model.getParent() != null) {
					String parent = model.getParent().getGroupId() + ":" + model.getParent().getArtifactId() + ":"
							+ model.getParent().getVersion();
					try {
						List<String> deps;
						deps = getMavenParentDependencies(parent);
						result.addAll(deps);
					} catch (DependencyResolutionException | ArtifactDescriptorException e) {
						logger.error(e.getMessage());
					}
				}
			} catch (XmlPullParserException | IOException e) {
				logger.error(e.getMessage());
			} 
		}
		return result;
	}

	private List<String> getMavenParentDependencies(String parent)
			throws DependencyResolutionException, ArtifactDescriptorException {
		List<String> dependencies = new ArrayList<>();
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		RepositorySystem system = newRepositorySystem(locator);
		RepositorySystemSession session = newSession(system);

		RemoteRepository central = new RemoteRepository.Builder("central", "default", "http://repo1.maven.org/maven2/")
				.build();

		org.eclipse.aether.artifact.Artifact artifact = new DefaultArtifact(parent);
		ArtifactDescriptorRequest request = new ArtifactDescriptorRequest(artifact, Arrays.asList(central), null);
		try {
			ArtifactDescriptorResult result = system.readArtifactDescriptor(session, request);
			for (org.eclipse.aether.graph.Dependency dependency : result.getManagedDependencies()) {
				dependencies.add(dependency.getArtifact().getGroupId() + ":" + dependency.getArtifact().getGroupId());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return dependencies;

	}

	/*
	 * newSession: relates to pom files and depedecieOs
	 */
	private static RepositorySystemSession newSession(RepositorySystem system) {
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
		LocalRepository localRepo = new LocalRepository("target/local-repo");
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
		// set possible proxies and mirrors
		session.setProxySelector(new DefaultProxySelector().add(new Proxy(Proxy.TYPE_HTTP, "host", 3625),
				Arrays.asList("localhost", "127.0.0.1")));
		session.setMirrorSelector(
				new DefaultMirrorSelector().add("my-mirror", "http://mirror", "default", false, "external:*", null));
		return session;
	}

	/*
	 * newSession: relates to pom files and depedecies
	 */
	private static RepositorySystem newRepositorySystem(DefaultServiceLocator locator) {
		locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
		locator.addService(TransporterFactory.class, FileTransporterFactory.class);
		locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
		return locator.getService(RepositorySystem.class);
	}
}
