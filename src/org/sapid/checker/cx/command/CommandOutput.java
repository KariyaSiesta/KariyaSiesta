/*
 * Copyright(c) 2008 Aisin Comcruise
 *  All Rights Reserved
 */
package org.sapid.checker.cx.command;

/**
 * Command クラスが読み込んだ実行結果を受け取る
 * @author Toshinori OSUKA
 */
public interface CommandOutput {
    /**
     * 一行ずつ実行結果が渡される
     * @param buffer
     * @return 実行結果（一行）
     */
    String hook(String buffer);
}
