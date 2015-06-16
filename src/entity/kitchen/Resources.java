package entity.kitchen;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import model.DriveThrough;
import desmoj.core.dist.ContDistNormal;
import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Queue;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;
import desmoj.extensions.visualization2d.animation.Form;
import desmoj.extensions.visualization2d.animation.Position;
import desmoj.extensions.visualization2d.animation.core.statistic.CountAnimation;

public class Resources extends Entity{

	private static final int HEIGHT = 50;
	private final int MAX_RESOURCE_COUNT = 3;
	private ContDistNormal creationTime;
	private ArrayList<CountAnimation> resources;
	private int neededResource;
	private TimeSpan neededTime;
	
	public Resources(DriveThrough model, Position pos, int types, double meanCreationTime, double stDevCreationTime) {
		super(model, "Resources", true);
		resources = new ArrayList<CountAnimation>();
		for(int i = 0; i<types; i++){
			Position p = new Position((int) pos.getPoint().getX(), (int)  pos.getPoint().getY()+i*HEIGHT*2+HEIGHT);
			CountAnimation resource = new CountAnimation(model, "Resource"+i, true, true);
			resource.createAnimation(p,  new Form(new Dimension(HEIGHT/2, HEIGHT/2)), true);
			resources.add(resource);
		}
		neededResource=-1;
		creationTime = new ContDistNormal(model, "Resource Creation Time", meanCreationTime, stDevCreationTime, true, true);
		creationTime.setNonNegative(true);
	}
	
	public TimeInstant consume(int resource){
		CountAnimation res = resources.get(resource);
		if(res.getValue()>0){
			res.update(-1);
			Queue<Cook> cooks = getDriveThrough().getCooks();
			if(!cooks.isEmpty())
				cooks.first().start();
			return presentTime();
		}else{
			if(getDriveThrough().getCookingCooks().isEmpty()){
				Cook c = getDriveThrough().getCooks().first();
				c.start();
				return c.getTimeWhenFinished();
			}else{
				Cook c = getCookWhoCooks(resource);
				if(c!=null)
					return c.getTimeWhenFinished();
				else {
					neededTime = creationTime.sampleTimeSpan();
					TimeInstant timeWhenFinished = getDriveThrough().getCookingCooks().first().getTimeWhenFinished();
					return new TimeInstant(timeWhenFinished.getTimeAsDouble() + neededTime.getTimeAsDouble());
				}
			}
		}
	}

	private Cook getCookWhoCooks(int resource) {
		Cook cook =null;
		for(Cook el:getDriveThrough().getCookingCooks())
			if(el.cooks(resource))
				if(cook==null)
					cook = el;
				else if(cook.getTimeWhenFinished().getTimeAsDouble()>el.getTimeWhenFinished().getTimeAsDouble())
					cook = el;
		return cook;
	}

	private DriveThrough getDriveThrough() {
		return (DriveThrough) getModel();
	}

	public TimeInstant generateResource(int resource) {
		if(neededResource>=0){
			neededResource = -1;
			TimeSpan t = neededTime;
			neededTime = new TimeSpan(0);
			return new TimeInstant(presentTime().getTimeAsDouble()+t.getTimeAsDouble());
		}
		CountAnimation min = resources.get(0);
		for(CountAnimation count: resources)
			if(count.getValue()<min.getValue())
				min = count;
		min.update();
		return new TimeInstant(creationTime.sampleTimeSpan(TimeUnit.SECONDS).getTimeAsDouble()+presentTime().getTimeAsDouble());
	}

	public int getResourceToCook() {
		if(neededResource>=0)
			return neededResource;
		int res=0;
		int min = Integer.MAX_VALUE;
		for(CountAnimation c: resources)
			if(c.getValue()<min)
				res = (int) c.getValue();
		return res;
	}

	public TimeInstant getTimeTilProducedRecource(int resource) {
		Cook min=getDriveThrough().getCookingCooks().first();
		for(Cook el: getDriveThrough().getCookingCooks()){
			if(el.getTimeWhenFinished().getTimeAsDouble()>min.getTimeWhenFinished().getTimeAsDouble())
				min = el;
		}
		this.neededResource = min.getResource();
		this.neededTime = creationTime.sampleTimeSpan();
		return new TimeInstant(min.getTimeWhenFinished().getTimeAsDouble()+neededTime.getTimeAsDouble());
	}

	public TimeInstant getCookingDuration() {
		return new TimeInstant(presentTime().getTimeAsDouble()+neededTime.getTimeAsDouble());
	}

	public boolean needsResourceToBeCooked() {
		if(neededResource>=0)
			return true;
		else for(CountAnimation re:resources)
			if(re.getValue()<MAX_RESOURCE_COUNT)
				return true;
		return false;
	}
}
