package com.creaty.photonwar.entity;

import java.util.ArrayList;

import android.util.Log;

import com.creaty.game.framework.SmartLog;
import com.creaty.photonwar.inferface.CombatUnit;

public class BattleField {

	public static final String tag = "BattleField";

	public static enum BATTLE_FEILD_STATE {
		NOT_START, PREPARE, PROCESSING, END
	}

	public static final float FIGHT_INTERVAL = 0.5f;
	/** 战斗计时器——记录战斗开始后已经经历的时间 */
	protected float fightTimer;
	protected CombatUnit defender;
	protected CombatUnit attacker;
	
	//ArrayList<CombatUnit> defenderReinfocement;
	protected BATTLE_FEILD_STATE state;

	// SmartLog log;

	public BattleField() {
		state = BATTLE_FEILD_STATE.NOT_START;
		// log = new SmartLog();
	}

	public void reset() {
		state = BATTLE_FEILD_STATE.NOT_START;
		defender = null;
		attacker = null;
	}

	public void prepareNewBattle(CombatUnit defender, CombatUnit attacker) {
		this.defender = defender;
		this.attacker = attacker;
		defender.prepareToBattle(attacker);
		attacker.prepareToBattle(defender);
		state = BATTLE_FEILD_STATE.PREPARE;
	}

	public void recieveNewCombatUnit(CombatUnit reinforcement) {
		if (state == BATTLE_FEILD_STATE.PROCESSING) {
			if (reinforcement.getOwner() == defender.getOwner()) {
				defender.recieveReinforcement(reinforcement);

			} else if (reinforcement.getOwner() == attacker.getOwner()) {
				attacker.recieveReinforcement(reinforcement);

			} else {
				throw new RuntimeException("how could this happen?");
			}
		}
	}

	public void updateBattleField(float deltatime) {
		// log.logStringPS(tag, state + "" +);
		if (state == BATTLE_FEILD_STATE.PREPARE && defender.isReadyToBattle()
				&& attacker.isReadyToBattle()) {
			state = BATTLE_FEILD_STATE.PROCESSING;
			fightTimer = 0;
		}
		if (state == BATTLE_FEILD_STATE.PROCESSING) {
			fightTimer += deltatime;
			if (fightTimer >= FIGHT_INTERVAL) {
				fightTimer -= FIGHT_INTERVAL;
				
				CombatUnit winner = getWinner();
				if (winner != null) {
					attacker.endBattle(winner);
					defender.endBattle(winner);
					state = BATTLE_FEILD_STATE.END;
				} else {

					float defDamage = defender.outputDamage();
					float attDamage = attacker.outputDamage();

					defender.inputDamage(attDamage);
					attacker.inputDamage(defDamage);
				}
				// attacker.updateCombatUnit(deltatime);
				// defender.updateCombatUnit(deltatime);
			}
		}
	}

	public boolean isNotStart() {
		if (state == BATTLE_FEILD_STATE.NOT_START)
			return true;
		return false;
	}

	public boolean isProcessing() {
		if (state == BATTLE_FEILD_STATE.PROCESSING)
			return true;
		return false;
	}

	public boolean isEnd() {
		if (state == BATTLE_FEILD_STATE.END)
			return true;
		return false;
	}

	public CombatUnit getWinner() {
		if (defender.isBreakDown() && !attacker.isBreakDown()) {
			return attacker;
		} else if (!defender.isBreakDown() && attacker.isBreakDown()) {
			return defender;
		} else if (defender.isBreakDown() && attacker.isBreakDown()) {
			return defender;
		} else {
			return null;
		}
	}
}
