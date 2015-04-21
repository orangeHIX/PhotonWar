package com.creaty.photonwar.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.androidgames.framework.model.GameObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.creaty.game.framework.SmartLog;
import com.creaty.game.framework.State;
import com.creaty.game.framework.State.StateUpdater;
import com.creaty.game.framework.StateMachine2;
import com.creaty.game.framework.StateMachine2.StateTransitionTester;
import com.creaty.photonwar.entity.Race.ShipBlueprint;
import com.creaty.photonwar.entity.Ship.SHIP_STATE;
import com.creaty.photonwar.entity.ShipGroup.GROUP_STATE;
import com.creaty.photonwar.inferface.CombatUnit;
import com.creaty.photonwar.inferface.EntityManager;

public class StrongHold extends GameObject implements CombatUnit {
	public static final String tag = "StrongHold";

	public static final int DEFAULT_MAX_POPULATION_FACTOR = 10;
	/** ����뾶ϵ�������ڼ������뾶 */
	public static final float ORBIT_FACTOR1 = 1.4f;
	/** ����뾶ϵ�������ڼ������뾶 */
	public static final float ORBIT_FACTOR2 = 0.5f;
	/** ����뾶ϵ�������ڼ������뾶 */
	public static final float ORBIT_FACTOR3 = 0.25f;
	/** �����Ƚ�������� */
	public static final float DEVELOP_RING_WIDTH = 0.15f;
	/** ��ֵϵ������ֹͣ������ֵ=�ȼ�*��ֵϵ�����ο���ʽ�� */
	public static final int THRESHOLD_FACTOR = 10;
	/** �����ٶ�ϵ������������=��ǰ������+�����ٶ�ϵ��/�ȼ����ο���ʽ�� */
	public static final float DEVELOP_RATE = 3.0f;
	/** ��������ϵ�� */
	public static final float PRODUCTIVITY_GROUTH_RATE = .5f;
	/** ս���������ÿ��ս�������¼���ʱ���� */
	public static final float FIGHT_INTERVAL = 0.5f;
	/** �ݵ�������ÿ����ǲ������Ŀ��ݵ��������ڸþݵ�פ���Ľ��������ı��� */
	public static final float DISPATCH_PROPOTION = 0.5f;
	/** �ݵ�������ÿ����ǲ������Ŀ���� */
	public static final int DISPATCH_MIN = 3;
	/** ����������� */
	public static final Random random = new Random();

	/** ��ʾ�ݵ�δ��ռ�죬��ʱ�ݵ㴦��ԭʼ��̬��������Ϊ�ޣ�������Ϊ0������Ϊ0 */
	public static final State STATE_BLANK = new State(0, "Blank",
			new StateUpdater() {
				@Override
				public void enter(Object obj) {
					StrongHold hold = (StrongHold) obj;
					hold.owner = null;
					hold.development = 0;
					hold.productivity = 0;
				};
			});
	/**
	 * ��ʾ�ݵ����ڱ�ռ��, �������״̬ʱ�������������ͣ���ڴ˴����������߲����������߱��Ϊ���������ߣ�����������Ϊ0��
	 * ���ڴ�״̬�����Ȼ���ʱ��������
	 */
	public static final State STATE_OCCUPY = new State(1, "Occupy",
			new StateUpdater() {
				@Override
				public void enter(Object obj) {
					StrongHold hold = (StrongHold) obj;
					if (hold.invadeGroup != null) {
						if (hold.governShips.isEmpty()
								&& (!hold.invadeGroup.shipList.isEmpty())) {
							// ת�ƽ���2������Ⱥ1��
							hold.takeOverShip(hold.invadeGroup);
							hold.invadeGroup.shipList.clear();
							// hold.invadeGroup.state = GROUP_STATE.NOTHING;
						}
						hold.invadeGroup.state = GROUP_STATE.RECYCLEING;
						hold.invadeGroup = null;
					}
					if (!hold.governShips.isEmpty()
							&& hold.governShips.get(0).owner != hold.owner) {
						hold.changeOwner(hold.governShips.get(0).owner);
						hold.development = 0; // ����������Ϊ0
					}
				};

				@Override
				public void hold(Object obj) {
					StrongHold hold = (StrongHold) obj;
					hold.development += hold.deltaTime
							* StrongHold.DEVELOP_RATE * hold.governShips.size();
					if (hold.development > 100.0f)
						hold.development = 100.0f;
				};
			});
	/** ��ʾ�ݵ����������������������У��������״̬ʱ�����ȱ��ֲ��� */
	public static final State STATE_FIGHT = new State(2, "Fight",
			new StateUpdater() {

			});
	/**
	 * ��ʾ�ݵ�Ϊһ��������ȫ��������ʱ������Ӧ��Ϊ100%�������߱�Ϊ��ʱ�ھݵ�ͣ�������������ߣ����ڴ�״̬���ܻ���������ͬʱ���ܻ�����µĽ�����
	 * �����������ߡ�
	 */
	public static final State STATE_HOLD = new State(3, "Hold",
			new StateUpdater() {
				// SmartLog log = new SmartLog();

				@Override
				public void hold(Object obj) {
					StrongHold hold = (StrongHold) obj;
					hold.productivity += hold.deltaTime
							* StrongHold.PRODUCTIVITY_GROUTH_RATE;
					if (hold.productivity > hold.maxProductivity) {
						hold.productivity = hold.maxProductivity;
					}
					// Ԥ���ۻ�����
					float expectP = hold.accumulatedProductivity
							+ hold.deltaTime
							* MathUtils.floor(hold.productivity);

					// ���������Ľ�����Ŀ
					int proShip = (int) Math.floor(expectP
							/ hold.owner.shipBlueprint.encon);
					for (int i = 0; i < proShip; i++) {
						hold.produceNewShip();
					}
					// hold.accumulatedProductivity = expectP;
					hold.accumulatedProductivity = expectP - proShip
							* hold.owner.shipBlueprint.encon;
					// log.logFloatPS("accumulatedProductivity", expectP);
				}
			});

