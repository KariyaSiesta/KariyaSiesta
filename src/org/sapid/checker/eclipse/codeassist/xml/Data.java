package org.sapid.checker.eclipse.codeassist.xml;

public class Data {
	private String content;
	private String sort;
	private String attributename;



	public Data(String content, String sort){
		this.setContent(content);
		this.sort = sort;
	}


	public Data(String content, String sort, String attributename){
		this.setContent(content);
		this.sort = sort;
		this.attributename = attributename;
	}

/*
 * 	public boolean equals(Object o){
		if(o == null){
			return false;
		}else if(!(o instanceof Data)){
			return false;
		}

		Data d = (Data)o;
		if ( this.getContent().equals(d.getContent()) && this.sort.equals(d.sort) ){
			return true;
		}else {
			return false;
		}
	}
*/
	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getAttribute() {
		return attributename;
	}


}
