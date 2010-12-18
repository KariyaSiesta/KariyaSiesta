package org.sapid.checker.eclipse.codeassist.parsing;


public class Token {
	private String token;
	private String sort;
	private int offset;
	private String info;

	public Token() {

	}

	public Token(String token) {
		this.token = token;
	}

	public Token(String token, String sort, String info,int offset) {
		this.token = token;
		this.sort = sort;
		this.offset = offset;
		this.info = info;
	}

	public Token(String token, String sort, String info, TokenList tl) {
		this.token = token;
		this.sort = sort;
		this.info = info;
		tl.addToken(this);
	}

	public Token(String token, String sort, TokenList tl) {
		this.token = token;
		this.sort = sort;
		tl.addToken(this);
	}

	public Token(String token, String sort,int offset) {
		this.token = token;
		this.sort = sort;
		this.offset = offset;
	}


	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public void setOffset(int offset){
		this.offset = offset;
	}

	public int getOffset(){
		return offset;
	}

	public String getToken() {
		return token;
	}

	public String getSort() {
		return sort;
	}

}