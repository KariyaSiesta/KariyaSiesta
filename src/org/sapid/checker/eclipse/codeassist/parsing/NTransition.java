package org.sapid.checker.eclipse.codeassist.parsing;


public class NTransition extends Transition{
	State current;
	State next;
	Token token;


	public NTransition() {

	}
	public NTransition(State current, State next) {
		super.current = current;
		super.next = next;
		this.current = current;
		this.next = next;
		current.addTransition(this);
	}

	public NTransition(State current, State next, Token token) {
		super.current = current;
		super.next = next;
		this.current = current;
		this.next = next;
		this.token = token;
		current.addTransition(this);
	}
}
