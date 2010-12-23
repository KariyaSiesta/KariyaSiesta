package org.sapid.checker.rule.js;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.rule.CheckRule;

public class JsRule implements CheckerClass {
	private static final String JS_SRC_KEY = "src";

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
		try {
			this.engine.eval(new InputStreamReader(getClass().getClassLoader()
					.getResourceAsStream("std.js")));
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Result> check(IFile file, CheckRule rule) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		String path = rule.getValue(JS_SRC_KEY);
		try {
			IResource resource = root.findMember(path);
			return this.eval(file, new FileReader(resource.getRawLocation()
					.toFile())).results;

		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(this.getClass().getName()
					+ " : " + path + "がありません。");
		} catch (Exception e) {
			throw new IllegalArgumentException(this.getClass().getName()
					+ " : ルールXMLの" + path + "が不正です。 : " + e.toString());
		}
	}

	@SuppressWarnings("unchecked")
	public ResultSet eval(IFile file, Reader code) throws Exception {
		engine.put("target", file);
		Object object = engine.eval(code);
		return new ResultSet(object, (List<Result>) engine.get("results"));
	}

	@SuppressWarnings("unchecked")
	public ResultSet eval(IFile file, String code) throws Exception {
		engine.put("target", file);
		Object object = engine.eval(code);
		return new ResultSet(object, (List<Result>) engine.get("results"));
	}
}
