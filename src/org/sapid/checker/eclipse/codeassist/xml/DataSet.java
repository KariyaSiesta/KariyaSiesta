package org.sapid.checker.eclipse.codeassist.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.sapid.checker.core.IFileFactory;
import org.sapid.parser.common.ParseException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public	class DataSet {

	ArrayList<Data> nodelist = new ArrayList<Data>();
	ArrayList<Data> attrlist = new ArrayList<Data>();
	ArrayList<Data> attrvallist = new ArrayList<Data>();
	ArrayList<Data> funclist = new ArrayList<Data>();
	ArrayList<Data> axislist = new ArrayList<Data>();
	ArrayList<Data> alllist = new ArrayList<Data>();
	Node rootnode;
	NodeList nlist = null;

	public DataSet(){
		nlist = getNodeList();
	}

	public void SetNlist(NodeList nlist){
		this.nlist = nlist;
	}

	public DataSet(String test){

	}

	private IFile getFile(ITextEditor editor) {
		return ((IFileEditorInput) editor.getEditorInput()).getFile();
	}


	public static ITextEditor getActiveEditor() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
		.getActivePage();
		IEditorPart activeEditor = page.getActiveEditor();
		if (!(activeEditor instanceof ITextEditor))
			return null;
		return (ITextEditor) activeEditor;
	}



	private NodeList getNodeList(){
		try {
			ITextEditor activeEditor = getActiveEditor();
			IFile file = getFile(activeEditor);
			String fullPath = file.getRawLocation().toString();

			org.sapid.checker.core.IFile target = IFileFactory.create(fullPath);
			this.nlist = target.getDOM().getElementsByTagName("*");
			rootnode = target.getDOM().getDocumentElement();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nlist;


	}

	public ArrayList<Data> getAllNode(){
		Data[] nodedata = null;
		NodeList nlist = null;
		ArrayList<String> templist = new ArrayList<String>();
		ArrayList<String> list = new ArrayList<String>();

			nlist = this.nlist;
			for(int i = 0; i < nlist.getLength(); i++){
				templist.add(nlist.item(i).getNodeName());
			}
			list = Reallocate(templist);
			nodedata = new Data[list.size()];
			for(int i = 0; i < list.size(); i++){
				nodedata[i] = new Data(list.get(i), "node");
				nodelist.add(nodedata[i]);
			}

		return nodelist;
	}

	public ArrayList<Data> getAllAttribute(){
		Data[] attrdata = null;
		NodeList nlist = null;
		ArrayList<String> templist = new ArrayList<String>();
		ArrayList<String> list = new ArrayList<String>();
			nlist = this.nlist;
			for(int i = 0; i < nlist.getLength(); i++){
				for(int j = 0; j < nlist.item(i).getAttributes().getLength(); j++){
					templist.add(nlist.item(i).getAttributes().item(j).getNodeName());
				}
			}
			list = Reallocate(templist);
			attrdata = new Data[list.size()];
			for(int i = 0; i < list.size(); i++){
				attrdata[i] = new Data(list.get(i), "attribute");
				attrlist.add(attrdata[i]);
			}

		return attrlist;
	}

	public ArrayList<Data> getAllAttributevalue(){
		NodeList nlist = null;
		ArrayList<String> valuelist = new ArrayList<String>();
		ArrayList<String> attrnamelist = new ArrayList<String>();

			nlist = this.nlist;
			for(int i = 0; i < nlist.getLength(); i++){
				for(int j = 0; j < nlist.item(i).getAttributes().getLength(); j++){
					valuelist.add(nlist.item(i).getAttributes().item(j).getNodeValue());
					attrnamelist.add(nlist.item(i).getAttributes().item(j).getNodeName());
				}
			}
			attrvallist = makeAttrData(valuelist, attrnamelist);
		return attrvallist;
	}

	private ArrayList<Data> makeAttrData(ArrayList<String> valuelist, ArrayList<String> attnamerlist){
		Data[] before = null;

		before = new Data[valuelist.size()];
		HashSet<Data> hash = new HashSet<Data>();
		ArrayList<Data> realloclist = new ArrayList<Data>();

		for(int i = 0; i < valuelist.size(); i++){
			before[i] = new Data(valuelist.get(i), "attrvalue", attnamerlist.get(i));
			hash.add(before[i]);
		}
		realloclist.addAll(hash);
		return realloclist;
	}

	/* 重複する要素を候補から除外 */
	private ArrayList<String> Reallocate(List<String> beforelist) {
		ArrayList<String> afterlist = new ArrayList<String>();
		for (int i = 0; i < beforelist.size(); i++) {
			if (afterlist.size() == 0 && !beforelist.get(i).equals("#text")) {
				afterlist.add(beforelist.get(i));
			} else {
				for (int j = 0; j < afterlist.size(); j++) {
					if (beforelist.get(i).equals(afterlist.get(j))) {
						break;
					}
					if (j == afterlist.size() - 1 && !beforelist.get(i).equals("#text")) {
						afterlist.add(beforelist.get(i));
					}
				}
			}
		}
		return afterlist;
	}

	public Node getRootNode(){
		return rootnode;
	}

	public ArrayList<Data> getAll(){
		alllist.addAll(getAllNode());
		alllist.addAll(getAllAttribute());
		alllist.addAll(getAllAttributevalue());

		return alllist;
	}
}

