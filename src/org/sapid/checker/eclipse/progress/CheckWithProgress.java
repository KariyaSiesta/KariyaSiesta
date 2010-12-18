package org.sapid.checker.eclipse.progress;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFileFactory;
import org.sapid.checker.core.Result;
import org.sapid.checker.core.Logger;
import org.sapid.checker.eclipse.CheckerActivator;
import org.sapid.checker.eclipse.Messages;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.CheckRuleParser;
import org.sapid.parser.common.ParseException;

/**
 * 解析の過程をプログレスバーに表示
 */
public class CheckWithProgress implements IRunnableWithProgress {
    public String fileName = null;
    public String ruleXML = null;
    IFile file;

    /**
     * Trowable を文字列にする
     * @param t
     * @return
     */
    public static String getString(Throwable t) {
        StringWriter buf = new StringWriter();
        t.printStackTrace(new PrintWriter(buf));
        return buf.toString();
    }

    /**
     * 解析対象のファイルを指定
     * @param fName
     */
    public void setFile(IFile fName) {
        file = fName;
        fileName = fName.getRawLocation().toString();
    }

    /**
     * ルール XML を指定
     * @param fName
     */
    public void setRuleXML(String fName) {
        ruleXML = fName;
    }

    public void run(IProgressMonitor monitor) throws InvocationTargetException,
            InterruptedException {
        List<CheckRule> rules = null;

        try {
            rules = CheckRuleParser.parseRuleXML(ruleXML);
        } catch (Throwable t) {
            CheckerActivator.log(t);
            String message = getString(t);
            throw new InterruptedException(Messages
                    .getString("CheckWithProgress.1")
                    + "\n" + message);
        }

        monitor.beginTask(Messages.getString("CheckWithProgress.0")
                + file.getName(), 4 + rules.size());

        monitor.subTask(Messages.getString("CheckWithProgress.2"));
        try {
            file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
        } catch (CoreException e) {
            CheckerActivator.log(e);
        } catch (Throwable t) {
            CheckerActivator.log(t);
            throw new InterruptedException(Messages
                    .getString("CheckWithProgress.9")
                    + getString(t));
        }

        if (monitor.isCanceled()) {
            throw new InterruptedException(Messages
                    .getString("CheckWithProgress.CANCELD"));
        }
        monitor.worked(1);

        monitor.subTask(Messages.getString("CheckWithProgress.4"));

        // file
        org.sapid.checker.core.IFile cfile = null;
        try {
            cfile = IFileFactory.create(fileName);
        } catch (ParseException e) {
            createMarker(file, 0, Messages.getString("CheckWithProgress.5"),
                    IMarker.SEVERITY_ERROR);
            return;
        } catch (IOException e) {
            createMarker(file, 0, Messages.getString("CheckWithProgress.6"),
                    IMarker.SEVERITY_ERROR);
            return;
        }
        monitor.worked(2);
        if (monitor.isCanceled()) {
            throw new InterruptedException(Messages
                    .getString("CheckWithProgress.CANCELD"));
        }

        // rules
        ArrayList<Result> results = new ArrayList<Result>();
        try {
            for (CheckRule rule : rules) {
                String moduleName = rule.getName();
                monitor.subTask(Messages.getString("CheckWithProgress.10")
                        + moduleName + " ...");
                CheckerClass Checker = createChecker(moduleName);
                results.addAll(Checker.check(cfile, rule));
                
                monitor.worked(1);
                if (monitor.isCanceled()) {
                    throw new InterruptedException(Messages
                            .getString("CheckWithProgress.CANCELD"));
                }
            }
        } catch (Throwable t) {
            String message = getString(t);
            throw new InterruptedException(Messages
                    .getString("CheckWithProgress.11")
                    + "\n" + message);
        }

        // Logging
        if (Logger.isEnableSaveLog()) {
        	Logger logger = new Logger();
        	logger.saveLog(cfile, results);
        }
        
        // marker
        monitor.subTask(Messages.getString("CheckWithProgress.13"));
        output(file, results);
        monitor.worked(1);

        if (monitor.isCanceled()) {
            throw new InterruptedException(Messages
                    .getString("CheckWithProgress.14"));
        }
        monitor.worked(1);

        monitor.done();
    }

    /**
     * checkerClassNameで指定されたCheckerClassオブジェクトを獲得し、返す。
     * @param checkerClassName
     * @return
     */
    static CheckerClass createChecker(String checkerClassName) {
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
     * 行番号のみからマーカー作成
     * @param file
     * @param lineNumber
     * @param message
     * @param severity
     */
    static void createMarker(IFile file, int lineNumber, String message,
            int severity) {
        try {
            IMarker marker = file.createMarker(IMarker.PROBLEM);
            marker.setAttribute(IMarker.MESSAGE, message);
            marker.setAttribute(IMarker.SEVERITY, severity);
            if (lineNumber <= 0) {
                lineNumber = 1;
            }
            marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
        } catch (CoreException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * offset を指定してマーカーを作成 TODO クラスにする
     * @param file
     * @param linenum
     * @param start
     * @param end
     * @param message
     * @param severity
     */
    public static void createMarker(IFile file, int linenum, int start,
            int end, String message, int severity) {
        try {
            // TODO use org.eclipse.ui.texteditor.MarkerUtilities
            IMarker marker = file.createMarker(IMarker.PROBLEM);
            marker.setAttribute(IMarker.CHAR_START, start);
            marker.setAttribute(IMarker.LINE_NUMBER, linenum);
            marker.setAttribute(IMarker.CHAR_END, end);
            marker.setAttribute(IMarker.MESSAGE, message);
            marker.setAttribute(IMarker.SEVERITY, severity);
        } catch (CoreException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 結果を元にマーカーを作成する
     * @param results
     */
    static void output(IFile file, List<Result> results) {
        for (Result result : results) {
            int start = result.getRange().getOffset();
            int end = start + result.getRange().getLength();
            int linenum = result.getLine();
            createMarker(file, linenum, start, end, result.getMessage(),
                    IMarker.SEVERITY_WARNING);
        }
    }
}
