/*
 * Copyright(c) 2008 Aisin Comcruise
 *  All Rights Reserved
 */
package org.sapid.checker.core;

import java.io.IOException;

import org.sapid.parser.common.ParseException;
import org.w3c.dom.Document;

/**
 * �����оݤȤʤ�ե�����Υ��󥿡��ե�����
 * @author Toshinori OSUKA
 */
public interface IFile {
    /**
     * �ե�����̾���������
     * @return
     */
    public String getFileName();

    /**
     * ���Ϸ�̤� DOM ���������<br />
     * DOM ���ʤ����Ϲ��ۤ���
     * @throws ParseException ��ʸ���Ϥ˼��Ԥ������
     * @throws IOException �����оݤ��ʤ����
     * @return DOM
     */
    public void buildDOM() throws ParseException, IOException;

    /**
     * �����оݤ���ꤹ��
     * @param filename �ե�ѥ�
     */
    public void setFileName(String filename);
    
    /**
     * DOM ���������
     * @return
     */
    public Document getDOM();
}
