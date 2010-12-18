package org.sapid.checker.eclipse.codeassist.parsing;
import java.util.ArrayList;



public class TokenList {

	private ArrayList<Token> tokenlist = new ArrayList<Token>();

	public TokenList() {

	}

	public void addToken(Token token) {
		tokenlist.add(token);
	}

	public ArrayList<Token> getTokenList() {
		return tokenlist;
	}

}
