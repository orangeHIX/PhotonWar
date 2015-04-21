package com.creaty.photonwar.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.androidgames.framework.model.DynamicGameObject;
import com.badlogic.gdx.math.Circle;
import com.creaty.game.framework.SmartLog;
import com.creaty.photonwar.inferface.CombatUnit;
import com.creaty.photonwar.inferface.EntityManager;

/** ս����Ⱥ���� */
public class ShipGroup extends DynamicGameObject implements CombatUnit {
	public static final String tag = "ShipGroup";

	public static final float ANGLE = 60;

	public static enum GROUP_STATE {
		/** ���״̬��ע�����GROUP��ʱ���ã����ǲ����� */
		NOTHING, FORMATION, GATHERING, VOYAGEING, ATTACKING, // DEFENSE,
		RECYCLEING
	}

	public static final int DEFAULT_SHIPLIST_SIZE = 50;
	public ArrayList<Ship> shipList;

	// public int shipLevel;
	/** ��ͼ�ð뾶 */
	public float drawRadius;
	// /** ս����̽��뾶����ս�뾶�� */
	// public float detectRadius;
	// /** ���� */
	// public int hp;
	// /** ������ */
	// public int atk;
	// /** ������ */
	// public int def;
	// /** ���� */
	// public int encon;
	/** ������ */
	Race owner;
	/** ���л������� */
	public float basicSpeed;
	public GROUP_STATE state;
	/** ��˼����ҪchargeTimeʱ����ɹ���һ�εĳ��� */
	public float chargeTime = 1.0f;
	/** ��ǰ����������ʱ�䣬Ĭ�ϸճ�ʼ����ɵ�ʱ���ǳ����� */
	public float currentCharge = chargeTime;
	/** Ŀǰ����ս������ */
	public int shipNumStand = 0;
	// /** ����Ŀ�ĵ�(�Ǿݵ�)*/
	// public Vector2 spaceDestination;
	/** ����Ŀ�ĵأ��ݵ�Ŀ�꣩ */
	public StrongHold destination;
	public StrongHold belongHold;
	public EntityManager entityManager;
	public static Random random = new Random();

	public ShipGroup() {
		this(0, 0, 0, DEFAULT_SHIPLIST_SIZE);

	}

	/**
	 * @param radius
	 *            ��ײ�����ð뾶
	 */
	public ShipGroup(float x, float y, float radius, int shipNum) {
		// TODO Auto-generated constructor stub
		super(x, y, radius);
		shipList = new ArrayList<Ship>(shipNum);
		state = GROUP_STATE.NOTHING;
		//this.entityManager = entityManager;
	}

	/** ����ɵĲ�����Ϣ */
	public void ClearOldInformation() {
		shipList.clear();
		this.state = GROUP_STATE.NOTHING;
		shipNumStand = 0;
	}

	/**
	 * ����GroupԤ�����ɶ��ٷɴ�
	 * 
	 * @deprecated shipListӦ�����ã�����Ҫ�µĶ���
	 */
	public void setGroupScale(int scale) {
		shipList = new ArrayList<Ship>(scale);
	}

	/** ��ʼ�������ɵ���ս��� ��ʼ��״̬Gathering */
	public void GenerateCombatGroup(StrongHold from, StrongHold to,
			List<Ship> ships) {
		state = GROUP_STATE.GATHERING;

		float x1 = from.position.x;
		float y1 = from.position.y;
		float x2 = to.position.x;
		float y2 = to.position.y;

		float r = getDrawRadius(ships.get(0).shipLevel);
		float ratio = (float) ((from.orbitBound + r) / (Math.sqrt((y2 - y1)
				* (y2 - y1) + (x2 - x1) * (x2 - x1))));
		float groupY = ratio * (y2 - y1) + y1;
		float groupX = ratio * (x2 - x1) + x1;
		Ship tempShip = ships.get(0);
		InitGroupPositionAndShape(groupX, groupY, r, r + tempShip.detectRadius);
		InitGroupInformation(tempShip);
		this.destination = to;

		int size = ships.size();
		for (int i = 0; i < size; i++) {
			AddShipToGroup(ships.get(i));
		}
	}

