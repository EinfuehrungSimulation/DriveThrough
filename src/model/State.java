package model;

/**
 * class to store state information
 *
 */
public class State{
	
	private final String NAME;
	private final String IMAGE_ID;
	private final String IMAGE_PATH;
	public State(String name, String imageId, String imagePath) {
		this.NAME = name;
		this.IMAGE_ID = imageId;
		this.IMAGE_PATH = imagePath;
	}
	
	public String getName(){
		return NAME;
	}

	public String getImageID() {
		return IMAGE_ID;
	}

	public String getImagePath() {
		return IMAGE_PATH;
	}
}