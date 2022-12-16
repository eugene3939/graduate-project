package com.example.greenyourlife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.valueOf;

public class treasure_subpage_m extends AppCompatActivity implements GoogleMap.OnMarkerClickListener {
    //find now location,shop type,one specific shop,my path
    Button btNow, btSearch, btSearchOne,btUserPath;
    //spinner choose type
    Spinner spType;
    //switch compact navigation
    SwitchCompat switchCompat;
    Boolean boolGo = false;
    Boolean add_point = false;

    //the destination user enter
    EditText InputDestination;

    //distance seekBar
    SeekBar sb_radius;
    TextView txt_dis;
    Integer seak_radius = 1000;
    //Integer seak_radius = 300000;

    //adapted speed for userPath
    Integer adaptSpeed = 25;

    //local shops set type
    final String[] placeNameList = {"環保旅店", "環保標章旅館", "綠色餐廳","綠色友善餐廳","連鎖型綠色商店","非連鎖型綠色商店"};
    //local shops set content
    ArrayList<LatLng> shopLatLng = new ArrayList<LatLng>();
    ArrayList<String> shopName = new ArrayList<String>();
    ArrayList<String> shopIntro = new ArrayList<String>();
    //only show nearBy shop
    ArrayList<String> shopNearBy = new ArrayList<String>();
    //save user path
    ArrayList<LatLng> userPath = new ArrayList<LatLng>();
    //not adapt speed path
    ArrayList<Boolean> pathSpeCheck = new ArrayList<Boolean>();
    double distance = 0.0;  //user path length

    //back ground path start point
    LatLng preBackgroundPath = null;
    //back ground path distance
    double background_distance = 0.0;

    //check permission
    Boolean accessOk = false;
    //Initialize variable
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;

    //set map don't pass main program
    GoogleMap map_global = null;

    //save user path
    Geocoder geocoder;
    LocationRequest locationRequest;
    Object Tag;
    Marker userLocationMarker = null;
    Circle userLocationAccuracyCircle = null;

    //initialize location
    Location location;
    //previous location
    LatLng preLocation = null;
    //last time draw path
    Integer lastPathTimes = 0;

    //now location information
    TextView Lat1, Lng1, Spe1;

    //choose user car
    Spinner carSpinner;
    List<userCar> userCarList;
    private car_adapter carAdapter;
    int selectCar;

    //bottomNavigationView
    BottomNavigationView bottomNavigationView;

