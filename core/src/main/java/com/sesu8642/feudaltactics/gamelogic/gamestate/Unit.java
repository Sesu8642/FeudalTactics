package com.sesu8642.feudaltactics.gamelogic.gamestate;

import java.util.Objects;

/**
 * Map object representing a unit. Units are used to conquer enemy tiles. They
 * need to be paid a salary every turn. There are several tiers of units with
 * different strength levels and salaries.
 **/
public class Unit implements MapObject {

	public static final int COST = 10;

	private boolean canAct = true;
	private UnitTypes unitType;

	/** Type of a unit determining its strength and sprite. **/
	public enum UnitTypes {
		PEASANT(1, 2, "peasant"), SPEARMAN(2, 6, "spearman"), KNIGHT(3, 18, "knight"), BARON(4, 54, "baron");

		private int strength;
		private int salary;
		private String spriteName;

		UnitTypes(int strength, int salary, String spriteName) {
			this.strength = strength;
			this.salary = salary;
			this.spriteName = spriteName;
		}

		public int strength() {
			return strength;
		}

		public int salary() {
			return salary;
		}

		public String spriteName() {
			return spriteName;
		}
	}

	// only for deserialization
	@Deprecated
	public Unit() {
		super();
	}

	public Unit(UnitTypes unitType) {
		super();
		this.unitType = unitType;
	}

	@Override
	public String getSpriteName() {
		return unitType.spriteName();
	}

	public UnitTypes getUnitType() {
		return unitType;
	}

	@Override
	public int getStrength() {
		return unitType.strength();
	}

	public boolean isCanAct() {
		return canAct;
	}

	public void setCanAct(boolean canAct) {
		this.canAct = canAct;
	}

	@Override
	public Unit getCopy() {
		Unit newUnit = new Unit(this.getUnitType());
		newUnit.setCanAct(this.canAct);
		return newUnit;
	}

	@Override
	public String toString() {
		String superStr = super.toString();
		return superStr + ", Type: " + unitType.toString() + ", CanAct: " + canAct;
	}

	@Override
	public int hashCode() {
		return Objects.hash(canAct, unitType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Unit other = (Unit) obj;
		return canAct == other.canAct && unitType == other.unitType;
	}

}