	/** ��ʼ�������ɵ���ս��� ��ʼ��״̬Nothing */
	public void GenerateCombatGroup(StrongHold from, List<Ship> ships) {
		state = GROUP_STATE.NOTHING;

		float x1 = from.position.x;
		float y1 = from.position.y;
		float r = getDrawRadius(ships.get(0).shipLevel);
		Ship tempShip = ships.get(0);
		InitGroupPositionAndShape(x1, y1, r, r + tempShip.detectRadius);
		// InitGroupInformation(tempShip);
		addAllShipToGroup(ships);

	}

	/** ����С�ӹ�ģ�İ뾶���뾶������ɷɴ��ĵȼ��������ɴ�Խ������뾶Խ�� */
	protected float getDrawRadius(int shiplevel) {
		float r = 0;
		if (shiplevel == Ship.MINITYPE)
			r = 0.5f;
		else if (shiplevel == Ship.MIDDLETYPE) {

		} else if (shiplevel == Ship.LIGHTTYPE) {

		} else if (shiplevel == Ship.BIGTYPE) {

		}
		return r;
	}

	/** ��ʼ����ս��λ�ľ�����Ϣ(��ʼ��λ�ú���״) */
	public void InitGroupPositionAndShape(float x, float y, float drawRadius,
			float radius) {
		this.position.x = x;
		this.position.y = y;
		this.drawRadius = drawRadius;
		((Circle) this.bounds).center.set(position);
		((Circle) this.bounds).radius = radius;
	}

	/** ��ʼ����ս��λ�ľ�����Ϣ��Ĭ���Ѿ��а뾶��λ����Ϣ�� */
	public void InitGroupInformation(Ship tempShip) {
		// tempShip = shipList.get(0);
		// this.detectRadius = tempShip.detectRadius;
		// this.atk = tempShip.atk;
		this.basicSpeed = tempShip.basicSpeed;
		// this.def = tempShip.def;
		// this.hp = tempShip.hp;
		this.owner = tempShip.owner;
		// this.shipLevel = tempShip.shipLevel;

	}

	// /** ����������Ժ���ߵ�ǰ�����Ѿ���ɵ�ʱ��ŵ��ã����������µ�ս���´��µ����� */
	// private void InitOrderToShips() {
	// if (state == GATHERING) {
	// state = VOYAGEING;
	// this.velocity.set(destination.position).sub(position).nor()
	// .mul(basicSpeed);
	// updateShipListDestination();
	// } else if (state == VOYAGEING) {
	// this.velocity.set(0, 0);
	// shipNumStand = 0;
	// state = FORMATION;
	// ShowFormation();
	// } else if (state == FORMATION) {
	// for (int i = 0; i < shipList.size(); i++) {
	// Ship shipTemp = shipList.get(i);
	// shipTemp.circle(destination,true);
	//
	// }
	// state = ATTACKING;
	// }
	//
	// }
	// public void joinOtherGroup(ShipGroup group){
	// this.addAllShipToGroup(group.shipList);
	// }
	public void addAllShipToGroup(List<Ship> ships) {
		int size = ships.size();
		for (int i = 0; i < size; i++) {
			AddShipToGroup(ships.get(i));
		}
	}

	public void AddShipToGroup(Ship ship) {
		this.shipList.add(ship);
		ship.belongGroup = this;
		ship.moveToGather(this);
	}

	/** ������ս���ִＯ�ϵص�ʱ���ã�ʹGroup֪�����Ѿ�������ͬʱ��ʽ������� */
	public void SingleShipAwairOrder(Ship ship) {
		if (shipList.size() > shipNumStand) {
			shipNumStand++;
		}
	}

	// /** �趨��ս��λǰ��Ŀ�� */
	// public void SetGroupDestination(StrongHold hold) {
	// this.destination = hold;
	// }

	/** ������ս��λ�����Ƶ�����ս�� */
	public void update(float deltatime) {
		switch (state) {
		case GATHERING:
			updateGathering(deltatime);
			break;
		case FORMATION:
			updateFormation(deltatime);
			break;
		case VOYAGEING:
			updateVoyaging(deltatime);
			break;
		case ATTACKING:
			updateAttacking(deltatime);
			break;
		// case DEFENSE:
		// // nothing to do here right now
		// break;
		case NOTHING:
			// do nothing
			break;
		case RECYCLEING:
			break;
		default:
			Gdx.app.log(tag, "unknown state");
		}
	}

