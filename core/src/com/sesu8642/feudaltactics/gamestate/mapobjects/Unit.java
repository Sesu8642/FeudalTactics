package com.sesu8642.feudaltactics.gamestate.mapobjects;

public class Unit extends MapObject {

	static public final int COST = 10;

	private boolean canAct = true;

	public enum UnitTypes {
		PEASANT(1, 2, "peasant"), SPEARMAN(2, 6, "spearman"), KNIGHT(3, 18, "knight"),
		BARON(4, 54, "baron");

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

	private UnitTypes unitType;

	public Unit() {
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
	public MapObject getCopy() {
		Unit newUnit = new Unit(this.getUnitType());
		newUnit.setCanAct(this.canAct);
		return newUnit;
	}
	
	@Override
	public String toString() {
		String superStr = super.toString();
		return superStr + ", Type: " + unitType.toString() + ", CanAct: " + canAct;
	}

}
