package com.lexecc.wk2014;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceBetActivity extends ActionBarActivity implements RetrieveTaskResponse {

    public final static String GAME_ID = "GAME_ID";
    public final static String PRONO_1 = "PRONO_1";
    public final static String PRONO_2 = "PRONO_2";
    public final static String TEAM_1 = "TEAM_1";
    public final static String TEAM_2 = "TEAM_2";
    public final static String IMG_1 = "IMG_1";
    public final static String IMG_2 = "IMG_2";

    private Integer mGameID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_bet);

        this.setTitle("Update Prono");

        Intent intent = getIntent();
        mGameID = intent.getIntExtra("GAME_ID", 0);
        ((TextView)findViewById(R.id.txtTeam1)).setText(intent.getStringExtra(TEAM_1));
        ((TextView)findViewById(R.id.txtTeam2)).setText(intent.getStringExtra(TEAM_2));
        ((TextView)findViewById(R.id.txtScoreTeam1)).setText(String.valueOf(intent.getIntExtra(PRONO_1, 0)));
        ((TextView)findViewById(R.id.txtScoreTeam2)).setText(String.valueOf(intent.getIntExtra(PRONO_2, 0)));
        ((ImageView)findViewById(R.id.imgTeam1)).setImageResource(intent.getIntExtra(IMG_1, 0));
        ((ImageView)findViewById(R.id.imgTeam2)).setImageResource(intent.getIntExtra(IMG_2, 0));
    }

    public void btnAccept_onClick(View view) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String[] parameters = new String[6];
        parameters[0] = "http://bedelec14.no-ip.org:8080/php/ws_place_bet.php";
        parameters[1] = sharedPrefs.getString("user_name", "");
        parameters[2] = sharedPrefs.getString("password", "");
        parameters[3] = "game_id;" + String.valueOf(mGameID);
        parameters[4] = "score_1;" + ((TextView)findViewById(R.id.txtScoreTeam1)).getText().toString();
        parameters[5] = "score_2;" + ((TextView)findViewById(R.id.txtScoreTeam2)).getText().toString();

        RetrieveTask task = new RetrieveTask();
        task.delegate = this;
        task.execute(parameters);
    }

    @Override
    public void dataReceived(String data) {
        if (data.startsWith("0")) {
            this.finish();
        }
    }

}
