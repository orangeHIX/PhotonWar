package com.creaty.photonwar.entity;

import java.util.ArrayList;
import java.util.List;

import com.creaty.math.Vector2;


/*势力*/
public class Race {

	public static class ShipBlueprint {
		/** 战舰级别 */
		public int shipLevel;
		/** 战舰的探测半径（作战半径） */
		public float detectRadius;
		/** 生命 */
		public int hp;
		/** 攻击力 */
		public int atk;
		/** 防御力 */
		public int def;
		/** 航行基础速率 */
		public float basicSpeed;

		/** 耗能 */
		public float encon;

		public ShipBlueprint(int shipLevel, float detectRadius, int hp, int atk,
				int def, float basicSpeed, float encon) {
			super();
			this.shipLevel = shipLevel;
			this.detectRadius = detectRadius;
			this.hp = hp;
			this.atk = atk;
			this.def = def;
			this.basicSpeed = basicSpeed;
			this.encon = encon;
		}
	}

	public static final int AI_PLAYER = 0;
	public static final int HU_PLAYER = 1;
	private static final int INIT_NUM_STRONGHOLD = 5;
	// /**势力拥有的总人口上限*/
	// protected int maxPopulation = 0;
	// /**势力拥有的总人口*/
	// public int population = 0;
	/** 势力拥有的舰船 */
	private List<StrongHold> allStrongHold;
	/** 势力ID，唯一区分 */
	public final int raceId;
	/** 判断操纵者是AI还是Human */
	public final int master;
	/** 舰船设计蓝图，每个势力根据各自的舰船蓝图生产舰船 */
	public final ShipBlueprint shipBlueprint;

	public Race(int id, int master, ShipBlueprint shipBlueprint) {
		this.raceId = id;
		this.master = master;
		this.shipBlueprint = shipBlueprint;
		allStrongHold = new ArrayList<StrongHold>(INIT_NUM_STRONGHOLD);
	}

	// public Race(int id, ArrayList<StrongHold> strongHoldList, boolean aiOrHu)
	// {
	// this.raceId = id;
	// this.master = aiOrHu ? HU_PLAYER : AI_PLAYER;
	// this.allStrongHold = strongHoldList;
	// }

	public boolean AddStrongHold(StrongHold strongHoldItem) {
		if (strongHoldItem == null || allStrongHold.contains(strongHoldItem))
			return false;
		allStrongHold.add(strongHoldItem);
		// maxPopulation += strongHoldItem.getPopulationCapacity();
		return true;
	}

	public boolean RemoveStrongHold(StrongHold strongHoldItem) {
		if (strongHoldItem == null || !allStrongHold.contains(strongHoldItem))
			return false;
		allStrongHold.remove(strongHoldItem);
		// maxPopulation -= strongHoldItem.getPopulationCapacity();
		return true;
	}

	public boolean SetStrongHoldList(ArrayList<StrongHold> strongHoldList) {
		if (strongHoldList == null)
			return false;
		this.allStrongHold = strongHoldList;
		return true;
	}

	/**零向量，改装舰船用*/
	private Vector2 emp = new Vector2();
	/**
	 * 根据势力的舰船蓝图改装舰船.<br>
	 * 这里不使用ship的ClearOldInformation()方法，
	 * 因为舰船的相关属性都会重置
	 * @param ship
	 *            需要改装的飞船
	 * @param 改装后的飞船
	 */
	public Ship degsinNewShip(Ship ship) {
		ship.resetShip(0f, emp, shipBlueprint.shipLevel,
				shipBlueprint.detectRadius, shipBlueprint.hp,
				shipBlueprint.atk, shipBlueprint.def, this,
				shipBlueprint.basicSpeed, emp, null, null);
		return ship;
	}

	// /**获得势力拥有的总人口*/
	// public int getPopulation() {
	// int size = allStrongHold.size();
	// int population = 0;
	// for(int i = 0; i < size; i++){
	// StrongHold hold = allStrongHold.get(i);
	// population += hold.ships1.size();
	// }
	// return population;
	// }

}
// public class Role {
//
// /**空角色标示*/
// public static final int ROLE_BLANK = 0;
// public static final int ROLE_PLAYER = 1;
// public final int identity;
// public Role( int identity )
// {
// this.identity = identity;
// }
//
// }
//
//
