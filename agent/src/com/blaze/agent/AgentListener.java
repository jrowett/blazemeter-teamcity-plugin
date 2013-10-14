package com.blaze.agent;

import java.util.Map;

import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildInterruptReason;
import jetbrains.buildServer.util.EventDispatcher;

import org.jetbrains.annotations.NotNull;

import com.blaze.api.BlazeBean;
import com.blaze.runner.BlazeMeterConstants;

public class AgentListener extends AgentLifeCycleAdapter{
	
	private BlazeBean blazeBean;
	
	public AgentListener(@NotNull final EventDispatcher<AgentLifeCycleListener> dispatcher){
		dispatcher.addListener(this);
	}
	
	@Override
	public void beforeBuildInterrupted(@NotNull AgentRunningBuild runningBuild, @NotNull BuildInterruptReason reason) {
		super.beforeBuildInterrupted(runningBuild, reason);
		stopTest(runningBuild);
	}

	@Override
	public void buildFinished(@NotNull AgentRunningBuild runningBuild, @NotNull BuildFinishedStatus buildStatus) {
		super.buildFinished(runningBuild, buildStatus);
		stopTest(runningBuild);
	}

	private void stopTest(AgentRunningBuild runningBuild) {
		Map<String, String> buildSharedMap = runningBuild
				.getSharedConfigParameters();
		blazeBean = new BlazeBean(
				buildSharedMap.get(BlazeMeterConstants.USER_KEY),
				buildSharedMap.get(BlazeMeterConstants.PROXY_SERVER_NAME),
				Integer.parseInt(buildSharedMap.get(BlazeMeterConstants.PROXY_SERVER_PORT)),
				buildSharedMap.get(BlazeMeterConstants.PROXY_USERNAME),
				buildSharedMap.get(BlazeMeterConstants.PROXY_PASSWORD));

		Map<String, String> runnerParams = runningBuild.getRunnerParameters();
		String testId = runnerParams.get(BlazeMeterConstants.SETTINGS_ALL_TESTS_ID);
		blazeBean.stopTest(testId, runningBuild.getBuildLogger());
	}
}
