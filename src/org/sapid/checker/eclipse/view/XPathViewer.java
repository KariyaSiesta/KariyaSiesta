package org.sapid.checker.eclipse.view;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.sapid.checker.core.IFileFactory;
import org.sapid.checker.core.Range;
import org.sapid.checker.core.Result;
import org.sapid.checker.eclipse.CheckerActivator;
import org.sapid.checker.eclipse.Messages;
import org.sapid.checker.eclipse.codeassist.AssistField;
import org.sapid.checker.eclipse.progress.CheckWithProgress;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.sapid.checker.rule.XPathChecker;
import org.sapid.checker.rule.XPathRule;
import org.sapid.checker.rule.XPathRule.Condition;
import org.sapid.parser.common.ParseException;

public class XPathViewer extends ViewPart {
	private Text xpathEditor = null;
	private Text xpathDisplay = null;
	public static final String ASSIST_ACTION_ID = "XPath.Assist";
	private Button getButton = null;
	private Button copyButton = null;
	private Button clearXPathButton = null;
	private Clipboard clipboard = null;
	private GetButtonListner getButtonListener = new GetButtonListner();
	private ClearButtonListner clearButtonListener = new ClearButtonListner();
	private CopyButtonListner copyButtonListener = new CopyButtonListner();
	private KeyListener xpathCheckListener = new XPathCheckListener();
	private Set<IMarker> xpathMarkerSet = new HashSet<IMarker>();
	private Display display = null;
	private Label outputLabel = null;

	private static final String NEW_LINE_CODE = System
			.getProperty("line.separator");

