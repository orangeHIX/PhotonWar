package com.creaty.photonwar.entity;

import java.util.Random;

import android.util.Log;

import com.badlogic.androidgames.framework.math.Circle;
import com.badlogic.androidgames.framework.math.Vector2;
import com.badlogic.androidgames.framework.model.DynamicGameObject;

public class Ammunition extends DynamicGameObject{

	public static final int STATE_MOVE = 1;
	public static final int STATE_EXPLODE = 2;
	/** 航行基础速率 */
	public int state;
	public float basicSpeed;
	Vector2 destination;
	
	public Ammunition(float x, float y, float radius) {
		super(x, y, radius);
		// TODO Auto-generated constructor stub 
		state = STATE_MOVE;
	}
	
	/** 执行航行指令 */
	public void MoveTo(Vector2 destination) {
		this.destination = destination;
		this.velocity.set(destination).sub(position).nor()
				.mul(basicSpeed);
		state = STATE_MOVE;
	}
	protected void updateMove(float deltaTime) {
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		((Circle)bounds).center.set(position);
		if (Math.abs(position.x - destination.x) <= 0.1
				&& Math.abs(position.y - destination.y) <= 0.1) {
			position.x = destination.x;
			position.y = destination.y;
			Explode();
		}
	}
	private void Explode(){
		state = STATE_EXPLODE;
	}
	public void update(float deltaTime) {
		switch (state) {
		case STATE_MOVE:
			updateMove(deltaTime);
			break;
		case STATE_EXPLODE:
			break;
		default:
			break;
		}
	}

}
