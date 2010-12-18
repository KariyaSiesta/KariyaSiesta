package org.sapid.checker.eclipse.codeassist.dtd;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.ITextEditor;
import org.sapid.checker.eclipse.view.XPathViewer;

import com.wutka.dtd.DTD;
import com.wutka.dtd.DTDParser;

public class DTDInfo {
	DTD dtd = null;
	String dtdpath = null;
	File dtdfile = null;
	DTDParser dtdparser = null;


	public DTDInfo(){
		DTDParser parser = null;
		parser = getDTDParser();
		try {
			dtd = parser.parse();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public DTDInfo(String forTest){
	}

	public DTD getDTD(){
		return dtd;
	}

	public void SetDTD(DTD dtd){
		this.dtd = dtd;
	}

	private String getDTDPath(){
		ITextEditor activeEditor = XPathViewer.getActiveEditor();
		IFile file = XPathViewer.getFile(activeEditor);
		Visitor visitor = new Visitor();
		IProject project = file.getProject();

		try {
			project.accept(visitor);
			dtdpath = visitor.getDTDPath();
		} catch (CoreException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return dtdpath;
	}

	private File getDTDFile(){
		dtdfile = new File(getDTDPath());
		return dtdfile;
	}

	private DTDParser getDTDParser(){
		try {
			dtdparser = new DTDParser(getDTDFile());
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return dtdparser;
	}



}
