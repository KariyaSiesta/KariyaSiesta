package org.sapid.checker.rule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Range;
import org.sapid.checker.core.Result;
import org.sapid.checker.rule.xpath.CXCheckerNamespaceContext;
import org.sapid.checker.rule.xpath.CXCheckerXPathFunctionResolver;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XPathChecker implements CheckerClass {

	private final static String XML_SRC = "src";
	private NodeList foundNodes;

	public NodeList getfound() {
		return foundNodes;
	}

	private final static XPathFactory xpathFactory = XPathFactory.newInstance();

	static {
		xpathFactory
				.setXPathFunctionResolver(new CXCheckerXPathFunctionResolver());
	}

	public List<Result> check(IFile file, CheckRule rule) {

		List<Result> results = new ArrayList<Result>();
		String filename = rule.getValue(XML_SRC);

		if (filename == null) {
			throw new IllegalArgumentException(this.getClass().getName()
					+ " : ルールXMLの" + XML_SRC + "パラメータが指定されていません。");
		}

		XPathRule[] rules = null;
		try {
			rules = XPathRule.parseRuleXML(filename);
		} catch (ParserConfigurationException e) {
			throw new IllegalArgumentException(this.getClass().getName()
					+ " : ルールXMLの" + filename + "が不正です。");
		} catch (SAXException e) {
			throw new IllegalArgumentException(this.getClass().getName()
					+ " : ルールXMLの" + filename + "が不正です。");
		} catch (IOException e) {
			throw new IllegalArgumentException(this.getClass().getName()
					+ " : " + e.toString());
		}

		for (int i = 0; i < rules.length; i++) {
			try {
				results.addAll(checkOneRule(file, rules[i]));
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		}

		return results;
	}

	public List<Result> checkOneRule(IFile file, XPathRule rule)
			throws XPathExpressionException {
		XPath xpath = XPathChecker.xpathFactory.newXPath();
		xpath.setNamespaceContext(new CXCheckerNamespaceContext());

		List<Result> results = new ArrayList<Result>();
		Node root = file.getDOM();
		XPathExpression xpathExpression = xpath.compile(rule.getXpath());

		switch (rule.getCondition()) {
		case REQUIRE:
			NodeList nodes = (NodeList) xpathExpression.evaluate(root,
					XPathConstants.NODESET);
			if (nodes.getLength() == 0) {
				Range range;
				range = new Range(0, 0, 0, 0, 0, 0);
				results.add(new Result(rule.getId(), range, rule.getLevel(),
						rule.getMessage()));
			}

			break;
		case PROHIBIT:
			foundNodes = (NodeList) xpathExpression.evaluate(root,
					XPathConstants.NODESET);
			for (int i = 0; i < foundNodes.getLength(); i++) {
				results.add(new Result(rule.getId(), new NodeOffsetUtil(file
						.getDOM(), foundNodes.item(i)).getRange(), rule
						.getLevel(), rule.getMessage()));
			}

			break;
		default:
			break;
		}
		return results;
	}

}
