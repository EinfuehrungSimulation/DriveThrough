package entity.schalter;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import model.DriveThrough;
import model.Manager;
import model.Manager.ShowInReport;
import desmoj.core.dist.ContDistNormal;
import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.TimeSpan;
import desmoj.core.statistic.Histogram;
import desmoj.extensions.visualization2d.animation.Form;
import desmoj.extensions.visualization2d.animation.FormExt;
import desmoj.extensions.visualization2d.animation.Position;
import desmoj.extensions.visualization2d.animation.core.simulator.QueueAnimation;
import desmoj.extensions.visualization2d.animation.core.statistic.CountAnimation;
import desmoj.extensions.visualization2d.animation.internalTools.EntityTypeAnimation;
import entity.car.Auto;

public abstract class Schalter extends Entity{

	protected CountAnimation 				rejected;
	protected ContDistNormal 				wartezeit;

	private QueueAnimation<Auto>			queue;
	private DriveThrough 					owner;
	private Position 						position;
	private ProcessEvent 					beginEvent;
	public boolean targetIsFull;
	private static Histogram histo;
	private int length; 
	

	public Schalter(DriveThrough owner, String name,
			EntityTypeAnimation entity, Position pos, double mean, double stdDev, boolean bHorizontal, int length){
		super(owner, name, true);
		this.owner = owner;
		this.position = pos;
		this.length = length;
		this.beginEvent = new ProcessEvent(name+"Event");
		this.wartezeit = new ContDistNormal(owner, name+" Normalverteilung", mean, stdDev , Manager.showInReport(ShowInReport.EXTENDED_REPORT), Manager.TRACE);
		wartezeit.setNonNegative(true);
		initAnimation(bHorizontal, length, entity);
	}
	
	public static void init(DriveThrough owner){
		histo = new Histogram(owner, "Unzufriedene Kunden gesamt", Manager.OPENING,Manager.CLOSING,Manager.CLOSING-Manager.OPENING, Manager.showInReport(ShowInReport.MINIMAL_REPORT), Manager.TRACE);
	}
	/**
	 * Perform action after time got consumed
	 */
	public abstract void doAfterCarProcessed(Auto auto);

	/**
	 * Perform actions before time get consumed. Determine the time to consume
	 * @param auto
	 * @return
	 */
	public abstract TimeSpan process(Auto auto);
	
	public void start(){
		Auto auto = queue.first();
		TimeSpan processTime = process(auto);
			auto.stopWaiting();
			beginEvent.schedule(auto, processTime);
	}

	public boolean insert(Auto auto) {
		auto.waitAt(this);
		if(queue.isEmpty()){
			queue.insert(auto);
			start();
			return true;
		}
		return queue.insert(auto);
	}
	
	protected Position getPosition() {
		return position;
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public boolean isFull(){
		boolean b = length<=queue.size();
		return b;
	}
	public String getName(){
		return super.getName();
	}
	protected DriveThrough getDriveThrough() {
		return owner;
	}
	
	class ProcessEvent extends Event<Auto> {

		public ProcessEvent(String name) {
			super(owner, name, true);
		}

		@Override
		public void eventRoutine(Auto auto) {
			if(queue.contains(auto))
				queue.remove(auto);
			doAfterCarProcessed(auto);				
		}
	}

	
	public void reject(Auto auto){
		histo.update(presentTime().getTimeAsCalender().get(Calendar.HOUR_OF_DAY));
		rejected.update();
		if(queue.contains(auto))
			queue.remove(auto);
		auto.disposeAnimation();
	}
	
	private void initAnimation(boolean bHorizontal, int length, EntityTypeAnimation entity){
		queue = new QueueAnimation<Auto>(owner, getName(), 0, length, Manager.showInReport(ShowInReport.MINIMAL_REPORT), Manager.TRACE);
		queue.createAnimation(position, new FormExt(bHorizontal, length, entity.getId()), true);
		rejected = new CountAnimation(owner, "Kunde verlässt "+getName(), Manager.showInReport(ShowInReport.NORMAL_REPORT), Manager.TRACE);
		rejected.createAnimation(
				new Position(-350 + (int) Math.round(position.getPoint().getX()),
						(int) Math.round(position.getPoint().getX())), new Form(20,
						30), true);
	}

	public TimeSpan getRndTime() {
		return wartezeit.sampleTimeSpan(TimeUnit.SECONDS);
	}

	public boolean contains(Auto auto) {
		return queue.contains(auto);
	}
}
