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
    private NodeList found;

    public NodeList getfound(){
        return found;
    }

    private final static XPathFactory xpathFactory = XPathFactory.newInstance();
    
    static {
    	xpathFactory.setXPathFunctionResolver(new CXCheckerXPathFunctionResolver());
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
            results.addAll(checkOneRule(file, rules[i]));
        }

        return results;
    }

    public List<Result> checkOneRule(IFile file, XPathRule r) {
    	XPath xpath = XPathChecker.xpathFactory.newXPath();
    	xpath.setNamespaceContext(new CXCheckerNamespaceContext());
    	
        List<Result> results = new ArrayList<Result>();
        try {
            ArrayList<Node> preNodes = new ArrayList<Node>();
            ArrayList<String> preRules = (ArrayList<String>) r
                    .getPrerequisite();

            if (preRules.size() == 0) {
                preNodes.add(file.getDOM());
            } else {
                for (String prexpath : preRules) {
                    XPathExpression preXPathExpression = xpath.compile(prexpath);
                    NodeList list = (NodeList) preXPathExpression.evaluate(file.getDOM(), XPathConstants.NODESET);
                    for (int i = 0; i < list.getLength(); i++) {
                        preNodes.add(list.item(i));
                    }
                }
            }

            XPathExpression xpathExpression = xpath.compile(r.getXpath());
            
            switch (r.getCondition()) {
            case REQUIRE: {
                for (Node n : preNodes) {
                    NodeList nodes = (NodeList) xpathExpression.evaluate(n, XPathConstants.NODESET);
                    if (nodes.getLength() == 0) {
                        Range range;
                        if (preRules.size() == 0) {
                            range = new Range(0, 0, 0, 0, 0, 0);
                        } else {
                            range = new NodeOffsetUtil(file.getDOM(), n)
                                    .getRange();
                            // new Node2Range(n).getRangeFromNode();
                        }
                        results.add(new Result(r.getId(), range, r.getLevel(),
                                r.getMessage()));
                    }
                }
            }
                break;
            case PROHIBIT: {
                for (Node n : preNodes) {
                    found = (NodeList) xpathExpression.evaluate(n, XPathConstants.NODESET);
                    for (int i = 0; i < found.getLength(); i++) {
                        results.add(new Result(r.getId(),
                        // new Node2Range(found.item(i)).getRangeFromNode(),
                                new NodeOffsetUtil(file.getDOM(), found.item(i)).getRange(),
                                r.getLevel(), r.getMessage()));
                    }
                }
            }
                break;
            default:
                break;
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return results;
    }

}
