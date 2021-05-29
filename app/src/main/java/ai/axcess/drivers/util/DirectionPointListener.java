package ai.axcess.drivers.util;

import com.google.android.gms.maps.model.PolylineOptions;

public interface DirectionPointListener {
    void onPath(PolylineOptions polyLine);
}