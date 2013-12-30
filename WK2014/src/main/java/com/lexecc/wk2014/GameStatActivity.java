package com.lexecc.wk2014;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GameStatActivity extends Activity implements RetrieveTaskResponse {

    public final static String GAME_ID = "GAME_ID";

    private Integer mGameID;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_stats);

        this.setTitle("Game Stats");

        Intent intent = getIntent();
        mGameID = intent.getIntExtra("GAME_ID", 0);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String[] parameters = new String[4];
        parameters[0] = "http://bedelec14.no-ip.org:8080/php/ws_get_game_stats.php";
        parameters[1] = sharedPrefs.getString("user_name", "");
        parameters[2] = sharedPrefs.getString("password", "");
        parameters[3] = "game_id;" + String.valueOf(mGameID);

        RetrieveTask task = new RetrieveTask();
        task.delegate = this;
        task.execute(parameters);
    }

    @Override
    public void dataReceived(String data) {
        try
        {
            Log.e("WK2014", data);
            Game game = null;
            ArrayList<Bet> bets = new ArrayList<Bet>();

            JSONArray jArray = new JSONArray(data);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject item = jArray.getJSONObject(i);

                if (i == 0) {
                    // game object
                    JSONObject itemDate = item.getJSONObject("Date");
                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(itemDate.getString("date"));

                    game = new Game(item.getInt("ID"), date, item.getString("Team1"), item.getString("Team2"), item.getInt("ScoreTeam1"), item.getInt("ScoreTeam2"),
                            0, 0, 0);
                }
                else {
                    // bet object
                    Bet bet = new Bet(item.getString("UserName"), item.getInt("ScoreTeam1"), item.getInt("ScoreTeam2"), item.getInt("Points"));
                    bets.add(bet);
                }
            }

            // display game data
            TextView txtDate = (TextView)findViewById(R.id.txtDate);
            ImageView imgTeam1 = (ImageView)findViewById(R.id.imgTeam1);
            ImageView imgTeam2 = (ImageView)findViewById(R.id.imgTeam2);
            TextView txtTeam1 = (TextView)findViewById(R.id.txtTeam1);
            TextView txtTeam2 = (TextView)findViewById(R.id.txtTeam2);
            TextView txtTeam1Score = (TextView)findViewById(R.id.txtTeam1Score);
            TextView txtTeam2Score = (TextView)findViewById(R.id.txtTeam2Score);
            TextView txtTime = (TextView)findViewById(R.id.txtTime);
            txtDate.setText(new SimpleDateFormat("EEEE d MMMM", Locale.getDefault()).format(game.getDate()));
            imgTeam1.setImageResource(getResources().getIdentifier(game.getImage(1), "drawable", getPackageName()));
            imgTeam2.setImageResource(getResources().getIdentifier(game.getImage(2), "drawable", getPackageName()));
            txtTeam1.setText(game.getTeam1());
            txtTeam2.setText(game.getTeam2());
            if (game.getScoreTeam1() >= 0) txtTeam1Score.setText(Integer.toString(game.getScoreTeam1()));
            if (game.getScoreTeam2() >= 0) txtTeam2Score.setText(Integer.toString(game.getScoreTeam2()));
            txtTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(game.getDate()));

            // display bet data
            ListView lv = (ListView)findViewById(R.id.lsvStats);
            lv.setAdapter(new ListAdapter(this, bets));
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class ListAdapter extends ArrayAdapter<Bet> {

        private final Activity context;
        private final ArrayList<Bet> bets;

        public ListAdapter(Activity context, ArrayList<Bet> bets) {
            super(context, R.layout.list_game_stat, bets);

            this.context = context;
            this.bets = bets;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();

            // create new view
            View retVal = inflater.inflate(R.layout.list_game_stat, null, true);

            // set data
            ((TextView)retVal.findViewById(R.id.txtUser)).setText(bets.get(position).getUserName());
            if (bets.get(position).getScoreTeam1() >= 0 && bets.get(position).getScoreTeam2() >= 0) {
                TextView txtProno = (TextView)retVal.findViewById(R.id.txtProno);
                txtProno.setText("Prono: " + bets.get(position).getScoreTeam1() + " - " + bets.get(position).getScoreTeam2());
            }
            ((TextView)retVal.findViewById(R.id.txtPoints)).setText(bets.get(position).getPoints() + " Point" + (bets.get(position).getPoints() != 1 ? "s" : ""));

            return retVal;
        }
    }
}