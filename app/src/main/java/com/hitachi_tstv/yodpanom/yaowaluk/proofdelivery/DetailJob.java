package com.hitachi_tstv.yodpanom.yaowaluk.proofdelivery;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailJob extends Activity implements View.OnClickListener {
    //Explicit
    private TextView jobNoTextView, storeCodeTextView, storeNameTextView, arrivalTextView, intentToCallTextView;
    private ListView listView;
    private ImageView firstImageView, secondImageView, thirdImageView, fourthImageView;
    private Uri firstUri, secondUri, thirdUri, fourthUri;
    private Button arrivalButton, takeImgButton, confirmButton, signatureButton, contractButton, backButton, startLoadButton, finLoadButton, sendButton;
    private MyConstant myConstant = new MyConstant();
    private String[] loginStrings, containerStrings, quantityStrings;
    private String planDtl2_Id, planIdString, planDateString, pathFirstImageString, pathSecondImageString, pathThirdImageString, pathFourthImageString, driverUserNameString, getTimeDate,storeLatString,storeLngString,storeRadiusString;
    private boolean firstImgSendABoolean, secondImgSendABoolean, thirdImgSendABoolean, fourthImgSendABoolean, sendStatus;
    private LocationManager locationManager;
    private Criteria criteria;
    private Bitmap firstBitmap = null;
    private Bitmap secondBitmap = null;
    private Bitmap thirdBitmap = null;
    private Bitmap fourthBitmap = null;
    private String startLoadString, finLoadString;
    private int version;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_job);


        version = Build.VERSION.SDK_INT;

        //bind Widget
        bindWidget();

        //Set Value
        firstImgSendABoolean = false;
        secondImgSendABoolean = false;
        thirdImgSendABoolean = false;
        fourthImgSendABoolean = false;
        sendStatus = false;

        //Set Invisible The Button
        firstImageView.setVisibility(View.GONE);
        secondImageView.setVisibility(View.GONE);
        thirdImageView.setVisibility(View.GONE);
        confirmButton.setVisibility(View.GONE);
        signatureButton.setVisibility(View.GONE);
        sendButton.setVisibility(View.GONE);
//        takeImgButton.setVisibility(View.GONE);
//        startLoadButton.setVisibility(View.GONE);
//        finLoadButton.setVisibility(View.GONE);

        int picRes = R.drawable.picture;
        firstImageView.setImageResource(picRes);
        secondImageView.setImageResource(picRes);
        thirdImageView.setImageResource(picRes);
        fourthImageView.setImageResource(picRes);

        //Get Intent Data
        loginStrings = getIntent().getStringArrayExtra("Login");
        planDtl2_Id = getIntent().getStringExtra("planDtl2_id");
        planDateString = getIntent().getStringExtra("Date");
        planIdString = getIntent().getStringExtra("PlanId");
        driverUserNameString = loginStrings[2];

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        getTimeDate = dateFormat.format(date);

        //Load Data
        SynData synData = new SynData(DetailJob.this);
        synData.execute(myConstant.getUrlDetailWherePlanId(), planDtl2_Id);

        SynContainList synContainList = new SynContainList(DetailJob.this);
        synContainList.execute(myConstant.getUrlContainerList(), planDtl2_Id);

        //Get Event From Click Button or Image
        firstImageView.setOnClickListener(DetailJob.this);
        secondImageView.setOnClickListener(DetailJob.this);
        thirdImageView.setOnClickListener(DetailJob.this);
        fourthImageView.setOnClickListener(DetailJob.this);
        arrivalButton.setOnClickListener(DetailJob.this);
//        takeImgButton.setOnClickListener(DetailJob.this);
        confirmButton.setOnClickListener(DetailJob.this);
        signatureButton.setOnClickListener(DetailJob.this);
        contractButton.setOnClickListener(DetailJob.this);
        backButton.setOnClickListener(DetailJob.this);
        sendButton.setOnClickListener(DetailJob.this);
