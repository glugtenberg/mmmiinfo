package com.gl.mmmiinfo_2;

import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;

public class ShootingCube extends Object3D {
	public float speed = 30;

	public ShootingCube(Object3D cube) {
		super(cube);
	}

	public void update(float t){
		rotateX(t);
		rotateY(t);
		rotateZ(t);
		
		translate(0, 0, -t*speed);
		
		SimpleVector tc =  this.getTransformedCenter();
		
		if(tc.z < -50) {
			translate(0, 0, 150);
		}
	}
}
