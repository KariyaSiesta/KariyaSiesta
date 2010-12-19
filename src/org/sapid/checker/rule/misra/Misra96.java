/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.rule.misra;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.cx.wrapper.CDefineElement;
import org.sapid.checker.cx.wrapper.CElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * MISRA-C ルール 96 関数型マクロの定義にそいて定義全体とパラメータのそれぞれのインスタンスには括弧をつけなければならない
 * 
 * @author Eiji Hirumuta
 */
public class Misra96 implements CheckerClass {
	/** ルールのレベル */
	private final static int LEVEL = 1;

	/** ルールのメッセージ */
	private final static String MESSAGE = "MISRA-C Rule 96";

	/** 検査結果 */
	List<Result> results = new ArrayList<Result>();

	/** 違反として検出するノードの集合 */
	Set<Element> problemNodes = new HashSet<Element>();

	/*
	 * ファイルのルールチェック時に呼ばれる
	 * 
	 * @return results
	 */
	public List<Result> check(IFile file, CheckRule rule) {
		List<Element> delemList = new ArrayList<Element>();
		NodeList nodeList = file.getDOM().getElementsByTagName("Define");
		for (int i = 0; i < nodeList.getLength(); i++) {
		    delemList.add((Element) nodeList.item(i));
		}
		
		// Get DefineElements
		for (Element element : delemList) {
			CDefineElement d = new CDefineElement(element);
			// マクロのパターンとボディを XML に変換する
			Element pattern = null;
			Element body = null;
			try {
				pattern = makeDOM(d.getMacroPattern());
				body = makeDOM(d.getMacroBody());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// マクロパターンから引数のリストを作成する
			List<String> args = new ArrayList<String>();
			args = createArgumentList(pattern);
			// 引数を持たないとき
			if (args.size() == 0) {
				break;
			}
			// (1) MacroBody に存在する引数が括弧でくくられているかチェック
			if (!(checkParenParams(body, args))) {
				problemNodes.add(d.getElem());
			}
			// (2) MacroBody 全体が括弧でくくられているかチェック
			if (!(checkParenEntire(body, args))) {
				problemNodes.add(d.getElem());
			}
		}

		// problemNodes.add(expressions[i].getElem());
		for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
			results.add(new Result(null, new NodeOffsetUtil(itr.next())
					.getRange(), LEVEL, MESSAGE));
		}
		return results;
	}

	/*
	 * 引数のリストを返す
	 * 
	 * @return
	 */
	private List<String> createArgumentList(Element root) {
		List<String> args = new ArrayList<String>();
		CElement croot = new CElement(root);
		Element[] words = croot.getChildrenNode("word");
		if (words.length > 1) {
			for (int i = 1; i < words.length; i++) {
				args.add(words[i].getTextContent());
			}
		}
		return args;
	}

	/*
	 * マクロ定義の中に存在する引数を括弧がくくっているかチェック
	 * 
	 * @return
	 */
	private boolean checkParenParams(Element root, List<String> args) {
		// 各変数の前後の括弧を確認
		NodeList words = root.getElementsByTagName("word");
		for (int i = 0; i < words.getLength(); i++) {
			// 引数かどうかチェックする
			if (args.contains(words.item(i).getTextContent())) {
				if (words.item(i).getPreviousSibling().getTextContent().equals(
						"(")
						&& words.item(i).getNextSibling().getTextContent()
								.equals(")")) {
					continue;
				}
				return false;
			}
		}
		return true;
	}

	/*
	 * マクロ定義全体を括弧がくくっているかチェック
	 * 
	 * @return
	 */
	private boolean checkParenEntire(Element root, List<String> args) {
		NodeList childs = root.getChildNodes();
		if (childs.getLength() <= 3) {
			// 3個以下の時は，最初と最後を確認する
			if (root.getFirstChild().getTextContent().equals("(")
					&& root.getLastChild().getTextContent().equals(")")) {
				return true;
			}
		} else {
			// 3個より多い時，このルールを満たすには，以下の2つを必ず満たす
			// 1.始めの ( の次には必ず 引数ではないもの が来る
			// 2.終わりの ) の前には必ず 引数ではないもの が来る
			if (root.getFirstChild().getTextContent().equals("(")
					&& root.getLastChild().getTextContent().equals(")")) {
				if (root.getFirstChild().getNextSibling().getNodeName().equals(
						"word")) {
					if (args.contains(root.getFirstChild().getNextSibling().getTextContent())) {
						return false;
					}
				}
				if (root.getLastChild().getPreviousSibling().getNodeName()
						.equals("word")) {
					if (args.contains(root.getLastChild().getPreviousSibling().getTextContent())) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
		return false;
	}

	/*
	 * 文字列を解析して，XMLを返す
	 * 
	 * @return
	 */
	public Element makeDOM(String b) throws IOException {
		String res = "<macrobody>";
		int token;

		StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(b));
		tokenizer.resetSyntax();
		tokenizer.wordChars('0', '9');
		tokenizer.wordChars('a', 'z');
		tokenizer.wordChars('A', 'Z');
		tokenizer.wordChars('_', '_');
		tokenizer.whitespaceChars(' ', ' ');
		tokenizer.whitespaceChars('\t', '\t');
		tokenizer.whitespaceChars('\n', '\n');
		tokenizer.whitespaceChars('\r', '\r');
		tokenizer.quoteChar('\'');
		tokenizer.quoteChar('\"');
		tokenizer.parseNumbers();
		tokenizer.eolIsSignificant(false);
		tokenizer.slashStarComments(true);
		tokenizer.slashSlashComments(true);

		while ((token = tokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
			switch (token) {
			case StreamTokenizer.TT_EOL:
				// System.out.println("<EOL/>");
				break;
			case StreamTokenizer.TT_NUMBER:
				// System.out.println("<number>" + tokenizer.nval +
				// "</number>");
				res += ("<number>" + tokenizer.nval + "</number>");
				break;
			case StreamTokenizer.TT_WORD:
				// System.out.println("<word>" + tokenizer.sval + "</word>");
				res += ("<word>" + tokenizer.sval + "</word>");
				break;
			case '\'':
				// System.out.println("<char>" + tokenizer.sval + "</char>");
				res += ("<char>" + tokenizer.sval + "</char>");
				break;
			case '\"':
				// System.out.println("<string>" + tokenizer.sval +
				// "</string>");
				res += ("<string>" + tokenizer.sval + "</string>");
				;
				break;
			default:
				// System.out.println("<token>" + (char) tokenizer.ttype +
				// "</token>");
				res += ("<token>" + (char) tokenizer.ttype + "</token>");
			}
		}
		res += "</macrobody>";

		// System.out.println(res);

		Element root = null;
		try {
			// DOM に変換
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			Document doc = builder
					.parse(new InputSource(new StringReader(res)));
			root = doc.getDocumentElement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return root;
	}
}