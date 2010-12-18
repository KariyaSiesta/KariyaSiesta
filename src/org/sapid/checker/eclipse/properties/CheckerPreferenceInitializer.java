/*
 * Copyright(c) 2008 Aisin Comcruise
 *  All Rights Reserved
 */
package org.sapid.checker.eclipse.properties;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.sapid.checker.eclipse.CheckerActivator;

/**
 * ����ν���Ȥ����ꤹ��
 * @author Toshinori OSUKA
 */
public class CheckerPreferenceInitializer extends AbstractPreferenceInitializer {
    /** �ǥե���Ȥ� Sapid ���󥹥ȡ���ǥ��쥯�ȥ� */
    private final static String DEFAULT_SAPID_DEST = "C:\\cygwin\\usr\\local\\Sapid-GCC";

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = CheckerActivator.getDefault()
                .getPreferenceStore();
        store.setDefault("SAPID_DEST", DEFAULT_SAPID_DEST);
    }

}
