package entity.car;

import java.awt.Dimension;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import model.DriveThrough;
import model.Manager;
import model.State;
import model.Manager.ShowInReport;
import desmoj.core.dist.ContDistNormal;
import desmoj.core.dist.DiscreteDistUniform;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.TimeSpan;
import desmoj.core.statistic.Accumulate;
import desmoj.core.statistic.Histogram;
import entity.schalter.BestellSchalter;


public class AutoGenerator{

	private static final String CAR_NAME= "auto";
	private static final String STATE_NAME = "color_state";
	public static Dimension CAR_SIZE;

	private DriveThrough owner;

	private Histogram histo;
	private DiscreteDistUniform colorSampler;
	private ContDistNormal defaultTimeSampler;
	private ContDistNormal[] timeSamplers;
	
	private AutoTypeAnimation	autoType;
	private String[] images;
	
	private double mean;
	private double standardDeviation;
	private int[] hours;
	private double[] scales;
	private Accumulate accumulate;
	
	public AutoGenerator(String[] images,int[] hours, double[] scales, double mean, double standardDeviation) {
		this.images = images;
		this.hours = hours;
		this.scales = scales;
		this.mean = mean;
		this.standardDeviation = standardDeviation;
		autoType = new AutoTypeAnimation("Auto",new State(CAR_NAME+0, CAR_NAME+0, images[0]));
		timeSamplers = new ContDistNormal[scales.length];
		for(int i=1; i<images.length;i++){
			autoType.addState(new State(CAR_NAME+i, STATE_NAME+i, images[i]));
		}
	}
	public AutoTypeAnimation getEntity(){
		return autoType;
	}
	
	public void init(DriveThrough owner) {
		this.owner = owner;
		accumulate = new Accumulate(owner, "Kunden Erstellung Gesamt", Manager.showInReport(ShowInReport.MINIMAL_REPORT), Manager.TRACE);
		this.histo = new Histogram(owner, "Kunden Erstellung", Manager.OPENING,Manager.CLOSING,Manager.CLOSING-Manager.OPENING, Manager.showInReport(ShowInReport.NORMAL_REPORT), Manager.TRACE);
		this.owner = owner;
		this.colorSampler = new DiscreteDistUniform(owner, "Color Sampler", 0, images.length-1, Manager.showInReport(ShowInReport.EXTENDED_REPORT), Manager.TRACE);
		this.defaultTimeSampler=new ContDistNormal(owner, "Kunden Erstellung", mean, standardDeviation,Manager.showInReport(ShowInReport.EXTENDED_REPORT), Manager.TRACE);
		for(int i=0;i<scales.length;i++){
			timeSamplers[i]=new ContDistNormal(owner, "Kunden Erstellung "+hours[2*i]+"-"+hours[2*i+1]+" Uhr", mean/scales[i], standardDeviation/scales[i], Manager.showInReport(ShowInReport.EXTENDED_REPORT), Manager.TRACE);
			timeSamplers[i].setNonNegative(true);
		}
		defaultTimeSampler.setNonNegative(true);
	
		autoType.init(owner);
	}

	private TimeSpan getTimeSpan(){
		int hourOfDay = owner.presentTime().getTimeAsCalender().get(Calendar.HOUR_OF_DAY);
		TimeSpan t = null;
		if(hourOfDay<Manager.OPENING || hourOfDay > Manager.CLOSING)
			t = owner.getTimeSpanTillOpen();
		else{
			for(int i = 0; i<hours.length-1; i+=2){
				if(hours[i]<=hourOfDay&&hours[i+1]>=hourOfDay){
					t = timeSamplers[i/2].sampleTimeSpan(TimeUnit.SECONDS);
					break;
				}
			}
		}
		if(t==null)
			t = defaultTimeSampler.sampleTimeSpan(TimeUnit.SECONDS);
		accumulate.update(t);
		return t;
	}
	
	public void start(DriveThrough model) {
		new GeneratorEvet(model).schedule(getTimeSpan());
	}
	
	public class GeneratorEvet extends ExternalEvent{


		public GeneratorEvet(DriveThrough owner) {
			super(owner, "Auto Generator", true);
		}

		@Override
		public void eventRoutine() {
			histo.update(presentTime().getTimeAsCalender().get(Calendar.HOUR_OF_DAY));
			autoType.init(getDriveThrough());
			autoType.setState(colorSampler.sample().intValue());
			BestellSchalter bestellShalter = getDriveThrough().getBestellShalter();
			Auto auto = autoType.create(getDriveThrough());
			if(!bestellShalter.insert(auto))
				bestellShalter.reject(auto);
			schedule(getTimeSpan());
		}

		private DriveThrough getDriveThrough() {
			return (DriveThrough) getModel();
		}
		
	}

}
