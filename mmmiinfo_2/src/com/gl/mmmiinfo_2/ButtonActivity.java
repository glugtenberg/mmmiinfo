package com.gl.mmmiinfo_2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class ButtonActivity extends GameActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
		View gui = inflater.inflate(R.layout.game, null);
		
		addContentView(gui, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
	}
	
	public void onLeftBtn(View v) {
		game.player.moveLeft();
    } 
	
	public void onRightBtn(View v) {
		game.player.moveRight();
    }

}