//        startLoadButton.setOnClickListener(DetailJob.this);
//        finLoadButton.setOnClickListener(DetailJob.this);

    }//Main Method

    private Bitmap rotateBitmap(Bitmap src) {

        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(90);
        Bitmap bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return bmp;
    }

    @Override
    public void onBackPressed() {

    }

    private class SynContainList extends AsyncTask<String, Void, String> {
        //Explicit
        private Context context;

        public SynContainList(Context context) {
            this.context = context;
            Log.d("Tag", "Construct SynContainList");
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder().add("isAdd", "true").add("planDtl2_id", strings[1]).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(strings[0]).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();

                return response.body().string();


            } catch (Exception e) {
                Log.d("12octV6", "e doInBack ==> " + e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONArray jsonArray = new JSONArray(s);

                containerStrings = new String[jsonArray.length()];
                quantityStrings = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    containerStrings[i] = jsonObject.getString("container");
                    quantityStrings[i] = jsonObject.getString("qty");
                }

                ContainerListAdapter containerListAdapter = new ContainerListAdapter(containerStrings, quantityStrings, context);
                listView.setAdapter(containerListAdapter);

            } catch (Exception e) {
                Log.d("12octV6", "e onPost ==> " + e);
            }
            Log.d("12octV6", "JSON ==> " + s);
        }
    }