    //background thread
    Thread thread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_subpage_m);
        //get bottonNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //set main page
        bottomNavigationView.setSelectedItemId(R.id.bt_page_treasure);

        //change listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bt_page_qrcode:
                        startActivity(new Intent(getApplicationContext(), QRcode_subpage_m.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bt_page_miles:
                        startActivity(new Intent(getApplicationContext(), login_main_page.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bt_page_treasure:
                        return true;
                    case R.id.bt_page_friends:
                        startActivity(new Intent(getApplicationContext(), friends_supage_m.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bt_page_greenshop:
                        startActivity(new Intent(getApplicationContext(), greenshop_subpage_m.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        //Assign variable
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        //Initialize fused location
        client = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this);

        //location request setting
        locationRequest = LocationRequest.create();
        //every 5 seconds
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //about now location
        btNow = findViewById(R.id.button_now);
        //now location information
        Lat1 = findViewById(R.id.tv_lat);
        Lng1 = findViewById(R.id.tv_lon);
        Spe1 = findViewById(R.id.tv_speed);
        //about spinner
        spType = findViewById(R.id.sp_type);
        btSearch = findViewById(R.id.bt_search);
        //about search shop
        btSearch = findViewById(R.id.bt_search);
        //user search only one shop
        btSearchOne = findViewById(R.id.bt_search_one);
        InputDestination = findViewById(R.id.et_destination);
        //user path
        btUserPath = findViewById(R.id.button_myPath);

        //distance seekBar
        sb_radius = findViewById(R.id.sb_distance);
        txt_dis = findViewById(R.id.txt_radius);

        //initialize user car spinner
        carSpinner = findViewById(R.id.sp_userCar);
        userCarList = addCarData();
        carAdapter = new car_adapter(treasure_subpage_m.this,R.layout.user_car,userCarList);
        carSpinner.setAdapter(carAdapter);

        //choose user car
        carSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(treasure_subpage_m.this, "你選擇了 "+ userCarList.get(position).getName(), Toast.LENGTH_SHORT).show();
                selectCar = position;
                //reset car information
                sync();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //set adapter on spinner
        spType.setAdapter(new ArrayAdapter<>(treasure_subpage_m.this, android.R.layout.simple_spinner_dropdown_item, placeNameList));

        //set info adapter
        //map_global.setInfoWindowAdapter(new locationInfoWindowAdapter(MainActivity.this));

        //check permission
        permissionCheck();

        //load local shop information
        setLocalshops();

        //switch compact navigation
        switchCompat = findViewById(R.id.switch_go_check);
        //save switch state in shared preference
        //save key value permanent
        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
        switchCompat.setChecked(sharedPreferences.getBoolean("value", true));

        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchCompat.isChecked()) {
                    //when switch checked
                    editor.putBoolean("value", true);
                    editor.apply();
                    switchCompat.setChecked(true);
                    boolGo = true;

                } else {
                    //when switch not checked
                    editor.putBoolean("value", false);
                    editor.apply();
                    switchCompat.setChecked(false);
                    boolGo = false;

                }
            }
        });

        //set distance seekBar
        sb_radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seak_radius = progress * 10;
                txt_dis.setText("搜尋半徑｜" + progress * 10 + "/1000 m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //show drag start information
                Toast.makeText(treasure_subpage_m.this, "拉動以調整距離", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //show end information
                Toast.makeText(treasure_subpage_m.this, "調整完畢", Toast.LENGTH_SHORT).show();
            }
        });

        //show user location now
        btNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });

        //user search shop type
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //which item user pick
                int clickItem = spType.getSelectedItemPosition();
                //spinner link to relative location
                ChooseShopType(clickItem);
            }
        });

        //user search for one shop
        btSearchOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserChooseShop(InputDestination);
            }
        });

        //set marker on click listener
        //map_global.setOnMarkerClickListener(this);

        //show user path
        btUserPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserPath();
            }
        });

        if (accessOk = true) {
            //get new location and sync
            getCurrentLocation();

        } else {
            //When permission denied
            //request permission
            ActivityCompat.requestPermissions(treasure_subpage_m.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    //add car data to adapter
    private List<userCar> addCarData(){
        List<userCar> list = new ArrayList<>();

        list.add(new userCar(R.drawable.car1,"1號車"));
        list.add(new userCar(R.drawable.car2,"2號車"));
        list.add(new userCar(R.drawable.car3,"3號車"));
        list.add(new userCar(R.drawable.car5,"4號車"));
        list.add(new userCar(R.drawable.face1,"臉A"));
        list.add(new userCar(R.drawable.face2,"臉B"));
        list.add(new userCar(R.drawable.face3,"臉C"));

        return list;
    }

    //reset new location
    private void getCurrentLocation() {
        //initialize task location
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
        //reset location
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location locationIn) {
                //when sucess
                location = locationIn;
                if (location != null) {
                    //sync map
                    sync();
                }
            }
        });
    }

    //show now location marker
    private void sync() {
        //Sync map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                //clear the past marker,circle
                googleMap.clear();
                //set global map
                if (map_global == null) {
                    map_global = googleMap;
                }

                //Initialize lat lng
                LatLng latLng = new LatLng(location.getLatitude()
                        , location.getLongitude());

                //Create marker options
                MarkerOptions options = new MarkerOptions().position(latLng)
                        .title("現在位置");
                //Zoom map
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            }
        });

        //reset car information
        userLocationMarker = null;
        userLocationAccuracyCircle = null;
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d(String.valueOf(Tag),"onLocationResult" + locationResult.getLastLocation());

            if (map_global != null){
                //marker movement depend on user
                setUserLocationMarker(locationResult.getLastLocation());
            }
        }
    };

    //set user location marker
    private void setUserLocationMarker(Location location){

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

        //first set
        if (userLocationMarker == null){

            //create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("現在位置");
            //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_car));
            markerOptions.icon(BitmapDescriptorFactory.fromResource(userCarList.get(selectCar).getImage()));
            markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float) 0.5,(float) 0.5);
            userLocationMarker = map_global.addMarker(markerOptions);
            //camera keep user location
            //map_global.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17))

            //check user speed
            double changeSpeed = changeSpeedUnit(location.getSpeed());  //from m/s ro km/hr

            //add user path
            if(changeSpeed < adaptSpeed){
                pathSpeCheck.add(true); //green path
            }else {
                pathSpeCheck.add(false);
            }
            userPath.add(latLng);

            //map_main LatLng information
            Lat1.setText(valueOf(Math.round(location.getLatitude()*10000)/10000.0));
            Lng1.setText(valueOf(Math.round(location.getLongitude()*10000)/10000.0));
            Spe1.setText(changeSpeed+" km/hr");

        } else {
            //use previous marker
            userLocationMarker.setPosition(latLng);
            userLocationMarker.setRotation(location.getBearing());

            //check user speed
            double changeSpeed = changeSpeedUnit(location.getSpeed());  //from m/s ro km/hr

            //add user path
            if(changeSpeed < adaptSpeed){
                pathSpeCheck.add(true); //green path
            }else {
                pathSpeCheck.add(false);
            }
            userPath.add(latLng);

            //map_main LatLng information
            Lat1.setText(valueOf(Math.round(location.getLatitude()*10000)/10000.0));
            Lng1.setText(valueOf(Math.round(location.getLongitude()*10000)/10000.0));
            Spe1.setText(changeSpeed+" km/hr");
        }

        //show accuracy circle
        if(userLocationAccuracyCircle == null){
            //create circle around user
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.strokeWidth(4);
            circleOptions.strokeColor(Color.argb(255,255,0,0));
            circleOptions.fillColor(Color.argb(32,255,0,0));
            circleOptions.radius(location.getAccuracy());
            userLocationAccuracyCircle = map_global.addCircle(circleOptions);
        }else {
            //use previous setting
            userLocationAccuracyCircle.setCenter(latLng);
            userLocationAccuracyCircle.setRadius(location.getAccuracy());
        }
    }

    //start location update
    private void startLocationUpdates() {
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
        client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    //stop location update
    private void stopLocationUpdates(){
        client.removeLocationUpdates(locationCallback);
    }

    //on app start
    @Override
    protected void onStart() {
        super.onStart();
        startLocationUpdates();
    }

    //on app stop
    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    //on pause
    @Override
    protected void onPause() {
        super.onPause();
        //new add
        if(thread == null){
            thread = new Thread()
            {
                @Override
                public void run() {
                    try {
                        int num =0;
                        //Log.i(String.valueOf(treasure_subpage_m.this),"進入thread");
                        while (thread != null){
                            //keep update location inf
                            startLocationUpdates();

                            //save background route to share reference
                            SharedPreferences shareRef = getSharedPreferences("background", MODE_PRIVATE);
                            SharedPreferences.Editor editor = shareRef.edit();
                            editor.putFloat(num + "lat", Float.valueOf(String.valueOf(location.getLatitude()))).commit();
                            editor.putFloat(num + "lng" , Float.valueOf(String.valueOf(location.getLongitude()))).commit();
                            editor.putInt("size",num).commit();
                            //Log.i(String.valueOf(treasure_subpage_m.this),num + "經度");
                            //Log.i(String.valueOf(treasure_subpage_m.this),"經度: " + (float) location.getLatitude());
                            num+=1;

                            //background_LatLng.add(new LatLng(location.getLatitude(),location.getLongitude()));
                            SystemClock.sleep(1000);
                        }

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            };

            thread.start();
        }
    }

    //on resume
    @Override
    protected void onResume() {
        super.onResume();
        thread.interrupted();

        //calculate cutting route
        connectAllcuttingRoute();
    }

    //calculate  background cutting route
    private void connectAllcuttingRoute() {
        //Log.i(String.valueOf(treasure_subpage_m.this),"連結進入");

        //share reference size
        int size = getSharedPreferences("background",MODE_PRIVATE).getInt("size",0);
        //Log.i(String.valueOf(treasure_subpage_m.this),"大小: " + size);
        //check after back ground
        if (size!= 0){
            for(int i = 0; i < size; i++){
                Float p_lat =  getSharedPreferences("background",MODE_PRIVATE).getFloat(i + "lat", (float) 0.0000);
                Float p_lng =  getSharedPreferences("background",MODE_PRIVATE).getFloat(i + "lng", (float) 0.0000);
                Log.i(String.valueOf(treasure_subpage_m.this),i + "經度: " + p_lat);
                Log.i(String.valueOf(treasure_subpage_m.this),i + "緯度: " + p_lng);
            }
        }
    }

    //show user path
    private void showUserPath() {
        //first draw path
        if (lastPathTimes == 0){
            int temp = 0;
            for (LatLng latLng: userPath){
                if (preLocation == null){
                    preLocation = latLng;
                }else {
                    //link preLocation and location
                    PolylineOptions polylineOptions = new PolylineOptions();
                    //add polyline
                    polylineOptions.add(preLocation,latLng);
                    polylineOptions.width(7);
                    //set color
                    if(pathSpeCheck.get(temp).equals(true) && pathSpeCheck.get(temp-1).equals(true)){   //under speed check
                        polylineOptions.color(Color.BLACK);
                        //calculate distance to m
                        distance += gps2m(preLocation.latitude,preLocation.longitude,latLng.latitude,latLng.longitude);
                    }else {
                        polylineOptions.color(Color.RED);
                    }

                    //draw polyline
                    map_global.addPolyline(polylineOptions);

                    //set new preLocation
                    preLocation = latLng;
                    Log.d(String.valueOf(Tag),"preLocation in" + preLocation);
                }
                temp++;
                //set last path end
                lastPathTimes++;
            }

            //calculate c point
            getPathCpoint(distance+ background_distance);
            //Toast.makeText(treasure_subpage_m.this, distance + " 公尺", Toast.LENGTH_SHORT).show();

            //show background path
            //drawBackgroundPath();

            //second draw path
        }else {
            int temp =0;
            for (LatLng latLng: userPath){
                if (temp>=lastPathTimes){
                    if (preLocation == null){
                        preLocation = latLng;
                    }else {
                        //link preLocation and location
                        PolylineOptions polylineOptions = new PolylineOptions();
                        //add polyline
                        polylineOptions.add(preLocation,latLng);
                        polylineOptions.width(7);
                        //set color
                        if(pathSpeCheck.get(temp).equals(true) && pathSpeCheck.get(temp-1).equals(true)){  //under speed check
                            polylineOptions.color(Color.BLACK);
                            //calculate distance to m
                            distance += gps2m(preLocation.latitude,preLocation.longitude,latLng.latitude,latLng.longitude);
                        }else {
                            polylineOptions.color(Color.RED);
                        }

                        //draw polyline
                        map_global.addPolyline(polylineOptions);

                        //set new preLocation
                        preLocation = latLng;
                        Log.d(String.valueOf(Tag),"preLocation in" + preLocation);
                    }
                    //set last path end
                    lastPathTimes++;
                }
                //keep add until to lastPathTimes
                temp++;
            }

            //Toast.makeText(treasure_subpage_m.this, distance  + " 公尺", Toast.LENGTH_SHORT).show();

            //show background path
            //drawBackgroundPath();

            //calculate c point
            getPathCpoint(distance + background_distance);
        }
    }

    //show background path
    private void drawBackgroundPath() {
        //share reference size
        int size = getSharedPreferences("background",MODE_PRIVATE).getInt("size",0);
        //Log.i(String.valueOf(treasure_subpage_m.this),"大小: " + size);
        //check after back ground
        if (size!= 0){  //whether back ground runs
            for(int i = 0; i < size; i++){
                Float p_lat =  getSharedPreferences("background",MODE_PRIVATE).getFloat(i + "lat", (float) 0.0000);
                Float p_lng =  getSharedPreferences("background",MODE_PRIVATE).getFloat(i + "lng", (float) 0.0000);
                Log.i(String.valueOf(treasure_subpage_m.this),"次數: " + i);
                //Log.i(String.valueOf(treasure_subpage_m.this),"經度: " + p_lat);
                //Log.i(String.valueOf(treasure_subpage_m.this),"緯度: " + p_lng);

                if (preBackgroundPath == null){    //set first point
                    preBackgroundPath = new LatLng(p_lat,p_lng);
                }else { //draw lines
                    //link preLocation and location
                    PolylineOptions polylineOptions = new PolylineOptions();
                    //add polyline
                    polylineOptions.add(preBackgroundPath, new LatLng(p_lat,p_lng));
                    polylineOptions.width(7);
                    polylineOptions.color(Color.BLUE);
                    //draw polyline
                    map_global.addPolyline(polylineOptions);
                    //add to background path
                    background_distance += gps2m(preBackgroundPath.latitude,preBackgroundPath.longitude,p_lat,p_lng);

                    //set new pre Background Location
                    preBackgroundPath = new LatLng(p_lat,p_lng);
                }
                if(i==size-1){
                    //refresh background size
                    SharedPreferences shareRef = getSharedPreferences("background", MODE_PRIVATE);
                    shareRef.edit().putInt("size",0).commit();
                    preBackgroundPath = null; //recycle background start point
                    //Log.i(String.valueOf(treasure_subpage_m.this),"里程: " + background_distance);
                }
            }
        }
    }

    //change path to c point
    private void getPathCpoint(double x) {
        double cPoint = 0.0;
        // y = (x - a )^3 +b
        //b = a^3
        double a = 100.0;
        double b = Math.pow(a,3);   //a^3
        x /= 1000;  //change x to km
        //cPoint = Math.pow( (x-a) ,3) + b;
        cPoint = x*100/10;   //新公式

        //dialog setting
        DialogStyle dialogStyle = DialogStyle.FLAT;
        DialogType dialogType = DialogType.SUCCESS;
        DialogAnimation dialogAnimation = DialogAnimation.SHRINK;

        //safe check
        add_point = true;
        //update c points
        updatePoints((int) cPoint);

        //initialize dialog
        AestheticDialog.Builder builder = new AestheticDialog.Builder(treasure_subpage_m.this,dialogStyle,dialogType);
        builder.setTitle("你得到: " + (int) cPoint + "碳積分");
        builder.setMessage("你走了: " + distance + (int)background_distance +"公尺");
        builder.setAnimation(dialogAnimation);
        builder.show();
    }

    //update mission points
    private void updatePoints(Integer plus) {
        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("User");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        dbr.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0 ){
                    if (add_point.equals(true)) { //prevent infinite loop
                        //update user point
                        Integer userPoint = Integer.valueOf(snapshot.child("carbonPoints").getValue().toString()) + plus;
                        Map<String,Object> updateMap = new HashMap<String, Object>();
                        updateMap.put("carbonPoints",userPoint);
                        dbr.child(mAuth.getCurrentUser().getUid()).updateChildren(updateMap);
                        add_point = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //check permission
    private void permissionCheck() {
        //check permission
        if (ActivityCompat.checkSelfPermission(treasure_subpage_m.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //permission grated
            accessOk = true;
        } else {
            //When permission denied
            //request permission
            ActivityCompat.requestPermissions(treasure_subpage_m.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            accessOk = false;
        }
    }

    //check request code
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 44){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //When permission grated
                //Call method
                //getCurrentLocation();
                getCurrentLocation();
            }
        }
    }

    //show shop user pick
    private void ChooseShopType(int clickItem) {
        map_global.clear();
        //set limit radius for local shop
        seLatLngRadius(location);
        //marker option
        MarkerOptions options = new MarkerOptions();

        //n'st number to compare
        int temp = 0;
        //find placeNametest[n] == shopIntro[n]
        for (LatLng point : shopLatLng) {
            //in the search radius
            if (shopNearBy.get(temp).equals(new String("true"))){
                //find placeNameList equals to shopIntro
                if (placeNameList[clickItem].equals(shopIntro.get(temp))) {
                    options.position(point);
                    options.title(valueOf(shopName.get(temp)));
                    options.snippet(valueOf(shopIntro.get(temp)));
                    map_global.addMarker(options);
                    //map_global.setInfoWindowAdapter(new locationInfoWindowAdapter(MainActivity.this));
                    map_global.setOnMarkerClickListener(this);
                }
            }
            temp++;
        }
        //reset car information
        userLocationMarker = null;
        userLocationAccuracyCircle = null;
    }

    //find the shop user input
    private void UserChooseShop(EditText InputDestination) {
        //marker option
        MarkerOptions options = new MarkerOptions();

        //clear map
        map_global.clear();
        //set limit radius for local shop
        seLatLngRadius(location);

        //n'st number to compare
        int temp =0;
        //find placeNametest[n] == shopIntro[n]
        for (String name : shopName ) {
            //find placeNameList equals to shopIntro
            if(name.contains(InputDestination.getText().toString())){
                //in search radius
                if (shopNearBy.get(temp).equals(new String("true"))) {
                    options.position(shopLatLng.get(temp));
                    options.title(valueOf(shopName.get(temp)));
                    options.snippet(valueOf(shopIntro.get(temp)));
                    Marker marker = map_global.addMarker(options);
                    marker.setTag(location);
                    map_global.setOnMarkerClickListener(this);
                }
            }
            temp++;
        }
        //reset car information
        userLocationMarker = null;
        userLocationAccuracyCircle = null;
    }

    //insert location inf
    private void setLocalshops() {
        //Kaohsiung
        readFirebaseData("hotel_G");
        readFirebaseData("res_G");
        readFirebaseData("store_G");
        //Taipei
        //readFirebaseData("hotel");
        //readFirebaseData("res");
        //readFirebaseData("store_T");
    }

    //read shop data with file name
    private void readFirebaseData(String fileName) {
        //get realtime data base information
        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child(fileName);

        dbr.addValueEventListener(new ValueEventListener() {
            int test = 0;
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //get random named location file
                Set<String> set = new HashSet<String>();
                //get hotel file data set
                Iterator i = snapshot.getChildren().iterator();
                //set all hotel file name
                while (i.hasNext()){
                    //set have all hotel file name
                    set.add(((DataSnapshot)i.next()).getKey());
                    //get all children from hotel file name set
                }

                //get all children from hotel file name set
                for (String filename: set){
                    //get child file name
                    DatabaseReference dbr2 = dbr.child(filename);
                    dbr2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            //more than one child
                            if (snapshot.exists() && snapshot.getChildrenCount() > 0 ){
                                test+=1;
                                //set shop name
                                String name = snapshot.child("name").getValue().toString();
                                String type = snapshot.child("cat").getValue().toString();
                                Double lat = Double.parseDouble((String) snapshot.child("lat").getValue());
                                Double lng = Double.parseDouble((String) snapshot.child("lng").getValue());

                                shopName.add(name);
                                shopIntro.add(type);
                                shopNearBy.add(new String("true"));
                                shopLatLng.add(new LatLng( lat, lng));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //show the place user choose
        //Toast.makeText(this, marker.getTitle(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, marker.getPosition().toString(), Toast.LENGTH_SHORT).show();

        if (switchCompat.isChecked()){
            //draw the path from user in
            DisplatTrack(location,marker.getTitle());
        }

        shopDetails(marker.getTitle());
        return false;
    }

    //show shop details
    private void shopDetails(String title) {
        //create bottomSheetDialog
        BottomSheetDialog dialog = new BottomSheetDialog(treasure_subpage_m.this);
        dialog.setContentView(R.layout.shop_bottom_sheet_dialog);
        dialog.setCanceledOnTouchOutside(false);

        TextView name = dialog.findViewById(R.id.sh_name);
        TextView type = dialog.findViewById(R.id.sh_cat);
        TextView address = dialog.findViewById(R.id.sh_address);
        TextView phone = dialog.findViewById(R.id.sh_phone);
//        ImageView picture = dialog.findViewById(R.id.sh_photo); //店家圖片

        //view Kaohsiung database information
        final String[] databaseSet = {"hotel_G", "res_G", "store_G"};
        //view Taipei database information
        //final String[] databaseSet = ("hotel","res","store_T");

        for (String file :databaseSet){
            //get realtime data base information
            DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child(file);

            dbr.addValueEventListener(new ValueEventListener() {
                //int test = 0;
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //get random named location file
                    Set<String> set = new HashSet<String>();
                    //get hotel file data set
                    Iterator i = snapshot.getChildren().iterator();
                    //set all hotel file name
                    while (i.hasNext()){
                        //set have all hotel file name
                        set.add(((DataSnapshot)i.next()).getKey());
                        //get all children from hotel file name set
                    }

                    //get all children from hotel file name set
                    for (String filename: set){
                        //get child file name
                        DatabaseReference dbr2 = dbr.child(filename);
                        dbr2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                //more than one child
                                if (snapshot.child("name").getValue().toString().equals(title)){
                                    //set shop name
                                    //String uname = snapshot.child("name").getValue().toString();
                                    String utype = snapshot.child("cat").getValue().toString();
                                    String uaddress = snapshot.child("address").getValue().toString();
                                    String uphone = snapshot.child("phone").getValue().toString();

                                    name.setText(title);
                                    type.setText(utype);
                                    address.setText(uaddress);
                                    phone.setText("電話：" + uphone);

                                    //photo type check
                                    switch (file){ //店家圖片
//                                        case "hotel_G":
//                                            picture.setImageResource(R.drawable.ic_hotel);
//                                            break;
//                                        case "res_G":
//                                            picture.setImageResource(R.drawable.ic_restaurant);
//                                            break;
//                                        case "store_G":
//                                            picture.setImageResource(R.drawable.ic_store);
//                                            break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }
        dialog.show();
    }

    private void DisplatTrack(Location sSource, String sDestination) {
        //If the device doesn't have a map installed,then redirect it to play station
        try {
            //when map is installed
            //initialize url
            //both source and destination String
            //Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + sSource + "/" + sDestination);
            //one of source or destination location type
            Uri uri = Uri.parse("https://www.google.com.tw/maps/dir/" + sSource.getLatitude()+ ","+sSource.getLongitude() + "/" + sDestination);
            //initialize intend with action view
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //set package
            intent.setPackage("com.google.android.apps.maps");
            //set flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //start activity
            startActivity(intent);
        }catch (ActivityNotFoundException e){
            //when google map is not installed
            //initialize uri
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            //initialize action with action view
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            //set flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //start activity
            startActivity(intent);
        }
    }

    //only show the shop smaller than radius
    private void seLatLngRadius(Location location) {
        //map_global.clear();
        LatLng position = new LatLng(location.getLatitude(),location.getLongitude());
        // Add circle in 10000 meters
        CircleOptions circleOptions = new CircleOptions()
                .center(position)
                .radius(seak_radius);

        //find distance smaller than radius
        int temp=0;
        for (LatLng latLng : shopLatLng ) {
            //radius
            Double radius = gps2m(location.getLatitude(),location.getLongitude(),latLng.latitude,latLng.longitude);
            //radius is smaller than some number
            if(radius>seak_radius){
                shopNearBy.set(temp,new String("false"));
            }else{
                shopNearBy.set(temp,new String("true"));
            }
            temp++;
        }
        map_global.addCircle(circleOptions).setStrokeColor(Color.DKGRAY);
    }

    private final double EARTH_RADIUS = 6378137.0;

    //calculate 2 point distance
    private double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
        double radLat1 = (lat_a * Math.PI / 180.0);
        double radLat2 = (lat_b * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (lng_a - lng_b) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    //change speed unit to km/hr
    private double changeSpeedUnit(float speed) {
        double km = speed * 3.6;
        km = Math.round(km * 10000)/10000.0;
        return km;
    }
}