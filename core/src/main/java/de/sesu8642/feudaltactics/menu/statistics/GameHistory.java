// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Contains multiple historic games. This class is used as a wrapper for persisting the game history, because json
 * .setElementType does not support List<GameHistory> as the first parameter directly.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameHistory {

    private List<HistoricGame> historicGames;

}
