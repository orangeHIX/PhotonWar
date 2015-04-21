package com.creaty.photonwar.inferface;

import com.creaty.photonwar.entity.Ammunition;
import com.creaty.photonwar.entity.BattleField;
import com.creaty.photonwar.entity.Ship;
import com.creaty.photonwar.entity.ShipGroup;
import com.creaty.photonwar.entity.StrongHold;

public interface EntityManager {
	public ShipGroup GetGroupInstance(StrongHold request);

	public void FreeGroupInstance(ShipGroup item);

	public Ship GetShipInstance();

	public void FreeShipInstance(Ship item);
	
	public Ammunition GetAmmunition();
	
	public void FreeAmmuition(Ammunition item);
	
	public BattleField GetBattleFieldInstance();
	
	public void FreeBattleField(BattleField item);

}
