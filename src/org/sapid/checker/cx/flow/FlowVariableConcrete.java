/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.flow;

import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class FlowVariableConcrete implements IFlowVariable {
	private CExpressionElement elem;
	private String defid;
	private String name;
	private Double value;

	public void setByIdentNodeExceptValue(Node ident) {
		this.setName(ident.getTextContent());
		this.setDefid(ident.getAttributes().getNamedItem("defid").toString()
				.replace("defid=\"", "").replace("\"", ""));
		this.setExpressElem(new CExpressionElement(((Element) ident
				.getParentNode())));
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * @param defid
	 *            the defid to set
	 */
	public void setDefid(String defid) {
		this.defid = defid;
	}

	/**
	 * @return the defid
	 */
	public String getDefid() {
		return defid;
	}

	/**
	 * @param elem
	 *            the elem to set
	 */
	public void setExpressElem(CExpressionElement elem) {
		this.elem = elem;
	}

	/**
	 * @return the elem
	 */
	public CExpressionElement getExpressElem() {
		return elem;
	}

}
