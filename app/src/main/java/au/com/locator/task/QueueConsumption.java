package au.com.locator.task;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.BlockingQueue;

import au.com.locator.DebugFeedback;
import au.com.locator.Location;
import au.com.locator.support.WebClient;


public class QueueConsumption extends AsyncTask<Void, Void, Void> {

    private final BlockingQueue<Location> queue;
    private final WebClient webClient;
    private final DebugFeedback debugFeedback;

    private static final int WAIT_CONNECTION = 2000;

    public QueueConsumption(BlockingQueue<Location> queue, WebClient webClient, DebugFeedback debugFeedback) {
        this.queue = queue;
        this.webClient = webClient;
        this.debugFeedback = debugFeedback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (true) {
            Location location;
            try {
                location = this.queue.take();
                debugFeedback.itemsInQueue(queue.size());
            } catch (InterruptedException e) {
                break;
            }
            while (!this.webClient.isConected() && !location.isExpired()) {
                // Wait the interval before verifying again if a connection is available
                // If the message expires, it gets out of loop
                synchronized (this) {
                    try {
                        this.wait(WAIT_CONNECTION);
                    } catch (InterruptedException e) {  }
                }
            }
            if (!location.isExpired()) {
                JSONObject json = null;
                try {
                    json = location.getJson(new Date());
                } catch (JSONException e) {
                    Log.e("Error JSON", e.getMessage(), e);
                }
                String reply = this.webClient.post(json);
                if (reply.startsWith("Error IO")) {
                    Log.e("Error sending", reply);
                } else {
                    this.debugFeedback.sentLocation(location);
                }
            } else {
                Log.i("Throw away message", "Thrown away message expired.");
            }
        }
        return null;
    }

}
