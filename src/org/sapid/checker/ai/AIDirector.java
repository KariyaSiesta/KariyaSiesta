/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai;

import java.util.ArrayList;
import java.util.List;

import org.sapid.checker.cx.wrapper.CFunctionElement;
import org.sapid.checker.cx.wrapper.CParameterElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Builder パターンの Director クラスを表すクラス． 
 * GoF のパターンでは Builder クラスがインタンスを持つが，
 * AIDirector クラスが構文木のルートを作成し返す．
 * 
 * @author hirumuta
 * 
 */
public class AIDirector {
	AIBuilder aibuilder;
	
	public AIDirector(AIBuilder aibuilder) {
		this.aibuilder = aibuilder;
	}
	
	public AIFunction construct(CFunctionElement cfuncelem) {
		String funcid = "";
		List<Variable> vars = new ArrayList<Variable>();
		
		// get function id
		funcid = cfuncelem.getId();
		
		// make parameters
		CParameterElement[] cparams = cfuncelem.getParams();
		createParameterList(cparams, vars);
		
		// make expressions
		NonterminalExpression[] exprs =	createSyntaxTree(cfuncelem);
		
		AIFunction aifunc = this.aibuilder.getAIFunction(funcid, exprs, vars, cfuncelem.getElem());
		
		return aifunc;
	}

	private void createParameterList(CParameterElement[] cparams,
			List<Variable> vars) {
		for (int i = 0; i < cparams.length; i++) {
			String defid = "";
			String name = "";
			
			NodeList idents = cparams[i].getElem().getElementsByTagName("ident");
			defid = ((Element)idents.item(0)).getAttribute("defid");
			
			name = idents.item(0).getTextContent();
			
			Value var = this.aibuilder.getAIVariable(defid, name);
			vars.add((Variable) var);
		}
		
	}
	
	private NonterminalExpression[] createSyntaxTree(CFunctionElement cfuncelem) {
		List<NonterminalExpression> exprs_collection = new ArrayList<NonterminalExpression>();
		// go function block
		List<Node> stmt_nodes = new ArrayList<Node>();
		getElementsByTagNameByChilds(stmt_nodes, cfuncelem.getElem(), "Stmt");

		// body
		createExprsByStmts(exprs_collection, stmt_nodes.get(0));		
		NonterminalExpression[] exprs = (NonterminalExpression[])exprs_collection.toArray(new NonterminalExpression[exprs_collection.size()]);
		
		return exprs;
	}

	/**
	 * @param exprs_collection
	 * @param stmt_elems
	 */
	private void createExprsByStmts(
			List<NonterminalExpression> exprs_collection, Node stmt_node) {
		List<Node> stmt_nodes = new ArrayList<Node>();
		getElementsByTagNameByChilds(stmt_nodes, (Element)stmt_node, "Stmt");
		for (int i = 0; i < stmt_nodes.size(); i++) {
			Node stmt = stmt_nodes.get(i);
			String sort = ((Element)stmt).getAttribute("sort");
			NonterminalExpression expr = null;
			if (sort.equals("")) {
				Node child = stmt_nodes.get(i).getFirstChild();
				Element root_expr = (Element)child;
				expr = (NonterminalExpression) postorderExpr(root_expr);
				
			} else if (sort.equals("If")) {
				NonterminalExpression true_expr = null;
				NonterminalExpression false_expr = null;
				// for condition_expr
				Node cond_node = ((Element)stmt).getElementsByTagName("Expr").item(0);
				Value condition_value =  (Value) postorderExpr((Element)cond_node);
				
				// for true or false part
				List<Node> if_stmt_nodes = new ArrayList<Node>();
				getElementsByTagNameByChilds(if_stmt_nodes, (Element)stmt, "Stmt");
				
				if (((Element)if_stmt_nodes.get(0)).getAttribute("sort").equals("block")) {
					List<NonterminalExpression> new_exprs_collection = new ArrayList<NonterminalExpression>();
					
					createExprsByStmts(new_exprs_collection, if_stmt_nodes.get(0));		
					NonterminalExpression[] exprs = (NonterminalExpression[])new_exprs_collection.toArray(new NonterminalExpression[new_exprs_collection.size()]);
					true_expr = aibuilder.getAIBlock(exprs, ((Element)if_stmt_nodes.get(0)));
					
				} else {
					true_expr = (NonterminalExpression) postorderExpr((Element)if_stmt_nodes.get(0).getFirstChild());
				}
				
				if (if_stmt_nodes.size() == 1) {
					false_expr = null;
				} else if (if_stmt_nodes.size() == 2) {
					if (((Element)if_stmt_nodes.get(1)).getAttribute("sort").equals("block")) {
						List<NonterminalExpression> new_exprs_collection = new ArrayList<NonterminalExpression>();
						
						createExprsByStmts(new_exprs_collection, if_stmt_nodes.get(1));		
						NonterminalExpression[] exprs = (NonterminalExpression[])new_exprs_collection.toArray(new NonterminalExpression[new_exprs_collection.size()]);
						false_expr = aibuilder.getAIBlock(exprs, ((Element)if_stmt_nodes.get(1)));	
					} else {
						false_expr = (NonterminalExpression) postorderExpr((Element)if_stmt_nodes.get(1).getFirstChild());
					}
				}
				
				expr = aibuilder.getAIIf(condition_value, true_expr, false_expr, ((Element)stmt));
			} else if (sort.equals("For")) {
				//TODO
			}
			
			
			exprs_collection.add(expr);
		}
	}