//    private class SynUpdateLoad extends AsyncTask<String, Void, String> {
//        private Context context;
//        private String option;
//
//        public SynUpdateLoad(Context context, String option) {
//            this.context = context;
//            this.option = option;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            Log.d("Tag", "On Post JSON ==> " + s);
//            if (s.equals("OK")) {
//                if (option == "start") {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(context, getResources().getString(R.string.start_load), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                } else {
//
//                    startLoadButton.setVisibility(View.GONE);
//                    finLoadButton.setVisibility(View.GONE);
//                    confirmButton.setVisibility(View.VISIBLE);
//                    signatureButton.setVisibility(View.VISIBLE);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(context, getResources().getString(R.string.fin_load), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                }
//            } else {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(context, getResources().getString(R.string.save_incomp), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            try{
//                OkHttpClient okHttpClient = new OkHttpClient();
//                RequestBody requestBody = new FormEncodingBuilder()
//                        .add("isAdd", "true")
//                        .add("PlanDtl2_ID", planDtl2_Id)
//                        .add("Driver_Name", loginStrings[2])
//                        .add("option", option)
//                        .build();
//                Request.Builder builder = new Request.Builder();
//                Request request = builder.url(myConstant.getUrlUpdateLoad()).post(requestBody).build();
//                Response response = okHttpClient.newCall(request).execute();
//                return response.body().string();
//            } catch (IOException e) {
//                return "NOK2";
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0: //From Take Photo
                if (resultCode == RESULT_OK) {
                    Log.d("12octV5", "Take Photo and Save Success");
                }
                break;
            case 1://From Select Image First

                if (resultCode == RESULT_OK) {

//                    Bitmap photo = (Bitmap) data.getExtras().get("data");
//                    firstImageView.setImageBitmap(photo);
//                    Uri uri = data.getData();
//                    if (version >= Build.VERSION_CODES.KITKAT) {
//
//                        pathFirstImageString = myFindPathImageOverKitkat(firstUri);
//                    } else {
//
//                        pathFirstImageString = myFindPathImage(firstUri);
//                    }
                    pathFirstImageString = firstUri.getPath().toString();
                    Log.d("12octV5", "Path First ==> " + pathFirstImageString);
                    try {
                        firstBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(firstUri));
                        if (firstBitmap.getHeight() < firstBitmap.getWidth()) {
                            firstBitmap = rotateBitmap(firstBitmap);
                        }
                        firstImageView.setImageBitmap(firstBitmap);
                        Log.d("TAG", "Height ==> " + firstBitmap.getHeight() + " Width ==> " + firstBitmap.getWidth());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

//                     firstImageView.setImageBitmap(BitmapFactory.decodeFile(pathFirstImageString));

//                     firstImageView.setImageURI(firstUri);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {

                    pathSecondImageString = secondUri.toString();
                    Log.d("12octV5", "Path Second ==> " + pathSecondImageString);
                    try {
                        secondBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(secondUri));
                        if (secondBitmap.getHeight() < secondBitmap.getWidth()) {
                            secondBitmap = rotateBitmap(secondBitmap);
                        }
                        secondImageView.setImageBitmap(secondBitmap);
                        Log.d("TAG", "Height ==> " + secondBitmap.getHeight() + " Width ==> " + secondBitmap.getWidth());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


//                    Uri uri = data.getData();
//                    if (version >= Build.VERSION_CODES.KITKAT) {
//
//                        pathSecondImageString = myFindPathImageOverKitkat(uri);
//                    } else {
//
//                        pathSecondImageString = myFindPathImage(uri);
//                    }
//                    Log.d("12octV5", "Path Second ==> " + pathSecondImageString);
//                    try {
//                        secondBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
//                        if (secondBitmap.getHeight() < secondBitmap.getWidth()) {
//                            secondBitmap = rotateBitmap(secondBitmap);
//                        }
//                        Log.d("Tag", "Height ==> " + secondBitmap.getHeight() + " , Width ==> " + secondBitmap.getWidth());
//
//
//                        secondImageView.setImageBitmap(secondBitmap);
//
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }

                    // firstImageView.setImageBitmap(BitmapFactory.decodeFile(pathFirstImageString));

                    // firstImageView.setImageURI(uri);
                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {

                    pathThirdImageString = thirdUri.toString();
                    Log.d("12octV5", "Path Third ==> " + pathThirdImageString);
                    try {
                        thirdBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(thirdUri));
                        if (thirdBitmap.getHeight() < thirdBitmap.getWidth()) {
                            thirdBitmap = rotateBitmap(thirdBitmap);
                        }
                        thirdImageView.setImageBitmap(thirdBitmap);
                        Log.d("TAG", "Height ==> " + thirdBitmap.getHeight() + " Width ==> " + thirdBitmap.getWidth());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

//                    Uri uri = data.getData();
//                    if (version >= Build.VERSION_CODES.KITKAT) {
//
//                        pathThirdImageString = myFindPathImageOverKitkat(uri);
//                    } else {
//
//                        pathThirdImageString = myFindPathImage(uri);
//                    }
//                    Log.d("12octV5", "Path Third ==> " + pathThirdImageString);
//
//                    try {
//                        thirdBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
//                        if (thirdBitmap.getHeight() < thirdBitmap.getWidth()) {
//                            thirdBitmap = rotateBitmap(thirdBitmap);
//                        }
//                        thirdImageView.setImageBitmap(thirdBitmap);
//
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }

                    // firstImageView.setImageBitmap(BitmapFactory.decodeFile(pathFirstImageString));

                    // firstImageView.setImageURI(uri);
                }
                break;
            case 4:
                if (resultCode == RESULT_OK) {

                    pathFourthImageString = fourthUri.toString();
                    Log.d("12octV5", "Path Fourth ==> " + pathFourthImageString);
                    try {
                        fourthBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(fourthUri));
                        if (fourthBitmap.getHeight() < fourthBitmap.getWidth()) {
                            fourthBitmap = rotateBitmap(fourthBitmap);
                        }
                        fourthImageView.setImageBitmap(fourthBitmap);
                        Log.d("TAG", "Height ==> " + fourthBitmap.getHeight() + " Width ==> " + fourthBitmap.getWidth());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

//                    Uri uri = data.getData();
//                    if (version >= Build.VERSION_CODES.KITKAT) {
//
//                        pathThirdImageString = myFindPathImageOverKitkat(uri);
//                    } else {
//
//                        pathThirdImageString = myFindPathImage(uri);
//                    }
//                    Log.d("12octV5", "Path Third ==> " + pathThirdImageString);
//
//                    try {
//                        thirdBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
//                        if (thirdBitmap.getHeight() < thirdBitmap.getWidth()) {
//                            thirdBitmap = rotateBitmap(thirdBitmap);
//                        }
//                        thirdImageView.setImageBitmap(thirdBitmap);
//
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }

                    // firstImageView.setImageBitmap(BitmapFactory.decodeFile(pathFirstImageString));

                    // firstImageView.setImageURI(uri);
                }
                break;
        }

    }