	protected void updateGathering(float deltatime) {
		updateShips(deltatime);
		// ��������������Ӹ�GATHERINGת����VOYAGEING
		if (shipList.size() == shipNumStand) {
			shipNumStand = 0;
			state = GROUP_STATE.VOYAGEING;
			this.velocity.set(destination.position).sub(position).nor()
					.mul(basicSpeed);
			// ����Ŀ�ĵ�λ�ø�������ս�����ٶ�
			for (int i = 0; i < shipList.size(); i++) {
				shipList.get(i).moveTo(destination);
			}
			updateVoyaging(0);
		}
	}

	SmartLog log = new SmartLog();

	protected void updateVoyaging(float deltatime) {
		updateShips(deltatime);
		position.add(velocity.x * deltatime, velocity.y * deltatime);
		((Circle) bounds).center.set(position);
		//log.logFloatPS("x", position.x);
		// log.logFloatPS("y", position.y);
	}

	protected void updateFormation(float deltatime) {
		updateShips(deltatime);
		// ��������������Ӹ�FORMATIONת����ATTACKING
		if (shipList.size() == shipNumStand) {
			shipNumStand = 0;
			for (int i = 0; i < shipList.size(); i++) {
				shipList.get(i).circle(destination, true);
			}
			state = GROUP_STATE.ATTACKING;
			updateAttacking(0);
		}
	}

	protected void updateAttacking(float deltatime) {
		updateShips(deltatime);
		Fire(destination.position.x, destination.position.y,
				destination.orbitBound, deltatime);
	}

	protected void updateShips(float deltatime) {
		for (int i = 0; i < shipList.size(); i++) {
			shipList.get(i).update(deltatime);
		}
	}

	// �мǻع�����״̬�Ժ����Ŀ�ĵأ��������״̬
	public void ShowDefenceFormation(float groupX, float groupY, float holdX,
			float holdY, float distance) {
		float tempY = groupY - holdY;
		float tempX = groupX - holdX;
		float degree = (float) ((float) (Math.atan((groupY - holdY)
				/ (groupX - holdX))) * 180 / Math.PI);
		float minDegree = 0f;
		float maxDegree = 0f;
		if (tempY >= 0 && tempX < 0) {
			minDegree = (degree + 90) - ANGLE / 2;
			maxDegree = (degree + 90) + ANGLE / 2;
		}
		if (tempY >= 0 && tempX >= 0) {
			minDegree = degree - ANGLE / 2;
			maxDegree = degree + ANGLE / 2;
		} else if (tempY < 0 && tempX <= 0) {
			minDegree = degree + 180 - ANGLE / 2;
			maxDegree = degree + 180 + ANGLE / 2;
		} else if (tempY < 0 && tempX > 0) {
			minDegree = degree + 360 - ANGLE / 2;
			maxDegree = degree + 360 + ANGLE / 2;
		}
		int sizeTemp = shipList.size();
		float ratio = (maxDegree - minDegree) / (sizeTemp - 1);
		for (int i = 0; i < sizeTemp; i++) {
			float tempRatio = minDegree + ratio * (i);
			float sinValue = (float) Math
					.sin((float) (tempRatio / 180 * Math.PI));
			float cosValue = (float) Math
					.cos((float) (tempRatio / 180 * Math.PI));
			float x = holdX + distance * cosValue;
			float y = holdY + distance * sinValue;
			shipList.get(i).MoveToShowFormation(x, y);
		}
	}

