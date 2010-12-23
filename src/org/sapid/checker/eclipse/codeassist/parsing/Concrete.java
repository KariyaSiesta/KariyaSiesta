package org.sapid.checker.eclipse.codeassist.parsing;

//import java.io.BufferedReader;
import java.io.IOException;
//import java.io.InputStreamReader;
import java.util.ArrayList;


public class Concrete {

	private TokenList tl = new TokenList();

	public TokenList getTokenList() {
		return tl;
	}



	private ArrayList<Token> axistokenlist = new ArrayList<Token>();		//tokenTransitionの引数のトークン群
	private ArrayList<Token> nodetokenlist = new ArrayList<Token>();		//tokenTransitionの引数のトークン群
	private ArrayList<Token> attrtokenlist = new ArrayList<Token>();		//tokenTransitionの引数のトークン群
	private ArrayList<Token> functokenlist = new ArrayList<Token>();		//tokenTransitionの引数のトークン群
	private ArrayList<Token> othertokenlist = new ArrayList<Token>();		//tokenTransitionの引数のトークン群
	private State t1 = null;

	TokenTransition tt = null;

	public Concrete() throws IOException, CloneNotSupportedException {
		addAxisTokenList();
		addFuncTokenList();
		addLogicalOp();

		//public State(String name, boolean isShift, boolean isLoopShift, boolean isReduce, boolean isSubLoopShift, boolean isNecessary )
//[1]LocationPath
		t1 = new State("t1", false, false ,false, false, true);
		State s1 = new State("s1", true, false, false, false, true);
		State s2 = new State("s2", false, false, true, false, true);
		State s3 = new State("s3", true, false, false, false, true);
		State s4 = new State("s4", false, false, true, false, true);

//[2]AbsoluteLocationPath
		State t2 = new State("t2", false, false, false, false, true);
		State s5 = new State("s5", false, false, false, false, true);
		State s6 = new State("s6", true, false, false, false, false);
		State s7 = new State("s7", false, false, true, false, true);
		State s8 = new State("s8", true, false, false, false, true);
		State s9 = new State("s9", false, false, true, false, true);

//[3]RelativeLocationPath
		State t3 = new State("t3", false, false, false, false, true);
		State s10 = new State("s10", true, false, false, false, true);
		State s11 = new State("s11", false, false, true, false, true);
		State s12 = new State("s12", true, true, false, false, true);
		State s13 = new State("s13", false, false, false, false, true);
		State s14 = new State("s14", true, false, false, false, true);
		State s15 = new State("s15", false, false, true, false, true);
		State s16 = new State("s16", true, true, false, true, true);
		State s17 = new State("s17", false, false, true, false, true);

//[4]Step
		State t4 = new State("t4", false, false, false, false, true);
		State s18 = new State("s18", true, false, false, false, true);
//		State s19 = new State("s19", true, false, false, false, true);
		State s20 = new State("s20", true, false, false, false, false);
		State s21 = new State("s21", false, false, true, false, true);
		State s22 = new State("s22", true, false, false, false, true);
		State s23 = new State("s23", false, false, true, false, true);

//[5]AxisSpecifier
		State t5 = new State("t5", false, false, false, false, true);
		State s24 = new State("s24", true, false, false, false, false);
		State s25 = new State("s25", false, false, false, false, true);
		State s26 = new State("s26", true, false, false, false, true);
		State s526 = new State("s526", false, false, true, false, true);
		State s27 = new State("s27", true, false, false, false, true);
		State s28 = new State("s28", false, false, true, false, true);

//[6]AxisName
		State t6 = new State("t6", false, false, false, false, true);
		State s29 = new State("s29", false, false, false, false, true);
		State s30 = new State("s30", false, false, true,  false,true);

//[8]Predicate
		State s66 = new State("s66", false, false, false, false, true);
		State s67 = new State("s67", true, false, false, false, true);
		State s68 = new State("s68", false, false, false, false, true);
		State s69 = new State("s69", false, false, true, false, true);

//[10]AbbreviatedAbsoluteLocationPath
		State s72 = new State("s72", false, false, false, false, true);
		State s73 = new State("s73", true, false, false, false, true);
		State s74 = new State("s74", false, false, true, false, true);

//[11]AbbreviateRelativeLocationPath
		State s75 = new State("s75", true, true, false, false, true);
		State s76 = new State("s76", false, false, false, false, true);
		State s77 = new State("s77", true, false, false, false, true);
		State s78 = new State("s78", false, false, true, false, true);

//[12]AbbreviateStep
		State t8 = new State("t8", false, false, false, false, true);
		State s79 = new State("s79", false, false, false, false, true);
		State s80 = new State("s80", false, false, true, false, true);
		State s81 = new State("s81", false, false, false, false, true);
		State s82 = new State("s82", false, false, true, false, true);

//[13]AbbreviatedAxisSpecifier
		State s84 = new State("s84", false, false, false, false, true);
		State s85 = new State("s85", true, false, false, false, true);
		State s1385 = new State("s1385", false, false, true, false, true);

//State(String name, boolean isShift, boolean isLoopShift, boolean isReduce, boolean isSubLoopShift, boolean isNecessary )
//[16]FunctionCall
		State s99 = new State("s99", true, false, false, false, true);
		State s100 = new State("s100", false, false, false, false, true);
		State s101 = new State("s101", true, false, false, false, false);
		State s102 = new State("s102", false, false, false, false, true);
		State s103 = new State("s103", true, false, false, false, true);
		State s104 = new State("s104", false, false, false, false, false);
		State s105 = new State("s105", false, false, false, false, true);
		State s106 = new State("s106", false, false, true, false, true);


//[19]PathExpr
		State t11 = new State("t11", false, false, false, false, true);
		State s119 = new State("s119", true, false, false, false, true);
		State s120 = new State("s120", false, false, true, false, true);
		State s121 = new State("s121", true, false, false, false, true);
		State s122 = new State("s121", false, false, true, false, true);

//[21]OrExpr
		State t13 = new State("t13", false, false, false, false, true);
		State s136 = new State("s136", true, false, false, false, false);
		State s137 = new State("s137", false, false, true, false, true);
		State s138 = new State("s138", true, true, false, false, true);
		State s139 = new State("s139", false, false, false, false, true);
		State s140 = new State("s140", true, false, false, false, false);
		State s141 = new State("s141", false, false, true, false, true);

//[21]OrExpr
		State t14 = new State("t14", false, false, false, false, true);
		State s142 = new State("s142", true, false, false, false, false);
		State s143 = new State("s143", false, false, true, false, true);
		State s144 = new State("s144", true, true, false, false, true);
		State s145 = new State("s145", false, false, false, false, true);
		State s146 = new State("s146", true, false, false, false, false);
		State s147 = new State("s147", false, false, true, false, true);

//[35]FunctionName
		State s289 = new State("s289", false, false, false, false, true);
		State s290 = new State("s290", false, false, true,  false,true);


//[37]NameTest
		State t25 = new State("t25", false, false, false, false, true);
		State s296 = new State("s296", false, false, false, false, true);
		State s297 = new State("s297", false, false, true, false, true);
		State s302 = new State("s302", true, false, false, false, true);
		State s303 = new State("s303", false, false, true, false, true);

//[40]NodeName
		State s314 = new State("s314", false, false, false, false, true);
		State s315 = new State("s315", false, false, true,  false,true);

//[41]Attribute
		State s316 = new State("s316", false, false, false, false, true);
		State s317 = new State("s317", false, false, true,  false,true);

		Token tk1 = new Token("(", "ExprToken", tl);
		Token tk2 = new Token(")", "ExprToken", tl);
//		Token tk3 = new Token("|", "ExprToken", tl);
		Token tk4 = new Token("[", "ExprToken", tl);
		Token tk5 = new Token("]", "ExprToken", tl);
		Token tk6 = new Token(".", "ExprToken", tl);
		Token tk7 = new Token("..", "ExprToken", tl);
		Token tk8 = new Token("@", "ExprToken", tl);
		Token tk9 = new Token(",", "ExprToken", tl);
		Token tk10 = new Token("::", "ExprToken", tl);
		Token tk11 = new Token("/", "Operator", tl);
		Token tk12 = new Token("//", "Operator", tl);
		Token tk13 = new Token("*", "allnode", tl);
//		Token tk14 = new Token(":", "NameSpace", tl);
		Token tk15 = new Token("or", "LogicalOperator", tl);
		Token tk16 = new Token("and", "LogicalOperator", tl);



		//	Transition(State current, State next)
		new Transition(t1, s1);
		new Transition(t1, s3);
		new Transition(t2, s5);
		new Transition(t2, s8);
		new Transition(t3, s10);
		new Transition(t3, s12);
		new Transition(t3, s16);
		new Transition(t4, s18);
		new Transition(t4, s22);
		new Transition(t5, s24);
		new Transition(t5, s27);
		new Transition(t6, s29);
		new Transition(t8, s79);
		new Transition(t8, s81);
		new Transition(t11, s119);
		new Transition(t11, s121);
		new Transition(t13, s136);
		new Transition(t13, s138);
		new Transition(t14, s142);
		new Transition(t14, s144);
		new Transition(s104, s102);	//   	 FunctionCall  	    ::=     	 FunctionName '(' ( Argument ( ',' Argument )* )? ')'  	  の*に対応
		new Transition(t25, s296);
		new Transition(t25, s302);

//	TokenTransition(State current, State next, String token)
		new TokenTransition(s5, s6, tk11);
		new TokenTransition(s13, s14, tk11);
		new TokenTransition(s25, s26, tk10);
		new TokenTransition(s29, s30, axistokenlist);
		new TokenTransition(s66, s67, tk4);
		new TokenTransition(s68, s69, tk5);
		new TokenTransition(s72, s73, tk12);
		new TokenTransition(s76, s77, tk12);
		new TokenTransition(s79, s80, tk6);
		new TokenTransition(s81, s82, tk7);
		new TokenTransition(s84, s85, tk8);
		new TokenTransition(s100, s101, tk1);
		new TokenTransition(s102, s103, tk9);
		new TokenTransition(s105, s106, tk2);
		new TokenTransition(s139, s140, tk15);
		new TokenTransition(s145, s146, tk16);

		new TokenTransition(s289, s290, functokenlist);
		new TokenTransition(s296, s297, tk13);
		new TokenTransition(s314, s315, nodetokenlist);
		new TokenTransition(s316, s317, attrtokenlist);

		new NTransition(s6, s7);
//		new NTransition(s84, s85, tk8);
		new NTransition(s104, s105);
		new NTransition(s20, s21);
		new NTransition(s101, s105);
		new NTransition(s24, s26);


//	ShiftTransition(State current, State next)
		new ShiftTransition(s1, t3);
		new ShiftTransition(s3, t2);
		new ShiftTransition(s6, t3);
		new ShiftTransition(s8, s72);
		new ShiftTransition(s10, t4);
		new ShiftTransition(s14, t4);
		new ShiftTransition(s18, t5);
		new ShiftTransition(s22, t8);
		new ShiftTransition(s24, s29);
		new ShiftTransition(s26, t25);
		new ShiftTransition(s27, s84);
		new ShiftTransition(s67, t13);
		new ShiftTransition(s73, t3);
		new ShiftTransition(s77, t4);
		new ShiftTransition(s85, s316);
		new ShiftTransition(s99, s289);
		new ShiftTransition(s101, t11);
		new ShiftTransition(s103, t11);
		new ShiftTransition(s119, t1);
		new ShiftTransition(s121, s99);
		new ShiftTransition(s136, t14);
		new ShiftTransition(s140, t14);
		new ShiftTransition(s142, t11);
		new ShiftTransition(s146, t11);
		new ShiftTransition(s302, s314);




//	LoopTransition(State current, State next)
		new LoopTransition(s12, t3);
		new LoopTransition(s16, s75);
		new LoopTransition(s75, t3);
		new LoopTransition(s20, s66);
		new LoopTransition(s138, t13);
		new LoopTransition(s144, t14);


//	ReduceTransition(State current, State next, State reduceState)
		new ReduceTransition(s2, s120, s119);
		new ReduceTransition(s4, s120, s119);
		new ReduceTransition(s7, s4, s3);
		new ReduceTransition(s9, s4, s3);
		new ReduceTransition(s11, s2, s1);
		new ReduceTransition(s11, s7, s6);
		new ReduceTransition(s11, s74, s73);
		new ReduceTransition(s11, s76, s75);
		new ReduceTransition(s11, s13, s12);
		new ReduceTransition(s15, s2, s1);
		new ReduceTransition(s15, s7, s6);
		new ReduceTransition(s15, s74, s73);
		new ReduceTransition(s15, s76, s75);
		new ReduceTransition(s15, s13, s12);
		new ReduceTransition(s17, s2, s1);
		new ReduceTransition(s17, s7, s6);
		new ReduceTransition(s17, s74, s73);
		new ReduceTransition(s17, s76, s75);
		new ReduceTransition(s17, s13, s12);
		new ReduceTransition(s21, s11, s10);
		new ReduceTransition(s21, s15, s14);
		new ReduceTransition(s21, s78, s77);
		new ReduceTransition(s23, s11, s10);
		new ReduceTransition(s23, s15, s14);
		new ReduceTransition(s23, s78, s77);
		new ReduceTransition(s526, s20, s18);
		new ReduceTransition(s28, s20, s18);
		new ReduceTransition(s30, s25, s24);
		new ReduceTransition(s69, s20, s20);	// Step	::=	AxisSpecifier  NodeTest  Predicate* の*に対応
		new ReduceTransition(s74, s9, s8);
		new ReduceTransition(s78, s17, s16);
		new ReduceTransition(s80, s23, s22);
		new ReduceTransition(s82, s23, s22);
		new ReduceTransition(s1385, s28, s27);
		new ReduceTransition(s106, s122, s121);

		new ReduceTransition(s120, s102, s101);
		new ReduceTransition(s122, s102, s101);
		new ReduceTransition(s120, s104, s103);
		new ReduceTransition(s122, s104, s103);
		new ReduceTransition(s120, s143, s142);
		new ReduceTransition(s122, s143, s142);
		new ReduceTransition(s120, s147, s146);
		new ReduceTransition(s122, s147, s146);
		new ReduceTransition(s137, s68, s67);
		new ReduceTransition(s137, s139, s138);
		new ReduceTransition(s141, s68, s67);
		new ReduceTransition(s141, s139, s138);
		new ReduceTransition(s143, s137, s136);
		new ReduceTransition(s143, s141, s140);
		new ReduceTransition(s143, s145, s144);
		new ReduceTransition(s147, s137, s136);
		new ReduceTransition(s147, s141, s140);
		new ReduceTransition(s147, s145, s144);

		new ReduceTransition(s290, s100, s99);
		new ReduceTransition(s297, s526, s26);
		new ReduceTransition(s303, s526, s26);

		new ReduceTransition(s315, s303, s302);
		new ReduceTransition(s317, s1385, s85);



	}


