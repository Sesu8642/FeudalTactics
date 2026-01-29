package de.sesu8642.feudaltactics.menu.achievements.model;

public class WinNGamesAchievement extends AbstractAchievement {
    private final int gamesToWin;

    public WinNGamesAchievement(int gamesToWin) {
        this.gamesToWin = gamesToWin;
    }

    @Override
    public String getId() {
        return "win-" + gamesToWin + "-games";
    }

    @Override
    public String getName() {
        return "Win " + gamesToWin + " Games";
    }

    @Override
    public String getDescription() {
        return "Win " + gamesToWin + " games, either by defeating your enemies or them giving up. Any difficulty and map size is allowed.";
    }
    
}
