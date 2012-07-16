package it.unisannio.ai;

import it.unisannio.legiolinteata.advisor.AttackAdvisor;
import it.unisannio.legiolinteata.advisor.FortificationAdvisor;
import net.yura.domination.engine.ai.Discoverable;
import net.yura.domination.engine.ai.commands.Attack;
import net.yura.domination.engine.ai.commands.Fortification;

@Discoverable
public class AIFuzzy extends AISimple2 {

	@Override
	protected Attack onAttack() {
        AttackAdvisor aa = new AttackAdvisor(game, player);
        Attack best = aa.getBestAdvice(5.0);
        //if(best != null)
        //	System.out.println(best.getOrigin() + "-->" + best.getDestination());
        return best;
	}

	@Override
	protected Fortification onFortification() {
		FortificationAdvisor fa = new FortificationAdvisor(game, player);
		Fortification best = fa.getBestAdvice(Double.NEGATIVE_INFINITY);
		return best;
	}
}
