package org.sapid.checker.main;

import java.text.DateFormat;
import java.util.Date;

public class Display {

    static DateFormat defDF = null;

    static int indent = 0;

    public static void setDateFormat(DateFormat df) {
        defDF = df;
    }

    public static void setIndent(int ind) {
        indent = ind;
    }

    public static void changeIndent(int ind) {
        indent += ind;
    }

    public static void showMessage(String mes) {
        Date date = new Date();
        DateFormat df;

        if (defDF != null) {
            df = defDF;
        } else {
            df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                    DateFormat.MEDIUM);
        }

        System.err.print(df.format(date) + " - ");

        for (int i = 0; i < indent; i++) {
            System.err.print(" ");
        }

        System.err.println(mes);
    }
}
