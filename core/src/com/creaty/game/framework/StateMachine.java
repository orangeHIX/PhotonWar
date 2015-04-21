package com.creaty.game.framework;

import java.util.ArrayList;



import com.badlogic.gdx.Gdx;
import com.creaty.game.framework.State.StateUpdater;

/** 存储着可用的状态列表和当前的状态以及状态转化的接口 */
public class StateMachine {

	public static final String tag = "StateMachine";

	/** 判断状态转换条件是否成立的接口 */
	public interface StateTransitionTester {
		public boolean tryChange(State from, State to);
	}

	/** 持有该状态机的对象 */
	Object objectWithState;
	/**所有状态*/
	ArrayList<State> stateList;
	State currentState;
	/**状态转换逻辑成员*/
	StateTransitionTester tester;

	public StateMachine(Object obj, StateTransitionTester tester) {
		this.objectWithState = obj;
		stateList = new ArrayList<State>();
		this.tester = tester;
	}

	/**
	 * 登记状态机所应该包含的状态，如果登记的状态是初始状态，则自动进入此状态
	 * 
	 * @param state
	 *            状态
	 * @param isInitial
	 *            知否是初始状态
	 * @return 是否成功登记此状态，如果之前有状态和要登记的状态相同会返回false
	 */
	public boolean registerState(State state, boolean isInitial) {
		if (!stateList.contains(state)) {
			if (isInitial) {
				currentState = state;
				currentState.stateUpdater.enter(objectWithState);
			}
			stateList.add(state);
			return true;
		} else {
			return false;
		}
	}

	public boolean registerState(int code, String name,
			StateUpdater stateUpdater, boolean isInitial) {
		return registerState(new State(code, name, stateUpdater), isInitial);
	}

	/** 尝试变换状态（将已有的所有状态与当前状态尝试转换，一旦成功将退出循环返回） */
	public void updateState() {
		int size = stateList.size();
		State state;
		for (int i = 0; i < size; i++) {
			state = stateList.get(i);
			if (tester.tryChange(currentState, state)) {
				Gdx.app.log(tag, "update state from " + currentState.name + " to "
						+ state.name);
				if (currentState.equals(state)) {
					currentState.stateUpdater.hold(objectWithState);
				} else {
					currentState.stateUpdater.exit(objectWithState);
					currentState = state;
					currentState.stateUpdater.enter(objectWithState);
				}
				break;
			}
		}
	}

	public int getCurrentStateCode() {
		return currentState.code;
	}

	public String getCurrentStateName() {
		return currentState.name;
	}

}
