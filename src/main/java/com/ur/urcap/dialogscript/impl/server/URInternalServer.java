package com.ur.urcap.dialogscript.impl.server;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * UR内カスタムサーバ
 * 
 * 
 */
public abstract class URInternalServer extends Thread {

    public int port;
    public boolean isConnected = false;
    public boolean isRunning = true;
    private Socket socket;
    private ServerSocket serverSocket;

    public URInternalServer(int port) {
        this.port = port;
    }

    /**
     * サーバー側受け入れ後の処理
     * 
     * @param recieveString 受信したバッファ文字列
     * @param reader        受信用ストリーム
     * @param writer        送信用ストリーム
     */
    public abstract void onAccept(String recieveString, BufferedReader reader, PrintWriter writer);

    /**
     * バッファ受信後の処理。オーバーライドで使用。
     * 
     * @param recieveString 受信したバッファ文字列
     * @param reader        受信用ストリーム
     * @param writer        送信用ストリーム
     */
    public abstract void onRecieveStream(String recieveString, BufferedReader reader, PrintWriter writer);

    /**
     * サーバ切断時の処理
     */
    public abstract void onDisconnect();

    public void run() {

        String MESSAGE_HEADER = "Server " + String.valueOf(port) + ":";

        while (isRunning) {

            try {
                Thread.sleep(1);

                serverSocket = new ServerSocket(port);
                serverSocket.setSoTimeout(2000);

                socket = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                System.out.println(MESSAGE_HEADER + "Server is accepted");
                isConnected = true;

                while (isRunning && isConnected) {

                    Thread.sleep(1);
                    System.out.println(MESSAGE_HEADER + "Response is waiting...");

                    String command = reader.readLine();
                    if (command == null) {
                        onDisconnect();
                        throw new IOException("Response is null");
                    }

                    System.out.println(MESSAGE_HEADER + "Response is \"" + command + "\"");
                    onRecieveStream(command, reader, writer);

                }

            } catch (SocketException e) {
                isConnected = false;
                System.out.println(MESSAGE_HEADER + e.getMessage());

            } catch (IOException e) {
                isConnected = false;
                System.out.println(MESSAGE_HEADER + e.getMessage());

            } catch (InterruptedException e) {
                isRunning = false;
                System.out.println(MESSAGE_HEADER + "Thread is interrpted");
                
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                    }
                }
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        System.out.println(MESSAGE_HEADER + "Thread is finished");

    }
}
