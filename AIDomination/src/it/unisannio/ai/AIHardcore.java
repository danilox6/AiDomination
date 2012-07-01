//  Group D

package it.unisannio.ai;

import java.util.Vector;

import net.yura.domination.engine.ai.Discoverable;
import net.yura.domination.engine.ai.core.*;
import net.yura.domination.engine.core.Continent;
import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Player;

/**
 * <p> Class for AIHardPlayer </p>
 * @author SE Group D
 */

@Discoverable
@SuppressWarnings({ "unchecked", "rawtypes" })
public class AIHardcore extends AIEasy {
	
	@Override
	public void onInit() {
		setCapitalAI(new AIHardCapital());
		setMissionAI(new AIHardMission());
	}

	/*
	 * = Strategies =
	 * 
	 * ## For initial placement
	 * 1.a. Rank continents according to a formula and pick the first available country there
	 * 1.b. If that fails, pick the first available country
	 * 2. If an opponent has almost conquered a continent, place an army there
	 */
	public String getPlaceArmies() {

		String output;

		// initial placement
		if ( game.NoEmptyCountries()==false ) {

			Continent[] cont = game.getContinents();

			/* ai looks at all the continents and tries to see which one it should place on
			The formula for placement is :
			extraArmiesForContinent - numberOfBorders - (neighborTerritories - numberOfBorders)+(territorynum * 0.9)-(IIF(numberofEnemyUnits=0,-3,numberofEnemyUnits) * 1.2)
			 */

			int extraArmiesForContinent = 0;
			int numberOfBorders = 0;
			int neighborTerritories = 0;
			int numberofEnemyUnits = -3;
			int territorynum = 0;
			boolean isBorder = false;
			double check = -20;
			double ratio = -20;
			String name=null;
			int val = -1;

			for (int i=0; i<cont.length; i++) {
				Vector ct = new Vector();
				Continent co = cont[i];
				
				// army bonus for that continent
				extraArmiesForContinent = co.getArmyValue();
				
				ct = co.getTerritoriesContained();

				// number of own territories and enemy territories
				for (int j=0; j<ct.size(); j++) {
					if ( ((Country)ct.elementAt(j)).getOwner() == player ) { 
						/* This territory belongs to the player */
						territorynum++; 
					}
					else {
						if (((Country)ct.elementAt(j)).getOwner() != null) {
							/* This territory belongs to an enemy */
							if (numberofEnemyUnits == -3) {
								numberofEnemyUnits = 1;
							}
							else {
								numberofEnemyUnits++;
							}
						}
					}
					
					// calculate borders
					Vector w = ((Country)ct.elementAt(j)).getNeighbours();
					
					// enemy neighbors outside continent
					for (int k=0; k<w.size(); k++) {
						if (((Country)w.elementAt(k)).getContinent() != co) {
							/* This is a territory to protect from */
							neighborTerritories++;				      
							isBorder = true;
						}
					}
					
					// number of borders
					if (isBorder) {
						numberOfBorders++;
					}
				}

				/* Calculate the value of that continent */ 

				ratio = extraArmiesForContinent - numberOfBorders - (neighborTerritories - numberOfBorders)+(territorynum * 0.9) - (numberofEnemyUnits * 1.2);

				// pick the best continent according to the ratio
				if (check <= ratio && hasFreeTerritories(ct) == true) {
					check = ratio;
					val = i;
				}

				territorynum = 0;
				numberofEnemyUnits = -3;
				neighborTerritories = 0;
				numberOfBorders = 0;
			}
			if (val==-1) { val=0; } // YURA: val is not being set

			// at this point we have our "best" continent
			
			/* ..pick country from that continent */
			
			// choose the first available territory
			boolean picked = false;
			if (check > 0) {
				Continent co = cont[val];
				Vector ct = co.getTerritoriesContained();

				for (int j=0; j<ct.size(); j++) {
					if ( ((Country)ct.elementAt(j)).getOwner() == null )  {
						name=((Country)ct.elementAt(j)).getColor()+"";
						picked = true;
						break;
					}
				}
			}

			// if nothing can be picked with that method
			// select the first available nation
			if (picked == false) {
				Continent co = cont[val];
				Vector ct = co.getTerritoriesContained();

				for (int j=0; j<ct.size(); j++) {
					if ( ((Country)ct.elementAt(j)).getOwner() == null )  {
						name=((Country)ct.elementAt(j)).getColor()+"";

						Vector v = ((Country)ct.elementAt(j)).getNeighbours();
						for (int k=0; k<v.size(); k++) { // YURA: was ct.size()
							// redundant logic: v = country.getNeighbours() => v[i].isNeighbours(country)
							if (((Country)v.elementAt(k)).getOwner() != player && ((Country)ct.elementAt(j)).isNeighbours((Country)v.elementAt(k))) {
								name=((Country)ct.elementAt(j)).getColor()+"";
								break;
							}
						}
					}
				}
			}

			// if an opponent has almost conquered a continent (1 free country left)
			// block him
			String s = blockOpponent(player);
			if( s != null )
				name = s;

			if (name == null)
				output = "autoplace";

			else
				output = "placearmies " + name +" 1";
		}
		else {
			// placement during game

			Vector t = player.getTerritoriesOwned();
//			Vector n;
			String name = null;
			
			// -------
			// PASS #1
			// Pick the first country with < 11 armies which is not surrounded by friendly nations
			// -------

			for (int a=0; a< t.size() ; a++) {

				if ( ownsNeighbours( (Country)t.elementAt(a)) == false && ((Country)t.elementAt(a)).getArmies() <= 11 ) {

					name=((Country)t.elementAt(a)).getColor()+"";
					break;
				}

				if ( name != null ) { break; }

			}
			
			// -------
			// PASS #2
			// Reinforces a country that is the only one not owned by a enemy in a continent,
			// if it has < 5 armies on it
			// -------

			String s = keepBlocking(player);
			if( s != null )
				name = s;
			
			// -------
			// PASS #3
			// Attempts to free a continent from an enemy player if it is possible to do so
			// -------

			String f = freeContinent(player);
			if( f != null )
				name = f;
			
			// -------
			// PASS #4			
			// Reinforces a nation next to one of those belonging to a weak player (< 4 countries owned)
			// -------

			String k = NextToEnemyToEliminate();
			if (k != null)
				name = k;
			
			// -------
			// PASS #5
			// If nothing has been found up to now, reinforce the first country that has an enemy neighbor
			// -------

			if (name == null)
				name = findAttackableTerritory(player);

			// -------
			// PASS #6
			// If nothing has been found yet, reinforce the first country among our possessions
			// -------
			
			if ( name == null )
				output = "placearmies " + ((Country)t.elementAt(0)).getColor() +" "+player.getExtraArmies();

			else if (game.getSetup() )
				output = "placearmies " + name +" "+player.getExtraArmies();

			else
				output = "placearmies " + name +" 1";
		}

		return output;

	}

