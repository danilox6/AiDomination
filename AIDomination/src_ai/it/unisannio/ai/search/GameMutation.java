package it.unisannio.ai.search;

import java.util.StringTokenizer;
import java.util.Vector;

import net.yura.domination.engine.core.Country;

public class GameMutation implements Comparable<GameMutation> {
	private GameScenario origin;
	private String command;
	private GameScenario destination;
	private float likelihood = 1;

	private int attackerId, defenderId;


	public GameMutation(GameScenario origin, String command){
		this.origin = origin;
		this.command = command;
		attackerId = origin.getAttackerId();
		defenderId = origin.getDefenderId();
		calcDestination();
		System.out.println(origin.getState() +" --" + command +"--> "+ destination.getState());
	}

	public float getUtility() {
		return destination.getUtility() * likelihood;
	}

	@Override
	public int compareTo(GameMutation m) {
		return (int) (this.getUtility() - m.getUtility()); //FIXME controllare ordine
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

			if(origin.getState().equals(GameScenario.State.INITIAL_PLACEMENT)){
				if(tokenizer.nextToken().equals("placearmies")){
					int nation = Integer.parseInt(tokenizer.nextToken());
					destination.countries.put(nation, 1);
					if(origin.enemyTurn){
						destination.enemyExtraArmies--;
					}else{
						destination.possessions.add(nation);
						destination.extraArmies--;
					}
					if(SearchUtility.thereAreEmptyCountries(destination)){
						destination.setState(GameScenario.State.INITIAL_PLACEMENT);
					}else
						destination.setState(GameScenario.State.INITIAL_FORTIFY);
					destination.enemyTurn = !origin.enemyTurn;
				}else
					throw new IllegalArgumentException("Wrong command: "+ command);
			}

			else if (origin.getState().equals(GameScenario.State.INITIAL_FORTIFY)){
				if(tokenizer.nextToken().equals("placearmies")){
					int nation = Integer.parseInt(tokenizer.nextToken());
					destination.countries.put(nation, 1);
					if(origin.enemyTurn){
						destination.enemyExtraArmies--;
					}else{
						destination.extraArmies--;
					}
					destination.enemyTurn = !origin.enemyTurn;
					if(destination.extraArmies != 0 || destination.enemyExtraArmies != 0){
						destination.setState(GameScenario.State.INITIAL_FORTIFY);
					}else{
						if(destination.enemyTurn)
							destination.enemyExtraArmies = SearchUtility.getNextTurnArmies(destination, true);
						else
							destination.extraArmies = SearchUtility.getNextTurnArmies(destination, false);
						destination.setState(GameScenario.State.FORTIFY);
					}
					
				}else
					throw new IllegalArgumentException("Wrong command: "+ command);
			}
			
			else if(origin.getState().equals(GameScenario.State.FORTIFY)){
				if(tokenizer.nextToken().equals("placearmies")){
					int nation = Integer.parseInt(tokenizer.nextToken());

					destination.countries.put(nation,(Integer.parseInt(tokenizer.nextToken())));
					if(origin.enemyTurn){
						destination.enemyExtraArmies--;
					}else{
						destination.extraArmies--;
					}
					if((origin.enemyTurn && destination.enemyExtraArmies != 0) || (!origin.enemyTurn && destination.extraArmies != 0)){
						destination.setState(GameScenario.State.FORTIFY);
					}else{
						if(destination.enemyTurn)
							destination.setState(GameScenario.State.DEFEND);
						else
							destination.setState(GameScenario.State.ATTACK);
					}
				}else
					throw new IllegalArgumentException("Wrong command: "+ command);
			}

			else if(origin.getState().equals(GameScenario.State.ATTACK)){
				String comm = tokenizer.nextToken(); 
				if(comm.equals("attack")){
					attackerId = Integer.parseInt(tokenizer.nextToken());
					defenderId = Integer.parseInt(tokenizer.nextToken());
					destination.setAttackerDefender(attackerId, defenderId);
					destination.setState(GameScenario.State.ROLL);
				}
				else if(comm.equals("endattack")){
					destination.setState(GameScenario.State.MOVE);
				}else					
					throw new IllegalArgumentException("Wrong command: "+ command);

			}

			else if(origin.getState().equals(GameScenario.State.ROLL)){
//				destination.setLikelihood(likelihood);///FIXME Serve?
				
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
				}else if(command.equals("lost")){
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

			else if(origin.getState().equals(GameScenario.State.BATTLEWON)){
				if(command.equals("move all")){
					int armies = destination.countries.get(attackerId) - 1;
					destination.countries.put(defenderId, armies);
					destination.countries.put(attackerId, 1);

					if(canAttack(destination))
						destination.setState(GameScenario.State.ATTACK);
					else
						destination.setState(GameScenario.State.MOVE);
				}else					
					throw new IllegalArgumentException("Wrong command: "+ command + ". Solo \"move all\" è supportato.");
			}

			else if(origin.getState().equals(GameScenario.State.MOVE)){
				String c = tokenizer.nextToken();
				if(c.equals("movearmies")){
					int source = Integer.parseInt(tokenizer.nextToken());
					int dest = Integer.parseInt(tokenizer.nextToken());
					int armies = Integer.parseInt(tokenizer.nextToken());
					destination.countries.put(dest, destination.countries.get(dest)+armies);
					destination.countries.put(source, destination.countries.get(source)-armies);
				}else if(!c.equals("nomove"))
					throw new IllegalArgumentException("Wrong command: "+ command);
				
				destination.enemyTurn = true;
				destination.enemyExtraArmies = SearchUtility.getNextTurnArmies(destination, true);
				destination.setState(GameScenario.State.FORTIFY);
				
			}

			else if(origin.getState().equals(GameScenario.State.DEFEND)){

				String comm = tokenizer.nextToken(); 
				if(comm.equals("defendattack")){
					attackerId = Integer.parseInt(tokenizer.nextToken());
					defenderId = Integer.parseInt(tokenizer.nextToken());
					destination.setAttackerDefender(attackerId, defenderId);
					destination.setState(GameScenario.State.DEFENDROLL);
				}
				else if(comm.equals("enddefend")){
					destination.setState(GameScenario.State.END);
				}else					
					throw new IllegalArgumentException("Wrong command: "+ command);
			}

			else if(origin.getState().equals(GameScenario.State.DEFENDROLL)){
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
			if( armies > 1){
				Vector<Country> neighbours = scenario.getGame().getCountryInt(country).getNeighbours();
				for(Country n : neighbours)
					if(!scenario.possessions.contains(n.getColor()))
						return true;
			}
		}			
		return false;
	}



	private boolean enemyCanAttack(GameScenario scenario) {
		for(Integer country: scenario.countries.keySet()){
			if(!scenario.possessions.contains(country) && scenario.countries.get(country)>1){
				Vector<Country> neighbours =  scenario.getGame().getCountryInt(country).getNeighbours();
				for(Country n: neighbours)
					if(scenario.possessions.contains(n.getColor()))
						return true;
			}
		}
		return false;
	}

}
