package it.unisannio.ai;

import it.unisannio.legiolinteata.advisor.AttackAdvisor;
import net.sourceforge.jFuzzyLogic.FIS;
import net.yura.domination.engine.ai.BaseAI;
import net.yura.domination.engine.ai.Discoverable;
import net.yura.domination.engine.ai.commands.Attack;
import net.yura.domination.engine.core.AbstractCountry;
import net.yura.domination.engine.core.Country;

@Discoverable
public class AIFuzzy extends AISimple2 {

	@Override
	protected Attack onAttack() {
        AttackAdvisor aa = new AttackAdvisor(game, player);
        Attack best = aa.getBestAdvice();
        //if(best != null)
        //	System.out.println(best.getOrigin() + "-->" + best.getDestination());
        return best;
	}

}