	public String getBattleWon() {

		String output;

		/* Attempt to safeguard critical areas when moving armies to won country */

		Country attacker = game.getAttacker();
		Continent continent = attacker.getContinent();
		if (continent.isOwned(player) == true || mostlyOwned(continent) == true) {

			/* Set 50% security limit */
			if (attacker.getArmies() > 6)
				output = "move " + (attacker.getArmies()-1);  // bug request ID: 1478706
			else if (attacker.getArmies() > 3 && attacker.getArmies() <= 6)
				output = "move " + (attacker.getArmies()-1);
			else
				output = "move all";
		}
		else
			output="move all";

		return output;

	}

	public String getTacMove() {

		String output=null;

		/* reinforces armies from less critical to more critical areas */

		Vector t = player.getTerritoriesOwned();
		Vector n;
		boolean possible = false;
		int difference = 0;
		Country sender = null;
		Country receiver = null;
		Country temp = null;
		int highest = 1;

		// select our country with most armies
		for (int a=0; a<t.size(); a++) {

			Country country = ((Country)t.elementAt(a));
			difference  = country.getArmies();
			if (difference > highest)  {
				highest = difference;
				sender = country;
			}
		}

		if (sender != null)  {
			
			// checks whether it's possible to make a tactical
			// move from the sender nation
			receiver = check1(sender);

			if (receiver == null) {
				n = sender.getNeighbours();
				for (int i=0; i<n.size(); i++) {
					temp = (Country) n.elementAt(i);
					if (temp.getOwner() == player) {
						possible = check2(temp);
					}
					if (possible == true)  { receiver = temp; break; }
				}
			}
		}

		if (receiver != null && receiver != sender) {
			output= "movearmies " + ((Country)sender).getColor() + " " + ((Country)receiver).getColor() + " " + (((Country)sender).getArmies()-1);
			//System.out.println(((Country)sender).getName()); TESTING
			//System.out.println(((Country)receiver).getName()); TESTING
		}

		if ( output == null ) {
			output = "nomove";
		}

		return output;

	}


