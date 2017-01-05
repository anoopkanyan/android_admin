package recode360.spreeadminapp.Networking;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.models.State;


public class StatesListRequest {

    private static String TAG = "States List Request";
    private static String tag_json_obj = "states list request";

    private static ArrayList<State> statesList = new ArrayList<State>();


    //specific id to get all the united states
    static String url = "http://mystore-anupkanyan.cs50.io/api/countries/232.json";

    public static ArrayList<State> getStates() {


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray states = response.getJSONArray("states");

                            for (int i = 0; i < states.length(); i++) {

                                State state = new State();
                                state.setAbbr(states.getJSONObject(i).getString("abbr"));
                                state.setCountry_id(states.getJSONObject(i).getInt("country_id"));
                                state.setId(states.getJSONObject(i).getInt("id"));
                                state.setName(states.getJSONObject(i).optString("name"));

                                statesList.add(state);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.networkResponse);
                // hide the progress dialog


            }
        });


        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        return statesList;
    }

}