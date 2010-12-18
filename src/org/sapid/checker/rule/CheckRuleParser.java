package org.sapid.checker.rule;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author tani
 */
public class CheckRuleParser extends DefaultHandler {
    static ArrayList<CheckRule> rules = null;

    static CheckRule rule = null;

    static final String XML_MODULE = "module";

    static final String XML_PARAM = "param";

    static final String XML_NAME = "name";

    static final String XML_VALUE = "value";

    public static List<CheckRule> parseRuleXML(String filename) {
        rules = new ArrayList<CheckRule>();

        try {
            SAXParserFactory spfactory = SAXParserFactory.newInstance();
            SAXParser parser = spfactory.newSAXParser();
            parser.parse(new File(filename), new CheckRuleParser());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rules;
    }

    public CheckRuleParser() {
        super();
    }

    /**
     * �ɥ�����ȳ��ϻ�
     */
    public void startDocument() {
    }

    /**
     * ���Ǥγ��ϥ����ɤ߹��߻�
     */
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {

        if (qName == XML_MODULE) {
            String name = attributes.getValue(XML_NAME);

            if (name != null) {
                rule = new CheckRule(name);
            }
        }

        if (qName != null && qName == XML_PARAM) {
            String name = attributes.getValue(XML_NAME);
            String value = attributes.getValue(XML_VALUE);

            if (name != null && value != null) {
                rule.addRule(name, value);
            }
        }
    }

    /**
     * �ƥ����ȥǡ����ɤ߹��߻�
     */
    public void characters(char[] ch, int offset, int length) {
    }

    /**
     * ���Ǥν�λ�����ɤ߹��߻�
     */
    public void endElement(String uri, String localName, String qName) {

        if (qName == XML_MODULE) {
            rules.add(rule);
            rule = null;
        }
    }

    /**
     * �ɥ�����Ƚ�λ��
     */
    public void endDocument() {
    }
}
