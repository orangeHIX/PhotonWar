package com.creaty.photonwar.entity;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import com.creaty.game.framework.SmartLog;
import com.creaty.photonwar.entity.BattleField.BATTLE_FEILD_STATE;
import com.creaty.photonwar.entity.Race.ShipBlueprint;
import com.creaty.photonwar.entity.ShipGroup.GROUP_STATE;
import com.creaty.photonwar.inferface.EntityManager;

public class World implements EntityManager {
	public static final String tag = "World";

	public interface WorldListener {

	}

	public static final float WORLD_WIDTH = 20;
	public static final float WORLD_HEIGHT = 12;

	WorldListener listener;
	/** 世界掌管的游戏实体，据点、舰船小组和战场 */
	public ArrayList<StrongHold> strongHolds;
	public ArrayList<ShipGroup> shipGroups;
	public ArrayList<Ammunition> ammunitions;
	public ArrayList<BattleField> battleFieldList;
	public Race race1;
	public Race race2;

	Pool<Ship> shipPool;

	Pool<ShipGroup> shipGroupPool;
	List<ShipGroup> shipGroupToFree;
	Pool<Ammunition> ammunitionPool;

	Pool<BattleField> battleFieldPool;
	List<BattleField> battleFieldToFree;

	public final int shipPoolSize;
	public final int shipGroupSize;
	public final int ammunitionSize;
	public final int battleFieldSize;
	public SpatialHashGridForWar grid;

	SmartLog log;

	public World(WorldListener worldListener) {

		listener = worldListener;
		grid = new SpatialHashGridForWar(WORLD_WIDTH, WORLD_HEIGHT, 4.0f);
		shipPoolSize = 1000;
		shipGroupSize = 100;
		battleFieldSize = 100;
		ammunitionSize = 10000;
		shipGroups = new ArrayList<ShipGroup>();
		strongHolds = new ArrayList<StrongHold>();
		battleFieldList = new ArrayList<BattleField>();
		shipPool = new Pool<Ship>(100, shipPoolSize){

			@Override
			protected Ship newObject() {
				// TODO Auto-generated method stub
				return null;
			}

			};
		shipGroupPool = new Pool<ShipGroup>(10, shipGroupSize){

			@Override
			protected ShipGroup newObject() {
				// TODO Auto-generated method stub
				return new ShipGroup();
			}
			
		};
		ammunitionPool = new Pool<Ammunition>(100, ammunitionSize){

			@Override
			protected Ammunition newObject() {
				// TODO Auto-generated method stub
				return new Ammunition(0, 0, 0.2f);
			}
			
		};
		battleFieldPool = new Pool<BattleField>( 10, battleFieldSize){

			@Override
			protected BattleField newObject(){
				// TODO Auto-generated method stub
				return new BattleField();
			}
			
		};

		shipGroupToFree = new ArrayList<ShipGroup>();
		battleFieldToFree = new ArrayList<BattleField>();
		ammunitions = new ArrayList<Ammunition>();

		ShipBlueprint shipBlueprint = new ShipBlueprint(Ship.MINITYPE, 1.0f, 3,
				1, 1, 2.0f, 5.0f);
		race1 = new Race(0, Race.HU_PLAYER, shipBlueprint);
		race2 = new Race(1, Race.AI_PLAYER, shipBlueprint);
		strongHolds.add(new StrongHold(1f, 1f, 1.0f, 10, 1, this, race1));
		strongHolds.add(new StrongHold(10f, 5f, 1.0f, 10, 1, this, race2));
		strongHolds.add(new StrongHold(19f, 10f, 1.0f, 10, 1, this, race1));
		int holdsize = strongHolds.size();

		for (int j = 0; j < holdsize; j++) {
			grid.insertStaticObject(strongHolds.get(j));
			for (int i = 0; i < 5; i++) {
				strongHolds.get(j).produceNewShip();
				strongHolds.get(j).accumulatedProductivity = 0;
			}

		}
		log = new SmartLog();
	}

	List<StrongHold> potentialTarget;

	// int count = 0;
	/** 更新据点和作战单位极其托管的战舰 */
	public void update(float deltaTime) {
		int holdsize = strongHolds.size();
		// 更新据点的状态以及他托管的战舰
		for (int i = 0; i < holdsize; i++) {
			strongHolds.get(i).update(deltaTime);
		}

		ShipGroup shipGroupItem;
		int groupSize = shipGroups.size();
		// 更新弹药的位置
		for (int i = 0; i < ammunitions.size(); i++) {
			Ammunition temp = ammunitions.get(i);
			if (temp.state == Ammunition.STATE_EXPLODE) {
				FreeAmmuition(temp);
			} else if (temp.state == Ammunition.STATE_MOVE)
				temp.update(deltaTime);
		}

		StrongHold hold;
		// 更新作战单位以及其托管的战舰
		for (int i = 0; i < groupSize; i++) {
			shipGroupItem = shipGroups.get(i);
			shipGroupItem.update(deltaTime);
			/** 碰撞检测和相应的更新 */
			if (shipGroupItem.state == GROUP_STATE.VOYAGEING) {
				// Gdx.app.log(tag, "check Collision");
				hold = checkGroupCollision(shipGroupItem);
				if (hold != null) {
					if (shipGroupItem.owner == hold.owner) {
						hold.takeOverShip(shipGroupItem);
						// Gdx.app.log(tag, "接管开始");
					} else if (!hold.hasGuard()) {
						hold.invadeGroup = shipGroupItem;
					} else {
						
						BattleField bf = getBattlefieldAlreadyExists(hold);
						if (bf == null) {
							bf = GetBattleFieldInstance();
							bf.prepareNewBattle(hold, shipGroupItem);
							Gdx.app.log(tag, "prepare new battle");
						}else{
							bf.recieveNewCombatUnit(shipGroupItem);
							shipGroupItem.state = GROUP_STATE.RECYCLEING;
							Gdx.app.log(tag, "battle exsit");
						}
					}
				}
			}
		}

		// 更新战场
		int fieldSize = battleFieldList.size();
		for (int i = 0; i < fieldSize; i++) {
			battleFieldList.get(i).updateBattleField(deltaTime);
		}

		recycleShipGroup();
		recycleBattleField();
	}

