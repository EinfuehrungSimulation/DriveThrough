package model.entity;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import model.DriveThrough;
import model.State;
import desmoj.core.dist.ContDistNormal;
import desmoj.core.simulator.Event;
import desmoj.extensions.visualization2d.animation.BackgroundElementAnimation;
import desmoj.extensions.visualization2d.animation.Form;
import desmoj.extensions.visualization2d.animation.FormExt;
import desmoj.extensions.visualization2d.animation.Position;
import desmoj.extensions.visualization2d.animation.core.simulator.EntityAnimation;
import desmoj.extensions.visualization2d.animation.core.simulator.QueueAnimation;
import desmoj.extensions.visualization2d.animation.core.statistic.CountAnimation;
import desmoj.extensions.visualization2d.animation.internalTools.EntityTypeAnimation;

public class Schalter {

	private static final String 			BASE = "SHALTER";
	private static final String 			NAME = "SHALTER";
	private static final String 			TEXT = null;
	private static final int 				TEXT_SIZE = 0;
	private static int 						width = 600;
	private static int 						height = 100;
	private QueueAnimation<Auto> queue;
	private CountAnimation 					rejected;
	private DriveThrough 					owner;
	private ContDistNormal 					wartezeit;
	private Schalter 						target;
	

	public Schalter(DriveThrough owner, String name, Schalter schalter, State state,
			EntityTypeAnimation entity, Position pos, double mean, double stdDev) {
		this.target = schalter;
		this.wartezeit = new ContDistNormal(owner, name+" Normalverteilung", mean, stdDev , true, true);
		this.owner = owner;
		BackgroundElementAnimation backgroundElementAnimation = new BackgroundElementAnimation(
				owner, BASE, NAME, TEXT, 0, TEXT_SIZE, 0, 100.0, pos, new Form(
						width, height), java.awt.Color.GRAY, Color.WHITE, true);
		backgroundElementAnimation.setImageId(state.getImageID());
		queue = new QueueAnimation<Auto>(owner, name, 0, 15, true,true);
		queue.createAnimation(pos, new FormExt(true, 15, entity.getId()), true);
		rejected = new CountAnimation(owner, name, true, true);
		rejected.createAnimation(
				new Position(-350 + (int) Math.round(pos.getPoint().getX()),
						(int) Math.round(pos.getPoint().getX())), new Form(20,
						30), true);
	}

	private DriveThrough getDriveThrough() {
		return owner;
	}

	public void insert(Auto auto) {
		if(queue.isEmpty()){
			queue.insert(auto);
			BestellEvent event = new BestellEvent(getDriveThrough());
			event.schedule(auto, wartezeit.sampleTimeSpan(TimeUnit.SECONDS));
		}else if(!queue.insert(auto)) {
			rejected.update();
			auto.disposeAnimation();
		}
	}

	class BestellEvent extends Event<Auto> {

		public BestellEvent(DriveThrough owner) {
			super(owner, "Bestellung", true);
		}

		@Override
		public void eventRoutine(Auto auto) {
			queue.remove(auto);
			if(target!=null)
				target.insert(auto);
			else
				auto.disposeAnimation();
			if(!queue.isEmpty()){
				this.schedule(queue.removeFirst(), wartezeit.sampleTimeSpan(TimeUnit.SECONDS));
			}
		}

	}
}
