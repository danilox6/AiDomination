package it.unisannio.ai;

import it.unisannio.ai.search.TreeSearcher;
import net.yura.domination.engine.ai.Discoverable;

@Discoverable
public class AITreeBased extends AISimple{
	TreeSearcher search = new TreeSearcher();
	
	@Override
	public String getPlaceArmies() {
		System.out.println(search.getBestMove(game).getCommand());
//		search.srotolaAlbero(game);
		return super.getPlaceArmies();
	}
}
