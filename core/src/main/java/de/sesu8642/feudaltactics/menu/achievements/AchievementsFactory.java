package de.sesu8642.feudaltactics.menu.achievements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Provides;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.model.AbortGameAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.HistoricPersonOrEvent;
import de.sesu8642.feudaltactics.menu.achievements.model.LoseAgainstWeakestAiAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.PlayMoreThanNRoundsAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinAgainstAiLevelAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinAgainstManyEnemiesAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinInNRoundsAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinNGamesAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinOnMapSizeAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinVeryHardGamesInARowAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinWhenStartingLastAchievement;

/**
 * Provides achievement classes, knows how to construct each individual achievement
 */
@Singleton
public class AchievementsFactory {

    @Inject
    public AchievementsFactory() {
    }

    public List<AbstractAchievement> CreateAchievements() {
        List<AbstractAchievement> list = new ArrayList<>();
        list.add(new WinNGamesAchievement(1));
        list.add(new WinNGamesAchievement(10));
        list.add(new WinNGamesAchievement(50).setHistoricConnection(HistoricPersonOrEvent.CHARLEMAGNE));    // Charlemagne won many battles
        list.add(new WinInNRoundsAchievement(18));
        list.add(new WinInNRoundsAchievement(14));
        list.add(new WinInNRoundsAchievement(12).setHistoricConnection(HistoricPersonOrEvent.JEANNE_DARC));    // Jeanne d'Arc won battles when she was very young
        list.add(new PlayMoreThanNRoundsAchievement(30).setHistoricConnection(HistoricPersonOrEvent.THIRTY_YEARS_WAR));
        list.add(new PlayMoreThanNRoundsAchievement(50).setHistoricConnection(HistoricPersonOrEvent.HUNDRED_YEARS_WAR));    // The Hundred Years' War lasted very long obviously
        list.add(new LoseAgainstWeakestAiAchievement().setHistoricConnection(HistoricPersonOrEvent.ROAD_TO_CANOSSA));    // The Walk to Canossa was a humiliation. And so is this achievement.
        list.add(new WinVeryHardGamesInARowAchievement(3));
        list.add(new WinVeryHardGamesInARowAchievement(10));
        list.add(new WinVeryHardGamesInARowAchievement(20).setHistoricConnection(HistoricPersonOrEvent.WILLIAM_THE_CONQUEROR));
        list.add(new WinAgainstManyEnemiesAchievement(3));
        list.add(new WinAgainstManyEnemiesAchievement(4));
        list.add(new WinAgainstManyEnemiesAchievement(5).setHistoricConnection(HistoricPersonOrEvent.LOUIS_XI));
        list.add(new WinOnMapSizeAchievement(MapSizes.SMALL));
        list.add(new WinOnMapSizeAchievement(MapSizes.MEDIUM));
        list.add(new WinOnMapSizeAchievement(MapSizes.LARGE));
        list.add(new WinOnMapSizeAchievement(MapSizes.XLARGE));
        list.add(new WinOnMapSizeAchievement(MapSizes.XXLARGE).setHistoricConnection(HistoricPersonOrEvent.RICHARD_THE_LIONHEART));
        list.add(new WinAgainstAiLevelAchievement(Intelligence.LEVEL_1));
        list.add(new WinAgainstAiLevelAchievement(Intelligence.LEVEL_2));
        list.add(new WinAgainstAiLevelAchievement(Intelligence.LEVEL_3));
        list.add(new WinAgainstAiLevelAchievement(Intelligence.LEVEL_4).setHistoricConnection(HistoricPersonOrEvent.FREDERICK_THE_GREAT));
        list.add(new WinWhenStartingLastAchievement().setHistoricConnection(HistoricPersonOrEvent.TOKUGAWA_IEYASU));
        list.add(new AbortGameAchievement().setHistoricConnection(HistoricPersonOrEvent.JOHN_THE_POSTHUMOUS));

        return Collections.unmodifiableList(list);
    }
}
