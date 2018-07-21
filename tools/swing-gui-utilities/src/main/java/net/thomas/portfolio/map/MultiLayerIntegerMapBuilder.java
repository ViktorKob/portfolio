package net.thomas.portfolio.map;

import java.awt.Point;

public class MultiLayerIntegerMapBuilder {
	private final int[][][] maps;

	public MultiLayerIntegerMapBuilder(int width, int height, int subMapCount) {
		maps = new int[width][height][subMapCount];
	}

	public void addElement(int subMapId, int pixelPosX, int pixelPosY, MapLayerShape shape) {
		final Point center = shape.getCenter();
		final Point corner = new Point(pixelPosX - center.x, pixelPosY - center.y);
		final int[][] shapeMap = shape.getShapeMap();
		for (int shapeX = 0; shapeX < shapeMap.length; shapeX++) {
			final int globalX = corner.x + shapeX;
			if (globalX > 0 && globalX < maps.length) {
				for (int shapeY = 0; shapeY < shapeMap[shapeX].length; shapeY++) {
					final int globalY = corner.y + shapeY;
					if (globalY > 0 && globalY < maps[globalX].length) {
						maps[globalX][globalY][subMapId] += shapeMap[shapeX][shapeY];
					}
				}
			}
		}
	}

	public int[][][] build() {
		return maps;
	}
}