	public static ITextEditor getActiveEditor() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
				.getActivePage();
		IEditorPart activeEditor = page.getActiveEditor();
		if (!(activeEditor instanceof ITextEditor))
			return null;
		return (ITextEditor) activeEditor;
	}

	private static ITextSelection getSelection(ITextEditor editor) {
		IEditorSite editorSite = editor.getEditorSite();
		ISelection selection = editorSite.getSelectionProvider().getSelection();
		if (!(selection instanceof ITextSelection)) {
			return null;
		}
		return (ITextSelection) selection;
	}

	public static IFile getFile(ITextEditor editor) {
		return ((IFileEditorInput) editor.getEditorInput()).getFile();
	}

	@Override
	public void dispose() {
		// XPathチェック機能でつけたマーカーを除去
		removeXPathMarkers();
		super.dispose();
	}

	protected void removeXPathMarkers() {
		for (IMarker marker : xpathMarkerSet) {
			try {
				marker.delete();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		xpathMarkerSet = new HashSet<IMarker>();
	}

	public static XPathRule getTempRule(String xpath) {
		String message = "xpath  (XPathViewer)";
		String id = "1";
		return new XPathRule(id, 3, message, xpath, Condition.PROHIBIT);
	}

	private static void addMarkers(IFile file, List<Result> results) {
		for (Result result : results) {
			Range range = result.getRange();
			int start = range.getOffset();
			int end = start + range.getLength();
			int line = range.getStartLine();
			CheckWithProgress.createMarker(file, line, start, end,
					result.getMessage(), IMarker.SEVERITY_WARNING);
		}
	}

	public void deleteMarkers() {
		ITextEditor activeEditor = getActiveEditor();
		if (activeEditor == null) {
			return;
		}

		IFile file = getFile(activeEditor);
		try {
			file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			CheckerActivator.log(e);
		}
	}

	// キー入力時のXPath評価
	public void Traverseselected() {
		ITextEditor activeEditor = getActiveEditor();
		if (activeEditor == null) {
			return;
		}

		IFile file = getFile(activeEditor);
		String fullPath = file.getRawLocation().toString();

		try {
			org.sapid.checker.core.IFile target = IFileFactory.create(fullPath);
			XPathChecker checker = new XPathChecker();
			String xpath = xpathEditor.getText().trim();

			try {
				List<Result> result = checker.checkOneRule(target,
						getTempRule(xpath));
				addMarkers(file, result);

				if (result.size() == 0) {
					xpathEditor
							.setBackground(new Color(display, 255, 255, 180));
					outputLabel.setText("検出箇所がありません");
				} else {
					outputLabel.setText(Integer.valueOf(result.size())
							.toString() + "個のノードが検出されました");
					xpathEditor
							.setBackground(new Color(display, 255, 255, 255));
				}
			} catch (XPathExpressionException e) {
				xpathEditor.setBackground(new Color(display, 255, 180, 180));
				outputLabel.setText("構文エラーです");
			}
		} catch (ParseException ex) {
			CheckerActivator.log(ex);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * クリアボタン
	 * 
	 * @author r-mizuno
	 */
	class ClearButtonListner implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			Button pushedButton = (Button) e.getSource();

			if (pushedButton == clearXPathButton) {
				xpathEditor.setBackground(new Color(display, 255, 255, 255));
				xpathEditor.setText("");
			}
		}
	}

	class XPathCheckListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			if (e.character == SWT.TAB || e.character == SWT.CR) {
				e.doit = false;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.character == SWT.TAB || e.character == SWT.CR) {
				deleteMarkers();

				if (xpathEditor.getText().trim().length() > 0) {
					copyButton.setEnabled(true);
					Traverseselected();
				} else {
					copyButton.setEnabled(false);
					xpathEditor
							.setBackground(new Color(display, 255, 255, 255));
					outputLabel.setText("");
				}
			}
		}
	}

	/**
	 * 取得ボタン
	 * 
	 * @author r-mizuno
	 */
	class GetButtonListner implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			getXPathformEditor();
		}

		private void getXPathformEditor() {
			ITextEditor activeEditor = getActiveEditor();
			if (activeEditor == null) {
				return;
			}

			ITextSelection selection = getSelection(activeEditor);
			if (selection == null) {
				return;
			}
			int start = selection.getOffset();

			IFile file = getFile(activeEditor);
			String fileFullPath = file.getRawLocation().toString();

			try {
				org.sapid.checker.core.IFile cfile = IFileFactory
						.create(fileFullPath);
				String xpath = new NodeOffsetUtil(cfile.getDOM(), start)
						.getXPath();
				xpathDisplay.setText(xpath);
			} catch (ParseException e) {
				CheckerActivator.log(e);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * コピーボタン
	 * 
	 * @author r-mizuno
	 */
	class CopyButtonListner implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			String data = format("", xpathEditor.getText());
			clipboard.setContents(new Object[] { data },
					new Transfer[] { TextTransfer.getInstance() });
		}

		private String format(String pre, String con) {
			String res = null;

			if (con.length() > 0) {
				res = "<oneRule>" + NEW_LINE_CODE + "\t<level></level>"
						+ NEW_LINE_CODE + "\t" + "<content></content>"
						+ NEW_LINE_CODE + "\t<xpath>" + con + "</xpath>"
						+ NEW_LINE_CODE + "\t<condition></condition>"
						+ NEW_LINE_CODE + "</oneRule>" + NEW_LINE_CODE;
			}

			return res;
		}
	}

	public XPathViewer() {
	}

	public void createPartControl(Composite parent) {
		display = parent.getDisplay();
		clipboard = new Clipboard(display);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		parent.setLayout(gridLayout);

		xpathEditor = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP);
		
		AssistField af = new AssistField();
		try {
			af.createContents(parent, xpathEditor);
		} catch (org.eclipse.jface.bindings.keys.ParseException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		xpathEditor.addKeyListener(xpathCheckListener);
		GridData griddata = new GridData(GridData.FILL_BOTH);
		griddata.verticalSpan = 2;
		xpathEditor.setLayoutData(griddata);
				
		clearXPathButton = new Button(parent, SWT.NONE);
		clearXPathButton.setText(Messages.getString("XPathViewer.CLEAR"));
		clearXPathButton.addSelectionListener(clearButtonListener);
		clearXPathButton.setLayoutData(new GridData());

		copyButton = new Button(parent, SWT.NONE);
		copyButton.setText(Messages.getString("XPathViewer.6"));
		copyButton.addSelectionListener(copyButtonListener);
		copyButton.setLayoutData(new GridData());
		
		outputLabel = new Label(parent, SWT.NONE);
		griddata = new GridData(GridData.FILL_HORIZONTAL);
		griddata.horizontalSpan = 2;
		outputLabel.setLayoutData(griddata);
		
		xpathDisplay = new Text(parent, SWT.SINGLE | SWT.BORDER);
		griddata = new GridData(GridData.FILL_HORIZONTAL);
		xpathDisplay.setLayoutData(griddata);
		
		getButton = new Button(parent, SWT.NONE);
		getButton.setText(Messages.getString("XPathViewer.5"));
		getButton.addSelectionListener(getButtonListener);
		getButton.setLayoutData(new GridData());
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
}