package model;

import java.awt.Color;
import java.util.Date;

import model.entity.Schalter;
import model.entity.AutoTypeAnimation;
import model.event.AutoGenerator;
import desmoj.extensions.visualization2d.animation.BackgroundElementAnimation;
import desmoj.extensions.visualization2d.animation.CmdGeneration;
import desmoj.extensions.visualization2d.animation.Form;
import desmoj.extensions.visualization2d.animation.Position;
import desmoj.extensions.visualization2d.animation.core.simulator.ModelAnimation;
import desmoj.extensions.visualization2d.animation.internalTools.EntityTypeAnimation;

/**
 * Jede Kasse hat eine eigene Warteschlange
 * 
 * @author Christian
 *
 */
public class DriveThrough extends ModelAnimation {

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

		generator = new AutoGenerator(cars, 150.0, 50.0);

		initEntityType(generator.getEntity());

	}

	public void initEntityType(AutoTypeAnimation entityType) {
		for (int i = 0; i < entityType.states(); i++) {
			this.addIcon(entityType.getState(i).getImageID(), entityType.getState(i)
					.getImagePath());
		}
		EntityTypeAnimation type = entityType.getEntityTypeAnimation();
		this.addEntityTypeAnimation(type);
	}


	@Override
	public void initAnimation() {
		new BackgroundElementAnimation(this, BASE, NAME, TEXT, 0, TEXT_SIZE, 0,
				100.0, new Position(0, 0), new Form(WIDTH, HEIGHT), BG_COLOR,
				FG_COLOR, true);

		ausgabeShalter = new Schalter(this, "Ausgabe", null,  new State(IDLE,
				AUSGABESCHALTER_ID, AUSGABESHALTER_IDLE_GIF), generator
				.getEntity().getEntityTypeAnimation(), new Position(50, 250), 45.0, 20.0);

		bestellShalter = new Schalter(this, "Bestellung", ausgabeShalter, new State(IDLE,
				BESTELLSHALTER_ID, BESTELLSHALTER_IDLE_GIF), generator
				.getEntity().getEntityTypeAnimation(), new Position(50, -250), 145.0, 20.0);

		generator.init(this);
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

}// end class