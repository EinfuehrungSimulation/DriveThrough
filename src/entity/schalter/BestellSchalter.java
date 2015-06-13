package entity.schalter;

import model.DriveThrough;
import model.State;
import desmoj.core.simulator.TimeInstant;
import desmoj.extensions.visualization2d.animation.Position;
import desmoj.extensions.visualization2d.animation.internalTools.EntityTypeAnimation;
import entity.car.Auto;

public class BestellSchalter extends Schalter {
	
	public BestellSchalter(DriveThrough owner, String name, Schalter schalter,
			State state, EntityTypeAnimation entity, Position pos, double mean,
			double stdDev, boolean horizontal, int length) {
		super(owner,schalter, name, state, entity, pos, mean, stdDev, horizontal,
				length);
	}
	
	@Override
	public TimeInstant doAfterEntityProcessed(Auto auto) {
		return null;
	}
}
