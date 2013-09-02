package org.apache.cordova.plugin.geo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by edewit on 9/2/13.
 */
public class GeofenceStore {
    public static final String KEYS = "org.apache.cordova.plugin.geo.KEY_LATITUDE";
    public static final String KEY_LATITUDE = "org.apache.cordova.plugin.geo.KEY_LATITUDE";
    public static final String KEY_LONGITUDE = "org.apache.cordova.plugin.geo.KEY_LONGITUDE";
    public static final String KEY_RADIUS = "org.apache.cordova.plugin.geo.KEY_RADIUS";
    public static final String KEY_EXPIRATION_DURATION =
            "org.apache.cordova.plugin.geo.KEY_EXPIRATION_DURATION";

    public static final String KEY_TRANSITION_TYPE =
            "org.apache.cordova.plugin.geo.KEY_TRANSITION_TYPE";

    public static final String KEY_PREFIX = "org.apache.cordova.plugin.geo.KEY";

    public static final long INVALID_LONG_VALUE = -999l;
    public static final float INVALID_FLOAT_VALUE = -999.0f;
    public static final int INVALID_INT_VALUE = -999;


    private final SharedPreferences preferences;
    private static final String PREFERENCE_NAME = GeofenceStore.class.getSimpleName();

    private Set<String> regionIds;

    public GeofenceStore(Context context) {
        preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public Set<String> getGeofences() {
        this.regionIds = preferences.getStringSet(KEYS, new HashSet<String>());
        return regionIds;
    }

    /**
     * Returns a stored geofence by its id, or returns {@code null}
     * if it's not found.
     *
     * @param id The ID of a stored geofence
     * @return A geofence defined by its center and radius. See
     * {@link Geofence}
     */
    public Geofence getGeofence(String id) {

        double lat = preferences.getFloat(
                getGeofenceFieldKey(id, KEY_LATITUDE),
                INVALID_FLOAT_VALUE);

        double lng = preferences.getFloat(
                getGeofenceFieldKey(id, KEY_LONGITUDE),
                INVALID_FLOAT_VALUE);

        float radius = preferences.getFloat(
                getGeofenceFieldKey(id, KEY_RADIUS),
                INVALID_FLOAT_VALUE);

        long expirationDuration = preferences.getLong(
                getGeofenceFieldKey(id, KEY_EXPIRATION_DURATION),
                INVALID_LONG_VALUE);

        int transitionType = preferences.getInt(
                getGeofenceFieldKey(id, KEY_TRANSITION_TYPE),
                INVALID_INT_VALUE);

        if (lat != INVALID_FLOAT_VALUE &&
                lng != INVALID_FLOAT_VALUE &&
                radius != INVALID_FLOAT_VALUE &&
                expirationDuration != INVALID_LONG_VALUE &&
                transitionType != INVALID_INT_VALUE) {

            return new Geofence(id, lat, lng, radius, expirationDuration, transitionType);
        } else {
            return null;
        }
    }

    /**
     * Save a geofence.
     *
     * @param geofence The {@link Geofence} containing the
     *                 values you want to save in SharedPreferences
     */
    public void setGeofence(String id, Geofence geofence) {
        Editor editor = preferences.edit();

        editor.putFloat(getGeofenceFieldKey(id, KEY_LATITUDE), (float) geofence.getLatitude());
        editor.putFloat(getGeofenceFieldKey(id, KEY_LONGITUDE), (float) geofence.getLongitude());
        editor.putFloat(getGeofenceFieldKey(id, KEY_RADIUS), geofence.getRadius());

        editor.putLong(getGeofenceFieldKey(id, KEY_EXPIRATION_DURATION),
                geofence.getExpirationDuration());

        editor.putInt(getGeofenceFieldKey(id, KEY_TRANSITION_TYPE), geofence.getTransitionType());

        regionIds.add(id);
        editor.putStringSet(KEYS, regionIds);
        editor.commit();
    }

    public void clearGeofence(String id) {
        Editor editor = preferences.edit();
        editor.remove(getGeofenceFieldKey(id, KEY_LATITUDE));
        editor.remove(getGeofenceFieldKey(id, KEY_LONGITUDE));
        editor.remove(getGeofenceFieldKey(id, KEY_RADIUS));
        editor.remove(getGeofenceFieldKey(id, KEY_EXPIRATION_DURATION));
        editor.remove(getGeofenceFieldKey(id, KEY_TRANSITION_TYPE));
        regionIds.remove(id);
        editor.putStringSet(KEYS, regionIds);
        editor.commit();
    }

    /**
     * Given a Geofence object's ID and the name of a field
     * (for example, KEY_LATITUDE), return the key name of the
     * object's values in SharedPreferences.
     *
     * @param id        The ID of a Geofence object
     * @param fieldName The field represented by the key
     * @return The full key name of a value in SharedPreferences
     */
    private String getGeofenceFieldKey(String id, String fieldName) {
        return KEY_PREFIX + id + "_" + fieldName;
    }
}
