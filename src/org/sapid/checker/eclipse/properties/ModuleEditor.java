package org.sapid.checker.eclipse.properties;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;
import org.sapid.checker.eclipse.Messages;
import org.sapid.checker.LineWidthChecker;

public class ModuleEditor extends ListEditor {

    private static final String initialValue = LineWidthChecker.class
            .getCanonicalName()
            + ";max=80";

    public ModuleEditor(String name, String labelText, Composite parent) {
        super(name, labelText, parent);
    }

    @Override
    protected String[] parseString(String stringList) {
        return PropertyParser.parseModules(stringList);
    }

    @Override
    protected String createList(String[] items) {
        return PropertyParser.unparseModules(items);
    }

    @Override
    protected String getNewInputObject() {
        InputDialog dialog = new InputDialog(this.getShell(), Messages
                .getString("ModuleEditor.title"), Messages
                .getString("ModuleEditor.description"), initialValue,
                new ClassNameValidator());

        int res = dialog.open();
        if (res == Dialog.OK) {
            return dialog.getValue();
        } else {
            return null;
        }
    }

}

class ClassNameValidator implements IInputValidator {

    public String isValid(String newText) {
        ArrayList<String> errors = new ArrayList<String>();

        PropertyParser.parseOneModule(newText, errors);

        if (errors.size() > 0) {
            return errors.get(0);
        } else {
            return null;
        }
    }

}
