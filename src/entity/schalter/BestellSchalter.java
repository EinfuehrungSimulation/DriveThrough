package entity.schalter;


import model.DriveThrough;
import model.State;
import desmoj.core.simulator.TimeSpan;
import desmoj.extensions.visualization2d.animation.Position;
import desmoj.extensions.visualization2d.animation.internalTools.EntityTypeAnimation;
import entity.car.Auto;

public class BestellSchalter extends Schalter {
	
	private boolean active = false;


	public BestellSchalter(DriveThrough owner, String name,
			State state, EntityTypeAnimation entity, Position pos, double mean,
			double stdDev, boolean horizontal, int length) {
		super(owner, name, state, entity, pos, mean, stdDev, horizontal,
				length);
	}

	@Override
	public TimeSpan process(Auto auto) {
		active = true;
		return getRndTime();
	}
	
	@Override
	public void doAfterCarProcessed(Auto auto) {
		queue.removeFirst();
		getDriveThrough().getAusgabeShalter().insert(auto);
		active = false;
		if(!this.isEmpty())
			this.start(queue.first());
	}
	
	
	public void activate(){
		if(!active)
			if(!isEmpty()){
				getDriveThrough().getAusgabeShalter().insert(queue.removeFirst());
				if(!isEmpty()){
					start(queue.first());
				}
			}
	}
}
