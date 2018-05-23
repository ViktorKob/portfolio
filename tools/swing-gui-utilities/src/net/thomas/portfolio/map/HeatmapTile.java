package net.thomas.portfolio.map;

import static java.awt.Color.LIGHT_GRAY;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static net.thomas.portfolio.map.EventType.BASE_COLOR;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.LinkedList;

public class HeatmapTile implements MapTile {
	private static final int POINT_SIZE = 7;
	private static final MapLayerShape SHAPE = new PointShape(POINT_SIZE);
	private static final EventType[] EVENT_TYPES = EventType.values();

	private final Collection<GeoPoint> points;
	private final Point2D lowerLeftGeoCoordinate;
	private final double tileWidthAsDegrees;
	private final double tileHeightAsDegrees;

	public HeatmapTile(Point2D lowerLeftGeoCoordinate, double tileWidthAsDegrees, double tileHeightAsDegrees) {
		this.lowerLeftGeoCoordinate = lowerLeftGeoCoordinate;
		this.tileWidthAsDegrees = tileWidthAsDegrees;
		this.tileHeightAsDegrees = tileHeightAsDegrees;
		points = new LinkedList<>();
	}

	public void addPoint(GeoPoint point) {
		synchronized (points) {
			points.add(point);
		}
	}

	@Override
	public Image renderAsImage(int width, int height) {
		if (width > 0 && height > 0) {
			final long stamp = System.nanoTime();
			final int[][][] map = buildMapData(width, height);
			final Image image = buildImage(width, height, map);
			System.out.println((System.nanoTime() - stamp) / 1000000 + " milis spend building image with " + points.size() + " points.");
			return image;
		} else {
			return null;
		}
	}

	private int[][][] buildMapData(int width, int height) {
		final MultiLayerIntegerMapBuilder mapBuilder = new MultiLayerIntegerMapBuilder(width, height, EVENT_TYPES.length);
		final double longitudeToPixelRatio = width / tileWidthAsDegrees;
		final double latitudeToPixelRatio = height / tileHeightAsDegrees;
		synchronized (points) {
			for (final GeoPoint geoPoint : points) {
				final int pixelPosX = normalize(longitudeToPixelRatio * (geoPoint.getLongitude() - lowerLeftGeoCoordinate.getX()), width);
				final int pixelPosY = normalize(height - latitudeToPixelRatio * (geoPoint.getLatitude() - lowerLeftGeoCoordinate.getY()), height);
				mapBuilder.addElement(geoPoint.getType().ordinal(), pixelPosX, pixelPosY, SHAPE);
			}
		}
		return mapBuilder.build();
	}

	private int normalize(double value, int max) {
		if (value < 0) {
			return 0;
		} else if (value >= max) {
			return max - 1;
		} else {
			return (int) value;
		}
	}

	private Image buildImage(int width, int height, final int[][][] map) {
		final BufferedImage image = new BufferedImage(width, height, TYPE_INT_ARGB);
		setBackground(width, height, image);
		addPointData(map, image);
		return image;
	}

	private void setBackground(int width, int height, final BufferedImage image) {
		final Graphics2D graphics = image.createGraphics();
		graphics.setColor(LIGHT_GRAY);
		graphics.fillRect(0, 0, width, height);
	}

	private void addPointData(final int[][][] map, final BufferedImage image) {
		for (int x = 0; x < map.length; x++) {
			for (int y = 0; y < map.length; y++) {
				final Integer color = calculateColor(map[x][y]);
				if (color != null) {
					image.setRGB(x, y, color);
				}
			}
		}
	}

	private Integer calculateColor(int[] typeCounts) {
		final short[] colorSum = new short[3];
		int colorCount = 0;
		for (int i = 0; i < typeCounts.length; i++) {
			if (typeCounts[i] > 0) {
				colorCount++;
				addToSum(colorSum, EVENT_TYPES[i].calculateColor(typeCounts[i]));
			}
		}
		if (colorCount > 0) {
			colorSum[0] = (short) (colorSum[0] / colorCount + BASE_COLOR[0]);
			colorSum[1] = (short) (colorSum[1] / colorCount + BASE_COLOR[1]);
			colorSum[2] = (short) (colorSum[2] / colorCount + BASE_COLOR[2]);
			return (128 & 0xFF) << 24 | (colorSum[0] & 0xFF) << 16 | (colorSum[1] & 0xFF) << 8 | (colorSum[2] & 0xFF) << 0;
		} else {
			return null;
		}
	}

	private void addToSum(short[] colorSum, byte[] calculatedColor) {
		colorSum[0] += calculatedColor[0];
		colorSum[1] += calculatedColor[1];
		colorSum[2] += calculatedColor[2];
	}
}
