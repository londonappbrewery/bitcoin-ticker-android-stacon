package com.londonappbrewery.bitcointicker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {

    // Constants:
    private final String APP_TAG = "Bitcoin";
    private final String BASE_URL = "https://apiv2.bitcoinaverage.com/indices/global/ticker/BTC";

    // Member Variables:
    TextView mPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPriceTextView = findViewById(R.id.priceLabel);
        Spinner spinner = findViewById(R.id.currency_spinner);

        // Create an ArrayAdapter using the String array and a spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_array, R.layout.spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(APP_TAG, "Selected: " + parent.getItemAtPosition(position));
                String currencySelected = parent.getItemAtPosition(position).toString();
                letsDoSomeNetworking(currencySelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(APP_TAG, "Nothing selected");
            }
        });

    }

    private void letsDoSomeNetworking(final String currency) {
        Log.d(APP_TAG, "letsDoSomeNetworking started");
        AsyncHttpClient client = new AsyncHttpClient();
        try {
            client.get(BASE_URL + currency, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    Log.d(APP_TAG, "Get method with " + BASE_URL + currency + " fired!");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(APP_TAG, "Success! JSON: " + response.toString());
                    try {
                        double equivalentPrice = response.getDouble("ask");
                        Log.d(APP_TAG, "Ask price is: " + Double.toString(equivalentPrice));
                        updateUI(equivalentPrice);
                    } catch (JSONException e) {
                        Log.e(APP_TAG, "Error on JSON parsing: " + e.toString());
                    }
//                WeatherDataModel weatherData = WeatherDataModel.fromJson(response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String fe, Throwable e) {
                    Log.e(APP_TAG, "Error Message: " + e.toString());
                    Log.d(APP_TAG, "Status code: " + statusCode);
                    Toast.makeText(MainActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(APP_TAG, e.toString());
        }

    }

    @SuppressLint("DefaultLocale")
    private void updateUI(double equivalentPrice) {
        mPriceTextView.setText(String.format("%1$,.2f", equivalentPrice));
    }
}
