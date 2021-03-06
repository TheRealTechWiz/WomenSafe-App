package com.kazim.womensafe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MainActivity2 extends DrawerDefault implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener
{

    Button btnShowLocation;private static final int REQUEST_CODE_PERMISSION = 2;
    TextInputLayout textInputLayout;
    int position = 0;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION,msg;
    GPSTracker gps;// GPSTracker class
    String[] phoneNo,ph = {"ph1","ph2","ph3","ph4","ph5"};
    SharedPreferences sp;int flag = 0;int p;
    EditText et;
    GestureDetector gestureDetector;


    @SuppressLint("ResourceType") @Override
    public void onCreate(Bundle savedInstanceState)
    {
        sp = MainActivity2.this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
//        p = sp.getInt("Light",3);
//        Intent theme = getIntent();
//        if(theme.hasExtra("theme"))
//            p = theme.getIntExtra("theme",3);
//        if(p == 3)
//        {
//            editor.putInt("Light",3);
//            setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);
//            editor.apply();
//        }
//        else if(p == 4)
//        {
//            editor.putInt("Light",4);
//            setTheme(R.style.AppTheme);
//            editor.apply();
//        }
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main2);
//        setDrawer();
        getLayoutInflater().inflate(R.layout.activity_main2,frameLayout);
        navigationView.setCheckedItem(R.id.sosactivity);
        activityName = "mainactivity2";

        gestureDetector = new GestureDetector(MainActivity2.this, MainActivity2.this);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>()
        {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse)
            {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });
        task.addOnFailureListener(this, new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                if (e instanceof ResolvableApiException)
                {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try
                    {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity2.this,100);
                    }
                    catch (IntentSender.SendIntentException sendEx)
                    {
                        // Ignore the error.
                    }
                }
            }
        });
        et = findViewById(R.id.et);
        textInputLayout = findViewById(R.id.jt);
        //getting the previous selected contacts
        phoneNo = new String[sp.getInt("length",0)];
        int i = 0;
        while (i < phoneNo.length)
        {
            phoneNo[i] = sp.getString(ph[i], null);
            //Toast.makeText(MainActivity2.this, "" + phoneNo[i], Toast.LENGTH_LONG).show();
            i++;
        }
        position = sp.getInt("pos",0);
        Intent pos = getIntent();
        if(pos.hasExtra("contact1"))
        {
            phoneNo = pos.getStringArrayExtra("contact1");
            //editor.clear();
            if(phoneNo[0]!=null)
                editor.putString(ph[0], PhoneNumberUtils.stripSeparators(phoneNo[0].split("\n")[1]));
            //Toast.makeText(MainActivity2.this, "" + ph[0], Toast.LENGTH_LONG).show();
            int j = 1;
            while(j < phoneNo.length && phoneNo[j]!=null)
            {
                editor.putString(ph[j], PhoneNumberUtils.stripSeparators(phoneNo[j].split("\n")[1]));
                //Toast.makeText(MainActivity2.this, "" + ph[j], Toast.LENGTH_LONG).show();
                j++;
            }
            editor.putInt("length",j);
            editor.apply();
        }
        if(pos.hasExtra("pos"))
        {
            position = pos.getIntExtra("pos",0);
            //editor.clear();
            editor.putInt("pos",position);
            editor.apply();
        }
        try
        {
            if (ActivityCompat.checkSelfPermission(this, mPermission) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{mPermission}, REQUEST_CODE_PERMISSION);
                // If any permission above not allowed by user, this condition will execute every time, else your else part will work
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        btnShowLocation = findViewById(R.id.b);
        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                gps = new GPSTracker(MainActivity2.this);// create class object
                if(gps.canGetLocation())// check if GPS enabled
                {
                    String uniqueID = UUID.randomUUID().toString();
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    //----------------------------------------------

                    //----------------------------------------------
                    msg = et.getText()+"\nHelp me...I'm at\n"+"https://www.google.com/maps?q="+latitude+","+longitude;
                    Toast.makeText(getApplicationContext(), msg+"", Toast.LENGTH_LONG).show();
                    send();
                }
                else
                {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
            }
        });
    }

    public void send()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
        {
            if(phoneNo.length == 0 && flag == 0)
            {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity2.this);
                builder1.setMessage("No Contacts is Selected");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
                AlertDialog alert11 = builder1.create();
                alert11.show();
                //Toast.makeText(MainActivity2.this,"Contact Not Selected",Toast.LENGTH_LONG).show();
            }
            else if(flag != 1)
            {
                try
                {
                    flag=0;
                    //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    //position = pos.getIntExtra("pos",0);
                    if(position == 0)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                        {
                            int i = 0;
                            while (i < phoneNo.length && phoneNo[i]!=null)
                            {
                                phoneNo[i]=PhoneNumberUtils.formatNumberToE164(phoneNo[i],"IN");
                                SmsManager.getSmsManagerForSubscriptionId(SmsManager.getDefaultSmsSubscriptionId()).sendTextMessage(phoneNo[i] + "", null, msg + "", null, null);
                                i++;
                            }
                        }
                        Toast.makeText(getApplicationContext(),"Message sent", Toast.LENGTH_LONG).show();
                    }
//                    else if(position == 1)
//                    {
//                        int i = 0;boolean isWhatsappInstalled = whatsappInstalledOrNot();
//                        if(isWhatsappInstalled)
//                        {
//                            Intent whatsapp = new Intent(Intent.ACTION_VIEW);
//                            while (i < phoneNo.length && phoneNo[i] != null)
//                            {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                    phoneNo[i] = PhoneNumberUtils.formatNumberToE164(phoneNo[i], "IN");
//                                }
//                                //Toast.makeText(this,phoneNo[i]+"",Toast.LENGTH_LONG).show();
//                                whatsapp.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNo[i] + "&text=" + URLEncoder.encode(msg, "UTF-8")));
//                                startActivity(whatsapp);
//                                onWindowFocusChanged(true);
//                                i++;
//                            }
//                            Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_LONG).show();
//                        }
//                        else
//                        {
//                            Uri uri = Uri.parse("market://details?id=com.whatsapp");
//                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//                            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
//                            startActivity(goToMarket);
//                        }
//                    }
//
                }
                catch (Exception ex)
                {
                    //Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    ex.printStackTrace();
                }
            }
        }
        else
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS},10);
            }
        }
    }
    //    private boolean whatsappInstalledOrNot()
