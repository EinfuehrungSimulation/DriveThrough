package entity.schalter;

import model.DriveThrough;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;
import desmoj.extensions.visualization2d.animation.Position;
import desmoj.extensions.visualization2d.animation.internalTools.EntityTypeAnimation;
import entity.car.Auto;

public class WartePlatz extends Schalter{

	public WartePlatz(DriveThrough owner, String name,
			EntityTypeAnimation entity, Position pos, double mean,
			double stdDev, boolean bHorizontal, int length) {
		super(owner, name, entity, pos, mean, stdDev, bHorizontal, length);
	}

	@Override
	public void doAfterCarProcessed(Auto auto) {
		auto.disposeAnimation();
	}

	@Override
	public TimeSpan process(Auto auto) {
		TimeInstant consumationTime = getDriveThrough().getResources().consume(auto.getOrder());
		TimeInstant processingTime = new TimeInstant(getRndTime().getTimeAsDouble()+presentTime().getTimeAsDouble());
		consumationTime = new TimeInstant(Math.max(consumationTime.getTimeAsDouble(), processingTime.getTimeAsDouble()));
		return new TimeSpan(consumationTime.getTimeAsDouble()-presentTime().getTimeAsDouble());
	}
	
}
