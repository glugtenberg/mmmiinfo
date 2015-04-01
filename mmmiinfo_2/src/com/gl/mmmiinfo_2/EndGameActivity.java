package com.gl.mmmiinfo_2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class EndGameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.endgame);
	}
	
	public void onClickButton(View v) {
		setResult(0);
		finish();
	}
	
}
