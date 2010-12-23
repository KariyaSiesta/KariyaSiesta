package org.sapid.checker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.rule.CheckRule;

public class LineWidthChecker implements CheckerClass {

  private static final String DEFAULT_ID      = "line_width";

  final int                   DEFAULT_WIDTH   = 80;

  final int                   DEFAULT_LEVEL   = 3;

  final String                DEFAULT_MESSAGE = "指定した最大行数を超えている (LineWidthChecker)";

  private static final String XML_ID          = "id";

  final String                XML_MAX         = "max";

  final String                XML_LEVEL       = "level";

  final String                XML_MESSAGE     = "message";

  public ArrayList<Result> check(IFile jspfile, CheckRule rule) {
    ArrayList<Result> results = new ArrayList<Result>();

    String id = DEFAULT_ID;
    int max_width = DEFAULT_WIDTH;
    int level = DEFAULT_LEVEL;
    String message = DEFAULT_MESSAGE;

    if (rule.getValue(XML_ID) != null) {
      id = rule.getValue(XML_ID);
    }

    if (rule.getValue(XML_MAX) != null) {
      max_width = Integer.parseInt(rule.getValue(XML_MAX));
    }

    if (rule.getValue(XML_LEVEL) != null) {
      level = Integer.parseInt(rule.getValue(XML_LEVEL));
    }

    if (rule.getValue(XML_MESSAGE) != null) {
      message = rule.getValue(XML_MESSAGE);
    }

    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(jspfile.getFileName()), "JISAutoDetect"));
      String line;

      int line_num = 1;

      while ((line = reader.readLine()) != null) {
        if (line.toString().length() > max_width) {
//          Range range = new Range(line_num, 0, line_num, 0, 0, 0);
          results.add(new Result(id, line_num, level, message));
        }
        line_num++;
      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (Exception e) {
      }
    }

    return results;
  }

}
