package com.creaty.game.framework;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;


public class StateMachine2 {

	public static final String tag = "StateMachine";

	/** 判断状态转换条件是否成立的接口 */
	public interface StateTransitionTester {
		/**判断是否能从一个状态转到另一个状态<br>
		 * 假设状态机拥有state_1, state_2, ..., state_n这n个状态，判断树应该有如下的结构：<br>
		 * if(from.state == state_1){<br>
		 * &emsp;//假设从state_1可以转换到state_2,state_5和自身（state_1）<br>
		 * &emsp;if(to.state == state_2){<br>
		 * &emsp;&emsp;if(从state_1转换到state_2的条件)<br>
		 * &emsp;&emsp;&emsp;return true;<br>
		 * &emsp;}else if(to.state == state_5){<br>
		 * &emsp;&emsp;if(从state_1转换到state_5的条件)<br>
		 * &emsp;&emsp;&emsp;return true;<br>
		 * &emsp;}else if(to.state == state_1){<br>
		 * &emsp;&emsp;if(从state_1转换到state_1的条件)<br>
		 * &emsp;&emsp;&emsp;return true;<br>
		 * &emsp;}<br>
		 * }<br>
		 * &emsp;...<br>
		 * }else if(from.state == state_2){<br>
		 * &emsp;...<br>
		 * }<br>
		 * ...<br>
		 * else if(from.state == state_n){<br>
		 * &emsp;...<br>
		 * }<br>
		 * return false;<br>
		 * */
		public boolean tryChange(Object objectWithState, State from, State to);
	}
	/**所有状态*/
	ArrayList<State> stateList;
	/**状态转换逻辑成员*/
	StateTransitionTester tester;
	/** 状态更新报告开关 */
	public boolean logSwitch;

	public StateMachine2(StateTransitionTester tester) {
		stateList = new ArrayList<State>();
		this.tester = tester;
		logSwitch = false;
	}

	/**
	 * 登记状态机所应该包含的状态，如果登记的状态是初始状态，则自动进入此状态
	 * 
	 * @param state
	 *            状态
	 * @return 是否成功登记此状态，如果之前有状态和要登记的状态相同会返回false
	 */
	public boolean registerState(State state) {
		if (!stateList.contains(state)) {
			stateList.add(state);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 开启状态机。如果状态机中有该初始状态，具有状态的对象进入此状态，否则开启状态机失败。
	 * 
	 * @param 具有状态的对象
	 */
	public boolean start(Object objectWithState, State initialState) {
		if (stateList.contains(initialState)) {
			initialState.stateUpdater.enter(objectWithState);
			return true;
		} else {
			return false;
		}
	}

	public boolean Stop(Object objectWithState) {
		return false;
	}

	/**
	 * 更新状态对象的状态
	 * 
	 * @param 状态对象
	 * @param 状态对象当前的状态
	 * @return 状态对象新的状态
	 */
	public State updateState(Object objectWithState, State currState) {
		int size = stateList.size();
		State state = null;
		State currentState = currState;
		if (currentState == null)
			return currentState;
		for (int i = 0; i < size; i++) {
			state = stateList.get(i);
			if (tester.tryChange(objectWithState, currentState, state)) {
//				if (logSwitch)
//					Log.d(tag, "state change from " + currentState.name
//							+ " to " + state.name);
				if (currentState.equals(state)) {
					currentState.stateUpdater.hold(objectWithState);
				} else {
					if (logSwitch)
						Gdx.app.log(tag, "state change from " + currentState.name
								+ " to " + state.name);
					currentState.stateUpdater.exit(objectWithState);
					currentState = state;
					currentState.stateUpdater.enter(objectWithState);
				}
				break;
			}
		}
		return currentState;
	}

	/** 根据代码返回对应的状态，如果没有找到相应的状态，返回null */
	public State getState(int code) {
		State state = null;
		int size = stateList.size();

		for (int i = 0; i < size; i++) {
			state = stateList.get(i);
			if (state.code == code) {
				return state;
			}
		}
		return null;
	}

}
