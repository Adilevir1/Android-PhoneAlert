package com.adilevi.phonealert;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class AlertActivity extends AppCompatActivity {

    public static final String NAME = "name";
    public static final String DISPLAY_MESSAGE_BODY = "displayMessageBody";
    private MediaPlayer mp;

    public static void startActivity(Context context, String name, String displayMessageBody) {
        Intent alert = new Intent(context, AlertActivity.class);
        alert.putExtra(NAME, name);
        alert.putExtra(DISPLAY_MESSAGE_BODY, displayMessageBody);
        alert.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(alert);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString(NAME);
            String displayMessageBody = extras.getString(DISPLAY_MESSAGE_BODY);

            setTitle(name);
            TextView text = findViewById(R.id.text);
            text.setText(displayMessageBody);
        }
        TextView close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.stop();
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp.seekTo(0);
                mp.start();
            }
        });
        mp.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mp.stop();
    }
}

