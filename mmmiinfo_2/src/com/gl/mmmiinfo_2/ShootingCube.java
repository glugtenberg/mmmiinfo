package com.gl.mmmiinfo_2;

import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;

public class ShootingCube extends Object3D {
	public float speed = 30;
	public boolean active = false;

	public ShootingCube(Object3D cube) {
		super(cube);
	}

	public void update(float dt){
		if (!active) return; 
		
		translate(0, 0, -dt*speed);
		
		SimpleVector tc =  this.getTransformedCenter();	
		if(tc.z < -50) setActive(false);
	}
	
	public void setActive(boolean active) {
		this.active = active;
		if (!active) {
			this.clearTranslation();
			this.translate(0, 0, -1000);
		}
	}
}
