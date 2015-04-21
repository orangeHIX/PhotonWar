package com.creaty.photonwar;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.androidgames.framework.model.GameObject;
import com.badlogic.androidgames.framework.model.SpatialHashGrid;
import com.creaty.photonwar.entity.Ship;
import com.creaty.photonwar.entity.ShipGroup;
import com.creaty.photonwar.entity.StrongHold;

public class SpatialHashGridForWar extends SpatialHashGrid {

	List<Ship> foundShips;

	public SpatialHashGridForWar(float worldWidth, float worldHeight,
			float cellSize) {
		super(worldWidth, worldHeight, cellSize);
		// TODO Auto-generated constructor stub
		foundShips = new ArrayList<Ship>();
		foundStrongHold = new ArrayList<StrongHold>();
	}

	public List<Ship> getPotentialArrivedShips(StrongHold hold) {
		foundShips.clear();
		int[] cellIds = getCellIds(hold);
		int i = 0;
		int cellId = -1;
		while (i <= 3 && (cellId = cellIds[i++]) != -1) {
			int len = dynamicCells[cellId].size();
			for (int j = 0; j < len; j++) {
				GameObject obj = dynamicCells[cellId].get(j);
				if (obj instanceof Ship) {
					Ship ship = (Ship) obj;
					if (ship.destination == hold) {
						if (!foundShips.contains(ship)) {
							foundShips.add(ship);
							// Log.d("potential", "hold "+ hold.position +
							// " ship " + ship.position);
						}
					}
				}
			}
		}
		return foundShips;
	}

	List<StrongHold> foundStrongHold;

	public List<StrongHold> getPotentialTarget(ShipGroup shipGroup) {
		foundStrongHold.clear();
		int[] cellIds = getCellIds(shipGroup);
		int i = 0;
		int cellId = -1;
		while (i <= 3 && (cellId = cellIds[i++]) != -1) {
			int len = staticCells[cellId].size();
			for (int j = 0; j < len; j++) {
				GameObject obj = staticCells[cellId].get(j);
				// Log.d("potential_1", ""+cellId +","+len);
				if (obj instanceof StrongHold) {
					StrongHold hold = (StrongHold) obj;
					// Log.d("potential_2", "hold "+ hold.position + " ship " +
					// ship.position);
					if (shipGroup.destination == hold) {
						if (!foundStrongHold.contains(hold)) {
							foundStrongHold.add(hold);
							// Log.d("potential_3", "hold "+ hold.position +
							// " ship " + ship.position);
						}
					}
				}
			}
		}
		return foundStrongHold;
	}

	public List<StrongHold> getPotentialTarget(float x, float y) {
		foundStrongHold.clear();
		int[] cellIds = getCellIds(x, x, y, y);
		int i = 0;
		int cellId = -1;
		while (i <= 3 && (cellId = cellIds[i++]) != -1) {
			int len = staticCells[cellId].size();
			for (int j = 0; j < len; j++) {
				GameObject obj = staticCells[cellId].get(j);
				// Log.d("potential_1", ""+cellId +","+len);
				if (obj instanceof StrongHold) {
					StrongHold hold = (StrongHold) obj;
					if (!foundStrongHold.contains(hold)) {
						foundStrongHold.add(hold);
					}

				}
			}
		}
		return foundStrongHold;
	}
}
