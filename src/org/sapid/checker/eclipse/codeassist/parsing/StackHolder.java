package org.sapid.checker.eclipse.codeassist.parsing;



import java.util.ArrayList;

@SuppressWarnings("hiding")
public class StackHolder<Stack> {
	private Stack[] queue;
	private final static int DEFAULT_CAPACITY = 10;
	private int sp;
	private int capacity;

	public StackHolder(){
		this(DEFAULT_CAPACITY);
	}

	@SuppressWarnings("unchecked")
	public StackHolder(int initialCapacity) {
		capacity = initialCapacity;
		queue = (Stack[]) new Object[initialCapacity];
	}


	public void add(Stack stack) {
		queue[sp++] = stack;
		capacityCheck();
	}

	public Stack removeFirst() {
		Stack s = queue[0];
		prestep(0);
		--sp;
		return s;
	}

	public Stack remove(int i){
		Stack s = queue[i];
		prestep(i);
		--sp;
		return s;
	}

	private void prestep(int offset){
		for(int i = offset; i < sp; i++){
			queue[i] = queue[i+1];
			if(i == sp-1){
				queue[sp-1] = null;
			}
		}
	}

	private void capacityCheck() {
		if(sp >= capacity)
			expandCapacity();
	}

	@SuppressWarnings("unchecked")
	private void expandCapacity() {
		capacity += 2;
		Object[] oldStack = queue;
		queue = (Stack[]) new Object[capacity];
		System.arraycopy(oldStack, 0, queue, 0, sp);
	}

	public Stack getFirst() {
		return queue[0];
	}

	public int size(){
		return sp;
	}

	public Stack get(int index){
		return queue[index];
	}

	public void swap(Stack tmp2) {
		removeFirst();
		add(tmp2);
	}

	@SuppressWarnings("unchecked")
	public void rot(int n) {
		Stack[] tmp = (Stack[]) new Object[n];
		for(int i=0; i < n; i++)
			tmp[i] = removeFirst();
		add(tmp[0]);
		for(int i = n-1; i > 0; i--)
			add(tmp[i]);
	}

	public StackHolder<Stack> clone(){
		StackHolder<Stack> sh = null;
		int sp = this.sp;
			sh = new StackHolder<Stack>();
			for(int j = 0; j < sp; j++){
				sh.add(get(j));
			}
			return sh;
	}


	public ArrayList<StackHolder<Stack>> copy(int size){
		ArrayList<StackHolder<Stack>> sslist = new ArrayList<StackHolder<Stack>>();
		StackHolder<Stack> ss = null;
		int sp = this.sp;
		for(int i = 0; i < size; i++){
			ss = new StackHolder<Stack>();
			for(int j = 0; j < sp; j++){
				ss.add(get(j));
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
