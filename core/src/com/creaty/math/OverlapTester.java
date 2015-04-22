package com.creaty.math;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

public class OverlapTester {
	public static boolean overlapCircles(Circle c1, Circle c2) {
		float distance = Vector2.dst2(c1.x, c1.y, c2.x, c2.y);
		float radiusSum = c1.radius + c2.radius;
		return distance <= radiusSum * radiusSum;
	}

	public static boolean overlapRectangles(Rectangle r1, Rectangle r2) {
		if (r1.x < r2.x + r2.width
				&& r1.x + r1.width > r2.x
				&& r1.y < r2.y + r2.height
				&& r1.y + r1.height > r2.y)
			return true;
		else
			return false;
	}

	public static boolean overlapCircleRectangle(Circle c, Rectangle r) {
		float closestX = c.x;
		float closestY = c.y;

		if (c.x < r.x) {
			closestX = r.x;
		} else if (c.x > r.x + r.width) {
			closestX = r.x + r.width;
		}

		if (c.y < r.y) {
			closestY = r.y;
		} else if (c.y > r.y + r.height) {
			closestY = r.y + r.height;
		}

		return Vector2.dst2(c.x, c.y, closestX, closestY) < c.radius * c.radius;
	}

	public static boolean pointInCircle(Circle c, Vector2 p) {
		return Vector2.dst2(c.x, c.y, p.x, p.y) < c.radius * c.radius;
	}

	public static boolean pointInCircle(Circle c, float x, float y) {
		return Vector2.dst2(c.x, c.y, x, y) < c.radius * c.radius;
	}

	public static boolean pointInRectangle(Rectangle r, Vector2 p) {
		return r.x <= p.x && r.x + r.width >= p.x
				&& r.y <= p.y && r.y + r.height >= p.y;
	}

	public static boolean pointInRectangle(Rectangle r, float x, float y) {
		return r.x <= x && r.x + r.width >= x
				&& r.y <= y && r.y + r.height >= y;
	}
}