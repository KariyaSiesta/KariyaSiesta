/*
 * Copyright(c) 2008 Aisin Comcruise
 *  All Rights Reserved
 */
package org.sapid.checker.cx.command;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Makefile に CC マクロが定義されているかチェックするクラス
 * @author Toshinori OSUKA
 */
public class Makefile {
    /** Makefile のパス */
    private String filepath;
    /** CC = gcc にマッチする正規表現 */
    private final static String CC_MACRO_DEF_REGEX = "\\s*CC\\s*=\\s*\\S+\\s*";

    /**
     * コンストラクタ
     * @param filepath
     */
    public Makefile(String filepath) {
        super();
        this.filepath = filepath;
    }

    /**
     * CC マクロが定義されているか
     * @return
     */
    public boolean isContainedCCMacro() throws FileNotFoundException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(
                    filepath)));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.matches(CC_MACRO_DEF_REGEX)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
