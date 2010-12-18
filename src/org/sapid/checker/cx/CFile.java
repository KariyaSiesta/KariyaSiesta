/*
 * Copyright(c) 2008 Aisin Comcruise
 *  All Rights Reserved
 */
package org.sapid.checker.cx;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.sapid.checker.core.IFile;
import org.sapid.parser.common.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * C ファイルの情報を持つクラス
 * @author Toshinori OSUKA
 */
public class CFile implements IFile {

    /** 対象ファイルのフルパス */
    private String filename;

    /** 解析結果の DOM */
    private Document dom = null;

    /** SDB のディレクトリ名 */
    private final static String SDB = Messages.getString("SDB_SPEC");

    /** コンストラクタ */
    public CFile(String filename) {
        super();
        this.filename = filename;
    }

    public void buildDOM() throws ParseException, IOException {
        if (dom == null) {
            File file = new File(filename);
            String xml = searchProjectPath(filename) + File.separator + SDB
                    + File.separator + file.getName() + ".xml";
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory
                    .newInstance();

            DocumentBuilder builder;
            try {
                builder = dbfactory.newDocumentBuilder();
                dom = builder.parse(new File(xml));
            } catch (IOException e) {
                throw new IOException(e);
            } catch (SAXException e) {
                throw new ParseException(e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Document getDOM() {
        return dom;
    }

    public String getFileName() {
        return filename;
    }

    public void setFileName(String filename) {
        this.filename = filename;
    }

    /**
     * SDB/SPEC があるディレクトリのパスを返す
     * @param filepath
     * @return
     * @throws IOException
     */
    private String searchProjectPath(String filepath) throws IOException {
        File f = new File(filepath);
        while (f.getParent() != null) {
            if (new File(f.getParent() + File.separator + SDB).exists()) {
                return f.getParent();
            }
            f = f.getParentFile();
        }
        throw new IOException();
    }

}
