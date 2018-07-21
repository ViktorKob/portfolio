package net.thomas.portfolio.map;

import static java.awt.Color.BLACK;
import static java.lang.Math.random;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

public class HeatmapTileTest extends JFrame {
	private static final long serialVersionUID = 1L;
	private final HeatmapTile tile;
	private Image tileRender;

	public HeatmapTileTest(int sampleSize) {
		setSize(1500, 1000);
		setLocation(1000, 400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		tile = new HeatmapTile(new Point2D.Double(-90.0, -90.0), 180.0, 180.0);
		for (int i = 0; i < sampleSize; i++) {
			tile.addPoint(new RandomGeoPoint());
		}
	}

	public void cacheTile() {
		tileRender = tile.renderAsImage(400, 400);
	}

	@Override
	public void paint(Graphics g) {
		if (tileRender == null) {
			cacheTile();
		}
		g.setColor(BLACK);
		g.drawRect(99, 99, 401, 401);
		g.drawImage(tileRender, 100, 100, null, null);
	}

	public static void main(String[] args) {
		final HeatmapTileTest test = new HeatmapTileTest(6000);
		test.setVisible(true);
	}
}

class RandomGeoPoint implements GeoPoint {

	private final EventType eventType;
	private final double longitude;
	private final double latitude;

	public RandomGeoPoint() {
		eventType = EventType.values()[(int) (random() * EventType.values().length)];
		longitude = random() * 180 - 90;
		latitude = random() * 180 - 90;
	}

	@Override
	public EventType getType() {
		return eventType;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}

	@Override
	public double getLatitude() {
		return latitude;
	}
}