	/** ս��չ���������� */
	public void ShowFormation() {
		float groupY = position.y;
		float groupX = position.x;
		float holdX = destination.position.x;
		float holdY = destination.position.y;
		float tempY = groupY - holdY;
		float tempX = groupX - holdX;
		float radius = (float) Math.sqrt((groupY - holdY) * (groupY - holdY)
				+ (groupX - holdX) * (groupX - holdX));
		float degree = (float) ((float) (Math.atan((groupY - holdY)
				/ (groupX - holdX))) * 180 / Math.PI);
		float minDegree = 0f;
		float maxDegree = 0f;
		if (tempY >= 0 && tempX < 0) {
			minDegree = (degree + 90) - ANGLE / 2;
			maxDegree = (degree + 90) + ANGLE / 2;
		}
		if (tempY >= 0 && tempX >= 0) {
			minDegree = degree - ANGLE / 2;
			maxDegree = degree + ANGLE / 2;
		} else if (tempY < 0 && tempX <= 0) {
			minDegree = degree + 180 - ANGLE / 2;
			maxDegree = degree + 180 + ANGLE / 2;
		} else if (tempY < 0 && tempX > 0) {
			minDegree = degree + 360 - ANGLE / 2;
			maxDegree = degree + 360 + ANGLE / 2;
		}
		int sizeTemp = shipList.size();
		float distance = (float) Math.sqrt((groupX - holdX) * (groupX - holdX)
				+ (groupY - holdY) * (groupY - holdY));
		float ratio = (maxDegree - minDegree) / (sizeTemp - 1);
		Random rand = new Random();
		for (int i = 0; i < sizeTemp; i++) {
			float tempRatio = minDegree + ratio * (i);
			float sinValue = (float) Math
					.sin((float) (tempRatio / 180 * Math.PI));
			float cosValue = (float) Math
					.cos((float) (tempRatio / 180 * Math.PI));
			float sign = rand.nextFloat();
			float x = 0;
			float y = 0;
			if (sign <= (1.0f / 3.0f)) {
				x = holdX + distance * cosValue;
				y = holdY + distance * sinValue;
			} else if (sign <= (2.0f / 3.0f)) {
				x = holdX + (distance + 0.2f) * cosValue;
				y = holdY + (distance + 0.2f) * sinValue;
			} else {
				x = holdX + (distance + 0.4f) * cosValue;
				y = holdY + (distance + 0.4f) * sinValue;
			}
			shipList.get(i).MoveToShowFormation(x, y);
		}
	}

	public void Fire(float x, float y, float radius, float deltaTime) {
		if (currentCharge >= chargeTime) {
			for (int i = 0; i < shipList.size(); i++) {
				Ship currentShip = shipList.get(i);
				Ammunition ammuntion = belongHold.entityManager.GetAmmunition();
				ammuntion.position.x = currentShip.position.x;
				ammuntion.position.y = currentShip.position.y;
				float destinationX = x - radius + random.nextFloat() * radius
						* 2;
				float upperY = FloatMath.sqrt(radius * radius
						- (destinationX - x) * (destinationX - x))
						+ y;
				float bottomY = y
						- FloatMath.sqrt(radius * radius - (destinationX - x)
								* (destinationX - x));
				float destinationY = bottomY + (upperY - bottomY)
						* random.nextFloat();
				ammuntion.MoveTo(new Vector2(destinationX, destinationY));
			}
			currentCharge -= chargeTime;
		}
	}

	@Override
	public void prepareToBattle(CombatUnit enemy) {
		// TODO Auto-generated method stub
		this.velocity.set(0, 0);
		shipNumStand = 0;
		state = GROUP_STATE.FORMATION;
		ShowFormation();
	}
	
	@Override
	public void beginbattle() {
		// TODO Auto-generated method stub

	}

	@Override
	public float outputDamage() {
		// TODO Auto-generated method stub
		int size = shipList.size();
		if (size > 0)
			return size * shipList.get(0).atk;
		else
			return 0;
	}

	@Override
	public void inputDamage(float damage) {
		// TODO Auto-generated method stub
		int size = shipList.size();
		if (size > 0) {
			int destory = (int) FloatMath.floor(damage / shipList.get(0).hp);
			if (size - destory < 0)
				destory = size;
			for (int i = size - 1; i >= size - destory; i--) {
				Ship ship = shipList.get(i);
				shipList.remove(ship);
				entityManager.FreeShipInstance(ship);
			}
		}
	}

	@Override
	public boolean isBreakDown() {
		// TODO Auto-generated method stub
		if (shipList.isEmpty())
			return true;
		return false;
	}

	@Override
	public boolean isReadyToBattle() {
		// TODO Auto-generated method stub
		if (state == GROUP_STATE.ATTACKING)
			return true;
		return false;
	}

	@Override
	public void endBattle(CombatUnit winner) {
		// TODO Auto-generated method stub
		if (winner != this) {
			state = GROUP_STATE.RECYCLEING;
		}
	}

	@Override
	public void recieveReinforcement(CombatUnit reinforcement) {
		// TODO Auto-generated method stub
		List<Ship> ships = reinforcement.getShipList();
		shipList.addAll(ships);
		for (int i = 0; i < ships.size(); i++) {
			ships.get(i).circle(destination, true);
		}
	}

	@Override
	public Race getOwner() {
		// TODO Auto-generated method stub
		return owner;
	}

	@Override
	public List<Ship> getShipList() {
		// TODO Auto-generated method stub
		return shipList;
	}

}
