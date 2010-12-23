package org.sapid.checker.eclipse.codeassist.parsing;

import java.util.ArrayList;


public class State  implements Cloneable{
	private String name;
	private boolean isShift;
	private boolean isReduce;
	private boolean isSubLoopShift;
	private boolean isLoopShift;
	private boolean isNecessary;		//構文規則の?,*に対応する
	ArrayList<Transition> tlist = new ArrayList<Transition>();


	public State(String name, boolean isShift, boolean isLoopShift, boolean isReduce, boolean isSubLoopShift, boolean isNecessary ){
		this.name = name;
		this.isShift = isShift;
		this.isReduce = isReduce;
		this.isSubLoopShift = isSubLoopShift;
		this.isLoopShift = isLoopShift;
		this.isNecessary = isNecessary;
	}

	public State() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public void addTransition(Transition transition) {
		tlist.add(transition);
	}

	public ArrayList<Transition> getTransitionList() {
		return tlist;
	}
	public String getName(){
		return this.name;
	}

	public boolean isShift() {
		return isShift;
	}

	public boolean isReduce() {
		return isReduce;
	}

	public boolean isSubLoopShift() {
		return isSubLoopShift;
	}

	public boolean isLoopShift() {
		return isLoopShift;
	}

	public boolean isNecessary() {
		return isNecessary;
	}


}
