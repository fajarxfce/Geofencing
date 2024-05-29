package com.example.geofencing;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class KmlUtil {
    public List<LatLng> parseKMLFile(int rawResourceId, Context context) {
        List<LatLng> latLngList = new ArrayList<>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            InputStream inputStream = context.getResources().openRawResource(rawResourceId);
            parser.setInput(inputStream, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.getName().equals("coordinates")) {
                    String coordinates = parser.nextText();
                    String[] splitCoordinates = coordinates.trim().split(" ");
                    for (String coord : splitCoordinates) {
                        String[] latlong = coord.split(",");
                        double longitude = Double.parseDouble(latlong[0]);
                        double latitude = Double.parseDouble(latlong[1]);
                        latLngList.add(new LatLng(latitude, longitude));
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return latLngList;
    }
}
