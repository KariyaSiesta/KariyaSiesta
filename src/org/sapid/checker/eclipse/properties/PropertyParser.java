package org.sapid.checker.eclipse.properties;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.eclipse.Messages;

public class PropertyParser {

    public static final String PATH_SEPARATOR = "|"; //$NON-NLS-1$

    public static final String PATH_SEPARATOR_REGEXP = "\\|"; //$NON-NLS-1$

    private static String[] trim(String[] strs) {
        ArrayList<String> ret = new ArrayList<String>();
        for (String str : strs) {
            if (!str.trim().equals("")) {
                ret.add(str);
            }
        }
        return ret.toArray(new String[0]);
    }

    public static String[] parsePath(String stringList) {
        return trim(stringList.split(PATH_SEPARATOR_REGEXP));
    }

    public static String unparsePath(String[] items) {
        StringBuffer path = new StringBuffer(""); //$NON-NLS-1$

        for (int i = 0; i < items.length; i++) {
            path.append(items[i]);
            path.append(PATH_SEPARATOR);
        }
        return path.toString();
    }

    private static final String MODULE_SEPARATOR = "\u0000"; //$NON-NLS-1$
    private static final String MODULE_SEPARATOR_REGEXP = "\u0000"; //$NON-NLS-1$

    private static final String MODULE_PARAM_SEPARATOR = ";"; //$NON-NLS-1$
    private static final String MODULE_PARAM_SEPARATOR_REGEXP = ";"; //$NON-NLS-1$

    public static String[] parseModules(String stringList) {
        return trim(stringList.split(MODULE_SEPARATOR_REGEXP));
    }

    public static String unparseModules(ArrayList<ModuleData> items) {
        ArrayList<String> strs = new ArrayList<String>();
        for (ModuleData module : items) {
            strs.add(unparseOneModule(module));
        }
        return unparseModules(strs.toArray(new String[0]));
    }

    public static String unparseModules(String[] items) {
        StringBuffer path = new StringBuffer(""); //$NON-NLS-1$

        for (int i = 0; i < items.length; i++) {
            path.append(items[i]);
            path.append(MODULE_SEPARATOR);
        }
        return path.toString();
    }

    private static String stackTrace(Exception ex) {
        StringWriter buf = new StringWriter();
        PrintWriter out = new PrintWriter(buf);
        ex.printStackTrace(out);
        buf.flush();
        return buf.toString();
    }

    public static String unparseOneModule(ModuleData m) {
        StringBuffer buf = new StringBuffer();
        buf.append(m.className);
        buf.append(MODULE_PARAM_SEPARATOR);
        for (String key : m.params.keySet()) {
            String value = m.params.get(key);
            buf.append(key + "=" + value);
            buf.append(MODULE_PARAM_SEPARATOR);
        }
        return buf.toString();
    }

    public static ModuleData parseOneModule(String newText, List<String> errors) {
        String[] params = trim(newText.split(MODULE_PARAM_SEPARATOR_REGEXP));

        if (params.length < 1) {
            errors.add(Messages.getString("PropertyParser.0"));
            return null;
        }

        ModuleData module = new ModuleData();
        module.className = params[0].trim();
        try {
            Class<?> clazz = Class.forName(module.className);
            Object o = clazz.newInstance();
            if (!(o instanceof CheckerClass)) {
                errors.add(Messages.getString("PropertyParser.1"));
            }
        } catch (ClassNotFoundException e) {
            errors.add(Messages.getString("PropertyParser.2") + "\n"
                    + stackTrace(e));
            return null;
        } catch (IllegalAccessException e) {
            errors.add(Messages.getString("PropertyParser.3") + "\n"
                    + stackTrace(e));
            return null;
        } catch (InstantiationException e) {
            errors.add(Messages.getString("PropertyParser.4") + "\n"
                    + stackTrace(e));
            return null;
        }

        for (String param : params) {
            if (param == params[0])
                continue;
            if ("".equals(param.trim()))
                continue;

            String[] pair = param.split("=");
            if (pair.length != 2) {
                errors.add(Messages.getString("PropertyParser.5"));
                return null;
            }
            module.params.put(pair[0], pair[1]);
        }
        return module;
    }
}

class ModuleData {
    String className;
    Map<String, String> params;

    public ModuleData() {
        params = new HashMap<String, String>();
    }
}