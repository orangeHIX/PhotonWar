package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Logger;

public class MyGdxGame implements ApplicationListener {
	SpriteBatch batch;
	Texture img;
	private Logger logger;
	private boolean renderInterrupted = true;

	@Override
	public void create() {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		logger = new Logger("Application lifecycle", Logger.INFO);
		logger.info("create");
	}

	@Override
	public void dispose() {
		logger.info("dispose");
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
		if (renderInterrupted) {
			logger.info("render");
			renderInterrupted = false;
		}
	}

	@Override
	public void resize(int width, int height) {
		logger.info("resize");
		renderInterrupted = true;
	}

	@Override
	public void pause() {
		logger.info("pause");
		renderInterrupted = true;
	}

	@Override
	public void resume() {
		logger.info("resume");
		renderInterrupted = true;
	}
}
