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
     * ドキュメント開始時
     */
    public void startDocument() {
    }

    /**
     * 要素の開始タグ読み込み時
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
     * テキストデータ読み込み時
     */
    public void characters(char[] ch, int offset, int length) {
    }

    /**
     * 要素の終了タグ読み込み時
     */
    public void endElement(String uri, String localName, String qName) {

        if (qName == XML_MODULE) {
            rules.add(rule);
            rule = null;
        }
    }

    /**
     * ドキュメント終了時
     */
    public void endDocument() {
    }
}
