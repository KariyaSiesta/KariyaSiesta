/*
 * Copyright(c) 2008 Aisin Comcruise
 *  All Rights Reserved
 */
package org.sapid.checker.core;

import java.io.IOException;

import org.sapid.checker.cx.CFile;
import org.sapid.checker.jspfile.JSPFile;
import org.sapid.parser.common.ParseException;

/**
 * IFile �Υե����ȥꥯ�饹<br />
 * ��ĥ�Ҥ� .jsp �ʤ� JSPFile ���֤��� ����ʳ��ʤ� CFile �ե�������֤�
 * @author Toshinori OSUKA
 */
public class IFileFactory {
    public static IFile create(String filename) throws ParseException,
            IOException {
        IFile file = null;
        if (filename.toLowerCase().endsWith(".jsp")) {
            file = new JSPFile(filename);
        } else {
            file = new CFile(filename);
        }
        file.buildDOM();
        return file;
    }

}
