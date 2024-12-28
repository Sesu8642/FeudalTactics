package de.sesu8642.feudaltactics.lib.gamestate;

/**
 * Checks for completed objectives during the tutorial and progresses the
 * tutorial accordingly.
 */
public class TutorialProgressChecker {

	static boolean progressed = false;

	static void updateProgress(GameState gameState) {
		System.out.println("checking progeess");
		if (gameState.getHeldObject() != null && gameState.getHeldObject().getClass().equals(Unit.class)
				&& !progressed) {
			gameState.setObjectiveProgress(gameState.getObjectiveProgress()+1);
			progressed = true;
		} else if (gameState.getHeldObject() == null && progressed) {
			progressed = false;
		}
	}

}
