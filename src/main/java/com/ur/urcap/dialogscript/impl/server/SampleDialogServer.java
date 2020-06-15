package com.ur.urcap.dialogscript.impl.server;

import java.io.BufferedReader;
import java.io.PrintWriter;

import com.ur.urcap.dialogscript.impl.*;

public class SampleDialogServer extends URInternalServer {

    private ScriptDialogInstallationNodeContribution contribution;
    private int port;

    public SampleDialogServer(int port, ScriptDialogInstallationNodeContribution contribution) {
        super(port);
        this.port = port;
        this.contribution = contribution;

    }


    @Override
    public void onAccept(String recieveString, BufferedReader reader, PrintWriter writer) {
    }


    @Override
    public void onRecieveStream(String recieveString, BufferedReader reader, PrintWriter writer) {
        String MESSAGE_HEADER = "Server " + String.valueOf(port) + ":";

        try {

            // オーダーフォームの表示 → モーダル,
            String command = recieveString;

            if (command.indexOf("showDialog") != -1) {

                SampleDialogFrame settingOrderFrame = new SampleDialogFrame(contribution);

                int resultDialog = settingOrderFrame.showDialog();

                if (resultDialog == SampleDialogFrame.ORDER_OK) {
                    writer.println("OK");
                    writer.flush();
                    System.out.println(MESSAGE_HEADER + "Send \"OK\"");

                    writer.println(settingOrderFrame.inputQTY);
                    writer.flush();
                    System.out.println(MESSAGE_HEADER + "Send \"" + String.valueOf(settingOrderFrame.inputQTY) + "\"");

                }

                else {
                    writer.println("Cancel");
                    writer.flush();
                    System.out.println(MESSAGE_HEADER + "Send \"Cancel\"");
                }

                settingOrderFrame.dispose();
            } else {
                System.out.println("Command is none");

            }

        } catch (Exception e) {
            System.out.println(MESSAGE_HEADER + e.getMessage());
        }

    }

    @Override
    public void onDisconnect() {
    }
}
