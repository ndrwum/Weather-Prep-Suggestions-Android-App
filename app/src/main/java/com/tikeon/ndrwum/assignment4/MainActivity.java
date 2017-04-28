package com.tikeon.ndrwum.assignment4;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, MediaPlayer.OnPreparedListener {

    private static MediaPlayer mPlayer;
    protected GoogleApiClient mGoogleApiClient;
    EditText cityName;
    TextView resultTextView;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        cityName = (EditText) findViewById(R.id.cityName);
        resultTextView = (TextView) findViewById(R.id.resultTextView);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onLocationChanged(Location location) {
        loc=location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 7);
        mMap.animateCamera(cameraUpdate);
    }

    public void findWeather(View view) {
        resultTextView.setText("");
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        try {
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            String encodedLatitude = URLEncoder.encode(String.valueOf(loc.getLatitude()), "UTF-8");
            String encodedLongitude = URLEncoder.encode(String.valueOf(loc.getLongitude()), "UTF-8");

            DownloadTask task = new DownloadTask();
            if (!encodedCityName.equals("")) {
                task.execute("http://api.openweathermap.org/data/2.5/forecast?q=" + encodedCityName + "&appid=df25a7ba0fc76dce7171effa6c0cedd3"+"&mode=json");
            } else if (loc!=null){
                task.execute("http://api.openweathermap.org/data/2.5/forecast?lat=" + encodedLatitude + "&lon=" + encodedLongitude + "&appid=df25a7ba0fc76dce7171effa6c0cedd3");
            }

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

            Toast.makeText(getApplicationContext(), "Could not find weather: 1", Toast.LENGTH_LONG).show();
        }
    }

    public void playMusic(int i) {
        String sss = "https://www.partnersinrhyme.com/pir/libs/media/Analog_Boys_2.wav";
        if (mPlayer == null || !mPlayer.isPlaying()) {
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                if (i==1) {
                    mPlayer.setDataSource(sss);
                }
                if (i==2) {
                    mPlayer.setDataSource(sss);
                }
                if (i==3) {
                    mPlayer.setDataSource(sss);
                }
                if (i==4) {
                    mPlayer.setDataSource(sss);
                }
                if (i==5) {
                    mPlayer.setDataSource(sss);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(this);
        }
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (Exception e) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
                    }
                });


            }

            return null;
        }

        /*Parsing JSON weather info from API*/
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Double f_temp=null;
            String weather = null;
            String description = null;
            try {

                String message = "";

                JSONObject jsonObject = (JSONObject) new JSONTokener(result).nextValue();

                String weatherInfo = jsonObject.getString("list");

                JSONArray arr = new JSONArray(weatherInfo);
            /*    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                currentDateTimeString = new SimpleDateFormat("MMM dd ha").format(new Date());
                Log.d("systime", currentDateTimeString);*/


                for (int i = 0; i < 4; i++) {

                    JSONObject tmp = arr.getJSONObject(i);
                    JSONObject temp1 = tmp.getJSONObject("main");
                    String temp = temp1.getString("temp");
                    String date_time = tmp.getString("dt_txt");
                    temp = String.valueOf(Double.valueOf(temp) * 9/5 - 459.67);
                    f_temp=Double.valueOf(temp);
                    Log.d("temp", f_temp.toString());
                    StringTokenizer token = new StringTokenizer(date_time);
                    String date1 = token.nextToken();
                    String time1 = token.nextToken();
                    SimpleDateFormat d_input = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat t_input = new SimpleDateFormat("HH:mm:ss");
                    SimpleDateFormat d_output = new SimpleDateFormat("MMM dd");
                    SimpleDateFormat t_output = new SimpleDateFormat("ha");
                    Date dt, dt2;
                    try {
                        dt = d_input.parse(date1);
                        dt2 = t_input.parse(time1);
                        date1 = d_output.format(dt);
                        time1 = t_output.format(dt2);
                        Log.d("time", date1+" "+time1);
                    } catch (ParseException e) {
                    e.printStackTrace();
                }
                    weather = tmp.getString("weather");
                    JSONArray mainWeather = new JSONArray(weather);

                    for (int j = 0; j < mainWeather.length(); j++) {
                        tmp = mainWeather.getJSONObject(j);
                        weather += tmp.getString("main");
                        description += tmp.getString("description");
                    }
                }

                /*Weather Conditionals*/
                if (weather != "" && description != "") {
                    assert description != null;
                    description= description.toLowerCase();
                    if (description.contains("rain")) {
                        message += "There's going to be rain later today. ";
                        message += "So don't forget your rain shoes and an umbrella!" + "\r\n";
                        playMusic(1);
                    }
                    if (description.contains("snow")) {
                        message += "There's going to be snow later today. ";
                        message += "So don't forget to wear snow boots!" + "\r\n";
                        playMusic(2);
                    }
                    if (description.contains("windy")) {
                        message += "It's going to be windy later today. ";
                        if(f_temp>70) {
                            message += "But it's going to be pretty warm today! " ;
                            message += "So don't forget to wear a hat, sunscreen and keep hydrated!"+ "\r\n";
                        }
                        playMusic(3);
                    }
                    if (description.contains("clouds")) {
                        message += "It's going to be cloudy today."+"\r\n";
                        if(f_temp>70) {
                            message += "But it's going to be pretty warm today! ";
                            message += "So don't forget to wear a hat, sunscreen and keep hydrated!" + "\r\n";
                        }
                        playMusic(4);
                    }
                    if(f_temp<60) {
                        message += "A bit chilly today so don't forget to wear warm clothes!" + "\r\n";
                    }
                    else {
                        message += "It's going to be clear weather today! ";
                        if(f_temp>70){
                            message += "But it's going to be pretty warm today! " ;
                            message += "So don't forget to wear a hat, sunscreen and keep hydrated"+ "\r\n";
                        }
                        playMusic(5);
                    }
                }


                /*No Weather Data Retrieved*/
                if (message != "") {
                    resultTextView.setText(message);
                } else {
                    Toast.makeText(getApplicationContext(), "Could not find weather: 2", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather : 3", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayer.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mPlayer != null) {
            mPlayer.stop();
        }
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPlayer != null) {
            mPlayer.start();
        }
        if (mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPlayer != null) {
            mPlayer.pause();
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(2000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }}

