package com.ennoblesoft.arif.shimmereffect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ennoblesoft.arif.shimmereffect.adapters.ContactAdapter;
import com.ennoblesoft.arif.shimmereffect.connections.Links;
import com.ennoblesoft.arif.shimmereffect.connections.MySingleton;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    MainActivity activity;
    RecyclerView rvContact;
    ContactAdapter contactAdapter;
    String result;

    //Shimmer Effect
    private ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        rvContact = findViewById(R.id.rv_contact);

        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);

        fetchAndAdaptContact();
    }

    private void fetchAndAdaptContact() {
        StringRequest stringRequest = new StringRequest(Links.contactURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Response = jsonObject.getString("status");
                            if (Response.equals("Success")) {
                                JSONArray contactArray = jsonObject.getJSONArray("data");
                                ArrayList<HashMap<String, String>> contactList = new ArrayList<>();
                                HashMap<String, String> contact;

                                for (int i = 0; i < contactArray.length(); i++) {
                                    JSONObject contacts = contactArray.getJSONObject(i);
                                    String name = contacts.getString("cname");
                                    String number = contacts.getString("cnumber");

                                    contact = new HashMap<>();
                                    contact.put("name", name);
                                    contact.put("number", number);
                                    contactList.add(contact);
                                }

                                contactAdapter = new ContactAdapter(activity, contactList);
                                rvContact.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                rvContact.setAdapter(contactAdapter);

                                // Stopping Shimmer Effect's animation after data is loaded to RecyclerView
                                shimmerFrameLayout.startShimmer();
                                shimmerFrameLayout.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            String message = getString(R.string.json_exception);
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message;
                        if (error instanceof TimeoutError) {
                            message = getString(R.string.timeout_error);
                        } else if (error instanceof NoConnectionError) {
                            message = getString(R.string.no_connection_error);
                        } else if (error instanceof AuthFailureError) {
                            message = getString(R.string.auth_failure_error);
                        } else if (error instanceof ServerError) {
                            message = getString(R.string.server_error);
                        } else if (error instanceof NetworkError) {
                            message = getString(R.string.network_error);
                        } else if (error instanceof ParseError) {
                            message = getString(R.string.parse_error);
                        } else {
                            message = getString(R.string.other_volley_error);
                        }
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
        MySingleton.getInstance(MainActivity.this).addToRequestQue(stringRequest);
    }


    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (shimmerFrameLayout.isShimmerStarted()) {
            shimmerFrameLayout.startShimmer();
        }
    }
}
