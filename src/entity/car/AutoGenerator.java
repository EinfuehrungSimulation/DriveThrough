package entity.car;

import java.awt.Dimension;
import java.util.concurrent.TimeUnit;

import model.DriveThrough;
import model.State;
import desmoj.core.dist.ContDistNormal;
import desmoj.core.dist.DiscreteDistUniform;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.TimeSpan;


public class AutoGenerator{

	private static final String CAR_NAME= "auto";
	private static final String STATE_NAME = "color_state";
	public static Dimension CAR_SIZE;
	
	private DiscreteDistUniform colorGenerator;
	private ContDistNormal timeGenerator;
	
	private AutoTypeAnimation	auto;
	private String[] images;
	private double mean;
	private double standardDeviation;
	
	public AutoGenerator(String[] images, double mean, double standardDeviation) {
		this.images = images;
		this.mean = mean;
		this.standardDeviation = standardDeviation;
		auto = new AutoTypeAnimation("Auto",new State(CAR_NAME+0, CAR_NAME+0, images[0]));
		for(int i=1; i<images.length;i++){
			auto.addState(new State(CAR_NAME+i, STATE_NAME+i, images[i]));
		}
	}
	
	public AutoTypeAnimation getEntity(){
		return auto;
	}
	
	public void init(DriveThrough owner) {
		this.colorGenerator = new DiscreteDistUniform(owner, "ColorGenerator", 0, images.length-1, false, false);
		this.timeGenerator=new ContDistNormal(owner, "Kunden Erstellung", mean, standardDeviation, true, true);
		timeGenerator.setNonNegative(true);
		auto.init(owner);
	}

	public void start(DriveThrough model) {
		new GeneratorEvet(model).schedule(timeGenerator.sampleTimeSpan(TimeUnit.SECONDS));
	}
	
	public class GeneratorEvet extends ExternalEvent{

		public GeneratorEvet(DriveThrough owner) {
			super(owner, "Auto Generator", true);
		}

		@Override
		public void eventRoutine() {
			auto.init(getDriveThrough());
			auto.setState(colorGenerator.sample().intValue());
			getDriveThrough().getBestellShalter().insert(auto.create(getDriveThrough()));
			TimeSpan time = timeGenerator.sampleTimeSpan(TimeUnit.SECONDS);
			schedule(time);
		}

		private DriveThrough getDriveThrough() {
			return (DriveThrough) getModel();
		}
		
	}

}
