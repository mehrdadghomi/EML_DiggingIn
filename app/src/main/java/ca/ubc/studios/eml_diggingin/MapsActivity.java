package ca.ubc.studios.eml_diggingin;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private GPSTracker gpsTracker;
    private Location userLocation;
    double user_latitude;
    double user_longitude;


    private TextView tvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Button getAssignment = findViewById(R.id.getAssignment);
        //Button back = findViewById(R.id.backButton);
        tvData = findViewById(R.id.tvJSONItem);

        getAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JSONTask().execute("http://sidl.es/testing/testingdigging-in/?di_download_json");
                Toast.makeText(MapsActivity.this,"This should display map markers", Toast.LENGTH_LONG).show();
                // Add a marker where the first assignment is

                //nathan_latitude = new JSONTask().getLatitude();
               // nathan_longitude = new JSONTask().getLongitude();

//                LatLng assignmentLatLng = new LatLng(nathan_latitude, nathan_longitude);
//                mMap.addMarker(new MarkerOptions().position(assignmentLatLng).title("An Assignment is Here"));

            }
        });


        gpsTracker = new GPSTracker(getApplicationContext());
        userLocation = gpsTracker.getLocation();

        user_latitude = userLocation.getLatitude();
        user_longitude = userLocation.getLongitude();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    public class JSONTask extends AsyncTask<String,String,String> {

        @Override
        public String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }


                String finalJSON = buffer.toString();
                //return buffer.toString();


                JSONArray parentArray = new JSONArray(finalJSON).getJSONArray(0);
                JSONObject finalObject = parentArray.getJSONObject(0);

                int nathan_id = finalObject.getInt("id");
                String nathan_String_latitude = finalObject.getString("latitude");
                String nathan_String_longitude = finalObject.getString("longitude");
                String nathan_name = finalObject.getString("name");
                String nathan_description = finalObject.getString("description");

                double nathan_latitude = Double.parseDouble(nathan_String_latitude);
                double nathan_longitude = Double.parseDouble(nathan_String_longitude);

                LatLng nathan_LatLng = new LatLng(nathan_latitude, nathan_longitude);



                //return finalJSON;
                return "Latitude is:" + nathan_String_latitude + "Longitude is:" + nathan_String_longitude + "Name is:" + nathan_name + "Description is:" + nathan_description;

            } catch(MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

//        public double getLatitude(){
//
//            return this.nathan_latitude;
//        }
//
//        public double getLongitude(){
//            return this.nathan_longitude;
//        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // HERE IS WHERE ASYNC STORED DATA ARE FINALIZED
            tvData.setText(result);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker where user is
        LatLng userLatLng = new LatLng(user_latitude, user_longitude);
        mMap.addMarker(new MarkerOptions().position(userLatLng).title("DiggingIn User is Here"));


        // move the camera and control the zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 10f));
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

    }
}
