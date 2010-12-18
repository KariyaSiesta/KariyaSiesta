/* 
 * Program:             $RCSfile: TestMisra005.java,v $  $Revision: 60.12 $
 * 
 *
 * Author:              G.Okada
 *                      S.Yamamoto  2010/10/09
 *
 * (C) Copyright:       G.Okada and S.Yamamoto  2010
 *                      This file is a product of the project Sapid.
 */

/* 
 * $Id: TestMisra005.java,v 60.12 2010/06/26 05:32:37 yamamoto Exp yamamoto $
 */

package org.sapid.checker.core;

public class Result {
    private String	id;	// null is permitted.
    private int		level;
    private String	message;
    private Range	range;

    /**
     * @param level
     * @param message
     */
    public Result(String id, Range range, int level, String message) {
        super();
        this.id = id;
        this.range = range;
        this.level = level;
        this.message = message;
    }

    public Result(String id, int line_num, int level, String message) {
        super();
        this.id = id;
        this.range = new Range(line_num, 0, line_num, 0, 0, 0);
        this.level = level;
        this.message = message;
    }

    public String getId() {
        return this.id;
    }

    public int getLevel() {
        return this.level;
    }

    public String getMessage() {
        return this.message;
    }

    public Range getRange() {
        return range;
    }

    public int getLine() {
        return this.range.getStartLine();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        } else if (!(anObject instanceof Result)) {
	    return false;
	}

	Result tmp = (Result)anObject;
	String i = tmp.getId();
	return (((tmp.id == null && i == null) ||
		 (tmp.id != null && i != null && this.id.equals(i))) &&
		this.level == tmp.getLevel() &&
		this.message.equals(tmp.getMessage()) &&
		this.range.equals(tmp.getRange()));
    }

    @Override
    public int hashCode() {
        return (this.message.hashCode() * 14 +
		(this.id != null ? this.id.hashCode() : 0));
    }
}
