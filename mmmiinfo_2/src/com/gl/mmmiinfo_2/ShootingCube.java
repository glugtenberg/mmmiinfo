package com.gl.mmmiinfo_2;

import com.threed.jpct.Object3D;

public class ShootingCube extends Object3D {

	public ShootingCube(Object3D cube) {
		super(cube);
	}

	public void update(long elapsedTime){
		rotateX(elapsedTime/1000.0f);
		rotateY(elapsedTime/1000.0f);
		rotateZ(elapsedTime/1000.0f);
	}
}