	public String getAttack() {
		String output=null;
		Vector t = player.getTerritoriesOwned();
		Continent[] cont = game.getContinents();
		Vector options = new Vector();
		Attack temp=null;
		Attack move=null;
		
		// -------
		// PASS #1
		// Find neighbors with half our troops, attack those belonging
		// to the most powerful player
		// -------

		// neighbors with half our troops
		options = findAttackableNeighbors(t,2);

		// players ordered from the most powerful to the least powerful
		// (territories + armies)-wise
		Player[] playersGreatestToLeast = OrderPlayers(player);  
		
		// select among the candidate territories the one belonging
		// to the most powerful player
		outer: for (int j=0; j<playersGreatestToLeast.length; j++) {
			for (int i=0; i<options.size(); i++) {
				temp = (Attack)options.get(i);
				if ( ((Country) temp.destination).getOwner().equals(playersGreatestToLeast[j]) ){
					output = temp.toString();
					break outer;
				}
			}  
		}
		
		// -------
		// PASS #2.a
		// Attack territories in continents that we own for more than 50%
		// -------
		
		/* attempt to attack continent which is almost all owned, if scenario is there */
		//
		// Refactoring: remove need for 'complex' boolean by sorting continents by how much you control them
		// 	either by absolute count or percentage
		//
		
		// attacks always the latest continent in the list with > 50% territories owned??
		// flawed logic!
		boolean complex = false;
		for (int i=0; i<cont.length; i++) {
			// if player owns > 50% territories in continent
			if ( mostlyOwned( cont[i] ) == true) {
				
				// now attacks from outside continent too!
				options = targetTerritories(cont[i].getTerritoriesContained());
				options = filterAttacks(options,1);    // after simple advantage
				
				// attacks a territory at random among the options
				if (options.size() > 0) {
					move = (Attack) options.elementAt( (int)Math.round(Math.random() * (options.size()-1) ) );
					output = move.toString();
					complex = true;
				}

			}
		}
		
		// -------
		// PASS #2.b
		// If pass #2.a gave no results, try to find a country to conquer
		// in the continent in which we have already most conquered
		// territories
		// -------

		// else attempt to attack continent with the greatest territories owned, that has yet to be conquered.
		if (complex == false) {
			int value = 0;
			int check = 0;   // pull out of for loop
			
			// select the continent with the most territories owned
			Continent choice = null;
			for (int i=0; i<cont.length; i++) {
				if ( cont[i].isOwned(player) == false) {
				
					check = countTerritoriesOwned(cont[i].getTerritoriesContained(), player);
					if (check > value){
						choice = cont[i];
						value = check;  // bug: should value be updated too?
					}
				}
			}

			if (choice !=null) {
				options = getPossibleAttacks(choice.getTerritoriesContained());

				// we attack a territory at random among those present in the
				// selected continent
				options = filterAttacks(options,1);
				if (options.size() > 0) {
					move = (Attack) options.elementAt( (int)Math.round(Math.random() * (options.size()-1) ) );
					output = move.toString();
				}
			}
		}
		
		// -------
		// PASS #3
		// check to see if there are any continents that can be broken
		// a continent should be "broken" when it's owned completely by another 
		// player and we should "break" it to disrupt his bonus
		// -------
		
		Vector continentsToBreak = GetContinentsToBreak(player);
		String tmp = null;
		
		//Attempt to find a path to the continent - distance of 1, then 2, then 3 away.
		if (continentsToBreak != null) {
			outer: for (int q=1; q<4; q++) { 
				for (int i=0; i<continentsToBreak.size(); i++) {
					for (int j=0; j<t.size(); j++) {
						Vector tNeighbors = ((Country)t.get(j)).getNeighbours();
						for (int k=0; k<tNeighbors.size(); k++) {
							//Fight to the death on the last step of breaking a continent bonus
							if (  (((Country)t.get(j)).getArmies()-1 > ((Country)tNeighbors.get(k)).getArmies() 
									|| (q==1 && ((Country)t.get(j)).getArmies() > 1)) &&
									ShortPathToContinent((Continent)continentsToBreak.get(i), (Country)t.get(j), (Country)tNeighbors.get(k), q)  ) {
								tmp = "attack " + ((Country)t.get(j)).getColor() + " " + ((Country)tNeighbors.get(k)).getColor();
								break outer;
							}

						}
					}
				}
			}
		}

		// if we found a nation that can be used to break an enemy's continent
		// we give it the precedence
		if (tmp != null)
			output = tmp;

		// -------
		// PASS #4
		// check to see if there are any players to eliminate
		// -------

		Vector<Player> players = game.getPlayers();
		Vector cankill = new Vector();

		// a player is considered "killable" if he has < 4 territories
		for (int i=0; i<players.size(); i++) {
			if (( (Player) players.elementAt(i)).getTerritoriesOwnedSize() < 4 && ( (Player) players.elementAt(i)) != player)
				cankill.addElement((Player) players.elementAt(i));
		}

		// check who we can kill with our armies
		if (cankill.size() > 0) {
			for (int i=0; i<cankill.size(); i++) {
				options = targetTerritories( ((Player)cankill.elementAt(i)).getTerritoriesOwned()  );
				options = filterAttacks(options, -2);
				if (options.size() > 0) {
					move = (Attack) options.elementAt( (int)Math.round(Math.random() * (options.size()-1) ) );
					output = move.toString();
					}
			}
		}

		if ( output == null ) {
			output="endattack";
		}
		
		return output;

	}