//    private String myFindPathImage(Uri uri) {
//        String result = null;
//        String[] strings = {MediaStore.Images.Media.DATA};
//        Cursor cursor = getContentResolver().query(uri,
//                strings, null, null, null);
//
//        if (cursor != null) {
//            cursor.moveToFirst();
//            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
//            result = cursor.getString(index);
//            cursor.close();
//        } else {
//            result = uri.getPath();
//        }
//
//        cursor.close();
//        return result;
//
//    }
//
//    private String myFindPathImageOverKitkat(Uri uri) {
//
//        String wholeId = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//            wholeId = DocumentsContract.getDocumentId(uri);
//        }
//        String id = wholeId.split(":")[1];
//
//        String result = null;
//        String[] strings = {MediaStore.Images.Media.DATA};
//
//        String sel = MediaStore.Images.Media._ID + "=?";
//
//        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                strings, sel, new String[]{id}, null);
//
//        int columnIndex = cursor.getColumnIndex(strings[0]);
//        if (cursor.moveToFirst()) {
//            result = cursor.getString(columnIndex);
//        }
//        cursor.close();
////        if (cursor != null) {
////            cursor.moveToFirst();
////            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
////            result = cursor.getString(index);
////            cursor.close();
////        } else {
////            result = uri.getPath();
////        }
//
//        Log.d("Tag", "Result ==> " + result + ", URI ==> " + uri);
//        Log.d("Tag", "Cursor ==> " + cursor.toString());
//
//        return result;
//
//    }

    private double getMeterFromLatLong(double lat1, double lat2, double lng1, double lng2) {
        Math math = null;
        double result,diffLat,diffLong;
        lat1 = lat1 * (math.PI / 180);
        lat2 = lat2 * (math.PI / 180);
        lng1 = lng1 * (math.PI / 180);
        lng2 = lng2 * (math.PI / 180);

        diffLat = lat2 - lat1;
        diffLong = lng2 - lng1;
        result = (math.sin(diffLat / 2) * math.sin(diffLat / 2)) + (math.cos(lat1) * math.cos(lat2) * (math.sin(diffLong / 2) * math.sin(diffLong / 2)));
        result = 2 * math.atan2(math.sqrt(result), math.sqrt(1 - result));
        result = 6371 * result;
        return result;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView5: //First
                if (firstImgSendABoolean) {

                } else {
//                loadDateString = DateFormat.getDateTimeInstance().format(new Date());
                    File originalFile1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "first.png");

                    Intent cameraIntent1 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    firstUri = Uri.fromFile(originalFile1);
                    Log.d("TAG", "Path 1 " + firstUri);
                    cameraIntent1.putExtra(MediaStore.EXTRA_OUTPUT, firstUri);
                    startActivityForResult(cameraIntent1, 1);

//                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
//                intent1.setType("image/*");
//                startActivityForResult(Intent.createChooser(intent1, "Please Choose Photo"), 1);

                }

                break;
            case R.id.imageView4: //Second
                if (secondImgSendABoolean) {

                } else {
                    File originalFile2 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "second.png");

                    Intent cameraIntent2 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    secondUri = Uri.fromFile(originalFile2);
                    Log.d("TAG", "Path 2 " +   secondUri);
                    cameraIntent2.putExtra(MediaStore.EXTRA_OUTPUT, secondUri);
                    startActivityForResult(cameraIntent2, 2);

//                Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
//                intent2.setType("image/*");
//                startActivityForResult(Intent.createChooser(intent2, "Please Choose Photo"), 2);

                }

                break;
            case R.id.imageView3: //Third
                if (thirdImgSendABoolean) {

                } else {
                    File originalFile3 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "third.png");

                    Intent cameraIntent3 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    thirdUri = Uri.fromFile(originalFile3);
                    Log.d("TAG", "Path 3 " + thirdUri);
                    cameraIntent3.putExtra(MediaStore.EXTRA_OUTPUT, thirdUri);
                    startActivityForResult(cameraIntent3, 3);

//                Intent intent3 = new Intent(Intent.ACTION_GET_CONTENT);
//                intent3.setType("image/*");
//                startActivityForResult(Intent.createChooser(intent3, "Please Choose Photo"), 3);

                }

                break;
            case R.id.imageView6://Fourth
                if (fourthImgSendABoolean) {

                } else {
                    File originalFile4 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "fourth.png");

                    Intent cameraIntent4 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    fourthUri = Uri.fromFile(originalFile4);
                    Log.d("TAG", "Path 4 " + fourthUri);
                    cameraIntent4.putExtra(MediaStore.EXTRA_OUTPUT, fourthUri);
                    startActivityForResult(cameraIntent4, 4);

    //                Intent intent3 = new Intent(Intent.ACTION_GET_CONTENT);
    //                intent3.setType("image/*");
    //                startActivityForResult(Intent.createChooser(intent3, "Please Choose Photo"), 3);

                }
                break;

