package entity.car;

import desmoj.core.dist.DiscreteDistUniform;
import desmoj.core.simulator.Model;
import desmoj.extensions.visualization2d.animation.core.simulator.EntityAnimation;
import desmoj.extensions.visualization2d.animation.core.simulator.ModelAnimation;

public class Auto extends EntityAnimation{

	private static DiscreteDistUniform order;
	private static final String NAME = "Auto";
	private static int id=0;

	public Auto(ModelAnimation owner) {
		super(owner, NAME+id, true);
		id++;
	}

	public static void init(Model owner, long maxValue){
		order = new DiscreteDistUniform(owner, "Order Distribution", 0, maxValue-1, true, true);
	}
	
	public int getOrder() {
		return order.sample().intValue();
	}
}
