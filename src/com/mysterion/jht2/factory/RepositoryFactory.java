package com.mysterion.jht2.factory;

import com.ibm.team.repository.common.TeamRepositoryException;
import com.mysterion.jht2.repository.Repository;
import com.mysterion.jht2.setting.SettingReader;

public class RepositoryFactory {
	private static final String JAZZURL = SettingReader.getInstance().getJazzUrl();
	private static final String PARTIAL_NAME = SettingReader.getInstance().getPartialName();
	private static final String PARTIAL_OWNER_NAME = SettingReader.getInstance().getPartialOwnerName();
    private static final String USR = SettingReader.getInstance().getUsername();
    private static final String PWD = SettingReader.getInstance().getPassword();
    
	
	public static Repository getRepository() throws TeamRepositoryException {
		com.mysterion.jht2.repository.Repository repo = Repository.getRepository(JAZZURL, PARTIAL_NAME, PARTIAL_OWNER_NAME, USR, PWD);
		if (repo.getiTeamRepository() == null) {
			repo.setMonitor(MonitorFactory.getMonitor());
			repo.login();
		}
		return repo;
	}
}
