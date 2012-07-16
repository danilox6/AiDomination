package it.unisannio.legiolinteata.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.yura.domination.engine.core.AbstractPlayer;
import net.yura.domination.engine.core.AbstractRiskGame;
import net.yura.domination.engine.core.Card;
import net.yura.domination.engine.core.Mission;

import aima.core.agent.State;

@SuppressWarnings({"rawtypes", "unchecked"})
public class GameState implements State, Cloneable, AbstractRiskGame<TreePlayer, TreeCountry, TreeContinent>{
	
	private int currentPlayerIndex;
	private AbstractRiskGame absGame;
	private Vector<TreePlayer> players;
	private TreeCountry[] countries;
	private TreeContinent[] continents;
	
	public GameState( TreePlayer[] players, AbstractRiskGame game) {
		this.absGame = game;

		this.players = new Vector<TreePlayer>();
		for(int i = 0; i < game.getPlayers().size();i++){
			AbstractPlayer p = (AbstractPlayer) game.getPlayers().get(i);
			TreePlayer player = new TreePlayer(p, this);
			players[i] = player;
			this.players.add(player);
			if (p.getColor() == game.getCurrentPlayer().getColor())
				currentPlayerIndex = i;
		}
		
		countries = new TreeCountry[game.getCountries().length];
		continents = new TreeContinent[game.getContinents().length];
		
		for(int i = 0; i < continents.length ; i++)
			continents[i] = new TreeContinent(game.getContinents()[i] ,this);
			
		for(int i = 0; i < countries.length ; i++)
			countries[i] = new TreeCountry(game.getCountries()[i], this);
	}

	private GameState(int currentPlayer, AbstractRiskGame absGame, Vector<TreePlayer> players, TreeCountry[] countries, TreeContinent[] continents) {
		this.currentPlayerIndex = currentPlayer;
		this.absGame = absGame;
		this.players = players;
		this.countries = countries;
		this.continents = continents;
	}

	public List<Integer> getFreeCountriesColor(){
		List<Integer> freeCountries = new ArrayList<Integer>();
		for(TreeCountry c: countries)
			if (c.getArmies() == 0)
				freeCountries.add(c.getColor());
		
		return freeCountries;
	}
	
	public void place(PlacementAction placamentAction){
		TreeCountry country =  getCountryInt(placamentAction.getCountryToOccupy());
		TreePlayer player = null;
		int playerIndex = -1;
		for(int i = 0; i<players.size(); i++){
			if (players.get(i).getColor() == placamentAction.getOccupyingPlayer()){
				player = players.get(i);
				playerIndex = i;
				break;
			}
		}
		country =  country.place(placamentAction.getArmies(), player);
		player = player.placeOnCountry(country, placamentAction.getArmies());
		
		players.remove(playerIndex);
		players.add(playerIndex, player);
		countries[country.getColor()-1] = country;
		
		updateState();
		
	}
	
	private void updateState() {

		for(int i = 0; i<continents.length; i++)
			continents[i] = continents[i].setGameState(this);
		
		for(int i = 0; i<countries.length; i++)
			countries[i] = countries[i].setGameState(this);
		
		Vector<TreePlayer> temp = new Vector<TreePlayer>();
		for(TreePlayer p: players)
			temp.add(p.setGameState(this));
		
		players = temp;
		
	}

	/**
	 * Aggiorna il giocatore corrente
	 */
	public void updateCurrentPlayerIndex(){
		currentPlayerIndex = ++currentPlayerIndex % players.size();
	}
	
	public int getCurrentPlayerIndex(){
		return currentPlayerIndex;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new GameState(currentPlayerIndex, absGame, (Vector<TreePlayer>) players.clone(), countries.clone(), continents.clone());
	}

//	public int[] getCountryOwners() {
//		return countryOwners;
//	}

	@Override
	public int getTradeAbsValue(String c1, String c2, String c3, int cardMode) {
		return absGame.getTradeAbsValue(c1, c2, c3, cardMode);
	}

