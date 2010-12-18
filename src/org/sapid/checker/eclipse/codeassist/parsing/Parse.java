package org.sapid.checker.eclipse.codeassist.parsing;

import java.util.ArrayList;


public class Parse implements Cloneable{
	private State state;
	private StackHolder<Stack<State>> stacklist = new StackHolder<Stack<State>>();
	private StackHolder<Stack<State>> trslist = new StackHolder<Stack<State>>();
	private ArrayList<Token> tokenlist = new ArrayList<Token>();
	private boolean initialize = true;


	public Parse() {
		// TODO 自動生成されたコンストラクター・スタブ
	}


	public State getCurrentState() {
		return state;
	}

	public void transit(State state) {
		while(stacklist.size() != 0){
			Stack<State> tempstack = stacklist.removeFirst();
			state = tempstack.getLast();
			this.state = state;
			ArrayList<Stack<State>> copystack;
			ArrayList<State> loopstack = new ArrayList<State>();
			boolean tokenflag = false;
			State s = null;
			ReduceTransition rt = null;

			if(state.isReduce() == false && state.isShift() == false	//t条件
					&& state.isNecessary() == true && state.getTransitionList().size() >= 2) {
				int tssize = state.getTransitionList().size();
				copystack = tempstack.copy(state.getTransitionList().size());
				State[] st = new State[tssize];
				for (int j = tssize-1; j >= 0; j--) {
					st[j] = copystack.get(j).getLast().getTransitionList().get(j).next;
					copystack.get(j).swap(st[j]);
					for(int i = copystack.get(j).size()-2; i >= 0 ; i--) {
						if(copystack.get(j).getLast().getName().equals(copystack.get(j).peek(i).getName())){
							copystack.remove(j);
							break;
						} else if(copystack.get(j).peek(i).isLoopShift() == false){
							break;
						}
					}
				}
				for(int i = 0; i < copystack.size(); i++){
					stacklist.add(copystack.get(i));
				}

			} else if(state.isShift() == true) {		//シフトのとき
				if(state.isNecessary() == true) {
					s = state.getTransitionList().get(0).next;
					tempstack.push(s);
					stacklist.add(tempstack);
				} else {
					copystack = tempstack.copy(2);
					for(int i = 0; i < tempstack.getLast().getTransitionList().size(); i++) {
						if(tempstack.getLast().getTransitionList().get(i) instanceof NTransition) {
							s = state.getTransitionList().get(i).next;
							copystack.get(i).swap(s);
							stacklist.add(copystack.get(i));
						} else {
							s = state.getTransitionList().get(i).next;
							copystack.get(i).push(s);
							stacklist.add(copystack.get(i));
						}
					}
				}

			} else if (state.isReduce() == true && tempstack.size() == 1) { //状態遷移が完全に終わるとき

			} else if (state.isReduce() == true && tempstack.size() >= 2) { //reduce

				if(tempstack.peek(tempstack.size()-2).isLoopShift() == true && tempstack.peek(tempstack.size()-2).isSubLoopShift() == false) {
					copystack = tempstack.copy(2);
					for(int i = 0; i < state.getTransitionList().size(); i++){
						if(state.getTransitionList().get(i) instanceof ReduceTransition) {
							rt = (ReduceTransition) state.getTransitionList().get(i);
						}
						if(rt.reduceState.getName().equals(tempstack.peek(tempstack.size()-2).getName())) {
							s = state.getTransitionList().get(i).next;
						}
					}

					copystack.get(0).pop();
					copystack.get(1).pop();

					for(int i = copystack.get(0).size(); i >= 0; i--) {
						if(copystack.get(0).getLast().isLoopShift() == false) {
							break;
						}
						loopstack.add(copystack.get(0).getLast());
						copystack.get(0).pop();
						copystack.get(1).pop();
					}
					for(int j = loopstack.size(); j > 0; j--) {
						for(int i = 0; i < copystack.size(); i++) {
							copystack.get(i).push(loopstack.get(j-1));
						}
					}

					for(int j = loopstack.size(); j > 0; j--) {			//改良する
						copystack.get(1).push(loopstack.get(j-1));
					}


					for(int i = 0; i < copystack.size(); i++) {
						copystack.get(i).swap(s);
						stacklist.add(copystack.get(i));
					}

				} else {
					for(int i = 0; i < state.getTransitionList().size(); i++){
						if(state.getTransitionList().get(i) instanceof ReduceTransition) {
							rt = (ReduceTransition) state.getTransitionList().get(i);
						}
						if(rt.reduceState.getName().equals(tempstack.peek(tempstack.size()-2).getName())) {
							s = state.getTransitionList().get(i).next;
						}
					}
					tempstack.pop();
					tempstack.swap(s);
					stacklist.add(tempstack);
				}


			} else if(state.isShift() == false && state.isReduce() == false) {	//ε遷移 & Token直前

				for(int i = 0; i < state.getTransitionList().size(); i++){
					if((state.getTransitionList().get(i) instanceof TokenTransition)){	//Token
						tokenflag = true;
						if(tokenflag == true) {
							if(state.isNecessary() == true) {
								trslist.add(tempstack);
								if(stacklist.size() != 0) {
								}
							} else {
								copystack = tempstack.copy(2);
								for(int j = 0; j < tempstack.getLast().getTransitionList().size(); j++) {
									if(tempstack.getLast().getTransitionList().get(j) instanceof NTransition) {
										s = state.getTransitionList().get(j).next;
										copystack.get(j).swap(s);
										stacklist.add(copystack.get(j));
									} else {
										trslist.add(copystack.get(j));
									}
								}
							}
						}
						break;
					} else if(i == state.getTransitionList().size()-1){	//ε遷移
						if(state.isNecessary() == true){
							s = state.getTransitionList().get(0).next;
							tempstack.swap(s);
							stacklist.add(tempstack);
						} else {
							copystack = tempstack.copy(2);
							for(int j = 0; j < tempstack.getLast().getTransitionList().size(); j++) {
								s = state.getTransitionList().get(j).next;
								copystack.get(j).swap(s);
								stacklist.add(copystack.get(j));
							}
						}
					}
				}
			}

		}
	}


