// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.ingame.botai;

/**
 * Possible intelligence levels for the AI.
 */
public enum Intelligence {
    LEVEL_1(0.5F, 0, false, Integer.MAX_VALUE, Integer.MAX_VALUE, 5, false, false),
    LEVEL_2(0.8F, 0, false, Integer.MAX_VALUE, Integer.MAX_VALUE, 5, false, true),
    LEVEL_3(1F, 4, false, 25, 20, 0, true, true), LEVEL_4(1F, 7, true, 25, 20, 0, true, true);

    /**
     * Chance that the bot will even try to conquer anything in a given turn.
     */
    public final float chanceToConquerPerTurn;

    /**
     * Minimum removal score for blocking objects to be removed. A value of 0 will
     * cause all blocking objects to be removed, if possible.
     */
    public final int blockingObjectRemovalScoreTreshold;

    /**
     * Whether to reconsider which tiles need to be protected after attacking may
     * have changed which tiles make sense to protect.
     */
    public final boolean reconsidersWhichTilesToProtect;

    /**
     * Minimum defense tile score to be worth protecting with a castle. Will be
     * protected with a unit if a castle is too expensive. Use a very high value to
     * disable protecting with castles. If {@link #smartDefending} is false, any
     * value above 0 will disable it as well.
     */
    public final int protectWithCastleScoreTreshold;

    /**
     * Minimum defense tile score to be worth protecting with a unit. Use a very
     * high value to disable protecting with units as a first choice. If
     * {@link #smartDefending} is false, any value above 0 will disable it as well.
     */
    public final int protectWithUnitScoreTreshold;

    /**
     * Offense tile scores for other bot players will be increased by this amount to
     * make the game easier for the human players.
     */
    public final int attackOtherBotsBias;

    /**
     * Whether to defend smartly: i.e. considering how many tiles are protected. If
     * false, the defense score of every tile is 0. This means that (basically)
     * random tiles near the border are protected.
     */
    public final boolean smartDefending;

    /**
     * Whether to attack smartly: prefer enemy kingdom tiles over unconnected ones,
     * destroy castles etc. If false, the offense score of every tile is 0. This
     * means that (basically) random tiles are conquered.
     */
    public final boolean smartAttacking;

    Intelligence(float chanceToConquerPerTurn, int blockingObjectRemovalScoreTreshold,
                 boolean reconsidersWhichTilesToProtect, int protectWithCastleScoreTreshold,
                 int protectWithUnitScoreTreshold, int attackOtherBotsBias, boolean smartDefending, boolean smartAttacking) {
        this.chanceToConquerPerTurn = chanceToConquerPerTurn;
        this.blockingObjectRemovalScoreTreshold = blockingObjectRemovalScoreTreshold;
        this.reconsidersWhichTilesToProtect = reconsidersWhichTilesToProtect;
        this.protectWithCastleScoreTreshold = protectWithCastleScoreTreshold;
        this.protectWithUnitScoreTreshold = protectWithUnitScoreTreshold;
        this.attackOtherBotsBias = attackOtherBotsBias;
        this.smartDefending = smartDefending;
        this.smartAttacking = smartAttacking;
    }

}