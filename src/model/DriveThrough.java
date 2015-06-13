package model;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Date;

import desmoj.core.simulator.Queue;
import desmoj.extensions.visualization2d.animation.BackgroundElementAnimation;
import desmoj.extensions.visualization2d.animation.CmdGeneration;
import desmoj.extensions.visualization2d.animation.Form;
import desmoj.extensions.visualization2d.animation.Position;
import desmoj.extensions.visualization2d.animation.core.simulator.ModelAnimation;
import desmoj.extensions.visualization2d.animation.internalTools.EntityTypeAnimation;
import entity.car.Auto;
import entity.car.AutoGenerator;
import entity.car.AutoTypeAnimation;
import entity.kitchen.Cook;
import entity.kitchen.Resources;
import entity.schalter.AusgabeSchalter;
import entity.schalter.BestellSchalter;
import entity.schalter.Schalter;

/**
 * Jede Kasse hat eine eigene Warteschlange
 * 
 * @author Christian
 * 
 */
public class DriveThrough extends ModelAnimation {

	//initialization values
	private static final int BESTELLUNG_CAPACITY = 15;
	private static final double BESTELLUNG_DAUER_STDV = 20.0;
	private static final double BESTELLUNG_DAUER_MEAN = 145.0;
	private static final int AUSGABE_CAPACITY = 7;
	private static final double AUSGABE_DAUER_STDV = 80.0;
	private static final double AUSGABE_DAUER_MEAN = 200.0;
	private static final int RESOURCE_COUNT = 5;
	private static final double RESOURCE_CREATION_TIME_STDV = 400;
	private static final double RESOURCE_CREATION_TIME_MEAN = 100;
	private static final int COOKS = 3;
	private static final double AUTO_GENERATION_STDV = 50.0;
	private static final double AUUTO_GENERATION_MEAN = 150.0;
	
	
	private static final String BESTELLSHALTER_ID = "bestellung";
	private static final String AUSGABESCHALTER_ID = "ausgabe";
	private static final String BESTELLSHALTER_IDLE_GIF = "schalter/schalter1_idle.gif";
	private static final String AUSGABESHALTER_IDLE_GIF = "schalter/schalter2_idle.gif";
	private static final String IDLE = "IDLE";
	

	private static int WIDTH = 800;
	private static int HEIGHT = 600;
	private static int TEXT_SIZE = 20;
	private static Color BG_COLOR = Color.GRAY;
	private static Color FG_COLOR = Color.LIGHT_GRAY;
	private static String BASE = "bg";
	private static String NAME = "Drive-Through";
	private static String TEXT = "Description";

	String[] cars = { "cars/car1.png", "cars/car2.png", "cars/car3.png" };

	private Schalter bestellShalter;
	private AutoGenerator generator;
	private Schalter ausgabeShalter;
	private Resources resources;
	private Queue<Cook> cooks;
	private Queue<Cook> cookingCooks;

	public DriveThrough(CmdGeneration cmdGen) {
		super(null, "Drive In", cmdGen, true, true, true);
		this.setModelProjectName("Desmo-J Descrete Simulation");
		this.setModelProjectIconId("DESMO-J");
		this.setModelAuthor("Sascha Wernegger");
		this.setModelDate(new Date().toString());
		this.setModelDescription(this.description());
		this.setGeneratedBy(DriveThrough.class.getName());
		this.addIcon(BESTELLSHALTER_ID, BESTELLSHALTER_IDLE_GIF);
		this.addIcon(AUSGABESCHALTER_ID, AUSGABESHALTER_IDLE_GIF);

		generator = new AutoGenerator(cars, AUUTO_GENERATION_MEAN, AUTO_GENERATION_STDV);

		initEntityType(generator.getEntity());

	}

	public void initEntityType(AutoTypeAnimation entityType) {
		for (int i = 0; i < entityType.states(); i++) {
			this.addIcon(entityType.getState(i).getImageID(), entityType
					.getState(i).getImagePath());
		}
		EntityTypeAnimation type = entityType.getEntityTypeAnimation();
		this.addEntityTypeAnimation(type);
	}

	@Override
	public void initAnimation() {
		Auto.init(this, RESOURCE_COUNT);
		new BackgroundElementAnimation(this, BASE, NAME, TEXT, 0, TEXT_SIZE, 0,
				100.0, new Position(0, 0), new Form(WIDTH, HEIGHT), BG_COLOR,
				FG_COLOR, true);

		initKitchen();

		ausgabeShalter = new AusgabeSchalter(this, "Ausgabe", null, new State(IDLE,
				AUSGABESCHALTER_ID, AUSGABESHALTER_IDLE_GIF), generator
				.getEntity().getEntityTypeAnimation(), new Position(-WIDTH/2, 0),
				AUSGABE_DAUER_MEAN, AUSGABE_DAUER_STDV, false,AUSGABE_CAPACITY);

		bestellShalter = new BestellSchalter(this, "Bestellung", ausgabeShalter,
				new State(IDLE, BESTELLSHALTER_ID, BESTELLSHALTER_IDLE_GIF),
				generator.getEntity().getEntityTypeAnimation(), new Position(
						50, -250), BESTELLUNG_DAUER_MEAN,BESTELLUNG_DAUER_STDV, true,BESTELLUNG_CAPACITY);
		generator.init(this);
	}

	private void initKitchen(){
		resources = new Resources(this, new Position(WIDTH / 2, -HEIGHT / 2),RESOURCE_COUNT, RESOURCE_CREATION_TIME_MEAN, RESOURCE_CREATION_TIME_STDV);

		cooks = new Queue<Cook>(this, "Cooks", true, true);
		cookingCooks = new Queue<Cook>(this, "Cooking Cooks", true, true);
		
		for(int i=0;i<COOKS;i++){
			Cook c =new Cook(this, resources, "Cook"+i, true);
			cooks.insert(c);
			c.start();
		}
	}
	
	@Override
	public String description() {
		return "Model of a Drive In";
	}

	@Override
	public void doInitialSchedules() {
		generator.start(this);
	}

	public Schalter getBestellShalter() {
		return bestellShalter;
	}

	public Schalter getAusgabeShalter() {
		return ausgabeShalter;
	}

	public Dimension getDimension() {
		return new Dimension(WIDTH, HEIGHT);
	}

	public Resources getResources() {
		return resources;
	}

	public Queue<Cook> getCooks() {
		return cooks;
	}

	public Queue<Cook> getCookingCooks() {
		return cookingCooks;
	}


}// end class