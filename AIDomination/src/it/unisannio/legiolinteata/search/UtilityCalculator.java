package it.unisannio.legiolinteata.search;

import aima.core.search.adversarial.Game;

public interface UtilityCalculator< G extends Game<STATE, ACTION, PLAYER>, STATE, PLAYER, ACTION> {
	
	float evaluateUtility(G tGame, STATE state, PLAYER player);
}