//    {
//        PackageManager pm = getPackageManager();
//        boolean app_installed = false;
//        try
//        {
//            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
//            app_installed = true;
//        }
//        catch (PackageManager.NameNotFoundException e)
//        {
//            e.printStackTrace();
//        }
//        return app_installed;
//    }
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Intent i =getIntent();
        boolean open = i.getBooleanExtra("open", false);
        if (open)
        {
            //Log.e("start", "Activity started");
        }
    }
    /**
     * Showing google speech input dialog
     * */
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private void promptSpeechInput()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,getString(R.string.speech_prompt));
        try
        {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        }
        catch (ActivityNotFoundException a)
        {
            Toast.makeText(getApplicationContext(),"speech_not_supported",Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case REQ_CODE_SPEECH_INPUT:
            {
                if (resultCode == RESULT_OK && data!=null)
                {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //txtSpeechInput.setText(result.get(0));
                    if(result!=null)
                    {
                        String s = result.get(0);
                        if(s.contains("help"))
                        {
                            send();
                            //Toast.makeText(this,result.get(0)+"",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            }

        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        this.gestureDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }
    @Override
    public boolean onDown(MotionEvent event)
    {
        return true;
    }
    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,float velocityX, float velocityY)
    {
        return false;
    }
    @Override
    public void onLongPress(MotionEvent event)
    {

    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY)
    {
        return false;
    }
    @Override
    public void onShowPress(MotionEvent event)
    {

    }
    @Override
    public boolean onSingleTapUp(MotionEvent event)
    {
        return false;
    }
    int dc = 0;static long t;
    @Override
    public boolean onDoubleTap(MotionEvent event)
    {
        dc++;
        t = System.currentTimeMillis();
        if(dc>=3 && (System.currentTimeMillis()-t)==0)
        {
            dc = 0;
            promptSpeechInput();
            Toast.makeText(MainActivity2.this, "Say 'Help'...", Toast.LENGTH_LONG).show();
        }
        //Toast.makeText(this,System.currentTimeMillis()-t+"",Toast.LENGTH_SHORT).show();
        return true;
    }
    @Override
    public boolean onDoubleTapEvent(MotionEvent event)
    {
        return true;
    }
    @Override
    public boolean onSingleTapConfirmed(MotionEvent event)
    {
        return false;
    }

    public void add_contact(View view) {
        Toast.makeText(getApplicationContext(),"Please wait Contact list is Loading.....",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(),Contacts.class);
        startActivity(intent);
        finish();
    }

    public void sos_clicked(View view) {
        textInputLayout.setVisibility(View.VISIBLE);
        btnShowLocation.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(),"SOS Clicked",Toast.LENGTH_SHORT).show();
        //-------------------------------------------------------
        GPSTracker gps = new GPSTracker(MainActivity2.this);
        String uniqueID = UUID.randomUUID().toString();
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();
        Geocoder geocoder = new Geocoder(MainActivity2.this,Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
            if(addressList!=null){
                String address = addressList.get(0).getAddressLine(0);
                String state = addressList.get(0).getAdminArea();
                sendDataToServer(uniqueID,latitude,longitude,address,state);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Log.w("About Current Location","Curent address cannnot be detected");
            Toast.makeText(getApplicationContext(),"Failed to get Location",Toast.LENGTH_LONG).show();
        }
        //----------------------------------------------------------------------
    }
    public void sendDataToServer(String uniqueID,double latitude,double longitude,String address,String state) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "https://police-dashboard.herokuapp.com/api/locations/";
            JSONObject postparams = new JSONObject();
            try {
                postparams.put("android_id", uniqueID);
                postparams.put("latitude", Double.toString(latitude));
                postparams.put("longitude", Double.toString(longitude));
                postparams.put("address", address);
                postparams.put("state", state.toLowerCase());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, URL, postparams,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
            requestQueue.add(jsonObjReq);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Guide_Self(View view) {
        Intent i = new Intent(getApplicationContext(),SelfdefenceActivity.class);
        startActivity(i);
    }
}
