package edu.uci.ics.fabflixmobile.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.SearchActivity;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private TextView message;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "cs122b_Project1_war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        username = binding.username;
        password = binding.password;
        message = binding.message;
        final Button loginButton = binding.login;


        //assign a listener to call a function to handle the user request when clicking a button
        loginButton.setOnClickListener(view -> login());
    }

    @SuppressLint("SetTextI18n")
    public void login() {
        //message.setText("Trying to login");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        //Log.d("login.error",new JSONObject(params).toString());
        String addToURL = username.getText().toString() + "&password=" +password.getText().toString();

        // request type is POST
        final JsonObjectRequest loginRequest = new JsonObjectRequest(
                Request.Method.POST,
                baseURL + "/api/login?username="+addToURL, new JSONObject(),
                response -> {
                    message.setText(response.toString());

                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.
                    try {
                        //response.getJSONObject("status").("success")
                        if (response.getString("status").equals("success")) {
                            Log.d("login.success", response.toString());
                            //Complete and destroy login activity once successful
                            finish();
                            // initialize the activity(page)/destination
                            Intent SearchPage = new Intent(LoginActivity.this, SearchActivity.class);
                            // activate the list page.
                            startActivity(SearchPage);
                        } else {
                            Log.d("login.fail", response.toString());
                            Log.d("login.fail",username.getText().toString());
                            Log.d("login.fail",password.getText().toString());

                            message.setText("Incorrect Login Information. Please try again.");
                        }
                    }
                    catch(Exception e){
                        Log.d("login.error", e.toString());
                    }

                },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}