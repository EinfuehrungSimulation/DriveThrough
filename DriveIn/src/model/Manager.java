package model;


public class Manager {
	//initialization values
	
	//-------------------Bestellung--------------------//
	public static final int 				BESTELLUNG_CAPACITY 			= 7;
	public static final double 				BESTELLUNG_DAUER_STDV 			= 20.0;
	public static final double 				BESTELLUNG_DAUER_MEAN 			= 80.0;

	//-------------------Ausgabe--------------------//
	public static final int 				AUSGABE_CAPACITY 				= 5;
	public static final double 				AUSGABE_DAUER_STDV 				= 80.0;
	public static final double 				AUSGABE_DAUER_MEAN 				= 150.0;
	public static final int 				WARTEPLAETZE 					= 3;

	//-------------------Küche--------------------//
	public static final int 				COOKS 							= 4;
	public static final int 				RESOURCE_LIMIT	 				= 5;
	public static final int 				RESOURCE_COUNT 					= 5;
	public static final double 				RESOURCE_CREATION_TIME_STDV		= 200;
	public static final double 				RESOURCE_CREATION_TIME_MEAN 	= 700;

	//-------------------Kunde--------------------//
	public static final double 				AUTO_GENERATION_STDV 			= 80.0;
	public static final double 				AUUTO_GENERATION_MEAN 			= 250.0;
	public static final double 				PATIENCE_STDV 					= 2.0;
	public static final double 				PATIENCE_MEAN 					= 19.0;
	public static final int[] 				TIME_SPANS 						= {11,13,13,16,16,18};
	public static final double[]			TIME_SCALES 					= {2.0,1.4,1.8};
	
	//-------------------Drive Through--------------------//
	public static final int 				OPENING 						= 6;
	public static final int 				CLOSING 						= 24;
	public static final int					RESOLUTION 						= 12;
	
	//-------------------Animation--------------------//
	public static final String 				BESTELLSHALTER_ID 				= "bestellung";
	public static final String 				AUSGABESCHALTER_ID 				= "ausgabe";
	public static final String 				BESTELLSHALTER_IDLE_GIF 		= "schalter/schalter1_idle.gif";
	public static final String 				AUSGABESHALTER_IDLE_GIF 		= "schalter/schalter2_idle.gif";
	public static final String 				IDLE = "IDLE";
	
	//-------------------Flags--------------------//
	public static final boolean 			TRACE 							= true;
	public static final double WARTEPLATZ_DAUER_MEAN 						= 10.0;
	public static final double WARTEPLATZ_DAUER_STDV 						= 2.0;	
	public static ShowInReport				SHOW_REPORT						= ShowInReport.NORMAL_REPORT;
	
	public static enum ShowInReport{
		NO_REPORT(0), MINIMAL_REPORT(1), NORMAL_REPORT(2), EXTENDED_REPORT(3), DEBUG_REPORT(4);
		private int value;
		private ShowInReport(int val) {
			value=val;
		}
	}
	
	public static boolean showInReport(ShowInReport show){
		if(show==ShowInReport.NO_REPORT)
			return false;
		return show.value <= SHOW_REPORT.value;
	}
}
