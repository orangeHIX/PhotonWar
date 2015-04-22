package com.badlogic.androidgames.framework.model;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.creaty.math.Vector2;

/** 具有方形边界的游戏实体 */
public class GameObject {

	/** 游戏实体中心在世界中的位置 */
	public final Vector2 position;
	/** 游戏实体的边界 */
	public final Shape2D bounds;

	/**
	 * 如果游戏实体模型是方形的，调用这个方法构造
	 * 
	 * @param x
	 *            游戏实体模型的中心在游戏世界中的x轴位置
	 * @param y
	 *            游戏实体模型的中心在游戏世界中的y轴位置
	 * @param width
	 *            游戏实体模型的宽度
	 * @param height
	 *            游戏实体模型的高度
	 */
	public GameObject(float x, float y, float width, float height) {
		this.position = new Vector2(x, y);
		this.bounds = new Rectangle(x - width / 2, y - height / 2, width,
				height);
	}

	/**
	 * 如果游戏实体模型是圆形的，调用这个方法构造
	 * 
	 * @param x
	 *            游戏实体模型的中心在游戏世界中的x轴位置
	 * @param y
	 *            游戏实体模型的中心在游戏世界中的y轴位置
	 * @param width
	 *            游戏实体模型半径
	 */
	public GameObject(float x, float y, float radius) {
		this.position = new Vector2(x, y);
		this.bounds = new Circle(x, y, radius);
	}

	// public void setPosition(Vector2 other) {
	// position.set(other);
	// updateBounds();
	// }
	//
	// public void setPosition(float x, float y) {
	// position.set(x, y);
	// updateBounds();
	// }
	//
	// protected void updateBounds() {
	// if (bounds instanceof Circle) {
	// ((Circle) bounds).center.set(position);
	// } else if (bounds instanceof Rectangle) {
	// Rectangle rec = (Rectangle) bounds;
	// rec.lowerLeft.set(position.x - rec.width / 2, position.y
	// - rec.height / 2);
	// }
	// }
}
