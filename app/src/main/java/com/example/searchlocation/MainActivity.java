package com.example.searchlocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView textView1,textView2;
    Button bt_location;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editText =(EditText) findViewById(R.id.edit_text);
        textView1 = findViewById(R.id.text_view1);
        bt_location = findViewById(R.id.bt_location);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);



        Places.initialize(getApplicationContext(),"AIzaSyCCOiNfJlVdhAuZdOiIlwj2tA7ZVRoPNwA" );
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                ,Place.Field.LAT_LNG,Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
                ,fieldList).build(MainActivity.this);
                startActivityForResult(intent, 100);
            }
        });

        bt_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this
                        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                    if (ActivityCompat.checkSelfPermission(MainActivity.this
                            , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        getLocation();
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ActivityCompat.checkSelfPermission(MainActivity.this
        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            getLocation();
        }
        else
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 44:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    getLocation();
                }
                return;
            }
        }
    }

    private void getLocation()
    {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location!=null)
                {
                    try {
                        Geocoder geocoder = new Geocoder(MainActivity.this,
                                Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(),1
                        );
                        String x =addresses.get(0).getLocality() + ", " + addresses.get(0).getAddressLine(0);
                        if(x.length()>24)
                        {
                            x=x.substring(0,24) + "...";
                        }
                        editText.setText(x);
                        x = addresses.get(0).getAddressLine(0);
                        if(x.length()>24)
                        {
                            x=x.substring(0,24) + "...";
                        }
                        textView1.setText(x);
//                        textView1.setText(addresses.get(0).getLocality() + " " + addresses.get(0).getAddressLine(0)
//                                );
//                        Log.e("rrr", "onComplete: "+(Html.fromHtml(
//                                "<font color='#6200EE'><br></font>"
//                                        + addresses.get(0).getLocality())) );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            String x =place.getName()+", "+place.getAddress();
            if(x.length()>24)
            {
                x=x.substring(0,24) + "...";
            }
            editText.setText(x);
            x = place.getAddress();
            if(x.length()>24)
            {
                x=x.substring(0,24) + "...";
            }
            textView1.setText(String.format(place.getName()));
        }
        else if(resultCode == AutocompleteActivity.RESULT_ERROR)
        {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(),status.getStatusMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}
