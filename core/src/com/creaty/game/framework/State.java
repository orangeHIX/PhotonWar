package com.creaty.game.framework;


/** 状态的定义 */
public class State {
	/** 包含进入，离开，保持状态三种方法的静态类 */
	public static class StateUpdater {
		public static final String tag = "StateUpdater";

		/**
		 * 进入此状态调用的方法
		 * 
		 * @param obj
		 *            持有此状态的实体对象
		 */
		public void enter(Object obj) {
			// Log.d(tag, "enter");
		};

		/**
		 * 离开此状态调用的方法
		 * 
		 * @param obj
		 *            持有此状态的实体对象
		 */
		public void hold(Object obj) {
			// Log.d(tag, "hold");
		};

		/**
		 * 保持此状态调用的方法
		 * 
		 * @param obj
		 *            持有此状态的实体对象
		 */
		public void exit(Object obj) {
			// Log.d(tag, "exit");
		};
	}

	/** 状态编码 */
	public final int code;
	/** 状态名 */
	public final String name;
	/** 状态更新器，具体使用时需要继承这个基类并重写需要调用的方法 */
	public StateUpdater stateUpdater;

	/**
	 * @param code
	 *            状态编码
	 * @param name
	 *            状态名
	 * @param stateUpdater
	 *            状态更新器，具体使用时需要继承这个基类并重写需要调用的方法
	 */
	public State(int code, String name, StateUpdater stateUpdater) {
		this.code = code;
		this.name = name;
		this.stateUpdater = stateUpdater;
	}

	/** 暂时没有用 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		return result;
	}

	@Override
	/**比较2个状态是否是一个状态*/
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (code != other.code)
			return false;
		return true;
	}

}