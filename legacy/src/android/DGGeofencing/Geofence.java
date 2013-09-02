package org.apache.cordova.plugin.geo;

/**
 * Created by edewit on 9/2/13.
 */
public class Geofence {
    private final String id;
    private final double latitude;
    private final double longitude;
    private final float radius;
    private long expirationDuration;
    private int transitionType;

    /**
     * @param geofenceId The Geofence's request ID
     * @param latitude   Latitude of the Geofence's center. The value is not checked for validity.
     * @param longitude  Longitude of the Geofence's center. The value is not checked for validity.
     * @param radius     Radius of the geofence circle. The value is not checked for validity
     * @param expiration Geofence expiration duration in milliseconds The value is not checked for
     *                   validity.
     * @param transition Type of Geofence transition. The value is not checked for validity.
     */
    public Geofence(
            String geofenceId,
            double latitude,
            double longitude,
            float radius,
            long expiration,
            int transition) {
        this.id = geofenceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.expirationDuration = expiration;
        this.transitionType = transition;
    }

    public Geofence(String id, double latitude, double longitude, float radius) {
        this(id, latitude, longitude, radius, -1, 0);
    }
    // Instance field getters

    /**
     * Get the geofence ID
     *
     * @return A Geofence ID
     */
    public String getId() {
        return id;
    }

    /**
     * Get the geofence latitude
     *
     * @return A latitude value
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Get the geofence longitude
     *
     * @return A longitude value
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Get the geofence radius
     *
     * @return A radius value
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Get the geofence expiration duration
     *
     * @return Expiration duration in milliseconds
     */
    public long getExpirationDuration() {
        return expirationDuration;
    }

    /**
     * Get the geofence transition type
     *
     * @return Transition type (see Geofence)
     */
    public int getTransitionType() {
        return transitionType;
    }
}
