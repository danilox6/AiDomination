package it.unisannio.ai.search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
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
		attraversamentoLeggero(game);
		p.append("Tempo per attraversamento completo turno "+ turno +": " + (System.currentTimeMillis()-inizioTurno)+"ms\n\n");
	}
	
	
	//Usa un HashSet per impedire il calcolo ripetuto su Scenari uguali
	private void attraversamentoLeggero(RiskGame game){

		GameScenario s = new GameScenario(game);
		HashSet<GameScenario> scenari = new HashSet<GameScenario>();
		scenari.add(s);
		GameScenario anOrigin;
		while ((anOrigin = scenari.iterator().next()).getState() != GameScenario.State.END){
			HashSet<GameScenario> temp = new HashSet<GameScenario>();
			for(GameScenario sc: scenari){
				List<GameMutation> mutations = sc.getMutations();
				for(GameMutation m: mutations){
					temp.add(m.getDestination());
				}
			}
			scenari = temp;
			GameScenario aDestination = scenari.iterator().next();
			p.append("	Ampiezza livello "+anOrigin.getState()+"("+ ((anOrigin.enemyTurn)? "nemico":"amico")
			+")->"+aDestination.getState() +"("+((aDestination.enemyTurn)? "nemico":"amico")+"): " + scenari.size()+"\n");
		}
	}
	
	
	@SuppressWarnings("unused")
	private void attraversamentoPesante(RiskGame game){
		GameScenario scenario = new GameScenario(game);
		List<GameMutation> mutations = null;
		while(mutations == null ||mutations.get(0).getDestination().getState() != GameScenario.State.END){
			if(mutations == null)
				mutations = scenario.getMutations();
			else{
				List<GameMutation> temp = new ArrayList<GameMutation>();
				for(GameMutation m: mutations){
					temp.addAll(m.getDestination().getMutations());
				}
				mutations = temp;
			}
			p.append("	Ampiezza livello "+ mutations.get(0).getOrigin().getState()+"->"+mutations.get(0).getDestination().getState() +": " + mutations.size()+"\n");
		}
	}
	
	
	private boolean fatto(HashSet<GameScenario> scenari){
		for (GameScenario s: scenari)
			if(s.getState() != GameScenario.State.END)
				return false;
		return true;
	}
}
