package entity.schalter;


import model.DriveThrough;
import desmoj.core.simulator.TimeSpan;
import desmoj.extensions.visualization2d.animation.Position;
import desmoj.extensions.visualization2d.animation.internalTools.EntityTypeAnimation;
import entity.car.Auto;

public class BestellSchalter extends Schalter {
	
	private boolean active = false;
	private Auto auto;


	public BestellSchalter(DriveThrough owner, String name,
			EntityTypeAnimation entity, Position pos, double mean,
			double stdDev, boolean horizontal, int length) {
		super(owner, name, entity, pos, mean, stdDev, horizontal,
				length);
	}

	@Override
	public TimeSpan process(Auto auto) {
		active = true;
		return getRndTime();
	}
	
	@Override
	public void doAfterCarProcessed(Auto auto) {
		active = false;
		AusgabeSchalter ausgabeShalter = getDriveThrough().getAusgabeShalter();
		if(!ausgabeShalter.isFull()){	
			this.auto = null;
			ausgabeShalter.insert(auto);
			if(!this.isEmpty())
				this.start();
		}else
			this.auto = auto;
	}
	

	public Auto activate(){
		if(!active){
			if(!isEmpty())
				start();
		}
		return auto;
	}
}