//            case R.id.button6: //Take Image
//
//                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//                startActivityForResult(intent, 0);
//
//                break;

            case R.id.button7: //Arrival
                if (pathFourthImageString == "" || pathFourthImageString == null) {
                    Toast.makeText(DetailJob.this, getResources().getString(R.string.err_conf1), Toast.LENGTH_SHORT).show();
                } else {
                    String strLat = "Unknown";
                    String strLng = "Unknown";
                    setupLocation();
                    Location networkLocation = requestLocation(LocationManager.NETWORK_PROVIDER, "No Internet");
                    if (networkLocation != null) {
                        strLat = String.format("%.7f", networkLocation.getLatitude());
                        strLng = String.format("%.7f", networkLocation.getLongitude());
                    }

                    Location gpsLocation = requestLocation(LocationManager.GPS_PROVIDER, "No GPS card");
                    if (gpsLocation != null) {
                        strLat = String.format("%.7f", gpsLocation.getLatitude());
                        strLng = String.format("%.7f", gpsLocation.getLongitude());
                    }


                    if (strLat.equals("Unknown") && strLng.equals("Unknown")) {
                        Toast.makeText(this, getResources().getString(R.string.err_gps1), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("13OctV1", " ++++++++++Latitude.-> " + strLat + " Longitude.-> " + strLng);

                        double lat1, lat2, lng1, lng2;
                        lat1 = Double.parseDouble(strLat);
                        lng1 = Double.parseDouble(strLng);
                        lat2 = Double.parseDouble(storeLatString);
                        lng2 = Double.parseDouble(storeLngString);

                        Log.d("TAG", "lat1 ==> " + lat1 + " lng1 ==> " + lng1 + " lat2 ==> " + lat2 + " lng2 ==> " + lng2);

                        double result = getMeterFromLatLong(lat1, lat2, lng1, lng2);
                        float km = (float) result;
                        float m = Float.parseFloat(String.format("%.2f", km)) * 1000;

                    if (m < Float.parseFloat(storeRadiusString)) {

                        SynUploadImage synUploadImage = new SynUploadImage(DetailJob.this, fourthBitmap);
                        synUploadImage.execute();

                        if (sendStatus) {

                        } else {
                            sendStatus = false;
                            pathFourthImageString = null;
                            fourthImgSendABoolean = true;
                        }

                        SynGPStoServer synGPStoServer = new SynGPStoServer(DetailJob.this);
                        synGPStoServer.execute(myConstant.getUrlArrivalGPS(), strLat, strLng, getTimeDate, driverUserNameString, planDtl2_Id);

                    } else {
                        Toast.makeText(this, getResources().getString(R.string.err_gps2), Toast.LENGTH_SHORT).show();
                    }
                }



                }
                break;

            case R.id.button8: //Signature
                Intent intentSign = new Intent(DetailJob.this, SignatureActivity.class);
                intentSign.putExtra("Login", loginStrings);
                intentSign.putExtra("PlanDtl", planDtl2_Id);
                intentSign.putExtra("Date", planDateString);
                intentSign.putExtra("PlanId", planIdString);
                startActivity(intentSign);
                break;

            case R.id.button9: //Confirm
//                String strLat = "Unknown";
//                String strLng = "Unknown";
//                setupLocation();
//                Location networkLocation = requestLocation(LocationManager.NETWORK_PROVIDER, "No Internet");
//                if (networkLocation != null) {
//                    strLat = String.format("%.7f", networkLocation.getLatitude());
//                    strLng = String.format("%.7f", networkLocation.getLongitude());
//                }
//
//                Location gpsLocation = requestLocation(LocationManager.GPS_PROVIDER, "No GPS card");
//                if (gpsLocation != null) {
//                    strLat = String.format("%.7f", gpsLocation.getLatitude());
//                    strLng = String.format("%.7f", gpsLocation.getLongitude());
//                }
//
//
//                if (strLat.equals("Unknown") && strLng.equals("Unknown")) {
//                    Toast.makeText(this, getResources().getString(R.string.err_gps1), Toast.LENGTH_SHORT).show();
//                } else {
//                    Log.d("13OctV1", " ++++++++++Latitude.-> " + strLat + " Longitude.-> " + strLng);
//
//                    double lat1, lat2, lng1, lng2;
//                    lat1 = Double.parseDouble(strLat);
//                    lng1 = Double.parseDouble(strLng);
//                    lat2 = Double.parseDouble(storeLatString);
//                    lng2 = Double.parseDouble(storeLngString);
//
//                    Log.d("TAG", "lat1 ==> " + lat1 + " lng1 ==> " + lng1 + " lat2 ==> " + lat2 + " lng2 ==> " + lng2);
//
//                    double result = getMeterFromLatLong(lat1, lat2, lng1, lng2);
//                    float km = (float) result;
//                    float m = Float.parseFloat(String.format("%.2f", km)) * 1000;
//
//                    if (m < Float.parseFloat(storeRadiusString)) {


                        SynUpdateStatus synUpdateStatus = new SynUpdateStatus(DetailJob.this);
                        synUpdateStatus.execute();

//                    } else {
//                        Toast.makeText(this, getResources().getString(R.string.err_gps2), Toast.LENGTH_SHORT).show();
//                    }
//                }

                break;

            case R.id.button10: //Call
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                String phoneNo = "tel:" + intentToCallTextView.getText().toString();
                callIntent.setData(Uri.parse(phoneNo));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);

                break;

            case R.id.button13: //Back
                Log.d("TAG", "Press Back Date ==> " + planDateString);
                Intent backIntent = new Intent(DetailJob.this, ServiceActivity.class);
                backIntent.putExtra("Login", loginStrings);
                backIntent.putExtra("Date", planDateString);
                backIntent.putExtra("PlanId", planIdString);
                backIntent.putExtra("TruckNo", "");
                startActivity(backIntent);
                finish();
                break;

            case R.id.button4:// Send Picture
                Log.d("TAG", "Path bool ==> " + (pathFirstImageString == null));
                Log.d("TAG", "Path bool ==> " + (pathSecondImageString == null));
                Log.d("TAG", "Path bool ==> " + (pathThirdImageString == null));

                if (pathFirstImageString == null && pathSecondImageString == null && pathThirdImageString == null) {
                    Toast.makeText(this, getResources().getString(R.string.err_conf1), Toast.LENGTH_SHORT).show();
                }

                if (pathFirstImageString != null) {
                    SynUploadImage synUploadImage = new SynUploadImage(DetailJob.this, firstBitmap);
                    synUploadImage.execute();
                    if (sendStatus) {

                    } else {
                        sendStatus = false;
                        pathFirstImageString = null;
                        firstImgSendABoolean = true;
                        checkPictureBeforeConfirm();
                    }
                }
                if (pathSecondImageString != null) {
                    SynUploadImage synUploadImage = new SynUploadImage(DetailJob.this, secondBitmap);
                    synUploadImage.execute();
                    if (sendStatus) {

                    } else {
                        sendStatus = false;
                        pathSecondImageString = null;
                        secondImgSendABoolean = true;
                        checkPictureBeforeConfirm();
                    }
                }
                if (pathThirdImageString != null) {
                    SynUploadImage synUploadImage = new SynUploadImage(DetailJob.this, thirdBitmap);
                    synUploadImage.execute();
                    if (sendStatus) {

                    } else {
                        sendStatus = false;
                        pathThirdImageString = null;
                        thirdImgSendABoolean = true;
                        checkPictureBeforeConfirm();
                    }
                }
                break;

