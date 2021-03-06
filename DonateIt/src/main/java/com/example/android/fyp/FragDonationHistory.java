package com.example.android.fyp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragDonationHistory extends Fragment {

    View view;
    ListView listView;
    ArrayList<FragDonationHistoryData> dataArrayList = new ArrayList<>();
    MyAdapter adapter;

    public FragDonationHistory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_frag_donation_history, container, false);

        listView = view.findViewById(R.id.lvDonationHistory);

        new dataFetch().execute();

        return view;
    }


    class dataFetch extends AsyncTask<String,Void,String> {
        SharedPreferences preferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String userId = preferences.getString("UserId","");
        SharedPreferences preferences1 = getActivity().getSharedPreferences("HiddenUrl", Context.MODE_PRIVATE);
        String currentURL = preferences1.getString("URL","");
        String JSON_STRING;
        String requestUrl = currentURL+"DonateIt/donationHistory.php?userId="+userId;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(requestUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                int c = httpURLConnection.getResponseCode();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                while ((JSON_STRING = bufferedReader.readLine())!=null)
                {
                    stringBuilder.append(JSON_STRING).append("\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }



        @Override
        protected void onPostExecute(String result) {
            JSONObject jsonObject;
            JSONArray jsonArray;
            try {
                jsonObject = new JSONObject(result);
                String success = jsonObject.getString("success");
                dataArrayList.clear();

                if (success.equalsIgnoreCase("true")) {
                    jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        String amount = (jsonObject.getString("amount"));
                        String postName = (jsonObject.getString("post_name"));

                        FragDonationHistoryData data = new FragDonationHistoryData(postName, amount);
                        dataArrayList.add(data);
                    }
                    adapter = new MyAdapter(getActivity(), dataArrayList);
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }

    class MyAdapter extends BaseAdapter {
        ArrayList<FragDonationHistoryData> arrayList = new ArrayList<>();
        LayoutInflater inflater;

        public MyAdapter(Context context,ArrayList<FragDonationHistoryData> arrayList) {
            this.arrayList = arrayList;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.full_donation_history, null);
                holder = new ViewHolder();

                holder.tvPostName = convertView.findViewById(R.id.postName_donationHistory);
                holder.tvAmount = convertView.findViewById(R.id.amount_donationHistory);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvPostName.setText(arrayList.get(position).getPostName());
            holder.tvAmount.setText(arrayList.get(position).getAmount());
            return convertView;
        }

        class ViewHolder {

            TextView tvPostName, tvAmount;

        }

    }

}
