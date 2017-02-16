package com.hitachi_tstv.yodpanom.yaowaluk.proofdelivery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class ServiceActivity extends Activity {

    //Explicit
    private TextView nameDriverTextView, idDriverTextView;
    private Button jobListButton;
    private ListView listView;
    private String[] loginStrings;
    private ImageView iconImageView;
    private MyConstant myConstant = new MyConstant();
    private String[] planDateStrings, cnt_storeStrings, planIdStrings;
    private boolean aBoolean = true;
    private String[] workSheetStrings, storeNameStrings,
            planArrivalTimeStrings, planDtl2_idStrings, truckIdStrings;
    private String driverChooseString, dateChooseString, truckString ,dateString, planString;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                ComponentName componentName = intent.getComponent();
                Intent backToMainIntent = IntentCompat.makeRestartActivityTask(componentName);
                startActivity(backToMainIntent);

                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        //Bind Widget
        nameDriverTextView = (TextView) findViewById(R.id.textView2);
        idDriverTextView = (TextView) findViewById(R.id.textView4);
        jobListButton = (Button) findViewById(R.id.button3);
        listView = (ListView) findViewById(R.id.listJob);
        iconImageView = (ImageView) findViewById(R.id.imageView);

        //Get Value from Intent
        loginStrings = getIntent().getStringArrayExtra("Login");
        driverChooseString = getIntent().getStringExtra("PlanId");
        dateChooseString = getIntent().getStringExtra("Date");
        truckString = getIntent().getStringExtra("TruckNo");

        Log.d("Tag", "Avatar bool 3 ==> " + loginStrings[3].equals("null"));
        if (!loginStrings[3].equals("null")) {
//            Log.d("Tag", "Gender ==> " + loginStrings[3]);
//            iconImageView.setImageResource(R.drawable.female);
            SynLoadImage synLoadImage = new SynLoadImage(iconImageView, this);
            synLoadImage.execute();
        } else {
            int res;
            Log.d("Tag", "Gender ==> " + loginStrings[4]);
            Log.d("Tag", "Gender M  bool ==> " + loginStrings[4].equals("M"));
            if (loginStrings[4].equals("M")) {
                res = R.drawable.male;
            }
            else{
                res = R.drawable.female;
            }
            iconImageView.setImageResource(res);
        }

        if (driverChooseString.length() == 0) {
        } else {
            //From Main Activity
            aBoolean = false;

        }
        Log.d("Tag", "Date bool ==> " + aBoolean);
        Log.d("Tag", "Date lenght ==> " + driverChooseString.length());

        //Show Name
        nameDriverTextView.setText(loginStrings[1]);

        //Syn data
        SynDataWhereByDriverID synDataWhereByDriverID = new SynDataWhereByDriverID(ServiceActivity.this);
        synDataWhereByDriverID.execute(myConstant.getUrlDataWhereDriverID());


    }   // Main Method

    private class SynDataWhereByDriverID extends AsyncTask<String, Void, String> {

        //Explicit
        private Context context;
        private ProgressDialog progressDialog;

        public SynDataWhereByDriverID(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                OkHttpClient okHttpClient = new OkHttpClient();

                Log.d("Tag1", "Login ==> " + loginStrings[0]);
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("driver_id", loginStrings[0])
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(strings[0]).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();


            } catch (Exception e) {
                progressDialog.dismiss();
                Log.d("12octV1", "e doInBack ==> " + e.toString());
                return null;
            }

        }   // doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("12octV1", "JSON ==> " + s);
            progressDialog.dismiss();
            try {

                JSONArray jsonArray = new JSONArray(s);
                planDateStrings = new String[jsonArray.length()];
                cnt_storeStrings = new String[jsonArray.length()];
                planIdStrings = new String[jsonArray.length()];
                truckIdStrings = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    planDateStrings[i] = jsonObject.getString("planDate");
                    cnt_storeStrings[i] = jsonObject.getString("cnt_store");
                    planIdStrings[i] = jsonObject.getString("planId");
                    truckIdStrings[i] = jsonObject.getString("truck_no");

                }   // for

                if (aBoolean) {

                    //Not Click on Button
                    jobListButton.setText(getResources().getString(R.string.joblist) + " : " + planDateStrings[0]);
                    dateString = planDateStrings[0];
                    planString = planIdStrings[0];
                    idDriverTextView.setText(truckIdStrings[0]);
                    createDetailList(planIdStrings[0]);


                } else {
                    // From Job List View
                    jobListButton.setText(getResources().getString(R.string.joblist) + " : " + dateChooseString);
                    idDriverTextView.setText(truckString);
                    dateString = dateChooseString;
                    planString = driverChooseString;
                    createDetailList(driverChooseString);
                }


                // Get Event From Click
                jobListButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(ServiceActivity.this, JobListView.class);
                        intent.putExtra("Date", planDateStrings);
                        intent.putExtra("Store", cnt_storeStrings);
                        intent.putExtra("Login", loginStrings);
                        intent.putExtra("PlanId", planIdStrings);
                        intent.putExtra("TruckNo", truckIdStrings);
                        startActivity(intent);