	//	@SuppressWarnings("unchecked")
	@SuppressWarnings("unchecked")
	public ArrayList<Token> getNextToken(ArrayList<Token> xpathtoken, State current){
		Token focus;

		if(initialize == true) {
			Stack<State> stack = new Stack<State>();
			stack.push(current);
			stacklist.add(stack);
			initialize = false;
			transit(stacklist.getFirst().getLast());
		}

		for(int i = 0; i < trslist.size(); i++) {
			TokenTransition t = (TokenTransition)trslist.get(i).getLast().getTransitionList().get(0);
			if(t.token != null){	//tのコンストラクタの判別   引数にlistがあるときはtokenlistにlist分追加する
				t.token.setOffset(0);
				this.tokenlist.add(t.token);
			} else {
				for (int l = 0; l < t.tokenlist.size(); l++) {
					t.tokenlist.get(l).setOffset(0);
					this.tokenlist.add(t.tokenlist.get(l));
				}
			}
		}
		realloc(this.tokenlist);

		if(xpathtoken.size() == 0){
			return tokenlist;
		} else {
			for(int i = 0; i < xpathtoken.size(); i++){
				ArrayList<Token> preTokenList = (ArrayList<Token>) tokenlist.clone();
				ArrayList<Token> tokenlist = new ArrayList<Token>();
				this.tokenlist = tokenlist;
				String partToken = null;
				String part = null;
				if(xpathtoken.size() == 1){
					for(int j= 0; j < preTokenList.size(); j++) {
						Token t = preTokenList.get(j);
						partToken = preTokenList.get(j).getToken();
						part = xpathtoken.get(i).getToken();
						if(t.getToken().length() > xpathtoken.get(i).getToken().length()-1){
							if (t.getToken().substring(0, xpathtoken.get(i).getToken().length()).equals(xpathtoken.get(i).getToken())) {
								if(!partToken.equals(part)){
									t.setOffset(xpathtoken.get(i).getToken().length());
									this.tokenlist.add(t);
								}
							}
						}
					}
				} else if(xpathtoken.size() > 1) {		//1つ前のトークンリスト
					for(int j = 0; j < preTokenList.size(); j++) {
						Token t = preTokenList.get(j);
						partToken = preTokenList.get(j).getToken();
						part = xpathtoken.get(i).getToken();
						if(preTokenList.get(j).getToken().length() > xpathtoken.get(i).getToken().length()-1){
							if (preTokenList.get(j).getToken().substring(0, xpathtoken.get(i).getToken().length()).equals(xpathtoken.get(i).getToken())) {
								if(!partToken.equals(part)){
									t.setOffset(xpathtoken.get(i).getToken().length());
									tokenlist.add(preTokenList.get(j));
								}
							}
						}
					}
				}
				focus = xpathtoken.get(i);
				removeStack(focus);
				StackHolder<Stack<State>> temptrslist =  trslist.clone();
				StackHolder<Stack<State>> trslist = new StackHolder<Stack<State>>();
				this.trslist = trslist;
				StackHolder<Stack<State>> stacklist = new StackHolder<Stack<State>>();
				this.stacklist = stacklist;
				if(temptrslist.size() > 0){
					for(int j = 0; j < temptrslist.size(); j++){
						this.stacklist.add(temptrslist.get(j));
						transit(this.stacklist.get(0).getLast());
					}
					StackHolder<Stack<State>> trs = this.trslist;
					if(trs != null){
						for (int k = 0; k < trs.size(); k++) {
							TokenTransition tt = (TokenTransition)trs.get(k).getLast().getTransitionList().get(0);
							if(tt.token != null){	//tのコンストラクタの判別　引数にlistがあるときはtokenlistにlist分追加するため
								tt.token.setOffset(0);
								tokenlist.add(tt.token);
							} else {
								for (int l = 0; l < tt.tokenlist.size(); l++) {
									tt.tokenlist.get(l).setOffset(0);
									tokenlist.add(tt.tokenlist.get(l));
								}
							}
						}
					}
				}
			realloc(tokenlist);
			}
			return tokenlist;
		}
	}