	public String getRoll() {

		String output;

		int n=((Country)game.getAttacker()).getArmies() - 1;


		/* Only roll for as long as while attacking player has more armies than defending player */
		int m=((Country)game.getDefender()).getArmies();

		// If we are trying to eliminate a player, fight longer.
		if (game.getDefender().getOwner().getTerritoriesOwnedSize() < 4)
			m -= 3;

		//If we are trying to break a continent bonus, fight to the death.
		if (game.getDefender().getContinent().isOwned(game.getDefender().getOwner()))
			m = 0;

		if (n > 3 && n > m) {
			output= "roll "+3;
		}
		else if (n > 0 && n <= 3 && n > m) {
			output= "roll "+n;
		}
		else {
			output= "retreat";
		}

		return output;

	}


	/**
	 * Checks if the player owns most of the territories within a continent
	 * @param p player object
	 * @return boolean True if the player owns most of the territories within a continent,
	 * otherwise false if the player does not own most of the territories
	 */
	public boolean mostlyOwned(Continent w) {
		int ownedByPlayer=0;
		Vector territoriesContained = w.getTerritoriesContained();

		/*
	// Extract into callable function for reuse
	for (int c=0; c< territoriesContained.size() ; c++) {
	    if ( ((Country)territoriesContained.elementAt(c)).getOwner() == player ) {
		ownedByPlayer++;
	    }
	} */
		ownedByPlayer = countTerritoriesOwned(territoriesContained, player);

		if ( ownedByPlayer>=(territoriesContained.size()/2) ) {
			return true;
		}
		else {
			return false;
		}

	}

