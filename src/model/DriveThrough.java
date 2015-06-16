package model;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import model.Manager.ShowInReport;
import desmoj.core.simulator.Queue;
import desmoj.core.simulator.TimeSpan;
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
import entity.schalter.WartePlatz;

/**
 * Jede Kasse hat eine eigene Warteschlange
 * 
 * @author Christian
 * 
 */
public class DriveThrough extends ModelAnimation {

	private static int WIDTH = 800;
	private static int HEIGHT = 600;
	private static int TEXT_SIZE = 20;
	private static Color BG_COLOR = Color.GRAY;
	private static Color FG_COLOR = Color.LIGHT_GRAY;
	private static String BASE = "bg";
	private static String NAME = "Drive-Through";
	private static String TEXT = "Description";

	String[] cars = { "cars/car1.png", "cars/car2.png", "cars/car3.png" };

	private BestellSchalter bestellShalter;
	private AutoGenerator generator;
	private AusgabeSchalter ausgabeShalter;
	private Resources resources;
	private Queue<Cook> waitingCooks;
	private Queue<Cook> cookingCooks;
	private WartePlatz wartePlatz;

	public DriveThrough(CmdGeneration cmdGen) {
		super(null, "Drive In", cmdGen, true, true, true);
		this.setModelProjectName("Desmo-J Descrete Simulation");
		this.setModelProjectIconId("DESMO-J");
		this.setModelAuthor("Sascha Wernegger");
		this.setModelDate(new Date().toString());
		this.setModelDescription(this.description());
		this.setGeneratedBy(DriveThrough.class.getName());
		this.addIcon(Manager.BESTELLSHALTER_ID, Manager.BESTELLSHALTER_IDLE_GIF);
		this.addIcon(Manager.AUSGABESCHALTER_ID,
				Manager.AUSGABESHALTER_IDLE_GIF);

		generator = new AutoGenerator(cars, Manager.TIME_SPANS,
				Manager.TIME_SCALES, Manager.AUUTO_GENERATION_MEAN,
				Manager.AUTO_GENERATION_STDV);
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

		Auto.init(this, Manager.RESOURCE_COUNT, Manager.PATIENCE_MEAN,
				Manager.PATIENCE_STDV);
		new BackgroundElementAnimation(this, BASE, NAME, TEXT, 0, TEXT_SIZE, 0,
				100.0, new Position(0, 0), new Form(WIDTH, HEIGHT), BG_COLOR,
				FG_COLOR, true);

		initKitchen();

		Schalter.init(this);
		
		wartePlatz = new WartePlatz(this, "Warteplatz", generator.getEntity()
				.getEntityTypeAnimation(), new Position(50, -50), 0.0, 0.0,
				true, Manager.WARTEPLAETZE);
		
		ausgabeShalter = new AusgabeSchalter(this, "Ausgabe", generator
				.getEntity().getEntityTypeAnimation(), new Position(-WIDTH / 2,
				0), Manager.AUSGABE_DAUER_MEAN, Manager.AUSGABE_DAUER_STDV,
				false, Manager.AUSGABE_CAPACITY);

		bestellShalter = new BestellSchalter(this, "Bestellung", generator
				.getEntity().getEntityTypeAnimation(), new Position(50, -250),
				Manager.BESTELLUNG_DAUER_MEAN, Manager.BESTELLUNG_DAUER_STDV,
				true, Manager.BESTELLUNG_CAPACITY);
		generator.init(this);
	}

	private void initKitchen() {
		resources = new Resources(this, new Position(WIDTH / 2, -HEIGHT / 2),
				Manager.RESOURCE_COUNT, Manager.RESOURCE_CREATION_TIME_MEAN,
				Manager.RESOURCE_CREATION_TIME_STDV);

		waitingCooks = new Queue<Cook>(this, "Unbeschäftigte Köche",
				Manager.showInReport(ShowInReport.EXTENDED_REPORT),
				Manager.TRACE);
		cookingCooks = new Queue<Cook>(this, "Kochende Köche",
				Manager.showInReport(ShowInReport.EXTENDED_REPORT),
				Manager.TRACE);

		for (int i = 0; i < Manager.COOKS; i++) {
			Cook c = new Cook(this, resources, "Koch" + i, true);
			waitingCooks.insert(c);
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

	public BestellSchalter getBestellShalter() {
		return bestellShalter;
	}

	public AusgabeSchalter getAusgabeShalter() {
		return ausgabeShalter;
	}

	public Dimension getDimension() {
		return new Dimension(WIDTH, HEIGHT);
	}

	public Resources getResources() {
		return resources;
	}

	public Queue<Cook> getCooks() {
		return waitingCooks;
	}

	public Queue<Cook> getCookingCooks() {
		return cookingCooks;
	}

	public TimeSpan getTimeSpanTillOpen() {
		int hourOfDay = presentTime().getTimeAsCalender().get(
				Calendar.HOUR_OF_DAY);
		if (hourOfDay < Manager.OPENING)
			return new TimeSpan(Manager.OPENING - hourOfDay, TimeUnit.HOURS);
		else if (hourOfDay > Manager.CLOSING)
			return new TimeSpan(24 - Manager.CLOSING + Manager.OPENING,
					TimeUnit.HOURS);
		else
			return new TimeSpan(0);
	}

	public WartePlatz getWartePlatz() {
		return wartePlatz;
	}

}// end class