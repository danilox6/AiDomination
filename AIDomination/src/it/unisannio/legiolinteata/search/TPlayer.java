package it.unisannio.legiolinteata.search;

import net.yura.domination.engine.core.Player;


//FIXME Utile solo per una maggiore leggibilità, altrimenti si potrebbe usare direttamente l'Integer del color
public class TPlayer  {

	private int color;
	
	public TPlayer(Player player) {
		color = player.getColor();
	}

	public int getColor() {
		return color;
	}

}
