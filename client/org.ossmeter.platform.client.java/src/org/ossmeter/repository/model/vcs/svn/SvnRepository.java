package org.ossmeter.repository.model.vcs.svn;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import org.ossmeter.repository.model.redmine.*;
import org.ossmeter.repository.model.vcs.svn.*;
import org.ossmeter.repository.model.cc.forum.*;
import org.ossmeter.repository.model.bts.bugzilla.*;
import org.ossmeter.repository.model.cc.nntp.*;
import org.ossmeter.repository.model.vcs.cvs.*;
import org.ossmeter.repository.model.eclipse.*;
import org.ossmeter.repository.model.googlecode.*;
import org.ossmeter.repository.model.vcs.git.*;
import org.ossmeter.repository.model.sourceforge.*;
import org.ossmeter.repository.model.github.*;
import org.ossmeter.repository.model.*;
import org.ossmeter.repository.model.metrics.*;
import org.ossmeter.platform.factoids.*;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,
	include=JsonTypeInfo.As.PROPERTY,
	property = "_type")
@JsonSubTypes({
	@Type(value = SvnRepository.class, name="org.ossmeter.repository.model.vcs.svn.SvnRepository"), })
@JsonIgnoreProperties(ignoreUnknown = true)
public class SvnRepository extends VcsRepository {

	protected String browse;
	protected String username;
	protected String password;
	
	public String getBrowse() {
		return browse;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	
}
