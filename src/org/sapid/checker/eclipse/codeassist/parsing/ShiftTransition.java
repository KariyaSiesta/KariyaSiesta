package org.sapid.checker.eclipse.codeassist.parsing;


public class ShiftTransition extends Transition{
	State current;
	State next;

	public ShiftTransition(State current, State next){
		super.current = current;
		super.next = next;
		this.current = current;
		this.current = current;
		current.addTransition(this);
	}
}
