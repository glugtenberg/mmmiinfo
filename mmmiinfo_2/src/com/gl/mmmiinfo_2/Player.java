package com.gl.mmmiinfo_2;

import java.util.ArrayList;

import android.util.Log;

import com.threed.jpct.SimpleVector;

public class Player {
	
	private ArrayList<SimpleVector> slots;
	public int slotIdx;
	
	private SimpleVector targetPos = new SimpleVector();
	private SimpleVector currentPos = new SimpleVector(); 
	private float maxChange = 1.5f; 
	private static final float POS_EPSILON = 0.01f; 
	
	public Player(ArrayList<SimpleVector> slots) {
		this.slots = new ArrayList<SimpleVector>();
		this.slots.addAll(slots);
		
		slotIdx = slots.size() / 2; 
		
		currentPos.set(slots.get(slotIdx));
	}
	
	public void update(Game game, float dt) {
		targetPos = slots.get(slotIdx);
		
		SimpleVector dPos = targetPos.calcSub(currentPos);
		float l = dPos.length(); 
		if (l > maxChange) dPos.scalarMul(maxChange / l);
		
		if (l < POS_EPSILON) {
			currentPos.set(targetPos);
		} else {
			currentPos.add(dPos);
		}
		
		game.world.getCamera().setPosition(currentPos);
		
		for (ShootingCube cube : game.spawner.cubes) {
			if (cube.active && testCollision(cube)) {
				Log.d("DEBUG", "HIT HIT HIT");
				cube.setActive(false); 
			}
		}
	}
	
	boolean testCollision(ShootingCube cube) {
		//TODO: get the halfSize of the cubes instead of the magic 2.5
		SimpleVector center = cube.getTransformedCenter();
		return center.z > -2.5f && center.z < 2.5f &&
			   center.x > currentPos.x - 2.5f && center.x < currentPos.x + 2.5;
	}
	
	public void moveLeft() {
		slotIdx = slotIdx > 0 ? slotIdx - 1 : slotIdx; 
	}
	
	public void moveRight() {
		slotIdx = slotIdx < slots.size() - 1 ? slotIdx + 1 : slotIdx;
	}
}
