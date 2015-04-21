package com.badlogic.androidgames.framework.model;

import com.badlogic.gdx.math.Vector2;

public class DynamicGameObject extends GameObject {
	/** 速度向量，时间段*速度向量=该物体在游戏世界中前进的距离 */
	public Vector2 velocity;
	/** 加速度向量 */
	public Vector2 accel;

	public DynamicGameObject(float x, float y, float width, float height) {
		super(x, y, width, height);
		velocity = new Vector2();
		accel = new Vector2();
	}

	public DynamicGameObject(float x, float y, float radius) {
		super(x, y, radius);
		velocity = new Vector2();
		accel = new Vector2();
	}

}
