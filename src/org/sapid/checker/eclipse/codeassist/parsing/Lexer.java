package org.sapid.checker.eclipse.codeassist.parsing;

import java.util.ArrayList;

public class Lexer {

	private ArrayList<Token> xpathtoken = new ArrayList<Token>();

	public Lexer() {

	}

	public ArrayList<Token> tokenize(String xpath, TokenList tl) {
		int position = 0;
		String token;
		ArrayList<Token> alltokenlist = null;
		alltokenlist = tl.getTokenList();
		boolean matchflag = false;

		for(int i = 0; i < xpath.length(); i++) {
			token = xpath.substring(position, i+1);
			matchflag = false;

/*
 * 次の文字取得
 * 			String nexttoken = "";
			if(xpath.length() > (i+1)){
				nexttoken = xpath.substring(i+1, i+2);
			}
*/
			for(int j = 0; j < alltokenlist.size(); j++) {
				if(alltokenlist.get(j).getToken().length()>token.length()-1){
					if(token.equals(alltokenlist.get(j).getToken().substring(0, token.length()))){
						matchflag = true;
						break;
					}
				}
			}

			if(matchflag == false){
				if(token.length()>1){
					xpathtoken.add(new Token(token.substring(0,token.length()-1)));
					i--;
					position = position + token.length()-1;
				} else {
					xpathtoken.add(new Token(token.substring(0,token.length())));
					position++;
				}
			}

			if(i == xpath.length()-1){
				xpathtoken.add(new Token(token));
			}
		}

		for(int i = xpathtoken.size()-1; i >= 0; i--){
			if(xpathtoken.get(i).getToken().matches("[\\s]+") || xpathtoken.get(i).getToken().matches("[\r\n]+")){
				xpathtoken.remove(i);
			}
		}
		return xpathtoken;
	}
}
