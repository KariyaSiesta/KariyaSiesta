/* 
 * Program:             $RCSfile: TestMisra005.java,v $  $Revision: 60.12 $
 * 
 *
 * Author:              R.Mizuno
 *                      S.Yamamoto  2010/10/09
 *
 * (C) Copyright:       R.Mizuno and S.Yamamoto  2010
 *                      This file is a product of the project Sapid.
 */

/* 
 * $Id: TestMisra005.java,v 60.12 2010/06/26 05:32:37 yamamoto Exp yamamoto $
 */

package org.sapid.checker.core;

/**
 * hitしたXPathの範囲を格納するクラス
 */
public class Range {
    private int startLine;
    private int startColumn;
    private int endLine;
    private int endColumn;
    private int offset;
    private int length;
	
    public Range(int startLine, int startColumn,
		 int endLine, int endColumn, int offset, int length) {
	super();
	this.startLine = startLine;
	this.startColumn = startColumn;
	this.endLine = endLine;
	this.endColumn = endColumn;
	this.offset = offset;
	this.length = length;
    }

    public int getEndColumn() {
	return endColumn;
    }

    public int getEndLine() {
	return endLine;
    }

    public int getStartColumn() {
	return startColumn;
    }

    public int getStartLine() {
	return startLine;
    }

    public int getLength() {
	return length;
    }

    public int getOffset() {
	return offset;
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        } else if (!(anObject instanceof Range)) {
	    return false;
	}

	Range tmp = (Range)anObject;
	return (this.startLine == tmp.getStartLine() &&
		this.startColumn == tmp.getStartColumn() &&
		this.endLine == tmp.getEndLine() &&
		this.endColumn == tmp.getEndColumn() &&
		this.offset == tmp.getOffset() &&
		this.length == tmp.getLength());
    }
}
