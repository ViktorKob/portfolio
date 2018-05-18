package net.model.types;

public class GeoLocation {
	public final double longitude;
	public final double latitude;

	public GeoLocation(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	@Override
	public String toString() {
		return "(longitude: " + longitude + ", latitude: " + latitude + ")";
	}
}
