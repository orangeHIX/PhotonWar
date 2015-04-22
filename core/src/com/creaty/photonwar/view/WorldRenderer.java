package com.creaty.photonwar.view;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.creaty.math.FanShape;
import com.creaty.math.Vector2;
import com.creaty.photonwar.Assets;
import com.creaty.photonwar.entity.Ship;
import com.creaty.photonwar.entity.Ship.SHIP_STATE;
import com.creaty.photonwar.entity.Ammunition;
import com.creaty.photonwar.entity.ShipGroup;
import com.creaty.photonwar.entity.StrongHold;
import com.creaty.photonwar.entity.World;

public class WorldRenderer {

	static final float FRUSTUM_WIDTH = 20;
	static final float FRUSTUM_HEIGHT = 12;
	World world;
	Camera cam;
	SpriteBatch batcher;
	FanShapeDrawer fanShapeDrawer;
//	NoTextureBatcher noTextureBatcher;
	/** 绘制图形用的扇形 */
	FanShape fan;

	public WorldRenderer(GLGraphics glGraphics, SpriteBatcher batcher,
			FanShapeDrawer fanShapeDrawer, NoTextureBatcher noTextureBatcher,World world) {
//		this.noTextureBatcher = noTextureBatcher;
		this.world = world;
		this.cam = new Camera(glGraphics, FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		this.batcher = batcher;
		this.fanShapeDrawer = fanShapeDrawer;
		tmp = new Vector2(); // 帮助计算的临时向量
		fan = new FanShape(5, 5, 5.0f, 0, 359);
	}

	public void render() {
		cam.setViewportAndMatrices();
		renderObjects();
	}

	public void renderObjects() {
		GL10 gl = glGraphics.getGL();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		fanShapeDrawer.beginBatch();
		//fanShapeDrawer.drawFanShape(fan, 1,0,0,1);
		renderStrongHoldDevelop();
		fanShapeDrawer.endBatch();

		batcher.beginBatch(Assets.texture);
		renderStrongHolds();
		renderWorldShips();
		batcher.endBatch();
		
		// 下面这样做纯属权宜之计，因为材质位置不同
		batcher.beginBatch(Assets.items);
		renderStrongHoldsShipsNum();
		batcher.endBatch();
//		noTextureBatcher.beginBatch();
		
		
		gl.glDisable(GL10.GL_BLEND);
	}

	private void renderStrongHolds() {
		int len = world.strongHolds.size();
		for (int i = 0; i < len; i++) {
			StrongHold strongHold = world.strongHolds.get(i);
			batcher.drawSprite(strongHold.position.x, strongHold.position.y,
					2.0f, 2.0f, Assets.bobRegion);
		}
	}

	private void renderStrongHoldDevelop() {
		int len = world.strongHolds.size();
		for (int i = 0; i < len; i++) {
			StrongHold strongHold = world.strongHolds.get(i);
			fan.c.setPosition(strongHold.position);
			fan.c.radius = strongHold.orbitInsideBound;
			fan.setStart(0);
			fan.setEnd(strongHold.development/100*360);
			if(strongHold.owner == world.race1){
				fanShapeDrawer.drawFanShapeRing(fan,
						strongHold.orbitInsideBound - StrongHold.DEVELOP_RING_WIDTH,
						0, 0, 1, 1, 0, 0, 0);
			}else if( strongHold.owner == world.race2 ){
				fanShapeDrawer.drawFanShapeRing(fan,
						strongHold.orbitInsideBound - StrongHold.DEVELOP_RING_WIDTH,
						1, 0, 0, 1, 0, 0, 0);
			}
		}
	}

	private void renderStrongHoldsShipsNum() {
		int len = world.strongHolds.size();
		for (int i = 0; i < len; i++) {
			StrongHold strongHold = world.strongHolds.get(i);
			Assets.font.drawText(batcher,
					Integer.toString(strongHold.governShips.size()),
					strongHold.position.x, strongHold.position.y, 0.03f);
		}
	}

	/** 临时的帮助计算的向量 */
	private Vector2 tmp;

	/** 单独绘制一个战舰 */
	private void renderSingleShip(Ship ship) {
		tmp.set(ship.position.x - ship.rotationCenter.x, ship.position.y
				- ship.rotationCenter.y);
		if (ship.getState() == SHIP_STATE.CIRCLE ) {
			if (ship.isClockwise == true) {
				batcher.drawSprite(ship.position.x, ship.position.y, 0.4f,
						0.2f, tmp.angle() + 270.0f, Assets.cannonRegion);
			} else {
				batcher.drawSprite(ship.position.x, ship.position.y, 0.4f,
						0.2f, tmp.angle() + 90.0f, Assets.cannonRegion);
			}
		} else {
				batcher.drawSprite(ship.position.x, ship.position.y, 0.4f, 0.2f,
					ship.velocity, Assets.cannonRegion);
		}
	}

//	private void RenderWorldAmmunition() {
//		int ammuntionSize = world.ammunitions.size();
//		for(int i = 0;i < ammuntionSize;i++)
//		{
//			Ammunition tempAmmunition = world.ammunitions.get(i);
//			noTextureBatcher.drawSprite(tempAmmunition.position.x,
//					tempAmmunition.position.y, 0.1f, 0.1f, 1f, 1f, 1f, 1f);
//		}
//		
//	}
	/** 绘制世界中所有的战舰 */
	private void renderWorldShips() {
		int holdSize = world.strongHolds.size();
		for (int i = 0; i < holdSize; i++) {
			StrongHold tempHold = world.strongHolds.get(i);
			int shipSize = tempHold.governShips.size();
			for (int i2 = 0; i2 < shipSize; i2++) {
				renderSingleShip(tempHold.governShips.get(i2));
			}
			// Log.d("renderShips", ship.position.x+","+ship.position.y);
		}
		int groupSize = world.shipGroups.size();
		for (int i = 0; i < groupSize; i++) {
			ShipGroup tempGroup = world.shipGroups.get(i);
			int shipSize = tempGroup.shipList.size();
			for (int i2 = 0; i2 < shipSize; i2++) {
				renderSingleShip(tempGroup.shipList.get(i2));
			}
		}
	}

	public final Camera2D getCamera2D() {
		return cam;
	}
}
