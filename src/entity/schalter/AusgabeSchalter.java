package entity.schalter;

import model.DriveThrough;
import model.State;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;
import desmoj.extensions.visualization2d.animation.Position;
import desmoj.extensions.visualization2d.animation.internalTools.EntityTypeAnimation;
import entity.car.Auto;

public class AusgabeSchalter extends Schalter {

	public AusgabeSchalter(DriveThrough owner, String name,
			State state, EntityTypeAnimation entity, Position pos, double mean,
			double stdDev, boolean horizontal, int length) {
		super(owner, name, state, entity, pos, mean, stdDev, horizontal,
				length);
	}
	@Override
	public void doAfterCarProcessed(Auto auto) {
		getDriveThrough().getBestellShalter().activate();
		auto.disposeAnimation();
		if(!this.isEmpty()){
			start(queue.first());
		}
	}
	
	@Override
	public TimeSpan process(Auto auto) {
		int resource = auto.getOrder();
		TimeInstant consumationTime = getDriveThrough().getResources().consume(resource);
		TimeInstant processingTime = new TimeInstant(getRndTime().getTimeAsDouble()+presentTime().getTimeAsDouble());
		consumationTime = new TimeInstant(Math.max(consumationTime.getTimeAsDouble(), processingTime.getTimeAsDouble()));
		return new TimeSpan(consumationTime.getTimeAsDouble()-presentTime().getTimeAsDouble());
	}
}