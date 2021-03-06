package entity.kitchen;


import model.DriveThrough;
import desmoj.core.simulator.Entity;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.TimeInstant;

public class Cook extends Entity{

	private static int ID=0;
	private int id;
	private CoockingEvent cookingEvent;
	private Resources resources;
	private int resource;
	private TimeInstant timeWhenFinished;

	public Cook(DriveThrough owner, Resources resources, String name, boolean showInTrace) {
		super(owner, name, showInTrace);
		this.resources = resources;
		ID++;
		id = ID;
		cookingEvent = new CoockingEvent();
		timeWhenFinished = presentTime();
	}
	
	public void start(){
		getDriveThrough().getCooks().remove(this);
		getDriveThrough().getCookingCooks().insert(this);
		if(timeWhenFinished.getTimeAsDouble()<=presentTime().getTimeAsDouble())
			cookingEvent.schedule(resources.generateResource(resources.getResourceToCook()));
	}

	private DriveThrough getDriveThrough() {
		return (DriveThrough) getModel();
	}
	/**
	 * Here are the ressources generated. If there is need for a special resource this is cooked, else one of the lowest is created
	 *
	 */
	class CoockingEvent extends ExternalEvent{

		public CoockingEvent() {
			super(resources.getModel(), "Coock"+id+" coocks",true);
		}

		@Override
		public void eventRoutine() {
			if(resources.needsResourceToBeCooked()){
				resource = resources.getResourceToCook();
				timeWhenFinished = resources.generateResource(resource);
				cookingEvent.schedule(timeWhenFinished);
			}else{
				timeWhenFinished = presentTime();
				getDriveThrough().getCookingCooks().remove(getCook());
				getDriveThrough().getCooks().insert(getCook());
			}
		}

	}
	public boolean cooks(int resource) {
		return this.resource == resource;
	}

	public Cook getCook() {
		return this;
	}

	public TimeInstant getTimeWhenFinished() {
		return timeWhenFinished;
	}

	public int getResource() {
		return resource;
	}

}
