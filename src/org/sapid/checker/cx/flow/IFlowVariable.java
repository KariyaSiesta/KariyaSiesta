/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.flow;

public interface IFlowVariable {

	/**
	 * variable name 
	 */
	String getName();
	void setName(String name);
	
	/**
	 * variable define id
	 */
	String getDefid();
	void setDefid(String defid);

}
