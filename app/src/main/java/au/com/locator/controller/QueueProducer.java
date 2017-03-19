package au.com.locator.controller;


import com.google.android.gms.location.LocationListener;

import java.util.concurrent.BlockingQueue;

import au.com.locator.DebugFeedback;
import au.com.locator.Location;


public class QueueProducer implements LocationListener {

    private BlockingQueue<Location> queue;
    private DebugFeedback debugFeedback;

    public QueueProducer(BlockingQueue<Location> queue, DebugFeedback debugFeedback){
        this.queue = queue;
        this.debugFeedback = debugFeedback;
    }


    @Override
    public void onLocationChanged(android.location.Location location) {
        Location local_location = new Location(location);
        this.debugFeedback.newLocationReceived(local_location);
        boolean add;
        do {
            add = queue.offer(local_location);
            if(add)
                debugFeedback.itemsInQueue(queue.size());
            else {
                queue.poll();
                debugFeedback.itemsInQueue(queue.size());
            }
        } while(!add);
    }


}
