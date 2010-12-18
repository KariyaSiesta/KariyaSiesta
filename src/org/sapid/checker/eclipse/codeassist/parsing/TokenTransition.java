package org.sapid.checker.eclipse.codeassist.parsing;

import java.util.ArrayList;


public class TokenTransition extends Transition{
	State current;
	State next;
	Token token;
	ArrayList<Token> tokenlist = new ArrayList<Token>();

	public TokenTransition(State current, State next, Token token){
		super.current = current;
		super.next = next;
		this.current = current;
		this.current = current;
		this.token = token;
		current.addTransition(this);
	}

	public TokenTransition(State current, State next, ArrayList<Token> tokenlist){
		super.current = current;
		super.next = next;
		this.current = current;
		this.current = current;
		this.tokenlist = tokenlist;
		current.addTransition(this);
	}


}
