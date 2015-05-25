package model;

import java.util.ArrayList;

import desmoj.extensions.visualization2d.animation.PositionExt;
import desmoj.extensions.visualization2d.animation.core.simulator.EntityAnimation;
import desmoj.extensions.visualization2d.animation.core.simulator.ModelAnimation;
import desmoj.extensions.visualization2d.animation.internalTools.EntityTypeAnimation;

public class MyEntity {

	private EntityAnimation myAnimation;
	private ArrayList<State> states;
	private final String NAME;

	public MyEntity(String name, State state) {
		this.NAME = name;
		this.states = new ArrayList<State>();
		this.states.add(state);
	}
	
	public void init(ModelAnimation owner){
		myAnimation = new EntityAnimation(owner, NAME, true);
	}
	
	public EntityTypeAnimation getEntityTypeAnimation(){
		EntityTypeAnimation entityType;
		entityType = new EntityTypeAnimation();
		entityType.setId("a");
		entityType.setGenereratedBy(getClass().getName());
		for(int i=0; i<states.size();i++)
			entityType.addPossibleState(states.get(i).getName(), states.get(i).getImageID());
		return entityType;
	}
	
	public State getState(int i){
		return states.get(i);
	}
	
	public int states(){
		return states.size();
	}
	
	public void show(PositionExt pos){
		myAnimation.createAnimation("a", states.get(0).getName(), pos, true);
	}
}
