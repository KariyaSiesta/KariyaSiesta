package org.sapid.checker.rule.js;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.rule.CheckRule;

public class JsRule implements CheckerClass {
	public class ResultSet {
		public Object object;
		public List<Result> results;

		ResultSet(Object object, List<Result> results) {
			this.object = object;
			this.results = results;
		}
	}

	private ScriptEngine engine;

	public JsRule() {
		ScriptEngineManager manager = new ScriptEngineManager();
		this.engine = manager.getEngineByName("javascript");
		this.engine.put("results", new ArrayList<ResultSet>());

	}

	@Override
	public List<Result> check(IFile file, CheckRule rule) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public ResultSet eval(IFile file, String code) throws Exception {
		engine.put("target", file);
		Object object = engine.eval(code);
		return new ResultSet(object, (List<Result>) engine.get("results"));
	}
}
