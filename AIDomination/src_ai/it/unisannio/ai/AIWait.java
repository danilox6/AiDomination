package it.unisannio.ai;

import it.unisannio.ai.search.TreeSearcher;
import net.yura.domination.engine.ai.AI;
import net.yura.domination.engine.ai.Discoverable;

@Discoverable
public class AIWait extends AI{
	TreeSearcher search = new TreeSearcher();

	
	private String aspetta(){
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String getPlaceArmies() {
		return aspetta();
	}

	@Override
	public String getTrade() {
		return aspetta();
	}


	@Override
	public String getAttack() {
		return aspetta();
	}


	@Override
	public String getRoll() {
		return aspetta();
	}


	@Override
	public String getBattleWon() {
		return aspetta();
	}


	@Override
	public String getTacMove() {
		return aspetta();
	}


	@Override
	public String getAutoDefendString() {
		return aspetta();
	}


	@Override
	public String getCapital() {
		return aspetta();
	}
}
