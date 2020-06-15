package com.ur.urcap.dialogscript.impl;

import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.InstallationNodeService;
import com.ur.urcap.api.domain.URCapAPI;

import java.io.InputStream;

import com.ur.urcap.api.domain.data.DataModel;

public class ScriptDialogInstallationNodeService implements InstallationNodeService {

	private ScriptDialogInstallationNodeContribution contribution;

	public ScriptDialogInstallationNodeService() {

	}

	@Override
	public InstallationNodeContribution createInstallationNode(URCapAPI api, DataModel model) {
		if (contribution != null) {
			contribution.dispose();
			contribution = null;
		}

		contribution = new ScriptDialogInstallationNodeContribution(api, model);

		return contribution;
	}

	@Override
	public String getTitle() {
		return "Dialog Script";
	}

	@Override
	public InputStream getHTML() {
		return this.getClass().getResourceAsStream("/com/ur/urcap/dialogscript/impl/installation_en.html");

	}
}