//            case R.id.button5://Start Load
//                //SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.getDefault());
//
////                startLoadString = format.format(new Date());
//
////                Log.d("Tag", "Start Date ==> " + startLoadString);
////                Toast.makeText(this, getResources().getString(R.string.start_load), Toast.LENGTH_SHORT).show();
//                startLoadString = "start";
//                SynUpdateLoad synUpdateLoad = new SynUpdateLoad(this, startLoadString);
//                synUpdateLoad.execute();
//
//                break;

//            case R.id.button4:
//                if (startLoadString.length() == 0){
//
//                }else{
////                    SimpleDateFormat format2= new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.getDefault());
////                    finLoadString =  format2.format(new Date());
//                    finLoadString = "finish";
//                    SynUpdateLoad synUpdateLoad2 = new SynUpdateLoad(this, finLoadString);
//                    synUpdateLoad2.execute();
////                    Log.d("Tag", "Finish Date ==> " + finLoadString);
//
////                    Toast.makeText(this, getResources().getString(R.string.fin_load), Toast.LENGTH_SHORT).show();
//
//                }
//                break;

        }//switch
    }// onClick

    private void checkPictureBeforeConfirm(){
        Log.d("TAG", "1 bool ==> " + firstImgSendABoolean);
        Log.d("TAG", "2 bool ==> " + secondImgSendABoolean);
        Log.d("TAG", "3 bool ==> " + thirdImgSendABoolean);

        if (firstImgSendABoolean && secondImgSendABoolean && thirdImgSendABoolean && fourthImgSendABoolean) {
            sendButton.setVisibility(View.GONE);
            confirmButton.setVisibility(View.VISIBLE);
        }
    }

    private class SynUpdateStatus extends AsyncTask<String, Void, String> {
        //Explicit
        private Context context;

        public SynUpdateStatus(Context context) {
            this.context = context;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("TAG", "JSON_Upload ==> " + s);
            boolean b = (s.equals("OK"));
            Log.d("Tag", "Bool ==> " + b);
            if (s.equals("OK")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getString(R.string.save_comp), Toast.LENGTH_SHORT).show();
                    }
                });

                Intent intent = new Intent(DetailJob.this, ServiceActivity.class);
                intent.putExtra("Login", loginStrings);
                intent.putExtra("Date", planDateString);
                intent.putExtra("PlanId", planIdString);
                intent.putExtra("TruckNo", "");
                startActivity(intent);

                finish();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getString(R.string.save_incomp), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("PlanDtl2_ID", planDtl2_Id)
                        .add("Driver_Name", loginStrings[2])
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(myConstant.getUrlUpdateStatus()).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();

                return response.body().string();
            } catch (Exception e) {
                return "NOK2";
            }
        }
    }

    private class SynUploadImage extends AsyncTask<Void, Void, String> {
        //Explicit
        private Context context;
        private Bitmap bitmap;
        private UploadImageUtils uploadImageUtils;
        private String mUploadedFileName;
        ProgressDialog progress;
        Runnable progressRunnable;
        Handler pdCanceller;


        public SynUploadImage(Context context, Bitmap bitmap) {
            this.context = context;
            this.bitmap = bitmap;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(context);
            progress.setCancelable(false);
            progress.setMessage(getResources().getString(R.string.waiting));
            progress.show();

            progressRunnable = new Runnable() {

                @Override
                public void run() {
                    progress.cancel();
                }
            };

//            progressDialog = new ProgressDialog(context);
//            progressDialog.setCancelable(false);
//            progressDialog.show(context,null,getResources().getString(R.string.waiting));
        }

        @Override
        protected String doInBackground(Void... voids) {
            uploadImageUtils = new UploadImageUtils();
            mUploadedFileName = uploadImageUtils.getRandomFileName();
            final String result = uploadImageUtils.uploadFile(mUploadedFileName, myConstant.getUrlSaveImage(), bitmap, planDtl2_Id, "P");
            Log.d("TAG", "Do in back after save:-->" + result);
            if (result == "NOK") {
                return "NOK1";
            } else {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Log.d("name", mUploadedFileName);
                    RequestBody requestBody = new FormEncodingBuilder()
                            .add("isAdd", "true")
                            .add("PlanDtl2_ID", planDtl2_Id)
                            .add("File_Name", mUploadedFileName)
                            .add("File_Path", result)
                            .add("Driver_Name", loginStrings[2])
                            .build();
                    Request.Builder builder = new Request.Builder();
                    Request request = builder.url(myConstant.getUrlSaveImagePath()).post(requestBody).build();
                    Response response = okHttpClient.newCall(request).execute();

                    return response.body().string();
                } catch (Exception e) {
                    sendStatus = true;
                    return "NOK2";
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {


            Log.d("TAG", "JSON_Upload ==> " + s);
            boolean b = (s.equals("OK"));
            Log.d("Tag", "Bool ==> " + b);
            if (s.equals("OK")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getString(R.string.add_img_comp), Toast.LENGTH_SHORT).show();
                    }
                });

                pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 3000);
