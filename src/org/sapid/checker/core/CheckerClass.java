package org.sapid.checker.core;

import java.util.List;

import org.sapid.checker.rule.CheckRule;

/**
 * JSPCheckerのチェックに用いるCheckerClassインタフェース
 * 
 * @author tani
 * 
 */
public interface CheckerClass {
	public List<Result> check(IFile file, CheckRule rule);
}
