package model;

import java.awt.Color;
import java.awt.Point;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeInstant;
import desmoj.extensions.visualization2d.animation.BackgroundElementAnimation;
import desmoj.extensions.visualization2d.animation.CmdGeneration;
import desmoj.extensions.visualization2d.animation.Form;
import desmoj.extensions.visualization2d.animation.Position;
import desmoj.extensions.visualization2d.animation.PositionExt;
import desmoj.extensions.visualization2d.animation.core.simulator.ModelAnimation;
import desmoj.extensions.visualization2d.animation.internalTools.EntityTypeAnimation;


/**
 * Jede Kasse hat eine eigene Warteschlange
 * @author Christian
 *
 */
public class DriveThrough extends ModelAnimation {

	private static int 					WIDTH = 800;
	private static int 					HEIGHT = 600;
	private static int 					TEXT_SIZE = 20;
	private static Color 				BG_COLOR = Color.GRAY;
	private static Color 				FG_COLOR = Color.LIGHT_GRAY;
	private static String 				BASE = "bg";
	private static String 				NAME = "Drive-Through";
	private static String 				TEXT = "Description";
	
	
	private BackgroundElementAnimation 	background;
	private MyEntity					entity;

	/** standard constructor */
	public DriveThrough(CmdGeneration cmdGen) {
		super(null, "Drive In",	cmdGen, true, true, true);
		this.setModelProjectName("Desmo-J Descrete Simulation");
		this.setModelProjectIconId("DESMO-J");
		this.setModelAuthor("Sascha Wernegger");
		this.setModelDate(new Date().toString());
		this.setModelDescription(this.description());
		this.setGeneratedBy(DriveThrough.class.getName());
		
		entity = new MyEntity("Entity", new State("IDLE", "image",
				"shalter_idle.gif"));
		addEntity(entity);
	}// end constructor

	@Override
	public void initAnimation() {
		background = new BackgroundElementAnimation(this, BASE, NAME, TEXT ,0, TEXT_SIZE, 0, 100.0, new Position(0, 0), new Form(WIDTH, HEIGHT), BG_COLOR, FG_COLOR, true);
		entity.init(this);
		entity.show(new PositionExt(new Point(500,500), 0.0, true));
	}

	void addEntity(MyEntity entity){
		for(int i=0;i<entity.states();i++){
			this.addIcon(entity.getState(i).getImageID(), entity.getState(i).getImagePath());
		}
		EntityTypeAnimation type = entity.getEntityTypeAnimation();
		this.addEntityTypeAnimation(type);
	}
	
	@Override
	public String description() {
		return "Model of a Drive In";
	}

	@Override
	public void doInitialSchedules() {
		TestEvent event = new TestEvent(this);
		event.schedule(new TimeInstant(1, TimeUnit.SECONDS));
	}
	
	class TestEvent extends ExternalEvent{

		public TestEvent(Model owner) {
			super(owner, "Test", true);

		}

		@Override
		public void eventRoutine() {
			
		}
		
	}
	/** initializes model components, required by superclass */
}// end class