package com.ardhaapps.india.news;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String TAG = MainActivity.class.getSimpleName();
    ArrayList<String> lang_list = new ArrayList<>();
    ArrayList<String> lang_id = new ArrayList<>();
    ArrayList<NewsPaper> arrayListNewspaper = new ArrayList<>();
    ArrayAdapter arrayAdapterLang;
    AppCompatSpinner appCompatSpinnerLang;
    Customs_Adapter customs_adapter;
    ListView listview;
    SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView my_recycler_view;
    private RecyclerAdapter recyclerAdapter;
    RelativeLayout layoutNodata,layoutNet;
    String status = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appCompatSpinnerLang = findViewById(R.id.spinner_language);
        layoutNodata = findViewById(R.id.layoutNodata);
        layoutNet = findViewById(R.id.layoutNet);
        my_recycler_view = findViewById(R.id.my_recycler_view);

        my_recycler_view.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        my_recycler_view.setLayoutManager(new GridLayoutManager(this, 2));

        if(Utililty.isConnected(MainActivity.this)) {
            layoutNet.setVisibility(View.GONE);
            my_recycler_view.setVisibility(View.VISIBLE);
            new getNews().execute("from_oncreate", "");
        } else {
            layoutNet.setVisibility(View.VISIBLE);
            my_recycler_view.setVisibility(View.GONE);
        }


        appCompatSpinnerLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Toast.makeText(MainActivity.this, "" + lang_list.get(i), Toast.LENGTH_SHORT).show();

                if(Utililty.isConnected(MainActivity.this)) {
                    layoutNet.setVisibility(View.GONE);
                    my_recycler_view.setVisibility(View.VISIBLE);
                    new getNews().execute("from_spinner", lang_id.get(i));
                } else {
                    layoutNet.setVisibility(View.VISIBLE);
                    my_recycler_view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(Utililty.isConnected(MainActivity.this)) {
                    new getNews().execute("from_swipeRefreshLayout", "");
                    swipeRefreshLayout.setRefreshing(false);
                    layoutNet.setVisibility(View.GONE);
                    my_recycler_view.setVisibility(View.VISIBLE);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    layoutNet.setVisibility(View.VISIBLE);
                    my_recycler_view.setVisibility(View.GONE);
                }


            }
        });

    }

    private class getNews extends AsyncTask<String, String, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            Utililty.mProgress(MainActivity.this, "Loading...", false).show();
        }

        @Override
        protected String[] doInBackground(String... arg) {
            System.out.println("update_thread starts " + arg[0]);
            System.out.println("update_thread starts " + arg[1]);


            try {
                JSONObject jobbject;
                String result = null;

                HttpHandler httpHandler = new HttpHandler(MainActivity.this);
                String url = "http://ardhaapps.nammatiruchengode.com/api/news-paper-app/news";
                JSONObject postDataParams = new JSONObject();
                try {
                    postDataParams.put("language", arg[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("ERROR----" + e.getMessage());
                }
                result = httpHandler.makeServiceCall(url, postDataParams);
                Log.e(TAG, "Response from url: " + result);

                try {
                    jobbject = new JSONObject(result);
                    JSONObject jsonObject = new JSONObject(jobbject.getString("data"));

                    System.out.println("Update===NewsPaperStatus " + jsonObject.getString("NewsPaperStatus"));

                    if (!arg[0].equals("from_spinner")) {

                        JSONArray jsonArrayLanguage = new JSONArray(jsonObject.getString("Language"));
                        lang_id.clear();
                        lang_list.clear();
                        lang_id.add("0");
                        lang_list.add("All");
                        for (int language = 0; jsonArrayLanguage.length() > language; language++) {
                            JSONObject getdata_object = jsonArrayLanguage.getJSONObject(language);
                            lang_id.add(getdata_object.getString("id"));
                            lang_list.add(getdata_object.getString("language"));

                            System.out.println("Update===language id " + getdata_object.getString("id"));
                            System.out.println("Update===language name " + getdata_object.getString("language"));
                        }
                    }

                    if (jsonObject.getString("NewsPaperStatus").equals("yesdata")) {
                        status = "yesdata";
                        JSONArray jsonArrayNews = new JSONArray(jsonObject.getString("NewsPaper"));
                        arrayListNewspaper.clear();
                        for (int news = 0; jsonArrayNews.length() > news; news++) {
                            JSONObject getdata_object = jsonArrayNews.getJSONObject(news);

                            try {
                                NewsPaper newsPaper = new NewsPaper();
                                newsPaper.setId(getdata_object.getString("id"));
                                newsPaper.setTitle(getdata_object.getString("title"));
                                newsPaper.setImageUrl(getdata_object.getString("logo"));
                                newsPaper.setWebsiteLink(getdata_object.getString("paper"));
                                arrayListNewspaper.add(newsPaper);

                            } catch (Exception e) {
                                System.out.println("error : " + e);
                            }

                            System.out.println("Update===news id " + getdata_object.getString("id"));
                            System.out.println("Update===news title " + getdata_object.getString("title"));
                            System.out.println("Update===news logo " + getdata_object.getString("logo"));
                            System.out.println("Update===news paper " + getdata_object.getString("paper"));
                            System.out.println("Update===news language " + getdata_object.getString("language"));
                        }
                    } else {
                        status = "nodata";
                        arrayListNewspaper.clear();
                    }

                } catch (JSONException e1) {
                    System.out.println("ERROR----" + e1.getMessage());
                }
                System.out.println("feedback_update_thread ends");

                  /*  Cursor c1 = myDB1.rawQuery("select * from state_list_table", null);
                    Cursor c2 = myDB1.rawQuery("select * from district_list_table", null);
                    Cursor c3 = myDB1.rawQuery("select * from taluk_list_table", null);

                    System.out.println("state_list_table_count : " + c1.getCount());
                    System.out.println("district_list_table_count : " + c2.getCount());
                    System.out.println("taluk_list_table_count : " + c3.getCount());*/


            } catch (Exception e) {

            }

            return arg;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            Utililty.mProgress.dismiss();

            if (!result[0].equals("from_spinner")) {
                arrayAdapterLang = new ArrayAdapter(getApplicationContext(), R.layout.spinner, lang_list);
                appCompatSpinnerLang.setAdapter(arrayAdapterLang);
            }

            if (status.equals("nodata")) {
                layoutNodata.setVisibility(View.VISIBLE);
                my_recycler_view.setVisibility(View.GONE);
            } else {
                layoutNodata.setVisibility(View.GONE);
                my_recycler_view.setVisibility(View.VISIBLE);
                recyclerAdapter = new RecyclerAdapter(MainActivity.this, arrayListNewspaper);
                my_recycler_view.setAdapter(recyclerAdapter);
            }
        }
    }

    public class Customs_Adapter extends BaseAdapter {


        private LayoutInflater inflater;


        public Customs_Adapter() {

        }

        @Override
        public int getCount() {
            return arrayListNewspaper.size();
        }

        @Override
        public Object getItem(int location) {
            return arrayListNewspaper.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.newslayout, null);
            ImageView thumbNail = convertView.findViewById(R.id.imageview);
            TextView sub_title = convertView.findViewById(R.id.sub_header);
            RelativeLayout rel_lay = convertView.findViewById(R.id.rel_lay);

            final NewsPaper newsPaper = arrayListNewspaper.get(position);

            sub_title.setText(newsPaper.getTitle());
            GlideApp.with(MainActivity.this)
                    .load(newsPaper.getImageUrl())
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.loading)
                    .transition(DrawableTransitionOptions.withCrossFade()) //Optional
                    .skipMemoryCache(false)  //No memory cache
                    .diskCacheStrategy(DiskCacheStrategy.ALL)   //No disk cache
                    // .onlyRetrieveFromCache(true)
                    .into(thumbNail);

            rel_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "" + newsPaper.getTitle(), Toast.LENGTH_SHORT).show();

                   /* Intent i = new Intent(Activity_maram_person.this, Activity_Maram_Person_Content.class);
                    i.putExtra("description", m.getNursery_descrption());
                    i.putExtra("title", m.getNursery_name());
                    i.putExtra("imgurl", m.getNursery_image());
                    i.putExtra("toolbar_name", "மரம் ஆர்வலர்கள்");
                    i.putExtra("id", m.getNursery_id());
                    i.putExtra("type", "tadam");
                    startActivity(i);*/

                }
            });

            return convertView;
        }

    }


}
