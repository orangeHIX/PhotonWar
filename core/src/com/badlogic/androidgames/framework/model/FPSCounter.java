package com.badlogic.androidgames.framework.model;

import com.badlogic.gdx.Gdx;


public class FPSCounter {
	long startTime = System.nanoTime();
	int frames = 0;

	/**
	 * 报告游戏帧率 frame/second, 一般在Game的present方法末尾调用以获取准确的帧率
	 */
	public void logFrame() {
		frames++;
		if (System.nanoTime() - startTime >= 1000000000) {
			Gdx.app.log("FPSCounter", "fps: " + frames);
			frames = 0;
			startTime = System.nanoTime();
		}
	}

}
