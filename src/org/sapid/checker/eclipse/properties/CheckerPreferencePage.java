/*
 * Copyright(c) 2008 Aisin Comcruise
 *  All Rights Reserved
 */
package org.sapid.checker.eclipse.properties;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.sapid.checker.eclipse.CheckerActivator;
import org.sapid.checker.eclipse.Messages;

/**
 * 設定ページ
 * @author Toshinori OSKA
 */
public class CheckerPreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    /** PreferenceStore のキー */
    private static final String SAPID_DEST = "SAPID_DEST";

    /**
     * コンストラクタ
     */
    public CheckerPreferencePage() {
        super(GRID);
        setDescription(Messages
                .getString("CheckerPreferencepage.0"));
        setPreferenceStore(CheckerActivator.getDefault().getPreferenceStore());
    }

    protected void createFieldEditors() {
        addField(new DirectoryFieldEditor(SAPID_DEST, SAPID_DEST,
                getFieldEditorParent()));
    }

    public void init(IWorkbench workbench) {
    }
}
