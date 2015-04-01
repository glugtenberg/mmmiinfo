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
	private static final int LOOKAT_Z_DISTANCE = 150;
	
	public float score = 0; 
	
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
		SimpleVector distancePt = new SimpleVector(0, 0, LOOKAT_Z_DISTANCE);
		game.world.getCamera().setPosition(currentPos);
		game.world.getCamera().lookAt(distancePt.calcSub(currentPos));
		
		for (ShootingCube cube : game.spawner.cubes) {
			if (cube.active && testCollision(cube)) {
				Log.d("DEBUG", "HIT HIT HIT");
				cube.setActive(false); 
				score--; 
			}
		}
	}
	
	boolean testCollision(ShootingCube cube) {
		//TODO: get the halfSize of the cubes instead of the magic 2.5
		SimpleVector center = cube.getTransformedCenter();
		return center.z > -2.5f && center.z < 2.5f &&
			   center.x > currentPos.x - 2.5f && center.x < currentPos.x + 2.5;
	}
	
	public void moveToLeftSide() {
		slotIdx = 0;
	}
	
	public void moveToCenter() {
		slotIdx = 1;
	}
	
	public void moveToRightSide() {
		slotIdx = 2; 
	}
	
	public void moveLeft() {
		slotIdx = slotIdx > 0 ? slotIdx - 1 : slotIdx; 
	}
	
	public void moveRight() {
		slotIdx = slotIdx < slots.size() - 1 ? slotIdx + 1 : slotIdx;
	}
}
