package org.sapid.checker.eclipse.view;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class JsConsole extends ViewPart {
	private String eval(String code) {
		return ">>>" + code;
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
					styledText.append("\r"+eval(code)+"\r");
					styledText.setCaretOffset(styledText.getCharCount());
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
