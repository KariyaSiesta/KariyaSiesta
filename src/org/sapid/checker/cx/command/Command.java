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
 * "java -version" �ʤɤΥ��ޥ�ɤ�¹Ԥ��륯�饹
 * @author Toshinori OSUKA
 */
public class Command {
    /** �¹Ԥ��륳�ޥ�� */
    protected String command;
    /** �����ȥǥ��쥯�ȥ� */
    protected String curDir;

    /**
     * �¹ԥ��ޥ�ɤ�����
     * @param command
     */
    public Command(String command, String curDir) {
        super();
        this.command = command;
        this.curDir = curDir;
    }

    /**
     * �¹Ԥ���
     * @param output �¹Է�̤��Ϥ� Output (null ���)
     * @return �¹Ԥ����ץ����� exitValue
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
                // sleep �򤷤ʤ��ȥ��ޥ�ɤε�ư���˥롼�פ�ȴ���Ƥ��ޤ����Ȥ�����
                // ���֤Ϥ��ޤ���פǤϤʤ�
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
     * ���ڡ����ȥ������Ȥǥѥ��򥹥ץ�åȤ���
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