	/**
	 * @name countTerritoriesOwned 
	 * @param t Vector of Territories
	 * @param p player object
	 * @return count of territories in vector t owned by player p
	 */
	public int countTerritoriesOwned(Vector t, Player p){
		int count=0;
		for (int i=0; i<t.size(); i++){
			if ( ((Country)t.elementAt(i)).getOwner() == p) { count++; }
		}
		return count;
	}


	/************
	 * @name targetTerritories
	 * @param t Vector of teritories you wish to obtain
	 * @return a Vector of possible attacks for a given list of territories
	 * 	(complement function to getPossibleAttacks())
	 **************/
	public Vector targetTerritories(Vector t){
		Vector output = new Vector();
		Vector n=new Vector();
		Country source,target;
		for (int a=0; a< t.size() ; a++) {
			target=(Country)t.elementAt(a);
			if ( target.getOwner() != player ) {
				n = target.getNeighbours();
				for (int b=0; b< n.size() ; b++) {
					source=(Country)n.elementAt(b);
					if ( source.isNeighbours(target) && source.getOwner() == player && source.getArmies() > 1) {     // simplify logic
						//output.add( "attack " + source.getColor() + " " + target.getColor() );
						output.add(new Attack(source,target));
					}
				}
			}
		}
		return output;
	}

	/**
	 * Checks if the player can make a valid tactical move
	 * @param p player object, b Country object
	 * @return Country if the tactical move is valid, else returns null.
	 */
	public Country check1(Country b) {

		Vector neighbours = b.getNeighbours();
		Country c = null;

		for (int i=0; i<neighbours.size(); i++) {
			if ( ownsNeighbours( (Country)neighbours.elementAt(i)) == false && ((Country)neighbours.elementAt(i)).getOwner() == player)
				return (Country)neighbours.elementAt(i);
		}
		return c;
	}

	/**
	 * Checks if the player can make a valid tactical move
	 * @param p player object, b Country object
	 * @return booelean True if the tactical move is valid, else returns false.
	 */
	public boolean check2(Country b) {

		Vector neighbours = b.getNeighbours();
//		Country c = null;

		for (int i=0; i<neighbours.size(); i++) {
			if ( ownsNeighbours( (Country)neighbours.elementAt(i)) == false && ((Country)neighbours.elementAt(i)).getOwner() == player)
				return true;
		}
		return false;
	}


