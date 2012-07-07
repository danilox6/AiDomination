package it.unisannio.legiolinteata.search;

import net.yura.domination.engine.core.AbstractPlayer;


//FIXME Utile solo per una maggiore leggibilità, altrimenti si potrebbe usare direttamente l'Integer del color
public class TPlayer  {

	private int color;
	
	public TPlayer(AbstractPlayer<?> player) {
		color = player.getColor();
	}

	public int getColor() {
		return color;
	}

}
