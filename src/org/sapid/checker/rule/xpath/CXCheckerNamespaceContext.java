package org.sapid.checker.rule.xpath;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class CXCheckerNamespaceContext implements NamespaceContext {

	@Override
	public String getNamespaceURI(String prefix) {
		if (prefix == null) {
			throw new NullPointerException("Null prefix");
		}
		
		if (prefix.equals("cx")) {
			return "http://www.sapid.org/cx";
		} else if (prefix.equals("xml")) {
			return XMLConstants.XML_NS_URI;
		} else {
			return XMLConstants.NULL_NS_URI;
		}
	}

	@Override
	public String getPrefix(String arg) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Iterator getPrefixes(String arg) {
		throw new UnsupportedOperationException();
	}

}
