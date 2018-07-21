package net.thomas.portfolio.map;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

import java.awt.Point;

public class PointShape implements MapLayerShape {
	private final Point center;
	private final int[][] shape;

	public PointShape(int radius) {
		final int squaredRadius = radius * radius;
		center = new Point(radius, radius);
		shape = new int[radius * 2 + 1][radius * 2 + 1];
		for (int x = 0; x < radius * 2 + 1; x++) {
			for (int y = 0; y < radius * 2 + 1; y++) {
				final int deltaX = abs(radius - x);
				final int deltaY = abs(radius - y);
				final double squaredDistance = deltaX * deltaX + deltaY * deltaY;
				if (squaredDistance < squaredRadius) {
					shape[x][y] = radius - (int) sqrt(squaredDistance);
				}
			}
		}
	}

	@Override
	public Point getCenter() {
		return center;
	}

	@Override
	public int[][] getShapeMap() {
		return shape;
	}
}