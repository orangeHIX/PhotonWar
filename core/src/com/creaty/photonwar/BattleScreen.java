package com.creaty.photonwar;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.androidgames.framework.model.FPSCounter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.creaty.photonwar.entity.World;
import com.creaty.photonwar.entity.World.WorldListener;
import com.creaty.photonwar.view.WorldRenderer;

public class BattleScreen extends Screen {

	public static final String tag = "BattleScreen";
	Game game;
	SpriteBatch batcher;
	FanShapeDrawer fanShapeDrawer;
	NoTextureBatcher noTextureBatcher;
	World world;
	WorldListener worldListener;
	WorldRenderer renderer;
	FPSCounter fpsCounter;
	/** 屏幕接收的触摸点坐标，使用时需要经历转换，转变为world中的坐标 */
	Vector2 touchPoint;
	CommandHandler commandHandler;

	public BattleScreen(Game game) {
		super(game);
		// TODO Auto-generated constructor stub
		this.game = (GLGame) game;
		batcher = new SpriteBatcher(this.game.getGLGraphics(), 1000);
		noTextureBatcher = new NoTextureBatcher(this.game.getGLGraphics(),10000);
		fanShapeDrawer = new FanShapeDrawer(this.game.getGLGraphics(), LEVEL.LEVEL_4, 10);
		worldListener = new WorldListener() {
			//
		};
		world = new World(worldListener);
		renderer = new WorldRenderer(this.game.getGLGraphics(),batcher, fanShapeDrawer, noTextureBatcher
				,world);

		fpsCounter = new FPSCounter();
		touchPoint = new Vector2();
		commandHandler = new CommandHandler(world);
	}

	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		world.update(deltaTime);
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int eventsize = touchEvents.size();
		TouchEvent te;
		for (int i = 0; i < eventsize; i++) {
			te = touchEvents.get(i);
			touchPoint.set(te.x, te.y);
			renderer.getCamera2D().touchToWorld(touchPoint);
			commandHandler.update(te.type, touchPoint);
			// Log.d(tag, touchPoint.toString());
		}
		// Log.d(tag, "update");
	}

	@Override
	public void present(float deltaTime) {
		// TODO Auto-generated method stub
		GL10 gl = game.getGLGraphics().getGL();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glEnable(GL10.GL_TEXTURE_2D);

		renderer.render();
		//fpsCounter.logFrame();
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
