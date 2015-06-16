package entity.car;

import java.util.concurrent.TimeUnit;

import desmoj.core.dist.ContDistNormal;
import desmoj.core.dist.DiscreteDistUniform;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import desmoj.extensions.visualization2d.animation.core.simulator.EntityAnimation;
import desmoj.extensions.visualization2d.animation.core.simulator.ModelAnimation;
import entity.schalter.Schalter;

public class Auto extends EntityAnimation{

	private static DiscreteDistUniform order;
	private static ContDistNormal patience;
	private static final String NAME = "Auto";
	private static int id=0;
	private boolean waiting;

	public Auto(ModelAnimation owner) {
		super(owner, NAME+id, true);
		id++;
	}

	public static void init(Model owner, long maxValue, double mean, double standardDeviation){
		order = new DiscreteDistUniform(owner, "Order Distribution", 0, maxValue-1, true, true);
		patience = new ContDistNormal(owner, NAME+id+patience, mean, standardDeviation, true, true);
	}
	
	public int getOrder() {
		return order.sample().intValue();
	}

	public void waitAt(Schalter schalter) {
		waiting = true;
		new WaitingEvent(getModel()).schedule(schalter, patience.sampleTimeSpan(TimeUnit.MINUTES));
	}
	
	class WaitingEvent extends Event<Schalter>{

		public WaitingEvent(Model owner) {
			super(owner, NAME+id+"WaitingEvent",true);
		}

		@Override
		public void eventRoutine(Schalter schalter) {
			if(waiting&&schalter.contains(getAuto()))
				schalter.reject(getAuto());
		}
	}

	protected Auto getAuto() {
		return this;
	}

	public void stopWaiting() {
		this.waiting = false;
	}
}
