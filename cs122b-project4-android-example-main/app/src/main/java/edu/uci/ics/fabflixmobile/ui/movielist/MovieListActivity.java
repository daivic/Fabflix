package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
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
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import org.json.JSONObject;
import org.json.JSONArray;

import com.android.volley.toolbox.JsonArrayRequest;


import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {

    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "cs122b_Project1_war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

    private final int currentPage = 0;
    private final String testQuery = NetworkManager.sharedManager(this).currentSearch;
    final ArrayList<Movie> movies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        String addToURL = testQuery + "&results=" + 10+ "&offset=" + currentPage*10 + "&order=m.title%20ASC";
        Log.d("movieList.error", baseURL + "/api/fullSearch?title="+addToURL);
        Log.d("movieList.error", movies.toString());

        // TODO: this should be retrieved from the backend server
        final JsonArrayRequest searchRequest = new JsonArrayRequest(
                Request.Method.GET,
                baseURL + "/api/fullSearch?title="+addToURL, new JSONArray(),

                response -> {


                    try {
                        //Log.d("response.error", response.getJSONObject(1).getString("movie_title"));


                        for (int i = 0; i < response.length(); i++) {
                            Log.d("response.error", response.getJSONObject(i).getString("movie_title"));

                            JSONObject currentMov = response.getJSONObject(i);
                            movies.add(new Movie(currentMov.getString("movie_title"),  currentMov.getString("movie_year"), "currentMov.getString(\"movie_year\")"));
                        }
                        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
                        ListView listView = findViewById(R.id.list);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            Movie movie = movies.get(position);
                            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        });

                    }
                    catch(Exception E){
                        Log.d("movieList.error","going through response error");

                    }
                    },
                error -> {
                    // error
                    Log.d("movieList.error", "response fail");
                }
        );
        //final ArrayList<Movie> movies = new ArrayList<>();
        queue.add(searchRequest);
        Log.d("test.error", "response fail");


    }
}