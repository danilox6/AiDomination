package net.yura.domination.engine.ai;

import java.util.Collection;
import java.util.HashMap;


public class AIManager {
	private static HashMap<String, AI> AIs = new HashMap<String, AI>();
	
	public static void setup(){
		AI ai;
		ai = new AIHuman();
		AIs.put(ai.getId(), ai);
		ai = new AICrap("AI Crap", "crap");
		AIs.put(ai.getId(), ai);
		ai = new AIEasy("AI Easy", "easy");
		AIs.put(ai.getId(), ai);
		ai = new AIHard("AI Hard", "hard");
		ai.setCapitalAI(new AIHardCapital());
		ai.setMissionAI(new AIHardMission());
		AIs.put(ai.getId(), ai);
		ai = new AIVeryHard("AI Molto Difficile", "veryhard");
		AIs.put(ai.getId(), ai);
	}
	
	
	public static AI getAI(String id){
		return AIs.get(id);
	}
	
	public static Collection<AI> getAIs(){
		return AIs.values();
	}
	
//	public static void addAI(AI ai){
//		AIs.put(ai.getId(), ai);	
//	}
	
}
