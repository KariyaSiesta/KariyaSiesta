package org.sapid.checker.eclipse.progress;

import java.io.IOException;
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
import org.sapid.checker.core.Logger;
import org.sapid.checker.core.Result;
import org.sapid.checker.eclipse.CheckerActivator;
import org.sapid.checker.eclipse.Messages;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.CheckRuleParser;
import org.sapid.parser.common.ParseException;

public class CheckAllWithProgress implements IRunnableWithProgress {
    public String fileName = null;
    public String ruleXML = null;
    private String projName = null;
    IFile file;
    List<IFile> jspFiles;

    public void setFile(List<IFile> files) {
        jspFiles = files;
    }

    public void setProjectName(String name) {
        projName = name;
    }

    public void setRuleXML(String fName) {
        ruleXML = fName;
    }

    public void run(IProgressMonitor monitor) throws InvocationTargetException,
            InterruptedException {
        int failJsp = 0;
        if (jspFiles == null || projName == null || ruleXML == null)
            throw new InterruptedException(Messages
                    .getString("CheckAllWithProgress.2"));

        monitor.beginTask(Messages.getString("CheckAllWithProgress.1")
                + projName, jspFiles.size() + 1);

        // rules
        List<CheckRule> rules;
        try {
            rules = CheckRuleParser.parseRuleXML(ruleXML);
        } catch (Throwable t) {
            CheckerActivator.log(t);
            throw new InterruptedException(Messages
                    .getString("CheckAllWithProgress.1"));
        }
        monitor.worked(1);

        // files
        for (IFile file : jspFiles) {
            if (monitor.isCanceled()) {
                throw new InterruptedException(Messages
                        .getString("CheckWithProgress.CANCELD"));
            }
            monitor.subTask(Messages.getString("CheckAllWithProgress.0")
                    + file.getName());
            failJsp += checkFile(file, rules);
            monitor.worked(1);
        }

        // errors
        if (failJsp > 0) {
            throw new InterruptedException(Messages
                    .getString("CheckAllWithProgress.4")
                    + "\n\nProject: "
                    + projName
                    + "\n"
                    + Messages.getString("CheckAllWithProgress.8")
                    + failJsp
                    + "\n\n" + Messages.getString("CheckAllWithProgress.11"));
        }

        monitor.done();
    }

    /**
     * ファイルとルールを与えてチェックを実行する
     * @param file
     * @param rules
     * @return 成功したら 0 失敗したら 1 を返す
     * @throws InterruptedException
     */
    private int checkFile(IFile file, List<CheckRule> rules)
            throws InterruptedException {
        String fileName = file.getRawLocation().toString();

        try {
            file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
        } catch (CoreException e) {
            CheckerActivator.log(e);
        }

        // file
        org.sapid.checker.core.IFile targetFile = null;
        try {
            targetFile = IFileFactory.create(fileName);
        } catch (ParseException e) {
            CheckWithProgress.createMarker(file, 0, Messages
                    .getString("CheckWithProgress.5"),
                    IMarker.SEVERITY_ERROR);
            return 1;
        } catch (IOException e) {
            CheckWithProgress.createMarker(file, 0, Messages
                    .getString("CheckWithProgress.6"),
                    IMarker.SEVERITY_ERROR);
            return 1;
        }

        // rules
        ArrayList<Result> results = new ArrayList<Result>();
        for (CheckRule rule : rules) {
            String moduleName = rule.getName();
            CheckerClass Checker = CheckWithProgress.createChecker(moduleName);
            results.addAll(Checker.check(targetFile, rule));
        }

        // Logging
        if (Logger.isEnableSaveLog()) {
        	Logger logger = new Logger();
        	logger.saveLog(targetFile, results);
        }
        
        // marker
        CheckWithProgress.output(file, results);
        return 0;
    }

}
