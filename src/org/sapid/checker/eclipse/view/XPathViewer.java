package org.sapid.checker.eclipse.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
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
	private Text text_con = null;
	public static final String ASSIST_ACTION_ID = "XPath.Assist";
	private Button but_get = null;
	private Button but_chk = null;
	private Button but_pst = null;
	private Button but_clx = null;
	private Clipboard clipboard = null;
	private GetButtonListner gbl = new GetButtonListner();
	private ClearButtonListner clbl = new ClearButtonListner();
	private PstButtonListner pbl = new PstButtonListner();
	private XPathCheckListener xcl = new XPathCheckListener();
	private Set<IMarker> xpathMarkerSet = new HashSet<IMarker>();
	private Display display = null;
	private Label label3 = null;

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

	public Text getText_Con(){
		Text xpath = text_con;
		return xpath;
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

	public static XPathRule getTempRule (String xpath){
		String message;
		String id = "1";
		List<String> prerequisiteList = new ArrayList<String>();
		message = "xpath  (XPathViewer)";
		return new XPathRule(id, 3, message, prerequisiteList, xpath,
				Condition.PROHIBIT);

	}

	private static void addMarkers(IFile file, List<Result> results) {
		for (Result result : results) {
			Range range = result.getRange();
			int start = range.getOffset();
			int end = start + range.getLength();
			int line = range.getStartLine();
			CheckWithProgress.createMarker(file, line, start, end, result
					.getMessage(), IMarker.SEVERITY_WARNING);
		}
	}

	public void deleteMarkers(){
		ITextEditor activeEditor = getActiveEditor();
		if (activeEditor == null) {
			return;
		}

		ITextSelection selection = getSelection(activeEditor);
		if (selection == null) {
			return;
		}

		IFile file = getFile(activeEditor);
		try {
			file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			CheckerActivator.log(e);
		}
	}

	//キー入力時のXPath評価
	public void Traverseselected(){
		text_con.setBackground(new Color(display,255,255,255));
		label3.setText("");

		ITextEditor activeEditor = getActiveEditor();
		if (activeEditor == null) {
			return;
		}

		ITextSelection selection = getSelection(activeEditor);
		if (selection == null) {
			return;
		}

		IFile file = getFile(activeEditor);

		String fullPath = file.getRawLocation().toString();

		try {
			org.sapid.checker.core.IFile target = IFileFactory
			.create(fullPath);

			XPathChecker checker = new XPathChecker();

			String xpath = text_con.getText().trim();


			if (!xpath.equals("")) {
				addMarkers(file, checker.checkOneRule(target, getTempRule(xpath)));
			}
			if (checker.checkOneRule(target, getTempRule(xpath)).size() == 0 ){
				text_con.setBackground(new Color(display,200, 240, 240));
				label3.setText("検出箇所がありません");
			}
		} catch (ParseException ex) {
			CheckerActivator.log(ex);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (IllegalArgumentException ex) {
			text_con.setBackground(new Color(display,246, 172, 172));
			label3.setText("構文エラーです");

		}
	}


	/**
	 * クリアボタン
	 * @author r-mizuno
	 */
	class ClearButtonListner implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			Button pushedButton = (Button) e.getSource();

			if (pushedButton == but_clx) {
				text_con.setText("");
			}
		}
	}

	class XPathCheckListener implements TraverseListener{

		public void keyTraversed(TraverseEvent e) {
			if (e.detail == SWT.TRAVERSE_TAB_NEXT) {
				if (text_con.getText().trim().length() > 0) {
					but_pst.setEnabled(true);
					deleteMarkers();
					Traverseselected();


				} else {
					but_pst.setEnabled(false);
					deleteMarkers();
					text_con.setBackground(new Color(display,255,255,255));
					label3.setText("");
				}

				if (text_con.getText().trim().length() > 0) {
					but_chk.setEnabled(true);
				} else {
					but_chk.setEnabled(false);
					deleteMarkers();
				}
			}
		}
	}

	/**
	 * 取得ボタン
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
				String xpath = new NodeOffsetUtil(cfile.getDOM(), start).getXPath();
				//new Offset2XPath(cfile.getDOM(), start).getXPathFromOffset();
				text_con.setText(xpath);
			} catch (ParseException e) {
				CheckerActivator.log(e);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * コピーボタン
	 * @author r-mizuno
	 */
	class PstButtonListner implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			String data = format("", text_con.getText());
			clipboard.setContents(new Object[] { data },
					new Transfer[] { TextTransfer.getInstance() });
		}

		private String format(String pre, String con) {
			String res = null;
			if (pre.length() > 0 && con.length() > 0) {
				res = "<oneRule>" + NEW_LINE_CODE + "\t<level></level>"
				+ NEW_LINE_CODE + "\t" + "<content></content>"
				+ NEW_LINE_CODE + "\t<prerequisite>" + pre
				+ "</prerequisite>" + NEW_LINE_CODE + "\t<xpath>" + con
				+ "</xpath>" + NEW_LINE_CODE
				+ "\t<condition></condition>" + NEW_LINE_CODE
				+ "</oneRule>" + NEW_LINE_CODE;

			} else if (con.length() > 0) {
				res = "<oneRule>" + NEW_LINE_CODE + "\t<level></level>"
				+ NEW_LINE_CODE + "\t" + "<content></content>"
				+ NEW_LINE_CODE + "\t<xpath>" + con + "</xpath>"
				+ NEW_LINE_CODE + "\t<condition></condition>"
				+ NEW_LINE_CODE + "</oneRule>" + NEW_LINE_CODE;
			}

			return res;
		}
	}

	/**
	 * The constructor.
	 */
	public XPathViewer() {
	}

	public void createPartControl(Composite parent) {
		Label label2;
		display = parent.getDisplay();
		clipboard = new Clipboard(display);

		GridData griddata = new GridData(GridData.FILL_BOTH);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);

		label2 = new Label(parent, SWT.NONE);
		label2.setText(Messages.getString("XPathViewer.3"));

		text_con = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL
				| SWT.WRAP);

		AssistField af = new AssistField();
		try {
			af.createContents(parent, text_con);
		} catch (org.eclipse.jface.bindings.keys.ParseException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		text_con.setLayoutData(griddata);
		text_con.addTraverseListener(xcl);

		but_clx = new Button(parent, SWT.NONE);
		but_clx.setText(Messages.getString("XPathViewer.CLEAR"));
		but_clx.addSelectionListener(clbl);

		but_get = new Button(parent, SWT.NONE);
		but_get.setText(Messages.getString("XPathViewer.5"));
		but_get.addSelectionListener(gbl);


		label3 = new Label(parent, SWT.NONE);
		label3.setLayoutData(data);

		but_pst = new Button(parent, SWT.NONE);
		but_pst.setText(Messages.getString("XPathViewer.6"));
		but_pst.addSelectionListener(pbl);


		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		parent.setLayout(gridLayout);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
}