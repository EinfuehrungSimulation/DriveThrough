package model.entity;

import java.util.ArrayList;

import model.State;
import desmoj.extensions.visualization2d.animation.PositionExt;
import desmoj.extensions.visualization2d.animation.core.simulator.EntityAnimation;
import desmoj.extensions.visualization2d.animation.core.simulator.ModelAnimation;
import desmoj.extensions.visualization2d.animation.internalTools.EntityTypeAnimation;

public class AutoTypeAnimation {

	protected Auto auto;
	protected ArrayList<State> states;
	protected int state;
	protected final String NAME;
	private static int id=0;
	
	
	public AutoTypeAnimation(String name, State state) {
		this.NAME = name+id++;
		this.states = new ArrayList<State>();
		this.states.add(state);
		this.state = 0;
	}
	
	public void addState(State state){
		states.add(state);
	}
	
	public EntityTypeAnimation getEntityTypeAnimation(){
		EntityTypeAnimation entityType;
		entityType = new EntityTypeAnimation();
		entityType.setId(NAME);
		entityType.setGenereratedBy(NAME);
		for(int i=0; i<states.size();i++)
			entityType.addPossibleState(states.get(i).getName(), states.get(i).getImageID());
		return entityType;
	}
		
	public State getState(int i){
		return states.get(i);
	}
	
	public State getState(){
		return states.get(state);
	}
	
	public void setState(int i){
		this.state = i;
		auto.setState(states.get(i).getName());
	}
	
	public int states(){
		return states.size();
	}

	public Auto init(ModelAnimation owner){
		auto = new Auto(owner);
		return auto;
	}

	public Auto create(ModelAnimation owner, PositionExt pos){
		auto = new Auto(owner);
		auto.createAnimation(NAME, states.get(state).getName(), pos, true);
		return auto;
	}
	
	public Auto create(ModelAnimation owner){
		auto.createAnimation(NAME, states.get(state).getName(), true);
		return auto;
	}

	public Auto create(ModelAnimation owner, PositionExt pos, int state){
		this.state = state;
		auto = new Auto(owner);
		auto.createAnimation(NAME, states.get(state).getName(), pos, true);
		return auto;
	}
	
	public EntityAnimation create(ModelAnimation owner, int state){
		this.state = state;
		auto.createAnimation(NAME, states.get(state).getName(), true);
		return auto;
	}

	public EntityAnimation getEntityAnimation() {
		return auto;
	}
}
