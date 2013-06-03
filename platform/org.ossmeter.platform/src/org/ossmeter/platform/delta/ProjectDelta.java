package org.ossmeter.platform.delta;

import org.ossmeter.platform.Date;
import org.ossmeter.platform.delta.vcs.IVcsManager;
import org.ossmeter.platform.delta.vcs.VcsProjectDelta;
import org.ossmeter.platform.delta.communicationchannel.ICommunicationChannelManager;
import org.ossmeter.platform.delta.communicationchannel.CommunicationChannelProjectDelta;
import org.ossmeter.platform.delta.bugtrackingsystem.IBugTrackingSystemManager;
import org.ossmeter.platform.delta.bugtrackingsystem.BugTrackingSystemProjectDelta;
import org.ossmeter.repository.model.Project;

public class ProjectDelta {
	protected Date date;
	protected Project project;
	protected IVcsManager vcsManager;
	protected ICommunicationChannelManager communicationChannelManager;
	protected IBugTrackingSystemManager bugTrackingSystemManager;
	
	protected VcsProjectDelta vcsDelta;
	protected CommunicationChannelProjectDelta communicationChannelDelta;
	protected BugTrackingSystemProjectDelta bugTrackingSystemDelta;
//	protected TheOtherDelta
	
	public ProjectDelta(Project project, Date date, 
			IVcsManager vcsManager, 
			ICommunicationChannelManager communicationChannelManager, 
			IBugTrackingSystemManager bugTrackingSystemManager) {
		this.project = project;
		this.date = date;	
		this.vcsManager = vcsManager;
		this.communicationChannelManager = communicationChannelManager;
		this.bugTrackingSystemManager = bugTrackingSystemManager;
	}
	
	public boolean create() {
		try {
			vcsDelta = new VcsProjectDelta(project, date, vcsManager);
			communicationChannelDelta = new CommunicationChannelProjectDelta(project, date, communicationChannelManager);
			bugTrackingSystemDelta = new BugTrackingSystemProjectDelta(project, date, bugTrackingSystemManager);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Date getDate() {
		return date;
	}
	
	public Project getProject() {
		return project;
	}
	
	public void setVcsDelta(VcsProjectDelta vcsDelta) {
		this.vcsDelta = vcsDelta;
	}
	
	public VcsProjectDelta getVcsDelta() {
		return this.vcsDelta;
	}

	public void setCommunicationChannelDelta(CommunicationChannelProjectDelta communicationChannelDelta) {
		this.communicationChannelDelta = communicationChannelDelta;
	}
	
	public CommunicationChannelProjectDelta getCommunicationChannelDelta() {
		return this.communicationChannelDelta;
	}

	public void setBugTrackingSystemDelta(BugTrackingSystemProjectDelta bugTrackingSystemDelta) {
		this.bugTrackingSystemDelta = bugTrackingSystemDelta;
	}
	
	public BugTrackingSystemProjectDelta getBugTrackingSystemDelta() {
		return this.bugTrackingSystemDelta;
	}

}