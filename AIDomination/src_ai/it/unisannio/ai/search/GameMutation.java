package it.unisannio.ai.search;

import java.util.StringTokenizer;

public class GameMutation implements Comparable<GameMutation> {
	private GameScenario origin;
	private String command;
	private GameScenario destination;
	private float likelihood;
	
	private int attackerId, defenderId;
	
	
	public GameMutation(GameScenario origin, String command){
		this.origin = origin;
		this.command = command;
		attackerId = origin.getAttackerId();
		defenderId = origin.getDefenderId();
		calcDestination();
	}
	
	public float getUtility() {
		return 0.0f;
	}

	@Override
	public int compareTo(GameMutation arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public GameScenario getDestination() {
		return destination;
	}

	public void setDestination(GameScenario destination) {
		this.destination = destination;
	}

	public GameScenario getOrigin() {
		return origin;
	}

	public String getCommand() {
		return command;
	}

	public float getLikelihood() {
		return likelihood;
	}

	public GameMutation setLikelihood(float likelihood) {
		this.likelihood = likelihood;
		return this;
	}
	
	public GameMutation calcDestination(){
		try {
			GameScenario destination = (GameScenario) origin.clone();

			StringTokenizer tokenizer = new StringTokenizer(command);
			
			if(origin.getState().equals(GameScenario.State.FORTIFY)
					|| origin.getState().equals(GameScenario.State.INITIAL_PLACEMENT)
					|| origin.getState().equals(GameScenario.State.INITIAL_FORTIFY)){
				if(tokenizer.nextToken().equals("placearmies")){
					String nation = tokenizer.nextToken();
					destination.countries.put(Integer.parseInt(nation),(Integer.parseInt(tokenizer.nextToken())));
					destination.extraArmies--;
					if(destination.extraArmies  == 0)
						destination.setState(GameScenario.State.ATTACK);
				}else
					throw new IllegalArgumentException("Wrong command: "+ command);
			}
			
			if(origin.getState().equals(GameScenario.State.ATTACK)){
				String comm = tokenizer.nextToken(); 
				if(comm.equals("attack")){
					attackerId = Integer.parseInt(tokenizer.nextToken());
					defenderId = Integer.parseInt(tokenizer.nextToken());
					destination.setState(GameScenario.State.ROLL);
					destination.setAttackerDefender(attackerId, defenderId);
				}
				else if(comm.equals("endattack")){
					destination.setState(GameScenario.State.MOVE);
				}else					
					throw new IllegalArgumentException("Wrong command: "+ command);
				
			}
			
			if(origin.getState().equals(GameScenario.State.ROLL)){
				int lostArmies = 0;
				int attackerArmies =  destination.countries.get(attackerId);
				int defenderArmies = destination.countries.get(defenderId);
				if(command.equals("won")){
					lostArmies = SearchUtility.getAttackerLostArmies(attackerArmies, defenderArmies);
					int survivedArmies = 0;
					if(lostArmies>=attackerArmies-1)
						survivedArmies = 2;
					else
						survivedArmies = attackerArmies-lostArmies;
					destination.countries.put(attackerId,survivedArmies );
					destination.possessions.add(defenderId);
					destination.setState(GameScenario.State.BATTLEWON);
				}if(command.equals("lost")){
					lostArmies = SearchUtility.getDefenderLostArmies(attackerArmies, defenderArmies);
					
					int survivedArmies = 0;
					if(lostArmies>=attackerArmies)
						survivedArmies = 1;
					else
						survivedArmies = defenderArmies-lostArmies;
					
					destination.countries.put(attackerId, 1);
					destination.countries.put(defenderId, survivedArmies);
					
					if(canAttack(destination))
						destination.setState(GameScenario.State.ATTACK);
					else
						destination.setState(GameScenario.State.MOVE);
				}else					
					throw new IllegalArgumentException("Wrong command: "+ command);
			}
			
			if(origin.getState().equals(GameScenario.State.BATTLEWON)){
				if(tokenizer.nextToken().equals("move all")){
					int armies = destination.getGame().getCountryInt(attackerId).getArmies() - 1;
					destination.countries.put(defenderId, destination.countries.get(armies));

					if(canAttack(destination))
						destination.setState(GameScenario.State.ATTACK);
					else
						destination.setState(GameScenario.State.MOVE);
				}else					
					throw new IllegalArgumentException("Wrong command: "+ command + ". Solo \"move all\" è supportato");
			}
			
			if(origin.getState().equals(GameScenario.State.MOVE)){
				if(tokenizer.nextToken().equals("movearmies")){
					int source = Integer.parseInt(tokenizer.nextToken());
					int dest = Integer.parseInt(tokenizer.nextToken());
					int armies = Integer.parseInt(tokenizer.nextToken());
					destination.countries.put(dest, destination.countries.get(dest)+armies);
					destination.countries.put(source, destination.countries.get(source)-armies);
					destination.setState(GameScenario.State.DEFEND);
				}if(tokenizer.nextToken().equals("nomove"))
					destination.setState(GameScenario.State.DEFEND);
				else					
					throw new IllegalArgumentException("Wrong command: "+ command);
			}
			
			if(origin.getState().equals(GameScenario.State.DEFEND)){
				String comm = tokenizer.nextToken(); 
				if(comm.equals("attack")){
					attackerId = Integer.parseInt(tokenizer.nextToken());
					defenderId = Integer.parseInt(tokenizer.nextToken());
					destination.setState(GameScenario.State.DEFENDROLL);
					destination.setAttackerDefender(attackerId, defenderId);
				}
				else if(comm.equals("endattack")){
					destination.setState(GameScenario.State.END);
				}else					
					throw new IllegalArgumentException("Wrong command: "+ command);
			}
			
			if(origin.getState().equals(GameScenario.State.DEFENDROLL)){
				//Si assume che il nemico sposta tutte le truppe rimaste (-1) se vince
				int lostArmies = 0;
				int attackerArmies =  destination.countries.get(attackerId);
				int defenderArmies = destination.countries.get(defenderId);
				if(command.equals("won")){ //Il nemico vince
					lostArmies = SearchUtility.getAttackerLostArmies(attackerArmies, defenderArmies);
					int survivedArmies = 0;
					if(lostArmies>=attackerArmies - 1)
						survivedArmies = 2;
					else
						survivedArmies = attackerArmies-lostArmies;
					
					destination.countries.put(attackerId, 1);
					destination.countries.put(defenderId, survivedArmies - 1);
					destination.possessions.remove(defenderId);
				}else if(command.equals("lost")){ //Il nemico perde
					lostArmies = SearchUtility.getDefenderLostArmies(attackerArmies, defenderArmies);
					
					int survivedArmies = 0;
					if(lostArmies>=attackerArmies)
						survivedArmies = 1;
					else
						survivedArmies = defenderArmies-lostArmies;
					
					
					destination.countries.put(attackerId, 1);
					destination.countries.put(defenderId, survivedArmies );

				}else					
					throw new IllegalArgumentException("Wrong command: "+ command);

				if(enemyCanAttack(destination))
					destination.setState(GameScenario.State.DEFEND);
				else
					destination.setState(GameScenario.State.END);
			}
			
			
			setDestination(destination);
			return this;
			
		} catch(NumberFormatException e){
			throw new IllegalArgumentException("Wrong command: "+ command);
		}  catch(NullPointerException e){
			throw new IllegalArgumentException("Wrong command: "+ command);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
		
	}

	private boolean canAttack(GameScenario scenario){
		for(Integer country: scenario.possessions){
			int armies = scenario.countries.get(country);
			if( armies > 1)
				return true;
		}			
		return false;
	}
	
	private boolean enemyCanAttack(GameScenario scenario) {
		for(Integer country: scenario.countries.keySet()){
			if(!scenario.possessions.contains(country) && scenario.countries.get(country)>1)
				return true;
		}
		return false;
	}
	
}
