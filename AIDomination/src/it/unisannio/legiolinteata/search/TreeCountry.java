package it.unisannio.legiolinteata.search;

import java.util.Vector;

import net.yura.domination.engine.core.AbstractContinent;
import net.yura.domination.engine.core.AbstractCountry;

@SuppressWarnings({"rawtypes", "unchecked"})
public class TreeCountry implements AbstractCountry<TreePlayer, TreeCountry, TreeContinent>{
	private AbstractCountry absCountry = null;
	private Vector<Integer> neighboursColors = new Vector<Integer>();
	private int ownerColor = 0 ;
	private int continentColor;
	private int armies = 0;
	private GameState gameState;
	
	 private TreeCountry(AbstractCountry absCountry, Vector<Integer> neighbours, int ownerColor, int continent, int armies, GameState gameState) {
		this.absCountry = absCountry;
		this.neighboursColors = neighbours;
		this.ownerColor = ownerColor;
		this.continentColor = continent;
		this.armies = armies;
		this.gameState = gameState;
	}

	public TreeCountry(AbstractCountry country, GameState gameState) {
		absCountry = country;
		armies = absCountry.getArmies();
		ownerColor = country.getOwner()==null? 0: country.getOwner().getColor();
		continentColor = country.getContinent().getColor();
		Vector<AbstractCountry> neighbours = country.getNeighbours();
		for(AbstractCountry c: neighbours){
			this.neighboursColors.add(c.getColor());
		}
		this.gameState = gameState;
	}

	@Override
	public Vector<TreeCountry> getNeighbours() {
		Vector<TreeCountry> neighbours = new Vector<TreeCountry>();
		for(Integer i : neighboursColors)
			neighbours.add(gameState.getCountries()[i-1]);
		return neighbours;
	}

	@Override
	public String getName() {
		return absCountry.toString();
	}

	@Override
	public int getArmies() {
		return armies;
	}

	@Override
	public TreeContinent getContinent() {
		return gameState.getContinentInt(continentColor);
	}

	@Override
	public TreePlayer getOwner() {
		return gameState.getPlayerInt(ownerColor);
	}

	@Override
	public int getColor() {
		return absCountry.getColor();
	}
	
	public TreeCountry setArmies(int armies){
		return new TreeCountry(absCountry, neighboursColors, ownerColor, continentColor, armies, gameState);
	}
	
	public TreeCountry addArmies(int armies){
		return setArmies(this.armies + armies);
	}
	
	public TreeCountry setOwner(TreePlayer owner) {
			return setOwner(owner.getColor());
	}
	
	public TreeCountry setOwner(int owner) {
		return new TreeCountry(absCountry, neighboursColors, owner, continentColor, armies, gameState);	
	}
	
	/**
	 * Aggiunge un certo numero di armate al Country assegnadone inoltre il proprietario
	 * @param armies
	 * @param owner
	 * @param gameState
	 * @return
	 */
	public TreeCountry place(int armies, TreePlayer owner){
		return new TreeCountry(absCountry, neighboursColors, owner.getColor(), continentColor, this.armies + armies, gameState);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractContinent) {
			AbstractCountry conuntry = (AbstractCountry) obj;
			if(this.getColor()==conuntry.getColor());
		}
		return false;
	}

	public GameState getGameState() {
		return gameState;
	}
	
	public TreeCountry setGameState(GameState gameState) {
		return new TreeCountry(absCountry, neighboursColors, ownerColor, continentColor, armies, gameState);
	}
}
