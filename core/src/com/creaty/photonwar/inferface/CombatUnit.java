package com.creaty.photonwar.inferface;

import java.util.List;

import com.creaty.photonwar.entity.Race;
import com.creaty.photonwar.entity.Ship;

public interface CombatUnit {
	/**׼��ս��*/
	public void prepareToBattle(CombatUnit enemy);
	public boolean isReadyToBattle();
	/**��ʾ��ս��λ��ʼս��*/
	public void beginbattle();
	/**����˺�*/
	public float outputDamage();
	/**�����˺�
	 * @param ��ս��λ�����ܵ��˺�*/
	public void inputDamage(float damage);
//	/**������ս��λ*/
//	public void updateCombatUnit(float deltatime);
	/**��ս��λ�Ƿ��Ѿ�����*/
	public boolean isBreakDown();
	/**����ս��*/
	public void endBattle(CombatUnit winner);
	public void recieveReinforcement(CombatUnit reinforcement);
	public List<Ship> getShipList();
	public Race getOwner();
}
