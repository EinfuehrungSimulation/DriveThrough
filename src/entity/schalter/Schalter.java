package entity.schalter;

import java.awt.Color;

import model.DriveThrough;
import model.State;
import desmoj.core.dist.ContDistNormal;
import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.TimeInstant;
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

	protected QueueAnimation<Auto> queue;
	protected CountAnimation 					rejected;
	protected ContDistNormal 					wartezeit;
	
	private static final String 			BASE = "SHALTER";
	private static final String 			NAME = "SHALTER";
	private static int 						width = 600;
	private DriveThrough 					owner;
	private Schalter 						target;
	private Position 						position;
	private ProcessEvent 					beginEvent;
	private DoAfterProcessedEvent 			endeEvent; 
	

	public Schalter(DriveThrough owner, Schalter target, String name, State state,
			EntityTypeAnimation entity, Position pos, double mean, double stdDev, boolean bHorizontal, int length){
		super(owner, name, true);
		this.target = target;
		this.owner = owner;
		this.position = pos;
		this.endeEvent = new DoAfterProcessedEvent();
		this.beginEvent = new ProcessEvent();
		this.wartezeit = new ContDistNormal(owner, name+" Normalverteilung", mean, stdDev , true, true);
		wartezeit.setNonNegative(true);
		initAnimation(bHorizontal, length, entity);
	}

	/**
	 * Perform Action and determine the Duration it takes to perform.
	 * @return
	 */
	public abstract TimeInstant doAfterEntityProcessed(Auto auto);
	
	public void insert(Auto auto) {
		if(queue.isEmpty()){
			queue.insert(auto);
			beginEvent.eventRoutine(auto);
		}else if(!queue.insert(auto)) {
			rejected.update();
			auto.disposeAnimation();
		}
	}
	
	protected Position getPosition() {
		return position;
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	protected DriveThrough getDriveThrough() {
		return owner;
	}
	
	class ProcessEvent extends Event<Auto> {

		public ProcessEvent() {
			super(owner, "Bestellungs-Begin-Event", true);
		}

		@Override
		public void eventRoutine(Auto auto) {
			endeEvent.schedule(auto, wartezeit.sampleTimeSpan());
		}
	}

	class DoAfterProcessedEvent extends Event<Auto>{


		public DoAfterProcessedEvent() {
			super(owner, "Bestellungs-Ende-Event", true);
		}

		@Override
		public void eventRoutine(Auto auto) {
			queue.remove(auto);
			if(target!=null)
				target.insert(auto);
			if(!queue.isEmpty()){
				TimeInstant t = doAfterEntityProcessed(auto);
				if(t==null)
					beginEvent.schedule(queue.first(),new TimeSpan(0.0));
				else
					beginEvent.schedule(queue.first(), t);
			}
		}
		
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
}
