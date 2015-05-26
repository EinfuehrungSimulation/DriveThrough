package model;


import java.awt.Color;
import java.awt.Point;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import model.entity.Auto;
import model.entity.Shalter;
import model.entity.SimpleAnimatedEntity;
import model.event.AutoGenerator;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;
import desmoj.extensions.visualization2d.animation.BackgroundElementAnimation;
import desmoj.extensions.visualization2d.animation.CmdGeneration;
import desmoj.extensions.visualization2d.animation.Form;
import desmoj.extensions.visualization2d.animation.FormExt;
import desmoj.extensions.visualization2d.animation.Position;
import desmoj.extensions.visualization2d.animation.PositionExt;
import desmoj.extensions.visualization2d.animation.core.simulator.EntityAnimation;
import desmoj.extensions.visualization2d.animation.core.simulator.ModelAnimation;
import desmoj.extensions.visualization2d.animation.core.simulator.QueueAnimation;
import desmoj.extensions.visualization2d.animation.internalTools.EntityTypeAnimation;


/**
 * Jede Kasse hat eine eigene Warteschlange
 * @author Christian
 *
 */
public class DriveThrough extends ModelAnimation {

	private static int 						WIDTH = 800;
	private static int 						HEIGHT = 600;
	private static int 						TEXT_SIZE = 20;
	private static Color 					BG_COLOR = Color.GRAY;
	private static Color 					FG_COLOR = Color.LIGHT_GRAY;
	private static String 					BASE = "bg";
	private static String 					NAME = "Drive-Through";
	private static String 					TEXT = "Description";

	String[] cars ={
			"cars/car1.png",
			"cars/car2.png",
			"cars/car3.png"
	};
	
	private BackgroundElementAnimation 		background;
	private SimpleAnimatedEntity			entity;
	private SimpleAnimatedEntity			entity2;
	private Shalter 						shalter;
	private AutoGenerator generator;
	
	/** standard constructor */
	public DriveThrough(CmdGeneration cmdGen) {
		super(null, "Drive In",	cmdGen, true, true, true);
		this.setModelProjectName("Desmo-J Descrete Simulation");
		this.setModelProjectIconId("DESMO-J");
		this.setModelAuthor("Sascha Wernegger");
		this.setModelDate(new Date().toString());
		this.setModelDescription(this.description());
		this.setGeneratedBy(DriveThrough.class.getName());

		entity = new SimpleAnimatedEntity("Entity", new State("IDLE", "image",
				"shalter_idle.gif"));
		entity2 = new SimpleAnimatedEntity("Entity2", new State("IDLE", "image2",
				"shalter_idle.gif"));
		generator= new AutoGenerator(cars, 150.0, 50.0);

		initEntityType(entity);
		initEntityType(entity2);
		initEntityType(generator.getTypeAnimation());
		
	}// end constructor

	@Override
	public void initAnimation() {
		new BackgroundElementAnimation(this, BASE, NAME, TEXT ,0, TEXT_SIZE, 0, 100.0, new Position(0, 0), new Form(WIDTH, HEIGHT), BG_COLOR, FG_COLOR, true);
		entity.init(this);
		entity2.init(this);
		entity.create(this, new PositionExt(new Point(250,250), 0.0, true));
		
		shalter = new Shalter(this, "Bestellung", new State("IDLE", "image3", "shalter_idle.gif"), entity2.getEntityTypeAnimation());
		shalter.insert(entity2.create(this));
		
		generator.init(this);
	}

	public void initEntityType(SimpleAnimatedEntity entity){
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
		generator.start(this);
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

	public void addCar(EntityAnimation auto) {
		shalter.insert(auto);
	}
}// end class