package au.com.locator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vasco on 31/01/2016.
 */
public class Location {

    private final int TIME_LIMIT = 3600000;

    private final long lgn_time;
    private final android.location.Location location;

    private static final SimpleDateFormat DF_DATE = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DF_HOUR = new SimpleDateFormat("hh:mm:ss");

    public Location(android.location.Location location) {
        this.location = location;
        this.lgn_time = System.currentTimeMillis();
    }


    public JSONObject getJson(Date dataHora) throws JSONException {
        JSONObject json = new JSONObject();
        json.putOpt("latitude", location.getLatitude());
        json.putOpt("longitude", location.getLongitude());
        json.putOpt("speed", location.getSpeed());
        json.putOpt("send_at", DF_DATE.format(dataHora)+"T"+ DF_HOUR.format(dataHora)+"Z");
        return json;
    }

    public long elapsedTime() {
        return System.currentTimeMillis() - this.lgn_time;
    }

    public boolean isExpired() {
        return this.elapsedTime() > TIME_LIMIT;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Latitude: "+ this.getLocation().getLatitude() + "\n");
        sb.append("Longitude: " + this.getLocation().getLongitude() +"\n");
        sb.append("Speed(m/s): "+ this.getLocation().getSpeed()+"\n");
        sb.append("Date/Hour: "+ new Date(this.lgn_time)+"\n");
        sb.append("Altitude: "+ this.getLocation().getAltitude());
        return sb.toString();
    }


    public android.location.Location getLocation() {
        return location;
    }
}
