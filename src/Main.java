import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import model.DriveThrough;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.TimeInstant;
import desmoj.extensions.visualization2d.animation.CmdGeneration;
import desmoj.extensions.visualization2d.engine.viewer.ViewerFrame;

public class Main {
	public static final String   	PATH_DATA				= 
			new File("").getAbsolutePath() + File.separator + "model_data" + File.separator;
	public static void main(String[] args) {
		URL cmdFile;
		URL iconDir;
		CmdGeneration cmdGen;
		try {
			cmdFile = new File("cmds/commands.cmds").toURI().toURL();
			iconDir = new File("resource").toURI().toURL();
			cmdGen = new CmdGeneration("cmds/commands.cmds", "log", iconDir);
		} catch (MalformedURLException e) {
			System.out.println("error, aborting ...");
			return;
		}
		// erzeuge Modell und Experiment
		DriveThrough model = new DriveThrough(cmdGen);
		Experiment exp = new Experiment("DriveIn");
		exp.setSeedGenerator(1234567890);
		cmdGen.setStartStopTime(new TimeInstant(0, TimeUnit.HOURS), new TimeInstant(24, TimeUnit.HOURS), TimeZone.getDefault());
		model.connectToExperiment(exp);

		// setze Experiment-Parameter
		exp.setShowProgressBar(true);
		System.out.println("start ...");// start
		cmdGen.experimentStart(exp, 5.0);
		// Beende CmdGeneration
		cmdGen.close();
		// Nach dem Experiment: Gib Report aus und beende.
		exp.report();
		exp.finish();
		
		createViewer(cmdFile, iconDir);
	}
	
	public static ViewerFrame createViewer(URL cmdFile, URL imagePath){

		ViewerFrame v = null;
		try{
			v = new ViewerFrame(cmdFile, imagePath, Locale.ENGLISH);
			v.setLocation(0, 0);
			v.setSize(800, 500);
			v.getViewerPanel().setDefaultPath(PATH_DATA, PATH_DATA, PATH_DATA);
			v.getViewerPanel().lastCall();
		}catch(Exception e){
			if(v != null)
				v.getViewerPanel().setStatusMessage(e.getMessage());
			e.printStackTrace();
		}
		return v;
		
	}
}
