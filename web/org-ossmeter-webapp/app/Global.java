import java.util.Arrays;
import java.util.*;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;

import controllers.routes;

import auth.MongoAuthenticator;

import play.Application;
import play.GlobalSettings;
import play.mvc.Call;
import play.libs.Akka;
import akka.actor.*;
import java.util.concurrent.TimeUnit;
import scala.concurrent.duration.Duration;

import play.libs.ws.*;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Result;
import static play.libs.F.Function;
import static play.libs.F.Promise;

import model.*;

import com.mongodb.Mongo;
import com.mongodb.DBCollection;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.BasicDBObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class Global extends GlobalSettings {

	public void onStart(Application app) {
		// Configure authentication server
		PlayAuthenticate.setResolver(new Resolver() {

			@Override
			public Call login() {
				// Your login page
				return routes.Application.login();
			}

			@Override
			public Call afterAuth() {
				// The user will be redirected to this page after authentication
				// if no original URL was saved
				return routes.Application.index();
			}

			@Override
			public Call afterLogout() {
				return routes.Application.index();
			}

			@Override
			public Call auth(final String provider) {
				// You can provide your own authentication implementation,
				// however the default should be sufficient for most cases
				return com.feth.play.module.pa.controllers.routes.Authenticate
						.authenticate(provider);
			}

			@Override
			public Call askMerge() {
				return routes.Account.askMerge();
			}

			@Override
			public Call askLink() {
				return routes.Account.askLink();
			}

			@Override
			public Call onException(final AuthException e) {
				if (e instanceof AccessDeniedException) {
					return routes.Signup
							.oAuthDenied(((AccessDeniedException) e)
									.getProviderKey());
				}

				// more custom problem handling here...
				return super.onException(e);
			}
		});

		// Start cron jobs
		Akka.system().scheduler().schedule(
			Duration.create(1, TimeUnit.SECONDS), // Initial delay
			Duration.create(1, TimeUnit.DAYS), // Frequency
			new Runnable() {
				public void run() {
					System.out.println("scheduled task is running :)");

					try {
						// String host = play.Play.application().configuration().getString("mongo.default.host");

						// System.out.println("host: " + host);
						// if (host == null) host = "localhost";
						// Integer port = play.Play.application().configuration().getInt("mongo.default.port");
						
						// System.out.println("port: " + port);

						// if (port == null) port = 27017;

						// final Mongo mongo = new Mongo(host, port);

						// DB db = mongo.getDB("users");
						

						final DB db = MongoAuthenticator.getUsersDb();
						final Users users = new Users(db);

						// Iterate all projects TODO:Paging
						Promise<List<Project>> projects = WS.url("http://localhost:8182/projects?size=300").get().map( 
							new Function<WSResponse, List<Project>>() {

								public List<Project> apply(WSResponse response) {
									ArrayNode pList = (ArrayNode)response.asJson();

									List<Project> ps = new ArrayList<>();
									ObjectMapper mapper = new ObjectMapper();

									Statistics stats = new Statistics();
									stats.setDate(new Date());


									int vcs = 0, bts = 0, cc = 0;

									for (JsonNode p : pList) {
										ObjectNode node = ((ObjectNode)p);
										try {
											String pId = node.get("shortName").asText();
											
											// Add if it doesn't exist
											if (users.getProjects().findOneByIdentifier(pId) == null){
												Project proj = new Project();
												proj.setId(pId);
												proj.setName(node.get("name").asText());

												users.getProjects().add(proj);
												users.getProjects().sync();
											}

											// Now do info source stats
											vcs += ((ArrayNode)node.get("vcsRepositories")).size();
											cc += ((ArrayNode)node.get("communicationChannels")).size();
											bts += ((ArrayNode)node.get("bugTrackingSystems")).size();
										
											// Now do bugs, messages, lines


										} catch (Exception e) {
											e.printStackTrace();
										}
									}

									stats.setNumberOfVcsRepositories(vcs);
									stats.setNumberOfBugTrackers(bts);
									stats.setNumberOfCommunicationChannels(cc);
									stats.setNumberOfProjects((int)users.getProjects().getDbCollection().count());
									stats.setNumberOfUsers((int)users.getUsers().getDbCollection().count());

									users.getStatistics().add(stats);
									users.getStatistics().sync();

									db.getMongo().close();
									return ps;
								}
							}
						);

						//

					} catch (Exception e) {
						e.printStackTrace();
						
					} 

				}	
			},
			Akka.system().dispatcher()
			);
	}
}