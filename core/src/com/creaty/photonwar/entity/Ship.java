package com.creaty.photonwar.entity;

import java.util.Random;

import com.badlogic.androidgames.framework.model.DynamicGameObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.creaty.math.Vector2;

public class Ship extends DynamicGameObject {
	public static final String tag = "Ship";
	/** 战舰处于停止状态 */
	public boolean isClockwise = false;

	public static enum SHIP_STATE {
		FORMATION, ATTACK, STOP, MOVE, CIRCLE, MOVE_TO_GATHER
	};

	// public static final int STATE_FORMATION = -3;
	// public static final int STATE_ATTACK = -2;
	// public static final int STATE_STOP = -1;
	// public static final int STATE_MOVE = 0;
	// public static final int STATE_CIRCLE = 1;
	// /** 另一种飞行状态，飞行到目的地以后停止 */
	// public static final int STATE_MOVE_TO_GATHER = 2;
	/** 战舰级别为微型（战斗机 */
	public static final int MINITYPE = 3;
	/** 战舰级别为轻型（护卫舰级） */
	public static final int LIGHTTYPE = 4;
	/** 战舰级别为中型（驱逐舰级） */
	public static final int MIDDLETYPE = 5;
	/** 战舰级别为大型（巡洋舰级） */
	public static final int BIGTYPE = 6;
	/** 战舰级别为重型（战列舰级） */
	public static final int WEIGHTTYPE = 7;

	protected SHIP_STATE state;

	/** 环绕角速度 单位：角度/秒；顺时针方向为正方向 */
	public float angularVelocity;
	/** 环绕中心 */
	public Vector2 rotationCenter;
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
	/** 所有者 */
	Race owner;
	/** 航行基础速率 */
	public float basicSpeed;

	/** 航行目的地(坐标位置) */
	public Vector2 spaceDestination;
	/** 航行目的地（据点目标） */
	public StrongHold destination;
	public Random random = new Random();
	/** 所属的作战单位 */
	public ShipGroup belongGroup = null;

	public Ship() {
		this(0, 0, 0, null, .0f, SHIP_STATE.MOVE, 0, MINITYPE);
	}

	public Ship(float x, float y, float radius, Race owner, float speed,
			SHIP_STATE state, int detectRadius, int shipLevel) {
		super(x, y, radius);
		// TODO Auto-generated constructor stub
		angularVelocity = 0;
		rotationCenter = new Vector2();
		this.spaceDestination = new Vector2();
		this.owner = owner;
		this.basicSpeed = speed;
		this.state = state;
		this.detectRadius = detectRadius;
		this.shipLevel = shipLevel;
	}

	/**
	 * 重置舰船
	 * 
	 * @param angularVelocity
	 *            舰船角速度
	 * @param rotationCenter
	 *            舰船环绕中心点
	 * @param shipLevel
	 *            战舰级别
	 * @param detectRadius
	 *            战舰的探测半径（作战半径）
	 * @param hp
	 *            生命
	 * @param atk
	 *            攻击
	 * @param def
	 *            防御
	 * @param owner
	 *            所有者
	 * @param basicSpeed
	 *            基础速度
	 * @param spaceDestination
	 *            航行目的地(非据点)
	 * @param destination
	 *            航行目的地（据点目标）
	 * @param belongGroup
	 *            所属的作战单位
	 */
	public void resetShip(float angularVelocity, Vector2 rotationCenter,
			int shipLevel, float detectRadius, int hp, int atk, int def,
			Race owner, float basicSpeed, Vector2 spaceDestination,
			StrongHold destination, ShipGroup belongGroup) {
		this.angularVelocity = angularVelocity;
		this.rotationCenter.set(rotationCenter);
		this.shipLevel = shipLevel;
		this.detectRadius = detectRadius;
		this.hp = hp;
		this.atk = atk;
		this.def = def;
		this.owner = owner;
		this.basicSpeed = basicSpeed;
		this.spaceDestination.set(spaceDestination);
		this.destination = destination;
		this.belongGroup = belongGroup;
	}

	/**
	 * 清除残留信息
	 */
	public void ClearOldInformation() {
		// not implemented
	}

