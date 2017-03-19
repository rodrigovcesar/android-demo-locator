# android-demo-locator
Demo project of Android using queue. I readpted this demo to show here. It's a simple app that gets your geocoordinates and shows them
on screen. 
The black area it's the data sent to the server and if there is no connection, then the coordinate will wait in queue.

The main libraries used in this demo was Google Maps Fused Location and the class ArrayBlockingQueue from Java. Actually, it's the concept
of Producers ans Consumers of the queue in different threads, usign AsyncTask from Android. 
