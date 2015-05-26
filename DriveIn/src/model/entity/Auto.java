package model.entity;

import desmoj.extensions.visualization2d.animation.core.simulator.EntityAnimation;
import desmoj.extensions.visualization2d.animation.core.simulator.ModelAnimation;

public class Auto extends EntityAnimation{

	private static final String NAME = "Auto";
	private static int id=0;

	public Auto(ModelAnimation owner) {
		super(owner, NAME+id, true);
		id++;
	}
}
