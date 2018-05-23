package net.thomas.portfolio.shared_objects.hbase_index.model.types;

public class GeoLocation {
	public double longitude;
	public double latitude;

	public GeoLocation(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public String toString() {
		return "(longitude: " + longitude + ", latitude: " + latitude + ")";
	}
}
