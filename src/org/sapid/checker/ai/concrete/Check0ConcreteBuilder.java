/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai.concrete;

import org.sapid.checker.ai.NonterminalExpression;
import org.sapid.checker.ai.Value;
import org.w3c.dom.Element;

/**
 * @author hirumuta
 *
 */
public class Check0ConcreteBuilder extends ConcreteBuilder {

	/**
	 * 
	 */
	public Check0ConcreteBuilder() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAIDivide(org.sapid.checker.ai.Value, org.sapid.checker.ai.Value)
	 */
	public NonterminalExpression getAIDivide(Value left_value, Value right_value, Element elem) {
		return new Check0ConcreteDivide(left_value, right_value, elem);
	}

	
}
