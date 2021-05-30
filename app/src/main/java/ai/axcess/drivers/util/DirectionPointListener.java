package ai.axcess.drivers.util;

import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public interface DirectionPointListener {
    void onPath(List<RoutePoints> routes);
}
