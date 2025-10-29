// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;

/**
 * Map object representing a unit. Units are used to conquer enemy tiles. They
 * need to be paid a salary every turn. There are several tiers of units with
 * different strength levels and salaries.
 **/
public class Unit implements TileContent {

    public static final int COST = 10;

    @Getter
    @Setter
    private boolean canAct = true;
    @Getter
    private UnitTypes unitType;

    // only for deserialization
    @Deprecated
    public Unit() {
    }

    public Unit(UnitTypes unitType) {
        this.unitType = unitType;
    }

    @Override
    public String getSpriteName() {
        return unitType.spriteName();
    }

    @Override
    public int getStrength() {
        return unitType.strength();
    }

    @Override
    public Unit getCopy() {
        final Unit newUnit = new Unit(getUnitType());
        newUnit.setCanAct(canAct);
        return newUnit;
    }

    @Override
    public String toString() {
        final String superStr = super.toString();
        return superStr + ", Type: " + unitType.toString() + ", CanAct: " + canAct;
    }

    @Override
    public int hashCode() {
        // calculating with enum strings because the hashcode must be consistent across
        // runs
        return Objects.hash(canAct, unitType.toString());
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
        final Unit other = (Unit) obj;
        return canAct == other.canAct && unitType == other.unitType;
    }

    /**
     * Type of a unit determining its strength and sprite.
     **/
    public enum UnitTypes {
        PEASANT(1, 2, "peasant"), SPEARMAN(2, 6, "spearman"), KNIGHT(3, 18, "knight"), BARON(4, 54, "baron");

        private final int strength;
        private final int salary;
        private final String spriteName;

        UnitTypes(int strength, int salary, String spriteName) {
            this.strength = strength;
            this.salary = salary;
            this.spriteName = spriteName;
        }

        public static UnitTypes ofStrength(int strength) {
            return Arrays.stream(UnitTypes.values()).filter(unitType -> unitType.strength() == strength).findAny()
                .orElseThrow(() -> new AssertionError("Cannot find unit with requested strength " + strength));
        }

        public static UnitTypes strongest() {
            return UnitTypes.BARON;
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

}
