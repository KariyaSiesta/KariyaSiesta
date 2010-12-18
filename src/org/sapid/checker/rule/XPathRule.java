package org.sapid.checker.rule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XPathRule {

    public enum Condition {

        REQUIRE("require"), PROHIBIT("prohibit");

        private String name;

        private Condition(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    private String id;

    private String xpath;

    private List<String> prerequisite;

    private int level;

    private String message;

    private Condition condition;

    private String filePath = null;

    public static final String REQUIRE = "require";

    public static final String PROHIBIT = "prohibit";

    public XPathRule(String id, int level, String message,
            List<String> prerequisite, String xpath, Condition condition) {
        super();
        this.id = id;
        this.xpath = xpath;
        this.prerequisite = prerequisite;
        this.level = level;
        this.message = message;
        this.condition = condition;
    }

    public XPathRule(String id, int level, String message,
            List<String> prerequisite, String xpath, Condition condition,
            String filePath) {
        super();
        this.id = id;
        this.xpath = xpath;
        this.prerequisite = prerequisite;
        this.level = level;
        this.message = message;
        this.condition = condition;
        this.filePath = filePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public List<String> getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(List<String> prerequisite) {
        this.prerequisite = prerequisite;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * XPath のルール XML をパースするメソッド
     * @param filename
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static XPathRule[] parseRuleXML(String filename)
            throws ParserConfigurationException, SAXException, IOException {
        List<XPathRule> list = new ArrayList<XPathRule>();

        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        dbfactory.setIgnoringElementContentWhitespace(true);
        dbfactory.setExpandEntityReferences(true);
        DocumentBuilder builder = dbfactory.newDocumentBuilder();
        Document doc = builder.parse(new File(filename));
        Element root = doc.getDocumentElement();
        NodeList nl = root.getElementsByTagName("oneRule");
        for (int i = 0; i < nl.getLength(); i++) {
            String id = null;
            NodeList ids = ((Element) nl.item(i)).getElementsByTagName("id");
            if (ids.getLength() == 1) {
                id = ids.item(0).getTextContent();
            }
            String level = ((Element) nl.item(i)).getElementsByTagName("level")
                    .item(0).getTextContent();
            String content = ((Element) nl.item(i)).getElementsByTagName(
                    "content").item(0).getTextContent();
            List<String> prerequisite = new ArrayList<String>();
            NodeList pnl = ((Element) nl.item(i))
                    .getElementsByTagName("prerequisite");
            for (int j = 0; j < pnl.getLength(); j++) {
                prerequisite.add(pnl.item(j).getTextContent().trim());
            }
            String xpath = ((Element) nl.item(i)).getElementsByTagName("xpath")
                    .item(0).getTextContent().trim();
            String condition = ((Element) nl.item(i)).getElementsByTagName(
                    "condition").item(0).getTextContent();
            Condition con = Condition.PROHIBIT;
            if (Condition.PROHIBIT.getName().equals(condition)) {
                con = Condition.PROHIBIT;
            } else {
                con = Condition.REQUIRE;
            }
            XPathRule rule = new XPathRule(id, Integer.valueOf(level), content,
                    prerequisite, xpath, con, filename);
            list.add(rule);
        }
        return (XPathRule[]) list.toArray(new XPathRule[list.size()]);
    }
}
