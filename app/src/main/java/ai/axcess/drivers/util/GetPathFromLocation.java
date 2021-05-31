package ai.axcess.drivers.util;


import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetPathFromLocation extends AsyncTask<String, Void, List<Routes>> {

    private String TAG = "GetPathFromLocation";
    private String API_KEY;
    private LatLng source, destination;
    private DirectionPointListener resultCallback;
    private boolean walkLine;
    private boolean alternatives;

    public GetPathFromLocation(LatLng source, LatLng destination, boolean alternatives, boolean walkLine,
                               String API_KEY, DirectionPointListener resultCallback) {
        this.API_KEY = API_KEY;
        this.source = source;
        this.destination = destination;
        this.walkLine = walkLine;
        this.alternatives = alternatives;
        this.resultCallback = resultCallback;
    }

    public String getUrl() {
        String str_origin = "origin=" + source.latitude + "," + source.longitude;
        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String units = "units=imperial";
        String alternatives = "alternatives=" + this.alternatives;
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + alternatives + "&" + units;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + API_KEY;
        return url;
    }

    @Override
    protected List<Routes> doInBackground(String... url) {
        List<Routes> routes = new ArrayList<>();
        try {
            //read DIRECTION Api response
            String json = routeResponse(getUrl());
            if (json.equals("")) return null;
            // Starts parsing data
            JSONObject jsonObject;
            jsonObject = new JSONObject(json);

            routes = new DirectionHelper().parse(jsonObject, source, destination,walkLine);
            if (routes != null && routes.size() > 0) {
                return routes;
            } else {
                return null;
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception in Executing Routes : " + e.toString());
            return null;
        }

    }

    @Override
    protected void onPostExecute(List<Routes> routes) {
        super.onPostExecute(routes);
        if (resultCallback != null && routes != null)
            resultCallback.onPath(routes);
    }

    private String routeResponse(String url) {
        InputStream inputStream = null;
        HttpURLConnection connection = null;
        String data = "";
        try {
            URL directionUrl = new URL(url);
            connection = (HttpURLConnection) directionUrl.openConnection();
            connection.connect();
            inputStream = connection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            data = stringBuffer.toString();
            bufferedReader.close();

        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.toString());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.disconnect();
        }
        return data;
    }
}
