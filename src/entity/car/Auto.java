package entity.car;

import java.util.concurrent.TimeUnit;

import model.Manager;
import model.Manager.ShowInReport;
import desmoj.core.dist.ContDistNormal;
import desmoj.core.dist.DiscreteDistUniform;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;
import desmoj.core.statistic.Accumulate;
import desmoj.extensions.visualization2d.animation.core.simulator.EntityAnimation;
import desmoj.extensions.visualization2d.animation.core.simulator.ModelAnimation;
import entity.schalter.Schalter;

public class Auto extends EntityAnimation{

	private static DiscreteDistUniform order;
	private static ContDistNormal patience;
	private static Accumulate waitingTime;
	
	private static final String NAME = "Auto";
	private static int id=0;
	private boolean waiting;
	private TimeInstant waitStart;
	private TimeSpan timeWaited;
	private int orderVal;

	public Auto(ModelAnimation owner) {
		super(owner, NAME+id, true);
		id++;
		timeWaited = new TimeSpan(0);
		orderVal = order.sample().intValue();
	}

	public static void init(Model owner, long maxValue, double mean, double standardDeviation){
		order = new DiscreteDistUniform(owner, "Order Distribution", 0, maxValue-1, Manager.showInReport(ShowInReport.NORMAL_REPORT), Manager.TRACE);
		patience = new ContDistNormal(owner, NAME+id+patience, mean, standardDeviation, Manager.showInReport(ShowInReport.MINIMAL_REPORT), Manager.TRACE);
		waitingTime = new Accumulate(owner, NAME+" Wartezeit", Manager.showInReport(ShowInReport.MINIMAL_REPORT), Manager.TRACE);
	}
	
	public int getOrder() {
		return orderVal;
	}

	public void waitAt(Schalter schalter) {
		waiting = true;
		waitStart = presentTime();
		new WaitingEvent(getModel()).schedule(schalter, patience.sampleTimeSpan(TimeUnit.MINUTES));
	}
	
	class WaitingEvent extends Event<Schalter>{

		public WaitingEvent(Model owner) {
			super(owner, NAME+id+"WaitingEvent",true);
		}

		@Override
		public void eventRoutine(Schalter schalter) {
			if(waiting&&schalter.contains(getAuto())){
				schalter.reject(getAuto());
				timeWaited = new TimeSpan(presentTime().getTimeAsDouble()-waitStart.getTimeAsDouble());
			}
		}
	}

	protected Auto getAuto() {
		return this;
	}
	@Override
	public void disposeAnimation() {
		super.disposeAnimation();
		waitingTime.update(timeWaited);
	}

	public void stopWaiting() {
		timeWaited = new TimeSpan(timeWaited.getTimeAsDouble()+presentTime().getTimeAsDouble()-waitStart.getTimeAsDouble());
	}
}
