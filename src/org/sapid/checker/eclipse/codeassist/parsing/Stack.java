package org.sapid.checker.eclipse.codeassist.parsing;
import java.util.ArrayList;


@SuppressWarnings("hiding")
public class Stack<State> implements Cloneable{
	private State[] stack;
	private final static int DEFAULT_CAPACITY = 10;
	private int sp;
	private int capacity;

	public Stack(){
		this(DEFAULT_CAPACITY);
	}

	@SuppressWarnings("unchecked")
	public Stack(int initialCapacity) {
		capacity = initialCapacity;
		stack = (State[]) new Object[initialCapacity];
	}

	@SuppressWarnings("unchecked")
	public Stack<State> clone() {
		try {
			Stack<State> ss = (Stack<State>)super.clone();
			return ss;
		} catch (CloneNotSupportedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return null;
		}
	}

	public void push(State state) {
		stack[sp++] = state;
		capacityCheck();
	}

	public State pop() {
		State s = stack[--sp];
		stack[sp] = null;
		return s;
	}

	private void capacityCheck() {
		if(sp >= capacity)
			expandCapacity();
	}

	@SuppressWarnings("unchecked")
	private void expandCapacity() {
		capacity += 2;
		Object[] oldStack = stack;
		stack = (State[]) new Object[capacity];
		System.arraycopy(oldStack, 0, stack, 0, sp);
	}

	public State getLast() {
		return stack[sp-1];
	}

	public int size(){
		return sp;
	}

	public State peek(int index){
		return stack[index];
	}

	public void swap(State tmp2) {
		pop();
		push(tmp2);
	}

	@SuppressWarnings("unchecked")
	public void rot(int n) {
		State[] tmp = (State[]) new Object[n];
		for(int i=0; i<n; i++)
			tmp[i] = pop();
		push(tmp[0]);
		for(int i=n-1; i>0; i--)
			push(tmp[i]);
	}



	public ArrayList<Stack<State>> copy(int size){
		ArrayList<Stack<State>> sslist = new ArrayList<Stack<State>>();
		Stack<State> ss = null;
		int sp = this.sp;
		for(int i = 0; i < size; i++){
			ss = new Stack<State>();
			for(int j = sp-1; j >= 0; j--){
				ss.push(peek(j));
				ss.rot(ss.size());
			}
			sslist.add(ss);
		}
		return sslist;
	}

	public boolean isEmpty(){
		if(sp == 0){
			return true;
		}else{
			return false;
		}
	}
}
