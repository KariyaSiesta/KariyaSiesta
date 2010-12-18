/* 
 * Program:             $RCSfile: TestMisra005.java,v $  $Revision: 60.12 $
 * 
 *
 * Author:              T.Toshiki
 *                      T.Osuka
 *                      S.Yamamoto  2010/06/23
 *
 * (C) Copyright:       T.Toshiki, T.Osuka and S.Yamamoto  2010
 *                      This file is a product of the project Sapid.
 */

/* 
 * $Id: TestMisra005.java,v 60.12 2010/06/26 05:32:37 yamamoto Exp yamamoto $
 */

package org.sapid.checker.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.IFileFactory;
import org.sapid.checker.core.Result;
import org.sapid.checker.core.Logger;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.CheckRuleParser;
import org.sapid.parser.common.ParseException;

public class Main {
    private static final String PREPARATION = "PREPARATION: $ sdb4 <TARGET_FILE>;spdMkCXModel -v -sdbD SDB/SPEC";
    private static final String USAGE = 
	  "[--tsv]   : Tab Separated Values Mode\n"
	+ " 1st Arg  : target File\n"
	+ "[2nd Arg] : XML File of Rules\n";

    private static final String DEFAULT_INPUTXML = "deafult.xml";

    private static boolean tabFlag = false;
	private static String targetFile = null;
    private static String inputXML = DEFAULT_INPUTXML;

    /**
     * ルールXML args[1]を用いてファイル args[0] をチェックする。
     * args[0]に"--tsv"を指定すると出力がタブ区切りになり、ルールXML args[2]を用いてファイルargs[1]をチェックする。
     * @param args[0] Tab Separated Values Mode
     * @param args[1] target File
     * @param args[2] Rule XML for Checker
     */
    public static void main(String[] args) {
    	parseParam(args);
	
        Display.showMessage("Checker Start.");
        Display.showMessage("Creating File...");
        Display.changeIndent(+2);

        IFile file;
        try {
            file = IFileFactory.create(targetFile);
            Display.changeIndent(-2);
            Display.showMessage("File Created.");

            List<CheckRule> rules = CheckRuleParser.parseRuleXML(inputXML);

            ArrayList<Result> results = new ArrayList<Result>();

            Display.showMessage("Checking Start.");
            Display.changeIndent(+2);

            for (CheckRule rule : rules) {
                String moduleName = rule.getName();
                Display.showMessage("Checking with \"" + moduleName + "\".");
                CheckerClass Checker = createChecker(moduleName);
                results.addAll(Checker.check(file, rule));
            }

            Display.changeIndent(-2);
            Display.showMessage("Checking End.");

            output(results);
            if (Logger.isEnableSaveLog()) {
            	Logger logger = new Logger();
            	logger.saveLog(file, results);
            }

            // Display.showMessage("Checker Terminated");
            Display.showMessage("Checker Completed.");
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(PREPARATION);
        } 
    }
    
    /**
     * 引数のパースを行う
     * @param args
     */
    private static void parseParam(String[] args) {
    	LongOpt[] longopts = new LongOpt[1];
    	longopts[0] = new LongOpt("tsv", LongOpt.NO_ARGUMENT, null, 0);
    	Getopt g = new Getopt("Main", args, "W;", longopts);

    	int ch;
    	while ((ch = g.getopt()) != -1) {
    	    switch(ch) {
    	    case 0:
    		tabFlag = true;
    		break;
    	    default:
    		System.out.println("Can't understand '-" + (char)ch + "' option.");
    		break;
    	    }
    	}
    	for (int i = g.getOptind(); i < args.length ; i++) {
    	    if (targetFile==null) {
    		targetFile = args[i];
    	    } else if (inputXML==DEFAULT_INPUTXML) {
    		inputXML = args[i];
    	    } else {
    		break;
    	    }
    	}
    	if (targetFile==null) {
    	    System.err.print(USAGE);
    	    System.exit(-1);
    	}
    }

    /**
     * checkerClassNameで指定されたCheckerClassオブジェクトを獲得し、返す。
     * @param checkerClassName
     * @return
     */
    public static CheckerClass createChecker(String checkerClassName) {
        try {
            return (CheckerClass) Class.forName(checkerClassName).newInstance();
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        } catch (InstantiationException e) {
            throw new InstantiationError(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
    }

    /**
     * チェック結果が格納された results を一覧表示する
     * @param results
     */
    public static void output(ArrayList<Result> results) {
	if (tabFlag==true) {
	        // Tab Separated Values Mode
	        // System.out.println("= Test result ================");
	        for (Result result : results) {
	            System.out.println(
			result.getId() +
			"\t" + result.getLevel() +
			/* Range. */
			"\t"    + result.getRange().getStartLine() +
			"\t"    + result.getRange().getEndLine() +
			"\t"    + result.getRange().getStartColumn() +
			"\t"    + result.getRange().getEndColumn() +
			"\t"    + result.getRange().getOffset() +
			"\t"    + result.getRange().getLength() +
			"\t" + result.getMessage());
	        }
	        // System.out.println("==============================");
	} else {
	        // System.out.println("= Test result ================");
	        for (Result result : results) {
	            System.out.println(
			"Id: " + result.getId() +
			", Level: " + result.getLevel() +

			/* Range. */
			", Line: "    + result.getRange().getStartLine() +
			" "    + result.getRange().getEndLine() +
			", Column: "    + result.getRange().getStartColumn() +
			" "    + result.getRange().getEndColumn() +
			", Offset: "    + result.getRange().getOffset() +
			", Length: "    + result.getRange().getLength() +

			", Message: " + result.getMessage());
	        }
        	// System.out.println("==============================");
	}
    }
}
