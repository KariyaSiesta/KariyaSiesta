package org.sapid.checker.eclipse.codeassist.parsing;


public class ReduceTransition extends Transition{
	State current;
	State next;
	State reduceState;		// reduce先を一意に決めるため


	public ReduceTransition() {

	}
	public ReduceTransition(State current, State next, State reduceState) {
		super.current = current;
		super.next = next;
		this.current = current;
		this.next = next;
		this.reduceState = reduceState;
		current.addTransition(this);
	}


	public void path(State current){
		this.current = current;
	}

}


