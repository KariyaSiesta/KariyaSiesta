/*
 * Copyright(c) 2008 Aisin Comcruise
 *  All Rights Reserved
 */
package org.sapid.checker.cx.command;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * "java -version" などのコマンドを実行するクラス
 * @author Toshinori OSUKA
 */
public class Command {
    /** 実行するコマンド */
    protected String command;
    /** カレントディレクトリ */
    protected String curDir;

    /**
     * 実行コマンドを設定
     * @param command
     */
    public Command(String command, String curDir) {
        super();
        this.command = command;
        this.curDir = curDir;
    }

    /**
     * 実行する
     * @param output 実行結果を渡す Output (null も可)
     * @return 実行したプログラムの exitValue
     * @throws IOException
     */
    public int run(CommandOutput output) throws IOException {
        int exitValue = -1;
        ProcessBuilder pb = new ProcessBuilder(parseCommand(command));
        pb.redirectErrorStream(true);
        if (curDir != null) {
            pb.directory(new File(curDir));
        }
        Process ps = pb.start();
        DataInputStream dis = new DataInputStream(ps.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(dis));
        String line;
        while (true) {
            try {
                // sleep をしないとコマンドの起動前にループを抜けてしまうことがある
                // 時間はあまり重要ではない
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            while (dis.available() > 0) {
                while ((line = br.readLine()) != null) {
                    if (output != null) {
                        output.hook(line);
                    }
                }
            }
            try {
                exitValue = ps.exitValue();
                break;
            } catch (IllegalThreadStateException e) {
                continue;
            }
        }
        return exitValue;
    }

    /**
     * スペースとクォートでパスをスプリットする
     * @param command
     * @return
     */
    private String[] parseCommand(String command) {
        List<String> list = new ArrayList<String>();
        String[] array = command.split(" ");
        boolean inQuate = false;
        String concat = "";
        for (String item : array) {
            if (item.startsWith("\"")) {
                inQuate = true;
                item = item.substring(1, item.length());
            }
            if (item.endsWith("\"")) {
                inQuate = false;
                item = item.substring(0, item.length() - 1);
            }
            concat += item;
            if (inQuate) {
                concat += " ";
            }
            if (!inQuate) {
                list.add(concat);
                concat = "";
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

}
