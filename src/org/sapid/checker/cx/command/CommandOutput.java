/*
 * Copyright(c) 2008 Aisin Comcruise
 *  All Rights Reserved
 */
package org.sapid.checker.cx.command;

/**
 * Command ���饹���ɤ߹�����¹Է�̤�������
 * @author Toshinori OSUKA
 */
public interface CommandOutput {
    /**
     * ��Ԥ��ļ¹Է�̤��Ϥ����
     * @param buffer
     * @return �¹Է�̡ʰ�ԡ�
     */
    String hook(String buffer);
}
