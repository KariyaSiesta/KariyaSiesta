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
 * MISRA-C �롼�� 96 �ؿ����ޥ��������ˤ�����������Τȥѥ�᡼���Τ��줾��Υ��󥹥��󥹤ˤϳ�̤�Ĥ��ʤ���Фʤ�ʤ�
 * 
 * @author Eiji Hirumuta
 */
public class Misra96 implements CheckerClass {
	/** �롼��Υ�٥� */
	private final static int LEVEL = 1;

	/** �롼��Υ�å����� */
	private final static String MESSAGE = "MISRA-C Rule 96";

	/** ������� */
	List<Result> results = new ArrayList<Result>();

	/** ��ȿ�Ȥ��Ƹ��Ф���Ρ��ɤν��� */
	Set<Element> problemNodes = new HashSet<Element>();

	/*
	 * �ե�����Υ롼������å����˸ƤФ��
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
			// �ޥ���Υѥ�����ȥܥǥ��� XML ���Ѵ�����
			Element pattern = null;
			Element body = null;
			try {
				pattern = makeDOM(d.getMacroPattern());
				body = makeDOM(d.getMacroBody());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// �ޥ���ѥ����󤫤�����Υꥹ�Ȥ��������
			List<String> args = new ArrayList<String>();
			args = createArgumentList(pattern);
			// ����������ʤ��Ȥ�
			if (args.size() == 0) {
				break;
			}
			// (1) MacroBody ��¸�ߤ����������̤Ǥ������Ƥ��뤫�����å�
			if (!(checkParenParams(body, args))) {
				problemNodes.add(d.getElem());
			}
			// (2) MacroBody ���Τ���̤Ǥ������Ƥ��뤫�����å�
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
	 * �����Υꥹ�Ȥ��֤�
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
	 * �ޥ�����������¸�ߤ���������̤������äƤ��뤫�����å�
	 * 
	 * @return
	 */
	private boolean checkParenParams(Element root, List<String> args) {
		// ���ѿ�������γ�̤��ǧ
		NodeList words = root.getElementsByTagName("word");
		for (int i = 0; i < words.getLength(); i++) {
			// �������ɤ��������å�����
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
	 * �ޥ���������Τ��̤������äƤ��뤫�����å�
	 * 
	 * @return
	 */
	private boolean checkParenEntire(Element root, List<String> args) {
		NodeList childs = root.getChildNodes();
		if (childs.getLength() <= 3) {
			// 3�İʲ��λ��ϡ��ǽ�ȺǸ���ǧ����
			if (root.getFirstChild().getTextContent().equals("(")
					&& root.getLastChild().getTextContent().equals(")")) {
				return true;
			}
		} else {
			// 3�Ĥ��¿���������Υ롼����������ˤϡ��ʲ���2�Ĥ�ɬ��������
			// 1.�Ϥ�� ( �μ��ˤ�ɬ�� �����ǤϤʤ���� �����
			// 2.������ ) �����ˤ�ɬ�� �����ǤϤʤ���� �����
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
	 * ʸ�������Ϥ��ơ�XML���֤�
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
			// DOM ���Ѵ�
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