package org.ossmeter.repository.model.bitbucket;

import com.mongodb.*;
import java.util.*;
import com.googlecode.pongo.runtime.*;
import com.googlecode.pongo.runtime.querying.*;

// protected region custom-imports on begin
// protected region custom-imports end

public class BitbucketBugTrackingSystem extends org.ossmeter.repository.model.BugTrackingSystem {
	
	
	// protected region custom-fields-and-methods on begin
    @Override
    public String getBugTrackerType() {
        return "bitbucket";
    }

    @Override
    public String getInstanceId() {
        return getUser() + '/' + getRepository();
    }

    // protected region custom-fields-and-methods end
	
	public BitbucketBugTrackingSystem() { 
		super();
		super.setSuperTypes("org.ossmeter.repository.model.bitbucket.BugTrackingSystem");
		USER.setOwningType("org.ossmeter.repository.model.bitbucket.BitbucketBugTrackingSystem");
		REPOSITORY.setOwningType("org.ossmeter.repository.model.bitbucket.BitbucketBugTrackingSystem");
		LOGIN.setOwningType("org.ossmeter.repository.model.bitbucket.BitbucketBugTrackingSystem");
		PASSWORD.setOwningType("org.ossmeter.repository.model.bitbucket.BitbucketBugTrackingSystem");
	}
	
	public static StringQueryProducer USER = new StringQueryProducer("user"); 
	public static StringQueryProducer REPOSITORY = new StringQueryProducer("repository"); 
	public static StringQueryProducer LOGIN = new StringQueryProducer("login"); 
	public static StringQueryProducer PASSWORD = new StringQueryProducer("password"); 
	
	
	public String getUser() {
		return parseString(dbObject.get("user")+"", "");
	}
	
	public BitbucketBugTrackingSystem setUser(String user) {
		dbObject.put("user", user);
		notifyChanged();
		return this;
	}
	public String getRepository() {
		return parseString(dbObject.get("repository")+"", "");
	}
	
	public BitbucketBugTrackingSystem setRepository(String repository) {
		dbObject.put("repository", repository);
		notifyChanged();
		return this;
	}
	public String getLogin() {
		return parseString(dbObject.get("login")+"", "");
	}
	
	public BitbucketBugTrackingSystem setLogin(String login) {
		dbObject.put("login", login);
		notifyChanged();
		return this;
	}
	public String getPassword() {
		return parseString(dbObject.get("password")+"", "");
	}
	
	public BitbucketBugTrackingSystem setPassword(String password) {
		dbObject.put("password", password);
		notifyChanged();
		return this;
	}
	
	
	
	
}