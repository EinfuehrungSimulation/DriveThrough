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
import entity.schalter.Counter;

public class Customer extends EntityAnimation{

	private static DiscreteDistUniform order;
	private static ContDistNormal patience;
	private static Accumulate waitingTime;
	
	private static final String NAME = "Auto";
	private static int id=0;
	private boolean waiting;
	private TimeInstant waitStart;
	private int orderVal;
	private boolean disposed = false;

	public Customer(ModelAnimation owner) {
		super(owner, NAME+id, true);
		id++;
		orderVal = order.sample().intValue();
		waitStart = presentTime();
	}
	/**
	 * Initialize static distributions and reportables used by all cars
	 * @param owner
	 * @param orderCount : used to initialize the distribution which is used to choose the order
	 * @param mean
	 * @param standardDeviation
	 */
	public static void init(Model owner, long orderCount, double mean, double standardDeviation){
		order = new DiscreteDistUniform(owner, "Kunden Bestellungsverteilung", 0, orderCount-1, Manager.showInReport(ShowInReport.EXTENDED_REPORT), Manager.TRACE);
		order.setDescription("Gleichverteilung die die Bestellung der Kunden darstellt");
		patience = new ContDistNormal(owner, "Kunden Geduld", mean, standardDeviation, Manager.showInReport(ShowInReport.MINIMAL_REPORT), Manager.TRACE);
		patience.setDescription("Zeitspanne bevor der Kunde die Warteschlange unzufrieden verlässt");
		waitingTime = new Accumulate(owner, NAME+" Wartezeit", Manager.showInReport(ShowInReport.MINIMAL_REPORT), Manager.TRACE);
		waitingTime.setDescription("Gesamte Zeitspanne die im DriveIn verbracht wurde");
	}
	
	public int getOrder() {
		return orderVal;
	}
	/**
	 * Start waiting
	 * @param schalter
	 */
	public void waitAt(Counter schalter) {
		new WaitingEvent(getModel()).schedule(schalter, patience.sampleTimeSpan(TimeUnit.MINUTES));
	}
	/**
	 * This event takes care of removing cars that are waiting too long
	 * @author sascha
	 *
	 */
	class WaitingEvent extends Event<Counter>{

		public WaitingEvent(Model owner) {
			super(owner, NAME+id+"WaitingEvent",true);
		}

		@Override
		public void eventRoutine(Counter schalter) {
			if(waiting&&schalter.contains(getAuto())){
				schalter.reject(getAuto());
			}
		}
	}

	protected Customer getAuto() {
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