//                        finish();

                    }   // onClick
                });


            } catch (Exception e) {
                progressDialog.dismiss();
                Log.d("12octV1", "e onPost ==> " + e.toString());
            }


        }   // onPost

    }   // SynDataWhereByDriverID

    private class SynLoadImage extends AsyncTask<String, Void, Bitmap> {
        private ImageView view;
        private Context context;
        private ProgressDialog progressDialog;

        public SynLoadImage(ImageView view, Context context) {
            this.view = view;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String urlString = myConstant.getUrlDriverPicture() + loginStrings[3];
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new java.net.URL(urlString).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                progressDialog.dismiss();
                e.printStackTrace();
            } catch (IOException e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            view.setImageBitmap(bitmap);
            progressDialog.dismiss();
        }
    }

    private void createDetailList(String planIDString) {

        SynDetail synDetail = new SynDetail(ServiceActivity.this,
                planIDString);
        synDetail.execute(myConstant.getUrlDataWhereDriverIDanDate());

    }   // createDetailList

    private class SynDetail extends AsyncTask<String, Void, String> {

        //Explicit
        private Context context;
        private String planIdString;
        private ProgressDialog progressDialog;

        public SynDetail(Context context,
                         String planIdString
        ) {
            this.context = context;
            this.planIdString = planIdString;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                Log.d("Tag2", "Login ==> " + loginStrings[0] + ", PlanId ==> " + planIdString);
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("planId", planIdString)
                        .add("driver_id", loginStrings[0])
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(strings[0]).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                progressDialog.dismiss();
                Log.d("12octV2", "e doInBack " + e.toString());
                return null;
            }

        }   // doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("12octV2", "JSoN ==> " + s);
            progressDialog.dismiss();

            try {

                JSONArray jsonArray = new JSONArray(s);

                workSheetStrings = new String[jsonArray.length()];
                storeNameStrings = new String[jsonArray.length()];
                planArrivalTimeStrings = new String[jsonArray.length()];
                planDtl2_idStrings = new String[jsonArray.length()];

                for (int i=0;i<jsonArray.length();i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Log.d("TAG", "Store Name ==> " + jsonObject.getString("store_name"));
                    workSheetStrings[i] = jsonObject.getString("work_sheet_no");
                    storeNameStrings[i] = jsonObject.getString("store_name");
                    planArrivalTimeStrings[i] = jsonObject.getString("plan_arrivalTime");
                    planDtl2_idStrings[i] = jsonObject.getString("planDtl2_id");

                }   // for

                DetailAdapter detailAdapter = new DetailAdapter(context,
                        workSheetStrings, storeNameStrings, planArrivalTimeStrings);
                listView.setAdapter(detailAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.d("TAG", "Date String ==> " + dateString);
                        Intent intent = new Intent(ServiceActivity.this,DetailJob.class);
                        intent.putExtra("Login", loginStrings);
                        intent.putExtra("planDtl2_id", planDtl2_idStrings[i]);
                        intent.putExtra("Date", dateString);
                        intent.putExtra("PlanId", planString);
                        startActivity(intent);
                        finish();
                    }
                });


            } catch (Exception e) {
                progressDialog.dismiss();
                Log.d("12octV2", "e onPost ==> " + e.toString());
            }


        }   // onPost

    }   // SynDetail


    @Override
    public void onBackPressed() {

    }


}   // Main Class