package com.lexecc.wk2014;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class ScoreBoardFragment extends Fragment implements RetrieveTaskResponse {

    public static ScoreBoardFragment newInstance() {
        ScoreBoardFragment fragment = new ScoreBoardFragment();
        return fragment;
    }

    public ScoreBoardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_score_board, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        update();
    }

    @Override
    public void dataReceived(String data) {
        try
        {
            ArrayList<User> users = new ArrayList<User>();

            JSONArray jArray = new JSONArray(data);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject item = jArray.getJSONObject(i);
                User user = new User(item.getInt("ID"), item.getString("Name"), item.getInt("Score"));
                users.add(user);
            }

            ListView lv = (ListView)getActivity().findViewById(R.id.lsvScoreBoard);
            lv.setAdapter(new ListAdapter(getActivity(), users));
        }
        catch (Exception e) {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void update() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String[] parameters = new String[3];
        parameters[0] = "http://bedelec14.no-ip.org:8080/php/ws_get_scoreboard.php";
        parameters[1] = sharedPrefs.getString("user_name", "");
        parameters[2] = sharedPrefs.getString("password", "");

        RetrieveTask task = new RetrieveTask();
        task.delegate = this;
        task.execute(parameters);
    }

    private class ListAdapter extends ArrayAdapter<User> {

        private final FragmentActivity context;
        private final ArrayList<User> users;

        public ListAdapter(FragmentActivity context, ArrayList<User> users) {
            super(context, R.layout.list_scoreboard, users);

            this.context = context;
            this.users = users;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();

            View retVal = inflater.inflate(R.layout.list_scoreboard, null, true);
            TextView txtRank = (TextView)retVal.findViewById(R.id.txtRank);
            TextView txtName = (TextView)retVal.findViewById(R.id.txtName);
            TextView txtScore = (TextView)retVal.findViewById(R.id.txtScore);
            txtRank.setText(Integer.toString(position + 1));
            txtName.setText(users.get(position).getName());
            txtScore.setText("Points: " + Integer.toString(users.get(position).getScore()));

            if (position < 3) {
                ImageView imgCup = (ImageView)retVal.findViewById(R.id.imgCup);
                if (position == 0) imgCup.setImageResource(R.drawable.cup_gold_64);
                else if (position == 1) imgCup.setImageResource(R.drawable.cup_silver_64);
                else imgCup.setImageResource(R.drawable.cup_bronze_64);
            }

            return retVal;
        }
    }
}