	public void MoveToShowFormation(float x, float y) {
		state = SHIP_STATE.FORMATION;
		move(x, y);
	}

	/**
	 * 执行航行指令
	 */
	public void moveTo(StrongHold stronghold) {
		state = SHIP_STATE.MOVE;
		move(stronghold.position.x, stronghold.position.y);
		destination = stronghold;
	}

	/** 去集合点所确定的作战单位附近集合 */
	public void moveToGather(ShipGroup groupItem) {

		state = SHIP_STATE.MOVE_TO_GATHER;

		Circle circle = ((Circle) groupItem.bounds);
		float drawRadius = groupItem.drawRadius;
		float x, y;
		x = circle.x - drawRadius + random.nextFloat() * drawRadius * 2;
		float upperY = (float) (Math.sqrt(drawRadius * drawRadius
				- (x - circle.x) * (x - circle.x))
				+ circle.y);
		float bottomY = (float) (circle.y - Math.sqrt(drawRadius
				* drawRadius - (x - circle.x) * (x - circle.x)));
		y = bottomY + (upperY - bottomY) * random.nextFloat();

		move(x, y);
	}

	/** 飞船停止 */
	protected void StopMove() {
		this.velocity.set(0, 0);
		if (state == SHIP_STATE.MOVE_TO_GATHER || state == SHIP_STATE.FORMATION) {
			this.belongGroup.SingleShipAwairOrder(this);
		}
		// 行进中停止？发生战斗？
		this.state = SHIP_STATE.STOP;
		
	}

	/** 执行环绕指令指令 */
	public void circle(StrongHold stronghold, boolean isClockwise) {
		state = SHIP_STATE.CIRCLE;
		destination = null;
		//this.spaceDestination.set(0, 0);
		this.isClockwise = isClockwise;
		// Gdx.app.log(tag, "change destination to null");
		float distance = this.position.dst(stronghold.position);
		angularVelocity = (float) ((basicSpeed / distance) / Math.PI / distance * 180.f);
		rotationCenter.set(stronghold.position);
	}
	
	/**
	 * 向指定位置前进
	 */
	protected void move(float x, float y) {

		this.velocity.set(x, y).sub(position).nor().mul(basicSpeed);
		spaceDestination.set(x, y);
	}
	
	public void SetOrientation(float targetX, float targetY) {
		this.velocity.set(targetX, targetY).sub(position).nor();
	}

	public void update(float deltaTime) {
		switch (state) {
		case MOVE:
			updateMove(deltaTime);
			break;
		case CIRCLE:
			updateCircle(deltaTime);
			break;
		case MOVE_TO_GATHER:
			UpdateMoveShort(deltaTime);
			break;
		case FORMATION:
			UpdateMoveShort(deltaTime);
			break;
		case STOP:
			updateStop(deltaTime);
			break;
		default:
			Gdx.app.log("Ship update", "never should be here!");
		}
	}
	
	/**短途航行更新，每次调用都会检查是否到达目的地。如果到达目的地，飞船自动转入STOP状态*/
	protected void UpdateMoveShort(float deltaTime) {
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		((Circle) bounds).setPosition(position);
		if (Math.abs(position.x - spaceDestination.x) <= 0.1
				&& Math.abs(position.y - spaceDestination.y) <= 0.1) {
			position.x = spaceDestination.x;
			position.y = spaceDestination.y;
			StopMove();
			updateStop(deltaTime);
		}
	}
	/**航行更新，需要外部检测是否到达目的地。飞船将一直按照其设定的速度航行*/
	protected void updateMove(float deltaTime) {
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		((Circle) bounds).setPosition(position);

	}

	protected void updateCircle(float deltaTime) {
		if (! isClockwise) {
			position.rotate(angularVelocity * deltaTime, rotationCenter);
			((Circle) bounds).setPosition(position);
		} else {
			position.rotate(-angularVelocity * deltaTime, rotationCenter);
			((Circle) bounds).setPosition(position);
		}
	}

	protected void updateStop(float deltaTime) {
		// right now, do nothing here
	}

	public final SHIP_STATE getState() {
		return state;
	}
}
