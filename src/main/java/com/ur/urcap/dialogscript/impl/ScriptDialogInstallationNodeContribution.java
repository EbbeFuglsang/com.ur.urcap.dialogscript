package com.ur.urcap.dialogscript.impl;

import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.domain.URCapAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.ui.annotation.Input;
import com.ur.urcap.api.ui.component.InputEvent;
import com.ur.urcap.api.ui.component.InputTextField;
import com.ur.urcap.api.ui.component.InputEvent.EventType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.ur.urcap.dialogscript.impl.server.SampleDialogServer;

public class ScriptDialogInstallationNodeContribution implements InstallationNodeContribution {

	public URCapAPI api;
	public DataModel model;

	private final int DEFAULT_PORT = 60002;
	private final String ARTIFACTID = "dialogscript";

	private SampleDialogServer dialogServer;


	@Input(id = "inputPort")
	private InputTextField inputPort;

	@Input(id = "inputPort")
	private void onChange_inputPort(InputEvent event) {
		if (event.getEventType() == EventType.ON_CHANGE) {
			setModelonEvent("inputPort", event);
		}
	}


	private void setModelonEvent(String name, InputEvent event) {
		if (event.getComponent() instanceof InputTextField) {
			InputTextField text = (InputTextField) event.getComponent();

			if (!text.getText().isEmpty())
				model.set(name, text.getText());
			else
				model.remove(name);

		}
	}


	private void updateForm() {

		inputPort.setText(model.get("inputPort", String.valueOf(DEFAULT_PORT)));

	}


	public boolean isEmptyOfString(String string) {
		if (string == null)
			return true;

		if (string == "")
			return true;

		return false;

	}


	public String[] readScriptFile(String filename) {
		try {

			BufferedReader br = new BufferedReader(
					new InputStreamReader(this.getClass().getResourceAsStream(filename)));

			ArrayList<String> list = new ArrayList<String>();

			String addstr;
			while ((addstr = br.readLine()) != null) {
				list.add(addstr);
			}

			br.close();
			String[] res = list.toArray(new String[0]);
			return res;

		} catch (IOException e) {
			return null;
		}

	}

	public ScriptDialogInstallationNodeContribution(URCapAPI api, DataModel model) {
		this.api = api;
		this.model = model;

		int port = model.get("inputPort", DEFAULT_PORT);

		dialogServer = new SampleDialogServer(port, this);
		dialogServer.start();

	}

	@Override
	public void openView() {
		updateForm();
	}

	@Override
	public void closeView() {

	}

	public boolean isDefined() {
		return true;
	}

	public void dispose() {

		dialogServer.interrupt();

		while (dialogServer.isAlive()) {
			Thread.yield();
		}

		System.out.println("Dialog Script Installation: Disposed");

	}

	@Override
	public void generateScript(ScriptWriter writer) {

		writer.appendLine("global portDialog = " + String.valueOf(model.get("inputPort", DEFAULT_PORT)));

		String[] scripts = readScriptFile("/com/ur/urcap/" + ARTIFACTID + "/impl/dialogscript.script");

		for (String str : scripts) {
			writer.appendLine(str);
		}

	}
}