//                if (progressDialog != null && progressDialog.isShowing()) {
//
//                    Log.d("TAG", "A1");
//                    progressDialog.dismiss();
//
//                } else {
//                    progressDialog.dismiss();
//
//                    Log.d("TAG", "B1");
//
//                }

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getString(R.string.add_img_incomp), Toast.LENGTH_SHORT).show();
                    }
                });

                pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 3000);
//                if (progressDialog != null && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//
//                    Log.d("TAG", "A2");
//
//                } else {
//                    progressDialog.dismiss();
//
//                    Log.d("TAG", "B2");
//                }
            }

        }
    }

    private class SynData extends AsyncTask<String, Void, String> {
        //Explicit
        private Context context;

        public SynData(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder().add("isAdd", "true").add("planDtl2_id", strings[1]).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(strings[0]).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();

                return response.body().string();


            } catch (Exception e) {
                Log.d("12octV4", "e doInBack ==> " + e);
                return null;
            }
        }//doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("12octV4", "JSON ==> " + s);

            try {

                JSONArray jsonArray = new JSONArray(s);
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                //Show Text
                jobNoTextView.setText(getResources().getString(R.string.job) + " : " + jsonObject.getString("work_sheet_no"));
                storeCodeTextView.setText(getResources().getString(R.string.store_code) + " : " + jsonObject.getString("store_code"));
                storeNameTextView.setText(getResources().getString(R.string.store_name) + " : " + jsonObject.getString("store_name"));
                arrivalTextView.setText(getResources().getString(R.string.time) + " : " + jsonObject.getString("plan_arrivalDateTime"));
                intentToCallTextView.setText(getResources().getString(R.string.call) + " : " + jsonObject.getString("store_tel"));

                storeLatString = jsonObject.getString("store_lat");
                storeLngString = jsonObject.getString("store_long");
                storeRadiusString = jsonObject.getString("gps_radius");
            } catch (Exception e) {
                Log.d("12octV4", "e onPost ==> " + e);
            }
        }
    }//SynData

    private void bindWidget() {

        //Bind Widget
        jobNoTextView = (TextView) findViewById(R.id.textView14);
        storeCodeTextView = (TextView) findViewById(R.id.textView15);
        storeNameTextView = (TextView) findViewById(R.id.textView16);
        arrivalTextView = (TextView) findViewById(R.id.textView17);
        intentToCallTextView = (TextView) findViewById(R.id.textView18);
        listView = (ListView) findViewById(R.id.livContainer);
        firstImageView = (ImageView) findViewById(R.id.imageView5);
        secondImageView = (ImageView) findViewById(R.id.imageView4);
        thirdImageView = (ImageView) findViewById(R.id.imageView3);
        fourthImageView = (ImageView) findViewById(R.id.imageView6);
        arrivalButton = (Button) findViewById(R.id.button7);
//        takeImgButton = (Button) findViewById(R.id.button6);
        confirmButton = (Button) findViewById(R.id.button9);
        signatureButton = (Button) findViewById(R.id.button8);
        contractButton = (Button) findViewById(R.id.button10);
        backButton = (Button) findViewById(R.id.button13);
        sendButton = (Button) findViewById(R.id.button4);
//        startLoadButton = (Button) findViewById(R.id.button5);
//        finLoadButton = (Button) findViewById(R.id.button4);
    }

    private class SynGPStoServer extends AsyncTask<String, Void, String> {

        //Explicit
        private Context context;

        public SynGPStoServer(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("Lat", params[1])
                        .add("Lng", params[2])
                        .add("stamp", params[3])
                        .add("drv_username", params[4])
                        .add("planDtl2_id", params[5])
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(params[0]).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                Log.d("13OctV1", "doInBackSynGPS--->" + e.toString());
                return null;
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("13OctV1", "JSON__GPS->" + s);
            if (s.equals("Success")) {
                pathFourthImageString = null;
                firstImageView.setVisibility(View.VISIBLE);
                secondImageView.setVisibility(View.VISIBLE);
                thirdImageView.setVisibility(View.VISIBLE);
//                startLoadButton.setVisibility(View.VISIBLE);
//                takeImgButton.setVisibility(View.VISIBLE);
//                finLoadButton.setVisibility(View.VISIBLE);
                signatureButton.setVisibility(View.VISIBLE);
                sendButton.setVisibility(View.VISIBLE);
                arrivalButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getString(R.string.add_gps_comp), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getString(R.string.add_gps_incomp), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }   //onPostExcute
    }// Class Syn GPS TO SERVER

    public Location requestLocation(String strProvider, String strError) {

        Location location = null;

        if (locationManager.isProviderEnabled(strProvider)) {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            locationManager.requestLocationUpdates(strProvider, 1000, 10, locationListener);
            location = locationManager.getLastKnownLocation(strProvider);

        } else {
            Log.d("GPS", strError);
        }


        return location;
    }

    public final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
//            latTextView.setText(String.format("%.7f", location.getLatitude()));
//            lngTextView.setText(String.format("%.7f", location.getLongitude()));
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

    private void setupLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);


    }   // setupLocation
}//Main Class
