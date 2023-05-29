package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.databinding.ActivitySearchBinding;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private EditText movieQuery;


    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "cs122b_Project1_war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        movieQuery = binding.movieTitle;
        final Button searchButton = binding.searchMovie;
        searchButton.setOnClickListener(view -> search());

        // TODO: this should be retrieved from the backend server

        };
    public void search() {
        // use the same network queue across our application
        //final RequestQueue queue = NetworkManager.sharedManager(this).queue;

//        String addToURL = movieQuery.getText().toString();
//        final Map<String, String> params = new HashMap<>();
//        params.put("query", movieQuery.getText().toString());
        // request type is POST
                        //response.getJSONObject("status").("success")
        //Complete and destroy login activity once successful
        NetworkManager.sharedManager(this).currentSearch =movieQuery.getText().toString();

        finish();
        // initialize the activity(page)/destination
        Intent MovieListPage = new Intent(SearchActivity.this, MovieListActivity.class);
        // activate the list page.
        Log.d("search.success", movieQuery.getText().toString());

        startActivity(MovieListPage);


        };
        // important: queue.add is where the login request is actually sent
    }
