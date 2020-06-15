package com.ur.urcap.dialogscript.impl;

import com.ur.urcap.api.contribution.InstallationNodeService;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		ScriptDialogInstallationNodeService settingOrderInstallationNodeService = new ScriptDialogInstallationNodeService();

		context.registerService(InstallationNodeService.class, settingOrderInstallationNodeService, null);

	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {

	}
}