	public static class HoldStateTransitionTester implements
			StateTransitionTester {

		@Override
		public boolean tryChange(Object objectWithState, State from, State to) {
			StrongHold hold = (StrongHold) objectWithState;
			if (from.equals(STATE_BLANK)) {
				if (to.equals(STATE_OCCUPY)) {
					if (hold.hasGuard()) // ��Ϊ������פ�����ӣ��ݵ����owner
						return true;
				} else if ((to.equals(STATE_BLANK))) {
					if (!hold.hasGuard()) {
						return true;
					}
				}
			} else if (from.equals(STATE_OCCUPY)) { // Ĭ�ϳ��˴���STATE_BLANK���⣬��������״̬�ľݵ㶼���ض���������
				if (to.equals(STATE_FIGHT)) {
					if (hold.hasOwner() && hold.hasInvader())
						return true;
					// if ((hold.defenseGroup.state == GROUP_STATE.ATTACKING)
					// && (hold.invadeGroup.state == GROUP_STATE.ATTACKING))
					// return true;
				} else if (to.equals(STATE_HOLD)) {
					if (hold.hasOwner() && !hold.hasInvader()
							&& hold.isDeveloped())
						return true;
				} else if (to.equals(STATE_OCCUPY)) {
					if (!hold.hasInvader() && !hold.hasInvader()
							&& !hold.isDeveloped())
						return true;
					// if (((hold.defenseGroup.state != GROUP_STATE.ATTACKING)
					// || (hold.invadeGroup.state != GROUP_STATE.ATTACKING))
					// && hold.development < 100.0f)
					// return true;
				}
			} else if (from.equals(STATE_FIGHT)) { // Ĭ�ϳ��˴���STATE_BLANK���⣬��������״̬�ľݵ㶼���ض���������
				if (to.equals(STATE_OCCUPY)) {
					if (!(hold.hasGuard() && hold.hasInvader()))
						return true;
					// if ((!hold.governShips.isEmpty())
					// && hold.invadeGroup.state == GROUP_STATE.NOTHING)
					// return true;
				} else if (to.equals(STATE_FIGHT)) {
					if ((hold.hasGuard() && hold.hasInvader()))
						return true;
				}
			} else if (from.equals(STATE_HOLD)) { // Ĭ�ϳ��˴���STATE_BLANK���⣬��������״̬�ľݵ㶼���ض���������
				if (to.equals(STATE_FIGHT)) {
					if (hold.hasInvader()&& hold.hasGuard())
						return true;
				} else if(to.equals(STATE_OCCUPY)){
					if (hold.hasInvader()&& !hold.hasGuard())
						return true;
				}else if (to.equals(STATE_HOLD)) {
					if (!hold.hasInvader())
						return true;
				}
			}
			return false;
		};

	}

