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
		// TODO ��ư�������줿�᥽�åɡ�������
		return contents.substring(offset, position);
	}

	@Override
	public int getCursorPosition() {
		// TODO ��ư�������줿�᥽�åɡ�������
		return position;
	}

	@Override
	public String getDescription() {
		// TODO ��ư�������줿�᥽�åɡ�������
		return description;
	}

	@Override
	public String getLabel() {
		// TODO ��ư�������줿�᥽�åɡ�������
		return contents;
	}
}
