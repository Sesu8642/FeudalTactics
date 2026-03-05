// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements;

import com.badlogic.gdx.Preferences;

import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.ingame.AutoSaveRepository;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.dagger.AchievementsPrefStore;
import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.BuyNCastlesAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.LoseAgainstWeakestAi;
import de.sesu8642.feudaltactics.menu.achievements.model.WinAgainstAiLevelAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinAgainstManyEnemiesAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.PlayMoreThanNRoundsAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinInNRoundsAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinNGamesAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinOnMapSizeAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinVeryHardGamesInARowAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinWithOnlyNCastlesAchievement;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Repository for managing achievements. Combines achievement storage and retrieval.
 */
@Singleton
public class AchievementRepository {

    public static final String ACHIEVEMENTS_NAME = "achievements";

    private final Preferences prefStore;

    @Getter
    private final List<AbstractAchievement> achievements;

    @Inject
    public AchievementRepository(
            @AchievementsPrefStore Preferences achievementsPrefs, 
            AutoSaveRepository autoSaveRepository,
            AchievementGameStateTracker gameStateTracker) {
        this.prefStore = achievementsPrefs;

        List<AbstractAchievement> list = new ArrayList<>();
        list.add(new WinNGamesAchievement(this, 1));
        list.add(new WinNGamesAchievement(this, 10));
        list.add(new WinNGamesAchievement(this, 50).setName("Charlemagne"));    // Charlemagne won many battles
        list.add(new WinInNRoundsAchievement(this, 18));
        list.add(new WinInNRoundsAchievement(this, 16));
        list.add(new WinInNRoundsAchievement(this, 15));
        list.add(new WinInNRoundsAchievement(this, 14).setName("Jeanne d'Arc"));    // Jeanne d'Arc won battles when she was very young
        list.add(new PlayMoreThanNRoundsAchievement(this, 25));
        list.add(new PlayMoreThanNRoundsAchievement(this, 35).setName("Hundred Years’ War"));    // The Hundred Years' War lasted very long obviously
        list.add(new LoseAgainstWeakestAi(this).setName("Road to Canossa"));    // The Walk to Canossa was a humiliation. And so is this achievement.
        list.add(new BuyNCastlesAchievement(this, 1, gameStateTracker));
        list.add(new BuyNCastlesAchievement(this, 20, gameStateTracker));
        list.add(new BuyNCastlesAchievement(this, 100, gameStateTracker).setName("Henry VIII"));    // Henry VIII built much military infrastructure
        list.add(new WinWithOnlyNCastlesAchievement(this, 0, gameStateTracker));
        list.add(new WinWithOnlyNCastlesAchievement(this, 1, gameStateTracker));
        list.add(new WinWithOnlyNCastlesAchievement(this, 3, gameStateTracker));
        list.add(new WinVeryHardGamesInARowAchievement(this, achievementsPrefs, 3));
        list.add(new WinVeryHardGamesInARowAchievement(this, achievementsPrefs, 10));
        list.add(new WinVeryHardGamesInARowAchievement(this, achievementsPrefs, 20).setName("William the Conqueror"));    // William the Conqueror won many battles in a row
        list.add(new WinAgainstManyEnemiesAchievement(this, 3));
        list.add(new WinAgainstManyEnemiesAchievement(this, 4));
        list.add(new WinAgainstManyEnemiesAchievement(this, 5));
        list.add(new WinOnMapSizeAchievement(this, MapSizes.SMALL));
        list.add(new WinOnMapSizeAchievement(this, MapSizes.MEDIUM));
        list.add(new WinOnMapSizeAchievement(this, MapSizes.LARGE));
        list.add(new WinOnMapSizeAchievement(this, MapSizes.XLARGE));
        list.add(new WinOnMapSizeAchievement(this, MapSizes.XXLARGE).setName("Richard the Lionheart"));    // Richard the Lionheart fought in the Crusades, far away from his home
        list.add(new WinAgainstAiLevelAchievement(this, Intelligence.LEVEL_1));
        list.add(new WinAgainstAiLevelAchievement(this, Intelligence.LEVEL_2));
        list.add(new WinAgainstAiLevelAchievement(this, Intelligence.LEVEL_3));
        list.add(new WinAgainstAiLevelAchievement(this, Intelligence.LEVEL_4).setName("Frederick the Great"));  // Frederick the Great is renowned for his intellect
        this.achievements = Collections.unmodifiableList(list);

        LoadPersistedAchievements();
    }

    private void LoadPersistedAchievements() {
        for (AbstractAchievement achievement : achievements) {
            achievement.setUnlocked(
                prefStore.getBoolean("achievement-" + achievement.getId(), false));
            achievement.setProgress(
                prefStore.getInteger("achievement-progress-" + achievement.getId(), 0));
        }
    }

    public void unlockAchievement(String achievementId) {
        prefStore.putBoolean("achievement-" + achievementId, true);
        prefStore.flush();
    }

    public void storeProgress(String id, int number) {
        prefStore.putInteger("achievement-progress-" + id, number);
        prefStore.flush();
    }

    public void onGameExited(de.sesu8642.feudaltactics.events.GameExitedEvent event) {
        for (AbstractAchievement achievement : achievements) {
            achievement.onGameExited(event);
        }
    }

    public void onMapRegeneration(RegenerateMapEvent event) {
        for (AbstractAchievement achievement : achievements) {
            achievement.onMapRegeneration(event);
        }
    }
}
