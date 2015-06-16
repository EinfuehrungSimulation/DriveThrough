package entity.schalter;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import model.DriveThrough;
import model.State;
import desmoj.core.dist.ContDistNormal;
import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.TimeSpan;
import desmoj.extensions.visualization2d.animation.BackgroundElementAnimation;
import desmoj.extensions.visualization2d.animation.Form;
import desmoj.extensions.visualization2d.animation.FormExt;
import desmoj.extensions.visualization2d.animation.Position;
import desmoj.extensions.visualization2d.animation.core.simulator.QueueAnimation;
import desmoj.extensions.visualization2d.animation.core.statistic.CountAnimation;
import desmoj.extensions.visualization2d.animation.internalTools.EntityTypeAnimation;
import entity.car.Auto;

public abstract class Schalter extends Entity{

	protected QueueAnimation<Auto>			queue;
	protected CountAnimation 				rejected;
	protected ContDistNormal 				wartezeit;
	
	private static final String 			BASE = "SHALTER";
	private static final String 			NAME = "SHALTER";
	private static int 						width = 600;
	private DriveThrough 					owner;
	private Position 						position;
	private ProcessEvent 					beginEvent;
	public boolean targetIsFull; 
	

	public Schalter(DriveThrough owner, String name, State state,
			EntityTypeAnimation entity, Position pos, double mean, double stdDev, boolean bHorizontal, int length){
		super(owner, name, true);
		this.owner = owner;
		this.position = pos;
		this.beginEvent = new ProcessEvent(name+"Event");
		this.wartezeit = new ContDistNormal(owner, name+" Normalverteilung", mean, stdDev , true, true);
		wartezeit.setNonNegative(true);
		initAnimation(bHorizontal, length, entity);
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
	
	public void start(Auto auto){
		beginEvent.schedule(auto, process(auto));
	}
	
	public boolean insert(Auto auto) {
		if(queue.isEmpty()){
			queue.insert(auto);
			start(auto);
			return true;
		}
		return 	queue.insert(auto);
	}
	
	protected Position getPosition() {
		return position;
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public boolean isFull(){
		return queue.maxLength()<=queue.size();
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
			doAfterCarProcessed(auto);				
		}
	}

	
	void reject(Auto auto){
		rejected.update();
		auto.disposeAnimation();
	}
	
	private void initAnimation(boolean bHorizontal, int length, EntityTypeAnimation entity){
		if(bHorizontal){
			new BackgroundElementAnimation(
				owner, BASE, NAME, null, 0, 0, 0, 100.0, position, new Form(
						width, length*7), java.awt.Color.GRAY, Color.WHITE, true);
		}else{
			new BackgroundElementAnimation(
					owner, BASE, NAME, null, 0, 0, 0, 100.0, position, new Form(
							length*15, width-100), java.awt.Color.GRAY, Color.WHITE, true);
		}
		queue = new QueueAnimation<Auto>(owner, getName(), 0, length, true,true);
		queue.createAnimation(position, new FormExt(bHorizontal, length, entity.getId()), true);
		rejected = new CountAnimation(owner, getName()+" rejected", true, true);
		rejected.createAnimation(
				new Position(-350 + (int) Math.round(position.getPoint().getX()),
						(int) Math.round(position.getPoint().getX())), new Form(20,
						30), true);
	}

	public TimeSpan getRndTime() {
		return wartezeit.sampleTimeSpan(TimeUnit.SECONDS);
	}
}