	public StrongHold checkGroupCollision(ShipGroup shipGroupItem) {
		StrongHold hold;
		potentialTarget = grid.getPotentialTarget(shipGroupItem);
		int sizepo = potentialTarget.size();
		for (int j = 0; j < sizepo; j++) {
			hold = (StrongHold) potentialTarget.get(j);
			if (OverlapTester.overlapCircles((Circle) hold.bounds,
					(Circle) shipGroupItem.bounds)) {
				return hold;
			}
		}
		return null;
	}

	protected BattleField getBattlefieldAlreadyExists(StrongHold hold) {
		int size = battleFieldList.size();
		BattleField bf;
		for (int i = 0; i < size; i++) {
			bf = battleFieldList.get(i);
			if (bf.defender == hold) {
				return bf;
			}
		}
		return null;
	}

	protected void recycleShipGroup() {
		int groupSize = shipGroups.size();
		ShipGroup tmp;
		for (int i = 0; i < groupSize; i++) {
			tmp = shipGroups.get(i);
			if (tmp.state == GROUP_STATE.RECYCLEING) {
				shipGroupToFree.add(tmp);
				tmp.update(0);
			}
		}
		int freeSizee = shipGroupToFree.size();
		for (int i = 0; i < freeSizee; i++) {
			tmp = shipGroupToFree.get(i);
			FreeGroupInstance(tmp);
			// shipGroups.remove(tmp); 不需要做这些 FreeGroupInstance会做
			// Gdx.app.log(tag, "recycle shipGroup");
		}
		shipGroupToFree.clear();
	}

	protected void recycleBattleField() {
		int fieldSize = battleFieldList.size();
		BattleField bf;
		for (int i = 0; i < fieldSize; i++) {
			bf = battleFieldList.get(i);
			if (bf.state == BATTLE_FEILD_STATE.END) {
				battleFieldToFree.add(bf);
				// bf.updateBattleField(0);
			}
		}
		int freeSize = battleFieldToFree.size();
		for (int i = 0; i < freeSize; i++) {
			bf = battleFieldToFree.get(i);
			FreeBattleField(bf);
			// battleFieldList.remove(bf); 不需要做这些 FreeBattleField会做
			// Gdx.app.log(tag, "recycle battlefield");
		}
		battleFieldToFree.clear();
	}

	@Override
	public ShipGroup GetGroupInstance(StrongHold requset) {
		// TODO Auto-generated method stub
		ShipGroup shipGroup = shipGroupPool.obtain();
		shipGroup.entityManager = this;
		// ships.add(ship);
		grid.insertDynamicObject(shipGroup);
		shipGroup.belongHold = requset;
		shipGroups.add(shipGroup);
		return shipGroup;
	}

	@Override
	public void FreeGroupInstance(ShipGroup item) {
		// TODO Auto-generated method stub
		Gdx.app.log(tag, "recycle shipGroup");
		item.ClearOldInformation(); // 清理是很重要的！
		item.entityManager = null;
		shipGroupPool.free(item);
		shipGroups.remove(item);
		grid.removeObject(item);
	}

	@Override
	public Ship GetShipInstance() {
		// TODO Auto-generated method stub
		Ship shipItem = shipPool.obtain();
		grid.insertDynamicObject(shipItem);
		return shipItem;
	}

	@Override
	public void FreeShipInstance(Ship item) {
		// TODO Auto-generated method stub
		shipPool.free(item);
		grid.removeObject(item);
	}

	@Override
	public Ammunition GetAmmunition() {
		// TODO Auto-generated method stub
		Ammunition ammunitionItem = ammunitionPool.newObject();
		ammunitions.add(ammunitionItem);
		return ammunitionItem;
	}

	@Override
	public void FreeAmmuition(Ammunition item) {
		// TODO Auto-generated method stub
		ammunitions.remove(item);
		ammunitionPool.free(item);
	}

	@Override
	public BattleField GetBattleFieldInstance() {
		// TODO Auto-generated method stub
		BattleField bf = battleFieldPool.obtain();
		battleFieldList.add(bf);
		return bf;
	}

	@Override
	public void FreeBattleField(BattleField item) {
		// TODO Auto-generated method stub
		Gdx.app.log(tag, "recycle BattleField");
		// Gdx.app.log(tag, battleFieldList.toString() + " " +
		// battleFieldList.size());
		battleFieldList.remove(item);
		item.reset();
		battleFieldPool.free(item);
	}
}
