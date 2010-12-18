package org.sapid.checker.rule;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tani
 * 
 */
public class CheckRule {
	private String name = null;

	private Map<String, String> params = new HashMap<String, String>();

	public CheckRule(String name) {
	    super();
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addRule(String name, String value) {
		params.put(name, value);
	}

	public Map<String, String> getAllRules() {
		return params;
	}

	public String getValue(String name) {
		return params.get(name);
	}
}
