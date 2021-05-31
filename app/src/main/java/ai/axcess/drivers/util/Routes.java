package ai.axcess.drivers.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class Routes {
    //the points for the driving route Polyline
    public List<LatLng> drivingRoute;
    //the points for the walking route source-> start of driving route
    public List<LatLng> sourceWalk;
    //the points for the walking route driving end of the driving route -> dest
    public List<LatLng> destWalk;
    //the route duration
    public double duration;
    public String text_duration;
    //the route distance
    public double distance;
    public String text_distance;

    public int route_id;
}