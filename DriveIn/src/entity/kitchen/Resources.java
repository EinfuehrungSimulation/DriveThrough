package entity.kitchen;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import model.DriveThrough;
import model.Manager;
import model.Manager.ShowInReport;
import desmoj.core.dist.ContDistNormal;
import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Queue;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;
import desmoj.core.statistic.Accumulate;
import desmoj.core.statistic.Histogram;
import desmoj.extensions.visualization2d.animation.Form;
import desmoj.extensions.visualization2d.animation.Position;
import desmoj.extensions.visualization2d.animation.core.statistic.CountAnimation;

public class Resources extends Entity{

	private static final int HEIGHT = 50;
	private ContDistNormal creationTime;
	private ArrayList<CountAnimation> resources;
	private int neededResource;
	private TimeSpan neededTime;
	private Histogram histo;
	private ArrayList<Accumulate> resourcenErstellt;
	
	public Resources(DriveThrough model, Position pos, int types, double meanCreationTime, double stDevCreationTime) {
		super(model, "Resources", true);
		resources = new ArrayList<CountAnimation>();
		resourcenErstellt = new ArrayList<Accumulate>();
		for(int i = 0; i<types; i++){
			Position p = new Position((int) pos.getPoint().getX(), (int)  pos.getPoint().getY()+i*HEIGHT*2+HEIGHT);
			CountAnimation resource = new CountAnimation(model, "Resource"+i, Manager.showInReport(ShowInReport.EXTENDED_REPORT), Manager.TRACE);
			resource.setDescription("Wenn eine Ressource consumiert wird wird sie sofort entfernt, daher gibt es hier auch negative Werte.\n"
					+ "Die Ressourcen-Limit ist: "+Manager.RESOURCE_LIMIT+".");
			resource.createAnimation(p,  new Form(new Dimension(HEIGHT/2, HEIGHT/2)), true);
			Accumulate accumulate = new Accumulate(model, "Resourcen Erstellt", Manager.showInReport(ShowInReport.NORMAL_REPORT), Manager.TRACE);
			accumulate.setDescription("Informationen �ber den Resourcenstand");
			resourcenErstellt.add(accumulate);
			resources.add(resource);
		}
		neededResource=-1;
		creationTime = new ContDistNormal(model, "Nachschub Erstellungsdauer", meanCreationTime, stDevCreationTime, Manager.showInReport(ShowInReport.MINIMAL_REPORT), Manager.TRACE);
		creationTime.setDescription("Dauer bis eine neue Ressource erstellt wird");
		creationTime.setNonNegative(true);
		histo = new Histogram(model, "Konsumierte Ressourcen", 1.0, (double)types-1,types-2,Manager.showInReport(ShowInReport.MINIMAL_REPORT), Manager.TRACE);
		histo.setDescription("Ressouren die von Kunden konsumiert wurden");
	}
	
	public TimeInstant consume(int resource){
		CountAnimation res = resources.get(resource);
		res.update(-1);
		resourcenErstellt.get(resource).update(res.getValue());
		histo.update(resource);
		TimeInstant t;
		if(res.getValue()>0){
			Queue<Cook> cooks = getDriveThrough().getCooks();
			if(!cooks.isEmpty())
				cooks.first().start();
			t = presentTime();
		}else{
			if(getDriveThrough().getCookingCooks().isEmpty()){
				Cook c = getDriveThrough().getCooks().first();
				c.start();
				t = c.getTimeWhenFinished();
			}else{
				Cook c = getCookWhoCooks(resource);
				if(c!=null)
					t = c.getTimeWhenFinished();
				else {
					neededTime = creationTime.sampleTimeSpan();
					TimeInstant timeWhenFinished = getDriveThrough().getCookingCooks().first().getTimeWhenFinished();
					t= new TimeInstant(timeWhenFinished.getTimeAsDouble() + neededTime.getTimeAsDouble());
				}
			}
		}
		double timeTillOpen = getDriveThrough().getTimeSpanTillOpen().getTimeAsDouble();
		if(timeTillOpen!=0);
			t = new TimeInstant(t.getTimeAsDouble()+timeTillOpen);
		return t;
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
		int i = 0;
		int min = 0;
		for(CountAnimation count: resources){
			if(count.getValue()<resources.get(min).getValue()){
				min = i;
			}
			i++;
		}
		resources.get(min).update();
		resourcenErstellt.get(min).update(resources.get(min).getValue());
		
		double t = creationTime.sampleTimeSpan(TimeUnit.SECONDS).getTimeAsDouble()+presentTime().getTimeAsDouble();
		double timeTillOpen = getDriveThrough().getTimeSpanTillOpen().getTimeAsDouble();
		if(timeTillOpen!=0)
			return	new TimeInstant(t+timeTillOpen);
		return new TimeInstant(t);
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
			if(re.getValue()<Manager.RESOURCE_LIMIT)
				return true;
		return false;
	}
}
