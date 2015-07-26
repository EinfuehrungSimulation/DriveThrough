package entity.schalter;

import model.DriveThrough;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;
import desmoj.extensions.visualization2d.animation.Position;
import desmoj.extensions.visualization2d.animation.internalTools.EntityTypeAnimation;
import entity.car.Customer;

/**
 * A counter that can block his predecessor and may not process a customer and instead add him to a holding area
 */
public class AusgabeSchalter extends Counter {

	private boolean haveToWait = false;

	public AusgabeSchalter(DriveThrough owner, String name,
			EntityTypeAnimation entity, Position pos, double mean,
			double stdDev, boolean horizontal, int length) {
		super(owner, name, entity, pos, mean, stdDev, horizontal,
				length);
	}
	
	
	@Override
	public void doAfterCarProcessed(Customer auto) {
		if(haveToWait){
			haveToWait = false;
			getDriveThrough().getWartePlatz().insert(auto);
		}else if(auto!=null)
			auto.disposeAnimation();
		Customer a = getDriveThrough().getBestellShalter().activate();
		if(a!=null&&!contains(a))
			insert(a);
		if(!this.isEmpty()){
			start();
		}
	}
	
	@Override
	public TimeSpan process(Customer auto) {
		int resource = auto.getOrder();
		TimeInstant consumationTime = getDriveThrough().getResources().consume(resource);
		if(consumationTime.getTimeAsDouble()>presentTime().getTimeAsDouble())
			if(!getDriveThrough().getWartePlatz().isFull()&&!this.isEmpty()){
				haveToWait = true;
				return new TimeSpan(0);
			}
		TimeInstant processingTime = new TimeInstant(getRndTime().getTimeAsDouble()+presentTime().getTimeAsDouble());
		consumationTime = new TimeInstant(Math.max(consumationTime.getTimeAsDouble(), processingTime.getTimeAsDouble()));
		return new TimeSpan(consumationTime.getTimeAsDouble()-presentTime().getTimeAsDouble());
	}
}