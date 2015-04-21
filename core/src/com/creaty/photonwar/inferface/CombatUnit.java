package com.creaty.photonwar.inferface;

import java.util.List;

import com.creaty.photonwar.entity.Race;
import com.creaty.photonwar.entity.Ship;

public interface CombatUnit {
	/**准备战斗*/
	public void prepareToBattle(CombatUnit enemy);
	public boolean isReadyToBattle();
	/**提示作战单位开始战斗*/
	public void beginbattle();
	/**输出伤害*/
	public float outputDamage();
	/**承受伤害
	 * @param 作战单位将承受的伤害*/
	public void inputDamage(float damage);
//	/**更新作战单位*/
//	public void updateCombatUnit(float deltatime);
	/**作战单位是否已经崩溃*/
	public boolean isBreakDown();
	/**结束战斗*/
	public void endBattle(CombatUnit winner);
	public void recieveReinforcement(CombatUnit reinforcement);
	public List<Ship> getShipList();
	public Race getOwner();
}
