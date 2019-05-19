package com.sesu8642.feudaltactics.gamestate.mapobjects;

import com.sesu8642.feudaltactics.gamestate.Kingdom;

public class Unit extends MapObject {

	static public final int COST = 10;

	private boolean canAct = true;

	public enum UnitTypes {
		PEASANT(1, 2, "sprite_peasant"), SPEARMAN(2, 6, "sprite_spearman"), KNIGHT(3, 18, "sprite_knight"),
		BARON(4, 54, "sprite_baron");

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

	public Unit(Kingdom kingdom, UnitTypes unitType) {
		super(kingdom);
		this.unitType = unitType;
	}

	@Override
	public String getSpriteName() {
		return unitType.spriteName;
	}

	public UnitTypes getUnitType() {
		return unitType;
	}

	@Override
	public int getStrength() {
		return unitType.strength;
	}

	public boolean isCanAct() {
		return canAct;
	}

	public void setCanAct(boolean canAct) {
		this.canAct = canAct;
	}

}
