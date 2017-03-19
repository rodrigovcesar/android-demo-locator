package au.com.locator;

/**
 * Created by vasco on 31/01/2016.
 */
public interface DebugFeedback {

    void newLocationReceived(Location location);
    void sentLocation(Location location);
    void itemsInQueue(int quantity);

}
