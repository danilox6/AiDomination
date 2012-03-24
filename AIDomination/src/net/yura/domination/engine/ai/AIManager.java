package net.yura.domination.engine.ai;

import java.util.Collection;
import java.util.HashMap;

/**
 * Utilizzato per integrare facilmente nuove AI nel gioco
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class AIManager {
	private static HashMap<String, AI> AIs = new HashMap<String, AI>();
	
	/**
	 * Utilizzato per integrare le Ai nel gioco
	 * Per far ciò, instanziare una AI ed aggiungerla alla mappa.
	 * Occhio a scegliere chiavi diverse
	 * 
	 */
	public static void setup(){
		AI ai;
		ai = new AIHuman(); 
		AIs.put(ai.getId(), ai);
		ai = new AICrap("ai crap", "AI Crap");
		AIs.put(ai.getId(), ai);
		ai = new AIEasy("ai easy", "AI Easy");
		AIs.put(ai.getId(), ai);
		ai = new AIHard("ai hard", "AI Hard");
		ai.setCapitalAI(new AIHardCapital());
		ai.setMissionAI(new AIHardMission());
		AIs.put(ai.getId(), ai);
		ai = new AIVeryHard("ai veryhard", "AI Molto Difficile");
		AIs.put(ai.getId(), ai);
	}
	
	/**
	 * Viene utilizzato quando l'engine risolve l'AI a partire dall'id
	 * @param id
	 * @return
	 */
	public static AI getAI(String id){
		return AIs.get(id);
	}
	
	/**
	 * Viene utilizzato dall'interfaccia grafica per visualizzare le AI disponibili
	 * @return
	 */
	public static Collection<AI> getAIs(){
		return AIs.values();
	}
	
}
