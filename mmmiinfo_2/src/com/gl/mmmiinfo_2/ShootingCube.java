package com.gl.mmmiinfo_2;

import com.threed.jpct.Object3D;

public class ShootingCube extends Object3D {

	public ShootingCube(Object3D cube) {
		super(cube);
	}

	public void update(long elapsedTime){
		rotateX(elapsedTime/1000);
		rotateY(elapsedTime/1000);
		rotateZ(elapsedTime/1000);
	}
}
