package it.unisannio.ai;

import it.unisannio.ai.search.TreeSearcher;
import net.yura.domination.engine.ai.Discoverable;

@Discoverable
public class AITreeBased extends AISimple{
	TreeSearcher search = new TreeSearcher();
	
	@Override
	public String getPlaceArmies() {
		return search.getBestMove(game).getCommand();
	}
	
	@Override
	public String getAttack() {
		return search.getBestMove(game).getCommand();
	}
	
	@Override
	public String getTacMove() {
		return search.getBestMove(game).getCommand();
	}
}
