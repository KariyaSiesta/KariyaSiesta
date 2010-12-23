package org.sapid.checker.eclipse.codeassist.dtd;

import java.util.ArrayList;

import org.sapid.checker.eclipse.codeassist.parsing.Token;
import org.sapid.checker.eclipse.codeassist.xml.DataSet;

import com.wutka.dtd.DTD;
import com.wutka.dtd.DTDChoice;
import com.wutka.dtd.DTDElement;
import com.wutka.dtd.DTDItem;
import com.wutka.dtd.DTDName;
import com.wutka.dtd.DTDSequence;

public class DTDControl {
//	DTDInfo dtdinfo = new DTDInfo("test");
	DTDInfo dtdinfo = new DTDInfo();

	ArrayList<Token> decendantnodes = new ArrayList<Token>();

	public DTDControl(){

	}

	public DTDInfo getDTDInfo(){
		return dtdinfo;
	}
	public ArrayList<Token> getChildNodes(String node){
		ArrayList<Token> childnodes = new ArrayList<Token>();
		childnodes.clear();
		DTD dtd = dtdinfo.getDTD();
		Object[] objs = dtd.getItems();

		for (int i = 0; i < objs.length; i++) {
			if(objs[i] instanceof DTDElement){
				DTDElement obj = (DTDElement) objs[i];
				if(obj.name.equals(node)){
					if(obj.getContent() instanceof DTDChoice){
						DTDChoice dtdchoice = ((DTDChoice)obj.getContent());
						for(int j = 0; j < dtdchoice.getItemsVec().size(); j++){
							String childnode = ((DTDName)dtdchoice.getItemsVec().get(j)).value;
							childnodes.add(new Token(childnode, "node", "DTD", 0));
						}
					} else if (obj.getContent() instanceof DTDSequence) {
						DTDSequence dtdseq = ((DTDSequence)obj.getContent());
						for (DTDItem item : dtdseq.getItems()){
							childnodes.add(new Token(((DTDName)item).value, "node", "DTD", 0));
						}
					}
				}
			}
		}
		return childnodes;
	}


	private void getDecNodes(String node){
		String nodes;
		int size = getChildNodes(node).size();
		for(int i = 0; i < size; i++){

			if(getChildNodes(node).size() != 0){
				if(this.decendantnodes.size()!=0){
					for(int j = 0; j < this.decendantnodes.size(); j++){
						if(this.decendantnodes.get(j).getToken().equals(getChildNodes(node).get(i).getToken())){
							break;
						}else if(j == this.decendantnodes.size()-1){
							this.decendantnodes.add(new Token(getChildNodes(node).get(i).getToken(), "node", "DTD", 0));
							nodes = getChildNodes(node).get(i).getToken();
							getDecNodes(nodes);
						}
					}
				} else {
					this.decendantnodes.add(new Token(getChildNodes(node).get(i).getToken(), "node", "DTD", 0));
					nodes = getChildNodes(node).get(i).getToken();
					getDecNodes(nodes);
				}
			}
		}
	}

	public ArrayList<Token> getDecendantNodes(String node){
		this.decendantnodes = new ArrayList<Token>();
		getDecNodes(node);
		return this.decendantnodes;

	}
	public ArrayList<Token> getAllNodes(){
		DataSet dataset = new DataSet();
		ArrayList<Token> allnode = new ArrayList<Token>();
		allnode.add(new Token(dataset.getRootNode().getNodeName(), "node", "DTD", 0));
		allnode.addAll(getDecendantNodes(dataset.getRootNode().getNodeName()));
		return allnode;
	}
}
