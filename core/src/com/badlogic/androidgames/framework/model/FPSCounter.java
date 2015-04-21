package com.badlogic.androidgames.framework.model;

import com.badlogic.gdx.Gdx;


public class FPSCounter {
	long startTime = System.nanoTime();
	int frames = 0;

	/**
	 * ������Ϸ֡�� frame/second, һ����Game��present����ĩβ�����Ի�ȡ׼ȷ��֡��
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
