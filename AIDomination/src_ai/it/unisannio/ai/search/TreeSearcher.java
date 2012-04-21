package it.unisannio.ai.search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import net.yura.domination.engine.core.RiskGame;


public class TreeSearcher {
	PrintStream p = null;
	int turno;
	
	public TreeSearcher(){
		turno = 0;
		try {
			p =  new PrintStream(new File("time.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	public void srotolaAlbero(RiskGame game){
		turno++;
		p.append("Turno "+turno+":\n");
		long inizioTurno = System.currentTimeMillis();
		GameScenario scenario = new GameScenario(game);
		List<GameMutation> mutations = null;
		while(mutations == null ||mutations.get(0).getDestination().getState() != GameScenario.State.END){
			if(mutations == null)
				mutations = scenario.getMutations();
			else{
				List<GameMutation> temp = new ArrayList<GameMutation>();
//				GameMutation m = mutations.get(0);
				for(GameMutation m: mutations){
					System.out.println("------------------------");
					temp.addAll(m.getDestination().getMutations());
				}
				mutations = temp;
			}
			System.out.println("************************************");
			p.append("	Ampiezza livello "+ mutations.get(0).getOrigin().getState()+"->"+mutations.get(0).getDestination().getState() +": " + mutations.size()+"\n");
			
		}
		p.append("Tempo per attraversamento completo turno "+ turno +": " + (System.currentTimeMillis()-inizioTurno)+"ms\n\n");
	}
}
