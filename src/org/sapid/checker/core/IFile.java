/*
 * Copyright(c) 2008 Aisin Comcruise
 *  All Rights Reserved
 */
package org.sapid.checker.core;

import java.io.IOException;

import org.sapid.parser.common.ParseException;
import org.w3c.dom.Document;

/**
 * 解析対象となるファイルのインターフェイス
 * @author Toshinori OSUKA
 */
public interface IFile {
    /**
     * ファイル名を取得する
     * @return
     */
    public String getFileName();

    /**
     * 解析結果の DOM を取得する<br />
     * DOM がない場合は構築する
     * @throws ParseException 構文解析に失敗した場合
     * @throws IOException 解析対象がない場合
     * @return DOM
     */
    public void buildDOM() throws ParseException, IOException;

    /**
     * 解析対象を指定する
     * @param filename フルパス
     */
    public void setFileName(String filename);
    
    /**
     * DOM を取得する
     * @return
     */
    public Document getDOM();
}