	/**
	 * Attempts to block an opposing player gaining a continent during the initial placement
	 * @param p player object
	 * @return String name if a move to block the opponent is required/possible, else returns null
	 */
	public String blockOpponent(Player p) {
		Continent[] continents = game.getContinents();
		Vector players = game.getPlayers();

		for (int i=0; i<players.size(); i++) {
			for (int j=0; j<continents.length; j++) {
				if ( almostOwned((Player) players.elementAt(i), continents[j] ) == true
						&& continents[j].isOwned( (Player)players.elementAt(i) ) == false
						&& (Player) players.elementAt(i) != p ) {
					Vector v = continents[j].getTerritoriesContained();
					for (int k=0; k<v.size(); k++) {
						if ( ((Country) v.elementAt(k)).getOwner() == null) {
							return ((Country) v.elementAt(k)).getColor()+"";
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Attempts to block an opposing player gaining a continent during the actual game
	 * @param p player object
	 * @return String name if a move to block the opponent is required/possible, else returns null
	 */
	public String keepBlocking(Player p) {
		Continent[] continents = game.getContinents();
		Vector players = game.getPlayers();

		for (int i=0; i<players.size(); i++) {
			for (int j=0; j<continents.length; j++) {
				if ( almostOwned((Player) players.elementAt(i), continents[j]) == true
						&& continents[j].isOwned((Player) players.elementAt(i)) == false
						&& (Player) players.elementAt(i) != p ) {
					Vector v = continents[j].getTerritoriesContained();
					for (int k=0; k<v.size(); k++) {
						if ( ((Country) v.elementAt(k)).getOwner() == p && ((Country) v.elementAt(k)).getArmies() < 5) {
							return ((Country) v.elementAt(k)).getColor()+"";
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Attempts to free a continent from an enemy player if it is possible to do so
	 * @param p player object
	 * @return Sring name is a move to free a continent is required/possible, else returns null
	 */
	public String freeContinent(Player p) {
		Vector continentsToBreak = GetContinentsToBreak(p);
		Vector t = p.getTerritoriesOwned();
		for (int q=1; q<4; q++) {
			for (int k=0; k<continentsToBreak.size(); k++) {
				for (int i=0; i<t.size(); i++) {
					Vector tNeighbors = ((Country)t.get(i)).getNeighbours();
					for (int j=0; j<tNeighbors.size(); j++) {
						if ( //((Country)t.get(i)).getArmies() + p.getExtraArmies() - 1 > ((Country)tNeighbors.get(j)).getArmies() && 
								ShortPathToContinent((Continent)continentsToBreak.get(k), (Country)t.get(i), (Country)tNeighbors.get(j), q))
							return ((Country)t.get(i)).getColor()+"";
					}
				}
			}
		}

		return null;
	}

	/**
	 * Checks if the player owns almost all of the territories within a continent
	 * @param p player object
	 * @return booelan True if the player owns almost all of the territories within a continent,
	 * otherwise false if the player does not own most of the territories
	 */
	public boolean almostOwned(Player p, Continent co) {

		int ownedByPlayer=0;
		Vector territoriesContained = co.getTerritoriesContained();

		for (int c=0; c< territoriesContained.size() ; c++) {

			if ( ((Country)territoriesContained.elementAt(c)).getOwner() == p ) {
				ownedByPlayer++;
			}

		}

		if ( ownedByPlayer>=(territoriesContained.size()-2) ) {
			return true;
		}
		else {
			return false;
		}

	}

	/**
	 * Checks whether a country owns its neighbours
	 * @param p player object, c Country object
	 * @return boolean True if the country owns its neighbours, else returns false
	 */
	public boolean ownsNeighbours(Country c) {
		Vector neighbours = c.getNeighbours();
		int count = 0;

		for (int i=0; i<neighbours.size(); i++) {
			if ( ((Country) neighbours.elementAt(i)).getOwner() == player)
				count++;
		}

		if (count == neighbours.size())
			return true;

		return false;
	}

	/**
	 * Checks if a player is still playing the game
	 * @param p player object
	 * @return booelan True if player is present, else return false
	 */
	public boolean playerKilled(Player p) {
		Vector play = game.getPlayers();

		for (int i=0; i<play.size(); i++) {
			if ( (Player) play.elementAt(i) == p)
				return true;
		}
		return false;
	}


	/**
	 * Checks if a continent has a free territory
	 * @param ct Vector of countries in the continent
	 * @return boolean true if there is a free country, otherwise returns false
	 */
	public boolean hasFreeTerritories(Vector ct) {
		for (int i=0; i<ct.size(); i++)
			if (((Country) ct.elementAt(i)).getOwner() == null)
				return true;
		return false;
	}

	/**
	 * Checks for continents that are owned by a single player which is not the active player
	 * @param player Player
	 * @return Vector containing all of the continents with one owner, which is not the active player.  null
	 * if none exist.
	 */
	public Vector GetContinentsToBreak(Player player) {
		Continent[] continents = game.getContinents();
		//sort the continents based on worth
		for (int i=0; i<continents.length-1; i++) {
			for (int j=0; j<continents.length-1; j++) {
				if (continents[j].getArmyValue() < continents[j+1].getArmyValue()) {
					Continent tmp = continents[j];
					continents[j] = continents[j+1];
					continents[j+1] = tmp;
				}
			}
		}

//		Vector players = game.getPlayers();
		Vector continentsToBreak = new Vector();
		Player owner;
		for (int i=0; i<continents.length; i++) {
			/*
		for (int j=0; j<players.size(); j++) {
    			if (!((Player)players.get(j)).equals(player) && continents[i].isOwned((Player)players.get(j))) {
    				continentsToBreak.add(continents[i]);
    			//	System.out.println("Continent to break: " + continents[i]);
    			}
    		} */
			// replace with new method getOwner()
			owner=continents[i].getOwner();
			if (owner != null && owner != player ){
				continentsToBreak.add(continents[i]);
			}
		}
		return continentsToBreak;

	}

	/**
	 * Orders the players other than the active player in order from greatest to least
	 * @param player Player
	 * @return Player[] with the players ordered from greatest to least by (territiores + armies).
	 */
	public Player[] OrderPlayers(Player player) {
		Vector players = game.getPlayers();
		Player[] orderedPlayers = new Player[players.size()-1];
		int num = 0;
		for (int i=0; i<players.size(); i++) {
			if (  !((Player)players.get(i)).equals(player) )
				orderedPlayers[num++] = (Player)players.get(i);
		}

		//Simple Bubble Sort to sort the players in order.    	
		for (int i=0; i<orderedPlayers.length-1; i++) {
			for (int j=0; j<orderedPlayers.length-1; j++) {
				if (orderedPlayers[j].getTerritoriesOwnedSize() + orderedPlayers[j].getNoArmies() <
						orderedPlayers[j+1].getTerritoriesOwnedSize() + orderedPlayers[j+1].getNoArmies()) {
					Player tmp = orderedPlayers[j];
					orderedPlayers[j] = orderedPlayers[j+1];
					orderedPlayers[j+1] = tmp;
				}
			}
		}

		return orderedPlayers;
	}

	/**
	 * Determines if a short path exists to the continent through that country
	 * @param Continent cont, Country attackFrom, Country attackThrough, int acceptableDistance
	 * @return boolean of true if there is a path existing, and false otherwise
	 */
	// does not take into account troops of countries on path.
	public boolean ShortPathToContinent(Continent cont, Country attackFrom, Country attackThrough, int acceptableDistance) {
		//Countries are not valid for attacking
		if (!attackFrom.isNeighbours(attackThrough) || attackThrough.getOwner().equals(attackFrom.getOwner()))
			return false;

		if (acceptableDistance <= 0 && !attackFrom.getContinent().equals(cont))
			return false;
		else if (attackFrom.getContinent().equals(cont))
			return true;
		else if (acceptableDistance > 0 && attackThrough.getContinent().equals(cont))
			return true;
		else {
			Vector throughNeighbors = attackThrough.getNeighbours();
			for (int i=0; i<throughNeighbors.size(); i++) {
				if (ShortPathToContinent(cont, attackThrough, (Country)throughNeighbors.get(i), acceptableDistance-1  )  )
					return true;
			}
		}

		return false;

	}

	public String NextToEnemyToEliminate() {
		
		// picks the players with < 4 countries
		Vector weakPlayers = new Vector();
		for (int i=0; i<game.getPlayers().size(); i++) {
			if (((Player)game.getPlayers().get(i)).getTerritoriesOwnedSize() < 4)
				weakPlayers.add(game.getPlayers().get(i));
		}
		if (weakPlayers.size() == 0)
			return null;
		
		// collects their territories
		Vector t = player.getTerritoriesOwned();
		Vector targetCountries = new Vector();
		for (int i=0; i<weakPlayers.size(); i++) {
			for (int j=0; j<((Player)weakPlayers.get(i)).getTerritoriesOwnedSize(); j++) {
				targetCountries.add(((Player)weakPlayers.get(i)).getTerritoriesOwned().get(j));
			}
		}
		
		// select the first neighboring country from the list 
		for (int i=0; i<t.size(); i++) {
			for (int j=0; j<targetCountries.size(); j++) {
				if (((Country)t.get(i)).isNeighbours((Country)targetCountries.get(j)))
					return ((Country)t.get(i)).getColor() + "";
			}
		}

		return null;
	}


}