	/** �ݵ�״̬�� */
	static StateMachine2 STATE_MACHINE;
	static {
		STATE_MACHINE = new StateMachine2(new HoldStateTransitionTester());
		//STATE_MACHINE.logSwitch = true;
		STATE_MACHINE.registerState(STATE_BLANK);
		STATE_MACHINE.registerState(STATE_OCCUPY);
		STATE_MACHINE.registerState(STATE_FIGHT);
		STATE_MACHINE.registerState(STATE_HOLD);
	}

	/** �þݵ㵱ǰ״̬ */
	public State state;
	/** ������ */
	public Race owner;
	/** ��������߽�뾶��С */
	public final float orbitBound;
	/** ������ڲ�߽�뾶��С */
	public final float orbitInsideBound;
	/** �����ȡ���������Ϊ100%���Ա�һ������ռ�� */
	public float development;
	/** ���ܡ�����λʱ���ڻ��۵Ĳ�����Ŀ */
	public float productivity;
	/** ��߲��ܡ����������� */
	public final float maxProductivity;
	/** ���۲��ܡ����Ѿ����۵Ĳ��� ���۲���=���۲���+����*���ʱ�䣨�ο���ʽ�� */
	public float accumulatedProductivity;
	/** �ȼ�������ݵ���������ʱ��ֹͣ�����ķ�ֵ����أ���ݵ㿪���ٶȸ���� */
	public final int level;

	/** ����Ⱥ1����ռ��þݵ������Ľ����������ȵ�½�þݵ������Ľ��� */
	public ArrayList<Ship> governShips;
	// /** ����ս������ */
	// public ShipGroup defenseGroup;
	/** ����ս������ */
	public ShipGroup invadeGroup;

	/** ��ȡ�����ʵ�����Դ�������������Դ */
	public EntityManager entityManager;
	// /**�ݵ㱾�����ս��*/
	// public BattleField battleField;
	/** ����bug�� */
	public SmartLog log;

	public StrongHold(float x, float y, float radius, int maxProductivity,
			int level, EntityManager entityManager, Race owner) {
		super(x, y, radius);
		// TODO Auto-generated constructor stub
		state = STATE_BLANK;
		STATE_MACHINE.start(this, STATE_BLANK);
		this.orbitBound = radius * ORBIT_FACTOR1 + ORBIT_FACTOR2;
		this.orbitInsideBound = radius + ORBIT_FACTOR3;
		this.maxProductivity = maxProductivity;
		this.accumulatedProductivity = 0;
		this.level = level;
		this.entityManager = entityManager;
		this.owner = owner;
		governShips = new ArrayList<Ship>(10);
		// defenseGroup = entityManager.GetGroupInstance(this);
		// invadeGroup = null;//entityManager.GetGroupInstance(this);
		// battleField = new BattleField();
		log = new SmartLog();
	}

	/**
	 * @return ���ؾݵ�ı߽磨�����ڹ���߽磩
	 */
	public float getBoundsRadius() {
		return ((Circle) bounds).radius;
	}

	/** ���¼��ʱ�� */
	float deltaTime;

	/** ���¾ݵ� */
	public void update(float deltaTime) {
		this.deltaTime = deltaTime;
		state = STATE_MACHINE.updateState(this, state);
		this.deltaTime = 0;
		for (int i = 0; i < governShips.size(); i++) {
			governShips.get(i).update(deltaTime);
		}
	}

	/** �����µĽ���,�ݵ��ۻ����ܽ��ᱻ���� */
	public void produceNewShip() {
		accumulatedProductivity -= owner.shipBlueprint.encon;
		Ship ship = entityManager.GetShipInstance();
		ship = owner.degsinNewShip(ship);
		ship.position.set(position.x + orbitInsideBound + random.nextFloat()
				* (orbitBound - orbitInsideBound), position.y + 0);
		ship.circle(this, false);
		ship.update(0);
		governShips.add(ship);
	}

