package com.blaze.runner;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.web.openapi.PagePlaces;

/**
 * 
 * @author Marcel Milea
 * Extension point for BlazeMeter administration page
 */
public class BlazeMeterAdminPage extends AdminPage{
	private BlazeMeterServerSettings mainSettings;
	
	public BlazeMeterAdminPage(PagePlaces pagePlaces, String tabId, String includeUrl, String title) {
		super(pagePlaces, tabId, includeUrl, title);
	}
	
	public BlazeMeterServerSettings getMainSettings() {
		return mainSettings;
	}

	public void setMainSettings(BlazeMeterServerSettings mainSettings) {
		this.mainSettings = mainSettings;
	}

	@Override
	public String getGroup() {
		return Constants.RUNNER_TYPE;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public void fillModel(Map<String, Object> model, HttpServletRequest request) {
		super.fillModel(model, request);
		if (mainSettings != null){
			model.put("user_key", mainSettings.getUserKey());
			model.put("blazeMeterUrl", mainSettings.getBlazeMeterUrl());
			model.put("blazeMeterApiVersion", mainSettings.getBlazeMeterApiVersion());
			model.put("serverName", mainSettings.getServerName());
			model.put("serverPort", mainSettings.getServerPort());
			model.put("username", mainSettings.getUsername());
			model.put("password", mainSettings.getPassword());
		}
	}

	@Override
	public String getPluginName() {
		return Constants.RUNNER_TYPE;
	}

}
