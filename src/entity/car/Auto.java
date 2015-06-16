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
	private int orderVal;
	private boolean disposed = false;

	public Auto(ModelAnimation owner) {
		super(owner, NAME+id, true);
		id++;
		orderVal = order.sample().intValue();
		waitStart = presentTime();
	}

	public static void init(Model owner, long maxValue, double mean, double standardDeviation){
		order = new DiscreteDistUniform(owner, "Kunden Bestellungsverteilung", 0, maxValue-1, Manager.showInReport(ShowInReport.EXTENDED_REPORT), Manager.TRACE);
		order.setDescription("Gleichverteilung die die Bestellung der Kunden darstellt");
		patience = new ContDistNormal(owner, "Kunden Geduld", mean, standardDeviation, Manager.showInReport(ShowInReport.MINIMAL_REPORT), Manager.TRACE);
		patience.setDescription("Zeitspanne bevor der Kunde die Warteschlange unzufrieden verlässt");
		waitingTime = new Accumulate(owner, NAME+" Wartezeit", Manager.showInReport(ShowInReport.MINIMAL_REPORT), Manager.TRACE);
		waitingTime.setDescription("Gesamte Zeitspanne die im DriveIn verbracht wurde");
	}
	
	public int getOrder() {
		return orderVal;
	}

	public void waitAt(Schalter schalter) {
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
			}
		}
	}

	protected Auto getAuto() {
		return this;
	}
	@Override
	public void disposeAnimation() {
		if(!disposed){
			this.disposed = true;
			super.disposeAnimation();
			waitingTime.update(new TimeSpan(presentTime().getTimeAsDouble() - waitStart.getTimeAsDouble()));
		}
	}

	public boolean isDisposed() {
		return disposed;
	}
}