	/** ��ǲ��Ϊ��ִ�к��� */
	public void sendShipsTo(StrongHold hold) {
		if (governShips.size() == 0)
			return;
		ShipGroup groupItem = entityManager.GetGroupInstance(this);

		int size = governShips.size();
		int dispatch = getDispatchNum();
		groupItem.GenerateCombatGroup(this, hold,
				governShips.subList(size - dispatch, size));
		governShips.subList(size - dispatch, size).clear();
	}

	/** ��ȡӦ����ǲ�Ľ�����Ŀ */
	protected int getDispatchNum() {
		int dispatch = (int) (governShips.size() * DISPATCH_PROPOTION);
		if (dispatch < DISPATCH_MIN) {
			dispatch = governShips.size();
		}
		return dispatch;
	}

	/** �ӹ������Ľ��� */
	public void takeOverShip(ShipGroup shipGroup) {
		// Log.d(tag, "takeOverShip");
		ArrayList<Ship> tempList = shipGroup.shipList;
		int listSize = tempList.size();
		float movetime;

		for (int i = 0; i < listSize; i++) {
			Ship ship = tempList.get(i);
			ship.velocity.set(this.position).sub(ship.position).nor()
					.mul(ship.basicSpeed);
			movetime = (ship.position.dst(this.position) - orbitInsideBound - random
					.nextFloat() * (orbitBound - orbitInsideBound))
					/ ship.basicSpeed;
			ship.position.add(ship.velocity.x * movetime, ship.velocity.y
					* movetime);
			governShips.add(ship);
			ship.circle(this, false);
		}
		shipGroup.state = GROUP_STATE.RECYCLEING;
	}

	public void changeOwner(Race newOwner) {
		owner.RemoveStrongHold(this);
		this.owner = newOwner;
		this.owner.AddStrongHold(this);
	}

	public void encounterInvader(ShipGroup group){
		if( invadeGroup != null ){
			invadeGroup.recieveReinforcement(group);
		}else{
			invadeGroup = group;
		}
	}

	/** ��ȡ�þݵ���˿����� */
	public int getPopulationCapacity() {
		return level * DEFAULT_MAX_POPULATION_FACTOR;
	}

	public boolean hasOwner() {
		if (owner != null) {
			return true;
		}
		return false;
	}

	public boolean isDeveloped() {
		if (Math.abs(development - 100.0) < 1.0e-6) {
			return true;
		}
		return false;
	}

	public boolean hasGuard() {
		if (governShips != null && !governShips.isEmpty()) {
			return true;
		}
		return false;
	}

	public boolean hasInvader() {
		if (invadeGroup != null && !invadeGroup.shipList.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public void prepareToBattle(CombatUnit enemy) {
		// TODO Auto-generated method stub
		invadeGroup = (ShipGroup) enemy;
		update(0);
	}

	@Override
	public void beginbattle() {
		// TODO Auto-generated method stub

	}

	@Override
	public float outputDamage() {
		// TODO Auto-generated method stub
		int size = governShips.size();
		if (size > 0)
			return size * governShips.get(0).atk;
		else
			return 0;
	}

	@Override
	public void inputDamage(float damage) {
		// TODO Auto-generated method stub
		int size = governShips.size();
		if (size > 0) {
			int destory = (int) MathUtils.floor(damage / governShips.get(0).hp);
			if (size - destory < 0)
				destory = size;
			for (int i = size - 1; i >= size - destory; i--) {
				Ship ship = governShips.get(i);
				governShips.remove(ship);
				entityManager.FreeShipInstance(ship);
			}
		}
	}

	@Override
	public boolean isBreakDown() {
		// TODO Auto-generated method stub
		if (governShips.isEmpty())
			return true;
		return false;
	}

	@Override
	public void endBattle(CombatUnit winner) {
		// TODO Auto-generated method stub
		update(0);
	}

	@Override
	public boolean isReadyToBattle() {
		// TODO Auto-generated method stub
		if (state.equals(STATE_FIGHT))
			return true;
		return false;
	}

	@Override
	public void recieveReinforcement(CombatUnit reinforcement) {
		// TODO Auto-generated method stub
		if( reinforcement instanceof ShipGroup)
			takeOverShip((ShipGroup)reinforcement);
	}

	@Override
	public Race getOwner() {
		// TODO Auto-generated method stub
		return owner;
	}

	@Override
	public List<Ship> getShipList() {
		// TODO Auto-generated method stub
		return governShips;
	}

}
