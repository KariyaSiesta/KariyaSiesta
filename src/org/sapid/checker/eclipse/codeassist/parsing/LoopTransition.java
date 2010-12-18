package org.sapid.checker.eclipse.codeassist.parsing;


public class LoopTransition extends Transition{
	State current;
	State next;


	public LoopTransition() {

	}
	public LoopTransition(State current, State next) {
		super.current = current;
		super.next = next;
		this.current = current;
		this.next = next;
		current.addTransition(this);
	}
}

