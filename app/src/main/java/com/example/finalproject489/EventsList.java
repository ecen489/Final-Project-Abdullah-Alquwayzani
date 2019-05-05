package com.example.finalproject489;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class EventsList extends AppCompatActivity {
    DatabaseReference db;
    StorageReference storageRef,uploadRef=null;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String chosenEvent;
    Uri filePath;
    Bitmap img;
    ArrayList<String> listofItems;
    ListView lv;
    RecyclerView rv;
    double[] LocationX;
    double[] LocationY;
    double locationx, locationy, locX, locY;
    LocationManager locationManager;
    StorageTask mUploadTask;

    private static final int cam_capture_image = 12358;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);
        db = FirebaseDatabase.getInstance().getReference("events");
        storageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        listofItems= new ArrayList<>();
        LocationX=new double[5];
        LocationY=new double[5];
        lv = (ListView) findViewById(R.id.listEvent);
        locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        if ( ContextCompat.checkSelfPermission( getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION )
                == PackageManager.PERMISSION_GRANTED || true ) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0, 0,LLoc);
        }
        Query query = db.orderByChild("locationx");
        query.addValueEventListener(EventsListner);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        uploadRef=storageRef.child("EventAtEvans");
                        getSharedPreferences("credentials",0).edit()
                                .putString("eventName","EventAtEvans");
                        locationx=LocationX[0];
                        locationy=LocationY[0];
                        Toast.makeText(EventsList.this,
                                "Evans",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        uploadRef=storageRef.child("EventAtZach");
                        getSharedPreferences("credentials",0).edit()
                                .putString("eventName","EventAtEvans");
                        locationx=LocationX[1];
                        locationy=LocationY[1];
                        Toast.makeText(EventsList.this,
                                "Zach",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    public void uploadImage(View view){
        double d = distance(locationy,locationx,locY,locX,"k");
        Toast.makeText(EventsList.this,
                Double.toString(d),
                Toast.LENGTH_SHORT).show();
        /* Note this function was modified for demo on virtual device with fixed location signal */
        if(uploadRef!=null && d-1541>0.5) { /////////////// Change this line for physical device ///////////////////
            Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(picIntent, cam_capture_image);
        }
    }
    LocationListener LLoc= new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                locY=location.getLatitude();
                locX=location.getLongitude();
                locationManager.removeUpdates(this);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == cam_capture_image && resultCode == RESULT_OK) {
            img = (Bitmap) data.getExtras().get("data");
            //filePath = getImageUri(getApplicationContext(),img);
            uploadFile();
        }
    }
    ValueEventListener EventsListner = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                NotificationManager notification_manager =(NotificationManager)getApplicationContext()
                        .getSystemService(getApplicationContext()
                                .NOTIFICATION_SERVICE);
                String channel_id ="3000";
                CharSequence name ="Channel Name";
                String description ="Chanel Description";
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel mChannel =new NotificationChannel(channel_id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.BLUE);
                notification_manager.createNotificationChannel(mChannel);
                NotificationCompat.Builder notification_builder=new NotificationCompat.Builder(getApplicationContext(),channel_id);
                Notification note = notification_builder.setContentTitle("New Event Added")
                        .setSmallIcon(R.drawable.icon)
                        .setContentText("Check the new event")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .build();
                notification_manager.notify(1, note);
                listofItems.clear();
                int i=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    EventNow event12 = snapshot.getValue(EventNow.class);
                    LocationX[i]=event12.getlocationx();
                    LocationY[i]=event12.getlocationy();
                    i=i+1;
                    String s=event12.getdescription();
                    listofItems.add(s);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(EventsList.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, listofItems);
                lv.setAdapter(adapter);
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            //log error
            Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
        }
    };
    public void signOut(View view){
        if(user!=null) {
            mAuth.signOut();
            user = null;
            Toast.makeText(getApplicationContext(),"Signed Out",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EventsList.this, MainActivity.class);
            startActivity(intent);
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, "title", "Title");
        return Uri.parse(path);
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    public double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit == "K") {
                dist = dist * 1.609344;
            } else if (unit == "N") {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }
    private void uploadFile() {
        if (filePath != null) {
            String childFile=getSharedPreferences("credentials",0)
                    .getString("eventName","");
            StorageReference fileReference = storageRef.child(childFile)
                    .child(System.currentTimeMillis()
                    + "." + getFileExtension(filePath));

            mUploadTask = fileReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(EventsList.this,
                                    "Upload successful",
                                    Toast.LENGTH_LONG).show();
                            Upload upload = new Upload("",
                                    taskSnapshot.getMetadata().getReference()
                                            .getDownloadUrl().toString());
                            String uploadId = db.child("photos").child("-zach").push().getKey();
                            db.child("photos").child("-zach").child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EventsList.this,
                                    e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
