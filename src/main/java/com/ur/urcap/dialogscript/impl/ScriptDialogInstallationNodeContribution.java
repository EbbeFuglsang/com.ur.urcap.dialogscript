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

	/**
	 * テキストボックスの定義
	 * 
	 */
	@Input(id = "inputPort")
	private InputTextField inputPort;

	@Input(id = "inputPort")
	private void onChange_inputPort(InputEvent event) {
		if (event.getEventType() == EventType.ON_CHANGE) {
			setModelonEvent("inputPort", event);
		}
	}

	/**
	 * 
	 * イベント処理時のデータモデルを更新する処理
	 * 
	 * @param name  コンポーネント名
	 * @param event イベントオブジェクト
	 */
	private void setModelonEvent(String name, InputEvent event) {
		if (event.getComponent() instanceof InputTextField) {
			InputTextField text = (InputTextField) event.getComponent();

			if (!text.getText().isEmpty())
				model.set(name, text.getText());
			else
				model.remove(name);

		}
	}

	/**
	 * 設定内容の表示を更新する処理
	 */
	private void updateForm() {

		inputPort.setText(model.get("inputPort", String.valueOf(DEFAULT_PORT)));

	}

	/**
	 * 文字列が空かnullか調べる
	 * 
	 * @param string 文字列
	 * @return 空かnullのいずれかでtrue。それ以外はfalse。
	 */
	public boolean isEmptyOfString(String string) {
		if (string == null)
			return true;

		if (string == "")
			return true;

		return false;

	}

	/**
	 * スクリプトファイルの読み込み
	 * 
	 * @param filename ファイル名
	 * @return 文字列の配列
	 */
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

	/**
	 * コストラクタ
	 * 
	 * @param api   urcapAPI
	 * @param model データモデル
	 */
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
