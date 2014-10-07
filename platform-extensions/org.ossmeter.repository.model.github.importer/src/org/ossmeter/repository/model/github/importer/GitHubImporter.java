package org.ossmeter.repository.model.github.importer;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.ossmeter.platform.Platform;
import org.ossmeter.platform.logging.OssmeterLogger;
import org.ossmeter.repository.model.ImportData;
import org.ossmeter.repository.model.Person;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.Role;
import org.ossmeter.repository.model.github.GitHubRepository;
import org.ossmeter.repository.model.github.GitHubUser;

import com.mongodb.Mongo;


public class GitHubImporter {

	private String authToken;
	private String authString;
	protected OssmeterLogger logger;
	public GitHubImporter() {
		this.authToken = getToken();
		logger = (OssmeterLogger) OssmeterLogger.getLogger("importer.gitHub");
		logger.addConsoleAppender(OssmeterLogger.DEFAULT_PATTERN);
		if (authToken != null)
			authString = "?access_token=" + authToken;
		else 
			authString = "";
/*		GitHubClient gitHubClient = new GitHubClient();
		gitHubClient.setOAuth2Token("ffab283e2be3265c7b0af244e474b28430351973");
		repositoryService = new RepositoryService();
		userService = new UserService();
		collaboratorService = new CollaboratorService();
		watcherService = new WatcherService();
*/	
	}
	private static String getToken(){
		try {
    		Properties forgeProperties = new Properties();
			forgeProperties.load(new FileReader("github_auth.properties"));
			return forgeProperties.getProperty("token");
			
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		
	}
	private int getRemainingResource()
	{
		try {
			String url = "https://api.github.com/rate_limit" + authString;
			InputStream is;
			is = new URL(url).openStream();
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));		
			String jsonText = readAll(rd);
	
			JSONObject obj=(JSONObject)JSONValue.parse(jsonText);
			JSONObject rate = (JSONObject)((JSONObject)obj.get("rate"));
			//logger.debug("attempt to");
			Integer remaining = new Integer(rate.get("remaining").toString());
			return remaining;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("Get remaining api exception");
			return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Get remaining api exception");
			return 0;
		}
	}
	
	public void waitApiRate(){
		
		String url = "https://api.github.com/rate_limit" + authString;
		boolean sleep = true;
		logger.info("API rate limit exceeded. Waiting to restart the importing...");
		while (sleep) {
			try {
					InputStream is = new URL(url).openStream();
					BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));		
					String jsonText = readAll(rd);
	
					JSONObject obj=(JSONObject)JSONValue.parse(jsonText);
					JSONObject rate = (JSONObject)((JSONObject)obj.get("rate"));
					Integer remaining = new Integer(rate.get("remaining").toString());
					if (remaining > 0) {
						sleep = false;
					}
				
			} catch (IOException e) {
				logger.error("Having difficulties to connect, retrying...");
				continue;
			}		
		}	
	}

	
	private boolean isNotNull(JSONObject currentRepo, String attribute ) 
	{
		if (currentRepo.get(attribute) == null)
			return false;
		else 
			return true;		
	}
	
	
	
	private static String readAll(Reader rd) throws IOException 
	{
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	
	public void importAll(Platform platform){
	
		int lastImportedId = 0;
		InputStream is = null;
		BufferedReader rd = null;
		String jsonText = null;
		boolean stop = false;
		
		int firstId = 0;
		int lastId = 0;


		if (platform.getProjectRepositoryManager().getProjectRepository().getGitHubImportData().size() != 0) {
			lastImportedId = new Integer(platform.getProjectRepositoryManager().getProjectRepository().getGitHubImportData().first().getLastImportedProject());
		} else {
			ImportData id = new ImportData();
			id.setLastImportedProject(String.valueOf(lastImportedId));
			platform.getProjectRepositoryManager().getProjectRepository().getGitHubImportData().add(id);	
			platform.getProjectRepositoryManager().getProjectRepository().sync();
		}

		while (!stop) {
			try {
				if (getRemainingResource()==0)
					waitApiRate();
				is = new URL("https://api.github.com/repositories?since=" + lastImportedId + "&access_token=" + this.authToken).openStream();		
				rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
				jsonText = readAll(rd);			
			} catch (IOException e) {
				logger.error("API rate limit exceeded. Waiting to restart the importing...");
//				platform.getProjectRepositoryManager().getProjectRepository().getGitHubImportingData().first().setLastImportedProject(String.valueOf(lastImportedId));
//				platform.getProjectRepositoryManager().getProjectRepository().sync();
				waitApiRate();
				break;				
			}
			
			JSONArray obj=(JSONArray)JSONValue.parse(jsonText);
			if (! obj.isEmpty()) {
				firstId = new Integer((((JSONObject)(obj.get(0))).get("id")).toString());
				lastId = new Integer((((JSONObject)(obj.get(obj.size()-1))).get("id")).toString());
				logger.info("Scanning page: " + "https://api.github.com/repositories?since=" + lastImportedId);
				logger.info("--> Importing repositories from id:" + firstId + " to id:" + lastId);
				platform.getProjectRepositoryManager().getProjectRepository().getGitHubImportData().first().setLastImportedProject(String.valueOf(firstId-1));
				platform.getProjectRepositoryManager().getProjectRepository().sync();	
				
				Iterator iter = obj.iterator();
				while (iter.hasNext()) {
					JSONObject entry = (JSONObject) iter.next();		
					GitHubRepository repository = importRepository((String) entry.get("full_name"), platform);
				}
				lastImportedId = lastId;
			} else {
				stop = true;
				logger.info("Importing completed.");
				break;
			}
		}
	}
	
	public void importProjects(Platform platform, int numberOfProjects){
		int lastImportedId = 1;
		InputStream is = null;
		BufferedReader rd = null;
		String jsonText = null;
		boolean stop = false;
		int firstId = 0;
		int lastId = 0;
		try
		{
			if (platform.getProjectRepositoryManager().getProjectRepository().getGitHubImportData().size() != 0) {
				lastImportedId = new Integer(platform.getProjectRepositoryManager().getProjectRepository().getGitHubImportData().first().getLastImportedProject());
			} else {
				ImportData id = new ImportData();
				id.setLastImportedProject(String.valueOf(lastImportedId));
				platform.getProjectRepositoryManager().getProjectRepository().getGitHubImportData().add(id);	
				platform.getProjectRepositoryManager().getProjectRepository().sync();
			}			
		}
		catch (Exception e)
		{
			ImportData id = new ImportData();
			id.setLastImportedProject(String.valueOf(lastImportedId));
			platform.getProjectRepositoryManager().getProjectRepository().getGitHubImportData().add(id);	
			platform.getProjectRepositoryManager().getProjectRepository().sync();
		}
		int iteration  = 0;
		while (!stop) {
			try {
				if (getRemainingResource()==0)
					waitApiRate();
				is = new URL("https://api.github.com/repositories?since=" + lastImportedId + authString).openStream();		
				rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
				jsonText = readAll(rd);			
				JSONArray obj=(JSONArray)JSONValue.parse(jsonText);
				if (! obj.isEmpty()) {
					firstId = new Integer((((JSONObject)(obj.get(0))).get("id")).toString());
					lastId = new Integer((((JSONObject)(obj.get(obj.size()-1))).get("id")).toString());
					logger.info("Scanning page: " + "https://api.github.com/repositories?since=" + lastImportedId);
					logger.info("--> Importing repositories from id:" + firstId + " to id:" + lastId);
					platform.getProjectRepositoryManager().getProjectRepository().getGitHubImportData().first().setLastImportedProject(String.valueOf(firstId-1));
					platform.getProjectRepositoryManager().getProjectRepository().sync();	
					
					Iterator iter = obj.iterator();
					while (iter.hasNext()) {
						JSONObject entry = (JSONObject) iter.next();		
						GitHubRepository repository = importRepository((String) entry.get("full_name"), platform);
						iteration++;
						if (iteration > numberOfProjects)
							break;
					}
					lastImportedId = lastId;
					if (iteration > numberOfProjects)
						break;
				} else {
					stop = true;
					logger.info("Importing completed.");
					break;
				}
			} catch (IOException e) {
				logger.error("API rate limit exceeded. Waiting to restart the importing...");
//				platform.getProjectRepositoryManager().getProjectRepository().getGitHubImportingData().first().setLastImportedProject(String.valueOf(lastImportedId));
//				platform.getProjectRepositoryManager().getProjectRepository().sync();
				waitApiRate();				
			}	
		}
	}
	
	
	public GitHubRepository importRepository(String projectId, Platform platform) {
		try {
//				int lastImportedId = new Integer(platform.getProjectRepositoryManager().getProjectRepository().getGitHubImportData().first().getLastImportedProject());

				GitHubRepository repository = null;
				
				JSONObject currentRepo = null;
				Boolean projectToBeUpdated = false;
				logger.info("---> processing repository " + projectId);
				InputStream is = new URL("https://api.github.com/repos/" + projectId + authString).openStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
				String jsonText = readAll(rd);
			     		
				currentRepo=(JSONObject)JSONValue.parse(jsonText);

				Iterable<Project> projects = platform.getProjectRepositoryManager().getProjectRepository().getProjects().findByName(currentRepo.get("name").toString());		

				Iterator<Project> ip = projects.iterator();
				Project p = null;
				while (ip.hasNext()) {
					p = ip.next();				
					if (p instanceof GitHubRepository) {
						repository = (GitHubRepository)p;
						if (repository.getFull_name().equals(projectId)) {
							logger.info("-----> repository " + repository.getFull_name() + " already in the repository. Its metadata will be updated.");
							projectToBeUpdated = true;
							break;
						}
					}
				}
					
				if (!projectToBeUpdated)  {
					repository = new GitHubRepository();
				}
								
				
				if ((isNotNull(currentRepo,"description")))					
					repository.setDescription(currentRepo.get("description").toString());

				repository.setFull_name(projectId);
				repository.setShortName(currentRepo.get("name").toString());
				repository.setName(currentRepo.get("name").toString());

				if ((isNotNull(currentRepo,"private")))					
					repository.set_private(new Boolean(currentRepo.get("private").toString()));

				if ((isNotNull(currentRepo,"fork")))					
					repository.setFork(new Boolean(currentRepo.get("fork").toString()));
				
				if ((isNotNull(currentRepo,"git_url")))					
					repository.setGit_url(currentRepo.get("git_url").toString());
				
				if ((isNotNull(currentRepo,"html_url")))					
					repository.setHtml_url(currentRepo.get("html_url").toString());
				
				if ((isNotNull(currentRepo,"clone_url")))					
					repository.setClone_url(currentRepo.get("clone_url").toString());

				if ((isNotNull(currentRepo,"homepage")))					
					repository.setHomePage(currentRepo.get("homepage").toString());

				if ((isNotNull(currentRepo,"mirror_url")))					
					repository.setMirror_url(currentRepo.get("mirror_url").toString());

				if ((isNotNull(currentRepo,"master_branch")))					
					repository.setMaster_branch(currentRepo.get("master_branch").toString());

				if ((isNotNull(currentRepo,"ssh_url")))					
					repository.setSsh_url(currentRepo.get("ssh_url").toString());
				
				if ((isNotNull(currentRepo,"svn_url")))					
					repository.setSvn_url(currentRepo.get("svn_url").toString());
				
				repository.setSize(new Integer(currentRepo.get("size").toString()));
								
				Role gitHubOwnerRole = platform.getProjectRepositoryManager().getProjectRepository().getRoles().findOneByName("gitHub_owner");
				
				if (gitHubOwnerRole == null) { 
					gitHubOwnerRole = new Role();
					gitHubOwnerRole.setName("gitHub_owner");
					platform.getProjectRepositoryManager().getProjectRepository().getRoles().add(gitHubOwnerRole);
				}

				JSONObject ownerObject = (JSONObject)currentRepo.get("owner");
				Iterable<Person> persons = platform.getProjectRepositoryManager().getProjectRepository().getPersons().findByName((String)ownerObject.get("login"));		
				Iterator<Person> ipersons = persons.iterator();
				Person person = null;
				GitHubUser user = null;
				while (ipersons.hasNext()) {
					person = ipersons.next();				
					if (person instanceof GitHubUser) {
						user = (GitHubUser) person;
						break;
					}
				}
			
				if (user == null) {
					user = new GitHubUser();
					user.getRoles().add(gitHubOwnerRole);
					platform.getProjectRepositoryManager().getProjectRepository().getPersons().add(user);
					repository.getPersons().add(user);								
				}
				
				user.setHtml_url((String)ownerObject.get("html_url"));
				user.setHomePage((String)ownerObject.get("html_url"));
				user.setUrl((String)ownerObject.get("url"));
				user.setLogin((String)ownerObject.get("login"));
				user.setName((String)ownerObject.get("login"));
				user.setFollowers_url((String)ownerObject.get("followers_url"));							
				
//				int lastImportedId = new Integer(currentRepo.get("id").toString()); 
//				platform.getProjectRepositoryManager().getProjectRepository().getGitHubImportData().first().setLastImportedProject(String.valueOf(lastImportedId));
				platform.getProjectRepositoryManager().getProjectRepository().sync();	
				if (!projectToBeUpdated) {
					platform.getProjectRepositoryManager().getProjectRepository().getProjects().add(repository);
				}
				
				platform.getProjectRepositoryManager().getProjectRepository().sync();	
				logger.info("Project has been importerd");
				return repository;
			} catch (FileNotFoundException e1) {
				logger.error("API rate limit exceeded. Waiting to restart the importing..." + e1.getMessage());
				return null;
			} catch (IOException e1) {
				if (getRemainingResource()==0)
				{
					logger.error("API rate limit exceeded. Waiting to restart the importing..." + e1.getMessage());
					waitApiRate();
					return importRepository(projectId, platform);
				}
				else
				{
					logger.error("Repository access blocked for project " + projectId + " " + e1.getMessage());
					return null;
				}
			} 
	}
	
	private String getProjectIdFromUrl(String url)
	{
		url = url.replace("http://", "");
		url = url.replace("https://", "");
		url = url.replace("www.", "");
		
		if (url.startsWith("github.com")) {
			url= url.replace("github.com/", "");
			if(url.contains("?"))
				url = url.substring(0, url.indexOf("?"));
			return url;
		}
		else return null;
	}
	public GitHubRepository importProjectByUrl(String url, Platform platform) {
		return importRepository(getProjectIdFromUrl(url), platform);
	}
	public boolean isProjectInDB(String projectId)
	{
		try {
			Mongo mongo;
			mongo = new Mongo();
			Platform platform = new Platform(mongo);
			Iterable<Project> projects = platform.getProjectRepositoryManager().getProjectRepository().getProjects().findByShortName(projectId);		
	
			Iterator<Project> ip = projects.iterator();
			Project p = null;
			while (ip.hasNext()) {
				p = ip.next();				
				if (p instanceof GitHubRepository) {
					return true;		
				}
			}
			return false;
		}
		catch (UnknownHostException e) {
			return false;
		}
	}
	public boolean isProjectInDBByUrl(String url)
	{
		return isProjectInDB(getProjectIdFromUrl(url));
	}
}
