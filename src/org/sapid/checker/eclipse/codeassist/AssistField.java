package org.sapid.checker.eclipse.codeassist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.texteditor.ITextEditor;
import org.sapid.checker.core.IFileFactory;
import org.sapid.checker.eclipse.codeassist.dtd.DTDControl;
import org.sapid.checker.eclipse.codeassist.parsing.Concrete;
import org.sapid.checker.eclipse.codeassist.parsing.Token;
import org.sapid.checker.eclipse.codeassist.xml.DataSet;

import org.sapid.checker.rule.XPathChecker;
import org.sapid.parser.common.ParseException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.sapid.checker.eclipse.view.XPathViewer;

public class AssistField {
	ArrayList<Token>tokenlist = new ArrayList<Token>();

	/**********************************************************************/

	public Control createContents(Composite parent, final Text text_con, final Text text_pre) throws org.eclipse.jface.bindings.keys.ParseException, ParseException {

		IControlContentAdapter contentAdapter = new TextContentAdapter();
		IContentProposalProvider provider = new IContentProposalProvider() {
			private IContentProposal[] contentProposals={};

			public IContentProposal[] getProposals(String contents, int position) {

				try {
					String xpath = text_con.getText().substring(0, position);
					String prerequisite = text_pre.getText().trim();
					String nonspacexpath = xpath.replaceAll("\r\n", "");


					ITextEditor activeEditor = XPathViewer.getActiveEditor();
					IFile file = XPathViewer.getFile(activeEditor);
					String fullPath = file.getRawLocation().toString();

					org.sapid.checker.core.IFile target = null;
					try {
						target = IFileFactory .create(fullPath);
					} catch (ParseException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}

					/*******構文規則から*******/

					Concrete ca = new Concrete();
					DTDControl dc = new DTDControl();
					ArrayList<Token> allnodelist = dc.getAllNodes();
					for(int i = 0; i < allnodelist.size(); i++){
						ca.addTokenList(allnodelist.get(i).getToken(), allnodelist.get(i).getSort(), "rule");
					}
					tokenlist = ca.getCandidate(xpath);
					/***************************/



					/*******nodeが来るタイミング*********/
					String targetString = "";
					boolean tokenflag = false;   // nodeの途中で補完
					boolean halftokenflag = false;
					for(int i = 0; i < tokenlist.size(); i++){
						if(tokenlist.get(i).getSort().equals("node")){
							if(tokenlist.get(i).getOffset() == 0){
								tokenflag = true;
							} else{
								tokenflag = true;
								halftokenflag = true;
							}

							break;
						}
					}

					/**********XPathの切り分け************/
					ArrayList<String> xpathlist = new ArrayList<String>();
					ArrayList<Integer> sinteger = new ArrayList<Integer>();
					int predcounter = 0;
					String s = "";

					sinteger.add(0);
					for(int i = 0; i < nonspacexpath.length(); i++){
						s = nonspacexpath.substring(i, i+1);
						if(s.equals("[")){
							sinteger.add(i);
							predcounter++;
						}else if(s.equals("(")){
							sinteger.add(i);
						}else if(s.equals(")")){
							sinteger.remove(sinteger.size()-1);
						}else if(s.equals("]")){
							sinteger.remove(sinteger.size()-1);
							predcounter--;
						}
					}


					for(int i = 0; i < sinteger.size(); i++){
						if(sinteger.size() == 1){
							xpathlist.add(nonspacexpath.concat("*"));
							break;
						}else if(i == 0){
							xpathlist.add(nonspacexpath.substring(sinteger.get(i),sinteger.get(i+1)));
						} else if(i == sinteger.size()-1){
							xpathlist.add(nonspacexpath.substring(sinteger.get(sinteger.size()-1)+1).concat("*"));
						} else{
							xpathlist.add(nonspacexpath.substring(sinteger.get(i)+1,sinteger.get(i+1)));
						}
					}

					int last = xpathlist.size()-1;


					for(int i = xpathlist.size()-1; i >= 0; i--){
						for(int j = xpathlist.get(i).length(); j > 0 ; j--){
							String partxpath = xpathlist.get(i).substring(j-1, j);
							ArrayList<Token> funclist = ca.getFuncList();
							if(j == 1){
								String temp = xpathlist.get(i).substring(j-1, xpathlist.get(i).length());
								for(int k = 0; k < funclist.size(); k++){
									if(temp.equals(funclist.get(k).getToken())){
										xpathlist.remove(i);
										last--;
									}
								}
							} else if(partxpath.equals("/")){
								String temp = xpathlist.get(i).substring(j, xpathlist.get(i).length());
								for(int k = 0; k < funclist.size(); k++){
									if(temp.equals(funclist.get(k).getToken())){
										xpathlist.set(i, xpathlist.get(i).substring(0, j-1));
									}
								}
							}

						}
					}

					// and orの除去
					if(xpathlist.get(xpathlist.size()-1).matches(".+(\\s+)(and|or)(\\s+).*") && xpathlist.size() > 0 && predcounter > 0){
						xpathlist.set(xpathlist.size()-1, xpathlist.get(xpathlist.size()-1).replaceAll(".+(\\s+)(and|or)\\s+", ""));
					}


					for(int i = 0; i < xpathlist.size(); i++){
						if(i != xpathlist.size()-1){
							xpathlist.set(i, xpathlist.get(i).concat("/"));
						}
						if(xpathlist.get(i).substring(0, 1).equals("/")){
							targetString = "";
						}
						targetString = targetString.concat(xpathlist.get(i));
					}


					/*******DTDから************/
					if(tokenflag == true){
						tlistfromDTD(targetString, dc);
					}


					/***************インスタンスから**********************/
					if(tokenflag == true){
						XPathChecker checker = new XPathChecker();
						if(halftokenflag == false){
							checker.checkOneRule(target, XPathViewer.getTempRule(prerequisite, targetString, false));
							NodeList nlist = checker.getfound();
							for(int i = tokenlist.size()-1; i+1 > 0; i--){
								for(int j = 0; j < nlist.getLength(); j++){
									nlist.item(j).getNodeName();
									if(tokenlist.get(i).getSort().equals("node")){
										if(tokenlist.get(i).getToken().equals(nlist.item(j).getNodeName())){
											tokenlist.get(i).setInfo("xml");
											break;
										}
									}
								}
							}
						} else if(halftokenflag == true){
							String strcmpxpath = null;
							String partnode = null;
							if(targetString.matches(".*[\\/].*")){
								partnode = extractMatchString("/([^\\/]*)\\*$", targetString);
								targetString = extractMatchString("(.*/+).+\\*$", targetString).concat("*");
							} else {
								partnode = extractMatchString("([^\\/]*)\\*$", targetString);
								targetString = "*";

							}
							checker.checkOneRule(target, XPathViewer.getTempRule(prerequisite, targetString, false));
							NodeList nodelist = checker.getfound();
							for(int i = nodelist.getLength()-1; i >= 0; i--){
								if(nodelist.item(i).getNodeName().length() >= partnode.length()){
									strcmpxpath = nodelist.item(i).getNodeName().substring(0, partnode.length());
								}
								if(partnode.equals(strcmpxpath)){
									for(int j = 0; j < tokenlist.size(); j++){
										if(tokenlist.get(j).getToken().equals(nodelist.item(i).getNodeName())){
											tokenlist.get(j).setInfo("xml");
										}
									}
								}
							}
						}
					}

					for(int i = tokenlist.size()-1; i >= 0 ; i--){
						if(tokenlist.get(i).getSort().equals("node") && tokenlist.get(i).getInfo().equals("rule")){
							tokenlist.remove(i);
						}

					}

					contentProposals = new IContentProposal[tokenlist.size()];
					for (int i = 0; i < tokenlist.size(); i++) {
						if(tokenlist.get(i).getInfo() != null){
							contentProposals[i] = new AssistProposal(tokenlist.get(i).getToken().concat(" : "+tokenlist.get(i).getInfo()), tokenlist.get(i).getSort(),
									tokenlist.get(i).getToken().length(), tokenlist.get(i).getOffset());
						}else {
							contentProposals[i] = new AssistProposal(tokenlist.get(i).getToken(), tokenlist.get(i).getSort(),
									tokenlist.get(i).getToken().length(), tokenlist.get(i).getOffset());
						}
					}

				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} catch (CloneNotSupportedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				return contentProposals;
			}

			private void tlistfromDTD(String targetString, DTDControl dtdcontrol) {
				// TODO 自動生成されたメソッド・スタブ
				DataSet dataset = new DataSet();
				String prenode = "";
				ArrayList<Token> dtdtokenlist = new ArrayList<Token>();
				String regex = "([^/]*)/{1,2}\\*$";
				Node rootnode = dataset.getRootNode();

				if(targetString.matches(".+/{1,2}\\*$")){
					prenode = extractMatchString(regex, targetString);
				}

				if(prenode.matches(".+\\[.*")){
					prenode = extractMatchString("([^\\[]*)", prenode);
				}

				if(targetString.equals("*")){
					dtdtokenlist = dtdcontrol.getDecendantNodes(rootnode.getNodeName());
				}else if(targetString.equals("/*")){

				}else if(targetString.matches(".+[^\\/][\\/]{1}[\\*]$")){
					dtdtokenlist = dtdcontrol.getChildNodes(prenode);
				}else if(targetString.matches(".+[\\/]{2}[\\*]$")){
					dtdtokenlist = dtdcontrol.getDecendantNodes(prenode);
				}

				for(int i = 0; i < dtdtokenlist.size(); i++){
					tokenlist.add(dtdtokenlist.get(i));
				}

			}

		};

		new ContentProposalAdapter(text_con, contentAdapter, provider, keystroke("Ctrl+Space"), null);

		return parent;

	}
	KeyStroke keystroke(String s) throws org.eclipse.jface.bindings.keys.ParseException, ParseException {
		return KeyStroke.getInstance(s);
	}

	private String extractMatchString(String regex, String target) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(target);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			throw new IllegalStateException("No match found.");
		}
	}


}