		public ArrayList<Token> getCandidate(String xpath){
			ArrayList<Token> nextToken = new ArrayList<Token>();
			ArrayList<Token> xpathtoken = new ArrayList<Token>();
			State start = t1;
			Parse csholder = new Parse();

			Lexer lexer = new Lexer();
			xpathtoken = lexer.tokenize(xpath, tl);

			nextToken = csholder.getNextToken(xpathtoken, start);

			return nextToken;

		}

	public void addTokenList(String token, String sort, String info){
			if (sort.equals("node")){
				nodetokenlist.add(new Token(token, sort, info, tl));
			} else if (sort.equals("attribute")){
				attrtokenlist.add(new Token(token, sort, info, tl));
			} else {
				othertokenlist.add(new Token(token, sort, info, tl));
			}
	}


	public void addAxisTokenList(){
		axistokenlist.add(new Token("ancestor", "axis", tl));
		axistokenlist.add(new Token("ancestor-or-self", "axis", tl));
		axistokenlist.add(new Token("descendant", "axis", tl));
		axistokenlist.add(new Token("descendant-or-self", "axis", tl));
		axistokenlist.add(new Token("ancestor", "axis", tl));
		axistokenlist.add(new Token("ancestor", "axis", tl));
		axistokenlist.add(new Token("ancestor", "axis", tl));
		axistokenlist.add(new Token("ancestor", "axis", tl));
		axistokenlist.add(new Token("ancestor", "axis", tl));
	}

	public void addFuncTokenList(){
		functokenlist.add(new Token("last", "func", tl));
		functokenlist.add(new Token("position", "func", tl));
		functokenlist.add(new Token("count", "func", tl));
		functokenlist.add(new Token("starts-with", "func", tl));
		functokenlist.add(new Token("contains", "func", tl));
		functokenlist.add(new Token("string-length", "func", tl));
	}

	public void addLogicalOp(){
		othertokenlist.add(new Token("and", "logical operator", "rule", tl));
		othertokenlist.add(new Token("or", "logical operator", "rule", tl));
	}

	public ArrayList<Token> getAxisList(){
		return axistokenlist;
	}


	public ArrayList<Token> getFuncList(){
		return functokenlist;
	}
}
