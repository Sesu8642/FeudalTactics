// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.ingame.botai;

import java.util.HashMap;
import java.util.Map;

import de.sesu8642.feudaltactics.lib.gamestate.Unit.UnitTypes;

/**
 * The AI can pick up more than one unit at once. This class keeps track of and
 * provides access to all the units that are picked up
 */
class PickedUpUnits {

	/** key = strength of the unit; value: number of picked up units. */
	private Map<Integer, Integer> internalPickedUpUnits = new HashMap<>();

	public PickedUpUnits() {
		for (UnitTypes type : UnitTypes.values()) {
			internalPickedUpUnits.put(type.strength(), 0);
		}
	}

	public Integer ofType(UnitTypes type) {
		return ofStrength(type.strength());
	}

	public Integer ofStrength(int strength) {
		return internalPickedUpUnits.get(strength);
	}

	public void addUnit(UnitTypes type) {
		addUnitOfStrength(type.strength());
	}

	public void addUnitOfStrength(int strength) {
		internalPickedUpUnits.put(strength, internalPickedUpUnits.get(strength) + 1);
	}

	public void removeUnit(UnitTypes type) {
		removeUnit(type, 1);
	}

	public void removeUnit(UnitTypes type, int amount) {
		removeUnitOfStrength(type.strength(), amount);
	}

	public void removeUnitOfStrength(int strength) {
		removeUnitOfStrength(strength, 1);
	}

	public void removeUnitOfStrength(int strength, int amount) {
		internalPickedUpUnits.put(strength, internalPickedUpUnits.get(strength) - amount);
	}

	public int getTotalNoOfUnits() {
		return internalPickedUpUnits.values().stream().mapToInt(Integer::intValue).sum();
	}
}