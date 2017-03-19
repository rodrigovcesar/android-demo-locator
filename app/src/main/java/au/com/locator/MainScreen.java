package au.com.locator;

import android.app.FragmentTransaction;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import au.com.locator.controller.QueueProducer;
import au.com.locator.databinding.ActivityMainScreenBinding;
import au.com.locator.support.WebClient;
import au.com.locator.task.QueueConsumption;


public class MainScreen extends FragmentActivity implements OnMapReadyCallback {

    private GoogleApiClient googleApiClient;
    private LocationListener listener;
    private QueueConsumption enviaDados;
    private GoogleMap map;
    private PolylineOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        BlockingQueue queue = new ArrayBlockingQueue<>(360);
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        WebClient webclient = new WebClient(cm);
        final ActivityMainScreenBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main_screen);
        options = new PolylineOptions().width(5).color(Color.RED);

        this.googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        LocationRequest request = LocationRequest.create();
                        request.setInterval(10000);
                        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        request.setSmallestDisplacement(5);

                        LocationServices.FusedLocationApi
                                .requestLocationUpdates(googleApiClient, request, listener);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        MainScreen.this.googleApiClient.connect();
                    }
                }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {


                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Toast.makeText(MainScreen.this, connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .build();

        MapFragment mapFragment = MapFragment.newInstance();
        FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.add(R.id.mostra_mapa, mapFragment);
        tx.commit();
        mapFragment.getMapAsync(this);


        DebugFeedback debugFeedback = new DebugFeedback() {
            @Override
            public void newLocationReceived(Location localizacao) {
                binding.setGeneratedText(localizacao.toString());

                LatLng localAtual = new LatLng(localizacao.getLocation().getLatitude(), localizacao.getLocation().getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(localAtual, 15.5f), 4000, null);
                options.add(localAtual);
                map.addPolyline(options);
            }

            @Override
            public void sentLocation(Location localizacao) {
                binding.setSentText(localizacao.toString());
            }

            @Override
            public void itemsInQueue(int qtde) {
                binding.setQueueText("Items in queue: " + qtde);
            }
        };

        this.enviaDados = new QueueConsumption(queue, webclient, debugFeedback);
        this.enviaDados.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        this.listener = new QueueProducer(queue, debugFeedback);
    }


        @Override
        protected void onResume () {
            super.onResume();
            this.googleApiClient.connect();
        }


        @Override
        protected void onDestroy () {
            this.closeListener();
            super.onDestroy();
        }


        private void closeListener () {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(this.googleApiClient, this.listener);
            this.googleApiClient.disconnect();

        }

        @Override
        public void onMapReady (GoogleMap map){
            this.map = map;
            this.map.setMyLocationEnabled(true);

        }


    }