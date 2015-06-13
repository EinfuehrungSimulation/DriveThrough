package entity.schalter;

import model.DriveThrough;
import model.State;
import desmoj.core.simulator.TimeInstant;
import desmoj.extensions.visualization2d.animation.Position;
import desmoj.extensions.visualization2d.animation.internalTools.EntityTypeAnimation;
import entity.car.Auto;

public class AusgabeSchalter extends Schalter {

	public AusgabeSchalter(DriveThrough owner, String name, Schalter schalter,
			State state, EntityTypeAnimation entity, Position pos, double mean,
			double stdDev, boolean horizontal, int length) {
		super(owner, null, name, state, entity, pos, mean, stdDev, horizontal,
				length);
	}
	@Override
	public TimeInstant doAfterEntityProcessed(Auto auto) {
		int resource = auto.getOrder();
		TimeInstant consumationTime = getDriveThrough().getResources().consume(resource);
		if(consumationTime == null)
			return getDriveThrough().getResources().getTimeTilProducedRecource(resource); 
		return consumationTime;
	}
}