	@Override
	public boolean canTrade() {
		return absGame.canTrade();
	}

	@Override
	public boolean NoEmptyCountries() {
		for(TreeCountry c: countries)
			if(c.getOwner()==null)
				return false;
		return true;
	}

	@Override
	public boolean getTradeCap() {
		return absGame.getTradeCap();
	}

	@Override
	public TreePlayer getCurrentPlayer() {
		return players.get(currentPlayerIndex);
	}

	@Override
	public Vector<TreePlayer> getPlayers() {
		return players;
	}

	@Override
	public TreeCountry getAttacker() {
		return getCountryInt(absGame.getAttacker().getColor());
	}

	@Override
	public Vector<Card> getCards() {
		return absGame.getCards();
	}

	@Override
	public int getNumberContinentsOwned(TreePlayer player) {
		int owned=0;
		for (TreeContinent c: continents)
			if(c.getOwner().getColor() == player.getColor())
				owned++;
		return owned;
	}

	@Override
	public TreeCountry getCountryInt(int color) {
		return countries[color-1];
	}

	@Override
	public Card[] getCards(String name1, String name2, String name3) {
		return absGame.getCards(name1, name2, name3);
	}

	@Override
	public Card findCard(String name) {
		return absGame.findCard(name);
	}

	@Override
	public int getNoPlayers() {
		return players.size();
	}

	@Override
	public TreeCountry[] getCountries() {
		return countries;
	}

	@Override
	public TreeContinent[] getContinents() {
		return continents;
	}

	@Override
	public int getNoCountries() {
		return countries.length;
	}

	@Override
	public int getNoContinents() {
		return continents.length;
	}

	@Override
	public Vector<Mission> getMissions() {
		return absGame.getMissions();
	}

	@Override
	public int getNoMissions() {
		return absGame.getNoMissions();
	}

	@Override
	public int getNoCards() {
		return absGame.getNoCards();
	}

	@Override
	public int getCardMode() {
		return absGame.getCardMode();
	}

	@Override
	public boolean checkTrade(Card card1, Card card2, Card card3) {
		return absGame.checkTrade(card1, card2, card3);
	}

	@Override
	public TreeCountry getDefender() {
		return getCountryInt(absGame.getDefender().getColor());
	}

	@Override
	public int getMaxDefendDice() {
		return absGame.getMaxDefendDice();
	}

	@Override
	public boolean getSetup() {
		for(TreePlayer p : players)
			if(p.getExtraArmies()!=0)
				return false;
		return true;
	}

	@Override
	public int getMustMove() {
		return absGame.getMustMove();
	}
	
	/**
	 * Restituisce il player con il color dato
	 * @param color
	 * @return
	 */
	public TreePlayer getPlayerInt(int color){
		for(TreePlayer p : players){
			if (p.getColor() == color)
				return p;
		}
		return null;
	}
	
	public TreeContinent getContinentInt(int color){
		for(TreeContinent c : continents){
			if (c.getColor() == color)
				return c;
		}
		return null;
	}
	
	public int getPlayerIndex(int color, TreePlayer player){
		for(int i = 0; i<players.size(); i++){
			if (players.get(i).getColor() == color){
				player = players.get(i);
				return i;
			}
		}
		return -1;
	}
	
	public String dump(boolean country){
		String dump = "";
		dump += "Players:\n";
		for(TreePlayer p: players){
			dump += p.getName() +"("+ p.getColor()+ ") owenedCountries: \n";
			Vector<TreeCountry> owned = p.getTerritoriesOwned();
			for(TreeCountry c: owned)
				dump += c.getName()+"\n";
			dump += "\n";
		}
		if(country){
			dump+="Countries:\n";
			for(TreeCountry c: countries){
				String owner = c.getOwner()==null?"null": c.getOwner().getName();
				dump += c.getName() +" owner: " +owner +"\n" ;
			}
		}
 		return dump;
	}
}
