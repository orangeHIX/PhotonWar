package com.creaty.game.framework;

import com.badlogic.gdx.Gdx;

public class SmartLog {

	long startTime = System.nanoTime();
	
	public void logIntPS( String tag, int data ){

		if (System.nanoTime() - startTime >= 1000000000) {
			Gdx.app.log("SmartLog", tag + ": " + data);

			startTime = System.nanoTime();
		}
	}
	public void logFloatPS( String tag, float data ){

		if (System.nanoTime() - startTime >= 1000000000) {
			Gdx.app.log("SmartLog", tag + "fps: " + data);

			startTime = System.nanoTime();
		}
	}
	public void logStringPS( String tag, String data ){

		if (System.nanoTime() - startTime >= 1000000000) {
			Gdx.app.log("SmartLog", tag + "fps: " + data);

			startTime = System.nanoTime();
		}
	}
}
