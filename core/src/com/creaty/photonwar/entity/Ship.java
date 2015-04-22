package com.creaty.photonwar.entity;

import java.util.Random;

import com.badlogic.androidgames.framework.model.DynamicGameObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.creaty.math.Vector2;

public class Ship extends DynamicGameObject {
	public static final String tag = "Ship";
	/** ս������ֹͣ״̬ */
	public boolean isClockwise = false;

	public static enum SHIP_STATE {
		FORMATION, ATTACK, STOP, MOVE, CIRCLE, MOVE_TO_GATHER
	};

	// public static final int STATE_FORMATION = -3;
	// public static final int STATE_ATTACK = -2;
	// public static final int STATE_STOP = -1;
	// public static final int STATE_MOVE = 0;
	// public static final int STATE_CIRCLE = 1;
	// /** ��һ�ַ���״̬�����е�Ŀ�ĵ��Ժ�ֹͣ */
	// public static final int STATE_MOVE_TO_GATHER = 2;
	/** ս������Ϊ΢�ͣ�ս���� */
	public static final int MINITYPE = 3;
	/** ս������Ϊ���ͣ����������� */
	public static final int LIGHTTYPE = 4;
	/** ս������Ϊ���ͣ����𽢼��� */
	public static final int MIDDLETYPE = 5;
	/** ս������Ϊ���ͣ�Ѳ�󽢼��� */
	public static final int BIGTYPE = 6;
	/** ս������Ϊ���ͣ�ս�н����� */
	public static final int WEIGHTTYPE = 7;

	protected SHIP_STATE state;

	/** ���ƽ��ٶ� ��λ���Ƕ�/�룻˳ʱ�뷽��Ϊ������ */
	public float angularVelocity;
	/** �������� */
	public Vector2 rotationCenter;
	/** ս������ */
	public int shipLevel;
	/** ս����̽��뾶����ս�뾶�� */
	public float detectRadius;
	/** ���� */
	public int hp;
	/** ������ */
	public int atk;
	/** ������ */
	public int def;
	/** ������ */
	Race owner;
	/** ���л������� */
	public float basicSpeed;

	/** ����Ŀ�ĵ�(����λ��) */
	public Vector2 spaceDestination;
	/** ����Ŀ�ĵأ��ݵ�Ŀ�꣩ */
	public StrongHold destination;
	public Random random = new Random();
	/** ��������ս��λ */
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
	 * ���ý���
	 * 
	 * @param angularVelocity
	 *            �������ٶ�
	 * @param rotationCenter
	 *            �����������ĵ�
	 * @param shipLevel
	 *            ս������
	 * @param detectRadius
	 *            ս����̽��뾶����ս�뾶��
	 * @param hp
	 *            ����
	 * @param atk
	 *            ����
	 * @param def
	 *            ����
	 * @param owner
	 *            ������
	 * @param basicSpeed
	 *            �����ٶ�
	 * @param spaceDestination
	 *            ����Ŀ�ĵ�(�Ǿݵ�)
	 * @param destination
	 *            ����Ŀ�ĵأ��ݵ�Ŀ�꣩
	 * @param belongGroup
	 *            ��������ս��λ
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
	 * ���������Ϣ
	 */
	public void ClearOldInformation() {
		// not implemented
	}

	public void MoveToShowFormation(float x, float y) {
		state = SHIP_STATE.FORMATION;
		move(x, y);
	}

	/**
	 * ִ�к���ָ��
	 */
	public void moveTo(StrongHold stronghold) {
		state = SHIP_STATE.MOVE;
		move(stronghold.position.x, stronghold.position.y);
		destination = stronghold;
	}

	/** ȥ���ϵ���ȷ������ս��λ�������� */
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

	/** �ɴ�ֹͣ */
	protected void StopMove() {
		this.velocity.set(0, 0);
		if (state == SHIP_STATE.MOVE_TO_GATHER || state == SHIP_STATE.FORMATION) {
			this.belongGroup.SingleShipAwairOrder(this);
		}
		// �н���ֹͣ������ս����
		this.state = SHIP_STATE.STOP;
		
	}

	/** ִ�л���ָ��ָ�� */
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
	 * ��ָ��λ��ǰ��
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
	
	/**��;���и��£�ÿ�ε��ö������Ƿ񵽴�Ŀ�ĵء��������Ŀ�ĵأ��ɴ��Զ�ת��STOP״̬*/
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
	/**���и��£���Ҫ�ⲿ����Ƿ񵽴�Ŀ�ĵء��ɴ���һֱ�������趨���ٶȺ���*/
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
