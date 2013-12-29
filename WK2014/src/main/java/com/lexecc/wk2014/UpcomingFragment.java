package com.lexecc.wk2014;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class UpcomingFragment extends Fragment implements RetrieveTaskResponse {

    public static UpcomingFragment newInstance() {
        UpcomingFragment fragment = new UpcomingFragment();
        return fragment;
    }
    public UpcomingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upcoming, container, false);
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
            ArrayList<Game> games = new ArrayList<Game>();

            JSONArray jArray = new JSONArray(data);
            for (int i = 0; i < jArray.length(); i += 2) {
                JSONObject jGame = jArray.getJSONObject(i);
                JSONObject jGameDate = jGame.getJSONObject("Date");
                JSONObject jBet = null;
                if (!jArray.isNull(i + 1)) {
                    jBet = jArray.getJSONObject(i + 1);
                }

                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jGameDate.getString("date"));
                Game game = new Game(jGame.getInt("ID"), date, jGame.getString("Team1"), jGame.getString("Team2"), jGame.getInt("ScoreTeam1"), jGame.getInt("ScoreTeam2"),
                        (jBet == null) ? -1 : jBet.getInt("ScoreTeam1"), (jBet == null) ? -1 : jBet.getInt("ScoreTeam2"), (jBet == null) ? 0 : jBet.getInt("Points"));
                games.add(game);
            }

            ListView lv = (ListView)getActivity().findViewById(R.id.lsvUpcoming);
            lv.setAdapter(new ListAdapter(getActivity(), games));
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                    // get data
                    Game game = (Game)view.getTag();
                    Integer img1 = (Integer)view.findViewById(R.id.imgTeam1).getTag();
                    Integer img2 = (Integer)view.findViewById(R.id.imgTeam2).getTag();

                    // set data
                    Intent intent = new Intent(getActivity(), PlaceBetActivity.class);
                    intent.putExtra(PlaceBetActivity.GAME_ID, game.getID());
                    intent.putExtra(PlaceBetActivity.PRONO_1, game.getPronoTeam1());
                    intent.putExtra(PlaceBetActivity.PRONO_2, game.getPronoTeam2());
                    intent.putExtra(PlaceBetActivity.TEAM_1, game.getTeam1());
                    intent.putExtra(PlaceBetActivity.TEAM_2, game.getTeam2());
                    intent.putExtra(PlaceBetActivity.IMG_1, img1);
                    intent.putExtra(PlaceBetActivity.IMG_2, img2);
                    startActivity(intent);
                }

            });
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
        parameters[0] = "http://bedelec14.no-ip.org:8080/php/ws_get_upcoming.php";
        parameters[1] = sharedPrefs.getString("user_name", "");
        parameters[2] = sharedPrefs.getString("password", "");

        RetrieveTask task = new RetrieveTask();
        task.delegate = this;
        task.execute(parameters);
    }

    private class ListAdapter extends ArrayAdapter<Game> {

        private final FragmentActivity context;
        private final ArrayList<Game> games;

        public ListAdapter(FragmentActivity context, ArrayList<Game> games) {
            super(context, R.layout.list_scoreboard, games);

            this.context = context;
            this.games = games;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();

            // create new view & supply tags
            View retVal = inflater.inflate(R.layout.list_upcoming, null, true);
            retVal.setTag(games.get(position));

            // date
            Calendar cal = Calendar.getInstance();
            cal.setTime(games.get(position).getDate());
            Integer currentDay = cal.get(Calendar.DAY_OF_MONTH);
            Integer previousDay = 0;
            if (position > 0) {
                cal.setTime(games.get(position - 1).getDate());
                previousDay = cal.get(Calendar.DAY_OF_MONTH);
            }
            TextView txtDate = (TextView)retVal.findViewById(R.id.txtDate);
            if (currentDay != previousDay) {
                txtDate.setText(new SimpleDateFormat("EEEE d MMMM", Locale.getDefault()).format(games.get(position).getDate()));
                txtDate.setVisibility(View.VISIBLE);
            }
            else {
                txtDate.setVisibility(View.GONE);
            }

            // remaining data
            ImageView imgTeam1 = (ImageView)retVal.findViewById(R.id.imgTeam1);
            ImageView imgTeam2 = (ImageView)retVal.findViewById(R.id.imgTeam2);
            TextView txtTeam1 = (TextView)retVal.findViewById(R.id.txtTeam1);
            TextView txtTeam2 = (TextView)retVal.findViewById(R.id.txtTeam2);
            TextView txtTeam1Score = (TextView)retVal.findViewById(R.id.txtTeam1Score);
            TextView txtTeam2Score = (TextView)retVal.findViewById(R.id.txtTeam2Score);
            TextView txtTime = (TextView)retVal.findViewById(R.id.txtTime);
            imgTeam1.setTag(getResources().getIdentifier(games.get(position).getImage(1), "drawable", context.getPackageName()));
            imgTeam2.setTag(getResources().getIdentifier(games.get(position).getImage(2), "drawable", context.getPackageName()));
            imgTeam1.setImageResource((Integer)imgTeam1.getTag());
            imgTeam2.setImageResource((Integer)imgTeam2.getTag());
            txtTeam1.setText(games.get(position).getTeam1());
            txtTeam2.setText(games.get(position).getTeam2());
            Integer prono1 = games.get(position).getPronoTeam1();
            Integer prono2 = games.get(position).getPronoTeam2();
            if (prono1 >= 0) txtTeam1Score.setText(Integer.toString(prono1));
            if (prono2 >= 0) txtTeam2Score.setText(Integer.toString(prono2));
            if (prono1 >= 0 && prono2 >= 0) retVal.findViewById(R.id.llProno).setBackgroundColor(Color.TRANSPARENT);
            txtTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(games.get(position).getDate()));

            return retVal;
        }
    }
}
