package org.sapid.checker.eclipse.view;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.sapid.checker.core.IFileFactory;
import org.sapid.checker.core.Range;
import org.sapid.checker.core.Result;
import org.sapid.checker.eclipse.progress.CheckWithProgress;
import org.sapid.checker.rule.js.JsRule;
import org.sapid.parser.common.ParseException;

public class JsConsole extends ViewPart {
	private JsRule jsRule = new JsRule();

	private IFile getFile(ITextEditor editor) {
		return ((IFileEditorInput) editor.getEditorInput()).getFile();
	}

	private ITextEditor getActiveEditor() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
				.getActivePage();
		IEditorPart activeEditor = page.getActiveEditor();
		if (!(activeEditor instanceof ITextEditor))
			return null;
		return (ITextEditor) activeEditor;
	}

	private IFile getEclipseIFile() {
		ITextEditor activeEditor = getActiveEditor();
		return getFile(activeEditor);
	}

	private org.sapid.checker.core.IFile getSapidIFile() {
		String fullPath = this.getEclipseIFile().getRawLocation().toString();

		try {
			return IFileFactory.create(fullPath);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private String eval(String code) {
		if (code.equals("")) {
			return "\r";
		}
		try {
			JsRule.ResultSet set = jsRule.eval(getSapidIFile(), code);
			this.addMarks(set.results);

			if (set.object == null) {
				return "\r";
			} else {
				return "\r>>> " + set.object.toString() + "\r";
			}
		} catch (Exception e) {
			return "Exception:" + e.toString();
		}
	}

	private void addMarks(List<Result> results) {
		for (Result result : results) {
			Range range = result.getRange();
			int start = range.getOffset();
			int end = start + range.getLength();
			int line = range.getStartLine();
			CheckWithProgress.createMarker(this.getEclipseIFile(), line, start,
					end, result.getMessage(), IMarker.SEVERITY_WARNING);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		TextViewer viewer = new TextViewer(parent, SWT.V_SCROLL);
		final StyledText styledText = viewer.getTextWidget();

		styledText.setWordWrap(true);
		styledText.addVerifyKeyListener(new VerifyKeyListener() {
			@Override
			public void verifyKey(VerifyEvent event) {
				if (event.keyCode == '\r') {
					int currentOffset = styledText.getCaretOffset();
					int lineno = styledText.getLineAtOffset(currentOffset);
					String code = styledText.getLine(lineno);
					styledText.append(eval(code));
					styledText.setSelection(styledText.getCharCount());
					event.doit = false;
				}
			}
		});

		viewer.setDocument(new Document());
	}

	@Override
	public void setFocus() {
	}
}
