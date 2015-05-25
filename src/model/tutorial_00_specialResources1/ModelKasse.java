package model.tutorial_00_specialResources1;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeInstant;
import desmoj.extensions.visualization2d.animation.CmdGeneration;
import desmoj.extensions.visualization2d.animation.core.simulator.ModelAnimation;


/**
 * Jede Kasse hat eine eigene Warteschlange
 * @author Christian
 *
 */
public class ModelKasse extends ModelAnimation {


	/** standard constructor */
	public ModelKasse(CmdGeneration cmdGen) {
		super(null, "Drive In",	cmdGen, true, true, true);
		this.setModelProjectName("Desmo-J Descrete Simulation");
		this.setModelProjectIconId("DESMO-J");
		this.setModelAuthor("Sascha Wernegger");
		this.setModelDate(new Date().toString());
		this.setModelDescription(this.description());
		this.setGeneratedBy(ModelKasse.class.getName());
		this.addIcon("client1_active", "resource/client1_active.gif" );
	}// end constructor

	@Override
	public void initAnimation() {
		
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