	private AbstractExpression postorderExpr(Element root_expr) {
		NonterminalExpression non_expr = null;

		List<Node> child_nodes = new ArrayList<Node>();
		//child_nodes = 
		getElementsByTagNameByChilds(child_nodes, root_expr, "Expr");
		int expr_num = child_nodes.size();
		if (expr_num == 0) {
			// case: only ident or literal
			return getTerminalExpression(root_expr);
			
		} else if (expr_num == 1) {
			// case: surround by ( )
			return postorderExpr((Element)child_nodes.get(0));
			
		} else if (expr_num == 2) {
			// case: dyadic operator 
			Value value_left = null;
			Value value_right = null;

			// get left
			Element left = (Element)child_nodes.get(0);
			if (left.getChildNodes().getLength() == 1) {
				value_left = getTerminalExpression(left);
			} else {
				value_left = (Value)postorderExpr(left);			
			}

			// get right
			Element right = (Element)child_nodes.get(1);
			if (right.getChildNodes().getLength() == 1) {
				value_right = getTerminalExpression(right);
			} else {
				value_right = (Value)postorderExpr(right);
			}
			
			// switch by operator
			// get opertor
			NodeList ops = root_expr.getElementsByTagName("op");
			Element elem_op = (Element)ops.item(0);
			String operator = elem_op.getTextContent();
			if (operator.equals("=")) {
				non_expr = (NonterminalExpression) aibuilder.getAIAssign(value_left, value_right, (Element)child_nodes.get(1));
				
			} else if (operator.equals("+")) {
				non_expr = (NonterminalExpression) aibuilder.getAIPlus(value_left, value_right, (Element)child_nodes.get(1));
				Value value = aibuilder.getAIResult(non_expr);
				return value;
				
			} else if (operator.equals("-")) {
				non_expr = (NonterminalExpression) aibuilder.getAIMinus(value_left, value_right, (Element)child_nodes.get(1));
				Value value = aibuilder.getAIResult(non_expr);
				return value;
				
			} else if (operator.equals("*")) {
				non_expr = (NonterminalExpression) aibuilder.getAIMulti(value_left, value_right, (Element)child_nodes.get(1));
				Value value = aibuilder.getAIResult(non_expr);
				return value;
				
			} else if (operator.equals("/")) {
				non_expr = (NonterminalExpression) aibuilder.getAIDivide(value_left, value_right, (Element)child_nodes.get(1));
				Value value = aibuilder.getAIResult(non_expr);
				return value;
				
			}
			
			
		}
		
		
		return non_expr;
	}
	
	private Boolean getElementsByTagNameByChilds(List<Node> new_nodes ,Element root_expr, String tagname) {
		
		NodeList child_nodes = root_expr.getChildNodes();
		for (int i = 0; i < child_nodes.getLength(); i++) {
			if (child_nodes.item(i).getNodeName().equals(tagname)) {
				new_nodes.add(child_nodes.item(i));
			}
		}
		return true;
	}

	private Value getTerminalExpression(Element expr) {
		Value value = null;
		
		// case: NonterminalExpression
		// literal or variable
		Element elem = (Element)expr.getFirstChild();
		String tag = elem.getTagName();
		if (tag.equals("literal")) {
			value = aibuilder.getAIValue(Double.parseDouble(elem.getTextContent()));
		} else if (tag.equals("ident")) {
			value = aibuilder.getAIVariable(elem.getAttribute("defid"), elem.getTextContent());
		}
		
		return value;
	}
	

}
