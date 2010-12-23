package org.sapid.checker.eclipse.codeassist;

import org.eclipse.jface.fieldassist.IContentProposal;

class AssistProposal implements IContentProposal{
	String contents;
	String description;
	int position;
	int offset;

	public AssistProposal(String contents, String description, int position, int offset){
		this.contents = contents;
		this.description = description;
		this.position = position;
		this.offset = offset;
	}

	@Override
	public String getContent() {
		// TODO 自動生成されたメソッド・スタブ
		return contents.substring(offset, position);
	}

	@Override
	public int getCursorPosition() {
		// TODO 自動生成されたメソッド・スタブ
		return position;
	}

	@Override
	public String getDescription() {
		// TODO 自動生成されたメソッド・スタブ
		return description;
	}

	@Override
	public String getLabel() {
		// TODO 自動生成されたメソッド・スタブ
		return contents;
	}
}