	public void removeStack(Token token) {
		int size = trslist.size();
		for(int i = size; i > 0; i--) {
			Transition t = trslist.get(i-1).getLast().getTransitionList().get(0);
			if(t instanceof TokenTransition) {
				TokenTransition tt = (TokenTransition) t;
				if(tt.token != null){
					if(!token.getToken().equals(tt.token.getToken())) {
						trslist.remove(i-1);
					} else {
						State s = trslist.get(i-1).getLast().getTransitionList().get(0).next;
						trslist.get(i-1).swap(s);
					}
				} else {
					for(int j = 0; j < tt.tokenlist.size(); j++){
						if(token.getToken().equals(tt.tokenlist.get(j).getToken())){
							State s = trslist.get(i-1).getLast().getTransitionList().get(0).next;
							trslist.get(i-1).swap(s);
							break;
						}
						if(j == tt.tokenlist.size()-1){
							trslist.remove(i-1);
						}
					}
				}
			}
		}
	}

	public void realloc(ArrayList<Token> tokenlist){		//重複tokenの除去
		ArrayList<Token> templist = new ArrayList<Token>();
		for(int j = 0; j < tokenlist.size(); j++) {
			if(j == 0) {
				templist.add(tokenlist.get(j));
			}
			for (int k = 0; k < templist.size(); k++) {
				if(templist.get(k).getToken().equals(tokenlist.get(j).getToken())){
					break;
				}else if(k == templist.size()-1){
					templist.add(tokenlist.get(j));
				}
			}
		}
		tokenlist.clear();
		tokenlist.addAll(templist);

	}

}
