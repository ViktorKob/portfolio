package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static java.lang.Double.doubleToLongBits;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoLocation {
	public double longitude;
	public double latitude;

	public GeoLocation() {
	}

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
	public int hashCode() {
		final long longitudeAsLong = doubleToLongBits(longitude);
		final long latitudeAsLong = doubleToLongBits(latitude);
		int hash = (int) (longitudeAsLong ^ longitudeAsLong >>> 32);
		hash = 37 * hash + (int) (latitudeAsLong ^ latitudeAsLong >>> 32);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GeoLocation) {
			final GeoLocation other = (GeoLocation) obj;
			return longitude == other.longitude && latitude == other.latitude;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "(longitude: " + longitude + ", latitude: " + latitude + ")";
	}
}
