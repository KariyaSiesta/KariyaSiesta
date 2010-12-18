package org.sapid.checker.eclipse.codeassist.parsing;


public class Transition {
	State current;
	State next;


	public Transition() {

	}
	public Transition(State current, State next) {
		this.current = current;
		this.next = next;
		current.addTransition(this);
	}
}
