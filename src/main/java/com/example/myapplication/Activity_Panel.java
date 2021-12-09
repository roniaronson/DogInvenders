package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.text.DecimalFormat;
import java.util.Random;

public class Activity_Panel extends AppCompatActivity {

    private ImageView[][] view_path;
    private int[][] vals;
    private ImageView[] panel_IMG_hearts;
    private ImageButton[] panel_IMG_direction;

    private SensorManager sensorManager;
    private Sensor accSensor;

    private final int MAX_LIVES = 3;

    private int DELAY = 400;

    private int timer = 0;

    private int curLives = 3;
    private int index_PoodleRow = 8;
    private int path_number = 5;
    private int IMG_number = 9;
    private int index_bone = 9;
    private int IMG_dog = 10;
    private int dog_id = 0;

    private  boolean is_slow = true;
    private boolean sensors = false;

    MediaPlayer mediaPlayer_background;
    MediaPlayer mediaPlayer_bark;
    MediaPlayer mediaPlayer_cry;


    final Handler handler = new Handler();
    private Runnable r = new Runnable() {
        public void run() {
            checkCrash();
            runLogic();
            updateUI();
            handler.postDelayed(r, DELAY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle b = getIntent().getExtras();
        dog_id = b.getInt("dog_id");
        is_slow = b.getBoolean("isSlow");
        sensors = b.getBoolean("sensors");
        mediaPlayer_background =  MediaPlayer.create(Activity_Panel.this, R.raw.music);
        mediaPlayer_background.start();
        mediaPlayer_bark = MediaPlayer.create(Activity_Panel.this, R.raw.single_bark);
        mediaPlayer_cry = MediaPlayer.create(Activity_Panel.this, R.raw.dog_cry);

        findViews();

        initDELAY(is_slow);

        if(sensors){
            initSensor();
            panel_IMG_direction[0].setVisibility(View.INVISIBLE);
            panel_IMG_direction[1].setVisibility(View.INVISIBLE);
        }

        panel_IMG_direction[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveDog(-1);
            }
        });
        panel_IMG_direction[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveDog(1);
            }
        });


    }

    private void initDELAY(boolean is_slow) {
        if(is_slow)
            this.DELAY = 400;
        else
            this.DELAY = 200;
    }

    @Override
    protected void onStart() {
        super.onStart();
        startTicker();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTicker();
    }

    private void startTicker() {
        handler.postDelayed(r, DELAY);
    }

    private void stopTicker() {
        handler.removeCallbacks(r);
    }


    private void findViews() {
        view_path = new ImageView[][]{
                {findViewById(R.id.panel_IMG_00), findViewById(R.id.panel_IMG_01), findViewById(R.id.panel_IMG_02), findViewById(R.id.panel_IMG_03), findViewById(R.id.panel_IMG_04)},
                {findViewById(R.id.panel_IMG_10), findViewById(R.id.panel_IMG_11), findViewById(R.id.panel_IMG_12), findViewById(R.id.panel_IMG_13), findViewById(R.id.panel_IMG_14)},
                {findViewById(R.id.panel_IMG_20), findViewById(R.id.panel_IMG_21), findViewById(R.id.panel_IMG_22), findViewById(R.id.panel_IMG_23), findViewById(R.id.panel_IMG_24)},
                {findViewById(R.id.panel_IMG_30), findViewById(R.id.panel_IMG_31), findViewById(R.id.panel_IMG_32), findViewById(R.id.panel_IMG_33), findViewById(R.id.panel_IMG_34)},
                {findViewById(R.id.panel_IMG_40), findViewById(R.id.panel_IMG_41), findViewById(R.id.panel_IMG_42), findViewById(R.id.panel_IMG_43), findViewById(R.id.panel_IMG_44)},
                {findViewById(R.id.panel_IMG_50), findViewById(R.id.panel_IMG_51), findViewById(R.id.panel_IMG_52), findViewById(R.id.panel_IMG_53), findViewById(R.id.panel_IMG_54)},
                {findViewById(R.id.panel_IMG_60), findViewById(R.id.panel_IMG_61), findViewById(R.id.panel_IMG_62), findViewById(R.id.panel_IMG_63), findViewById(R.id.panel_IMG_64)},
                {findViewById(R.id.panel_IMG_70), findViewById(R.id.panel_IMG_71), findViewById(R.id.panel_IMG_72), findViewById(R.id.panel_IMG_73), findViewById(R.id.panel_IMG_74)},
                {findViewById(R.id.panel_IMG_80), findViewById(R.id.panel_IMG_81), findViewById(R.id.panel_IMG_82), findViewById(R.id.panel_IMG_83), findViewById(R.id.panel_IMG_84)}
        };
        vals = new int[view_path.length][view_path[0].length];
        for (int i = 0; i < vals.length; i++) {
            for (int j = 0; j < vals[i].length; j++) {
                vals[i][j] = 0;
            }
        }
        vals[8][2] = IMG_dog;

        panel_IMG_direction = new ImageButton[]{
                findViewById(R.id.panel_IMG_left), findViewById(R.id.panel_IMG_right)};

        panel_IMG_hearts = new ImageView[]{
                findViewById(R.id.panel_IMG_heart1), findViewById(R.id.panel_IMG_heart2), findViewById(R.id.panel_IMG_heart3)};
    }

    private void runLogic() {
        for (int i = vals.length - 2; i > 0; i--) {
            for (int j = 0; j < vals[i].length; j++) {
                vals[i][j] = vals[i - 1][j];
            }
        }
        for (int i = 0; i < vals[0].length; i++) {
            vals[0][i] = 0;
        }

        if (timer % 2 == 0) {
            Random random_IMG = new Random();
            Random random_POS = new Random();
            int random_res_IMG = random_IMG.nextInt(IMG_number) + 1;
            int random_res_POS = random_POS.nextInt(path_number);
            for (int i = 0; i < vals[0].length; i++) {
                vals[0][i] = 0;
            }
            vals[0][random_res_POS] = random_res_IMG;
        }
        timer++;
    }


    private void checkCrash() {
        int curPos = getDogPosition();
        if (vals[index_PoodleRow - 1][curPos] != 0 && vals[index_PoodleRow - 1][curPos] != index_bone){
            mediaPlayer_cry.start();
            updateLives(false);
        }

        if (vals[index_PoodleRow - 1][curPos] == index_bone){
            mediaPlayer_bark.start();
            updateLives(true);
        }

    }

    private void updateLives(boolean isBone) {
        if (isBone) {
            if (curLives < 3) {
                panel_IMG_hearts[curLives].setVisibility(View.VISIBLE);
                curLives++;
            }
        } else {
            vibrate();
            if (curLives > 0) {
                panel_IMG_hearts[curLives - 1].setVisibility(View.INVISIBLE);
                curLives--;
            } else {
                mediaPlayer_background.stop();
                finish();
                Intent gameOverScreen = new Intent(this, Activity_GameOver.class);
                startActivity(gameOverScreen);
            }
        }
    }

    private void updateUI() {
        for (int i = 0; i < view_path.length; i++) {
            for (int j = 0; j < view_path[i].length; j++) {
                if (vals[i][j] == 0) {
                    view_path[i][j].setVisibility(View.INVISIBLE);
                }
                if (vals[i][j] == 1) {
                    view_path[i][j].setImageResource(R.drawable.spaceinvaders);
                    view_path[i][j].setVisibility(View.VISIBLE);
                }
                if (vals[i][j] == 2) {
                    view_path[i][j].setImageResource(R.drawable.ufo);
                    view_path[i][j].setVisibility(View.VISIBLE);
                }
                if (vals[i][j] == 3) {
                    view_path[i][j].setImageResource(R.drawable.planet);
                    view_path[i][j].setVisibility(View.VISIBLE);
                }
                if (vals[i][j] == 4) {
                    view_path[i][j].setImageResource(R.drawable.img_astronaut);
                    view_path[i][j].setVisibility(View.VISIBLE);
                }
                if (vals[i][j] == 5) {
                    view_path[i][j].setImageResource(R.drawable.ufo2);
                    view_path[i][j].setVisibility(View.VISIBLE);
                }
                if (vals[i][j] == 6) {
                    view_path[i][j].setImageResource(R.drawable.ufo3);
                    view_path[i][j].setVisibility(View.VISIBLE);
                }
                if (vals[i][j] == 7) {
                    view_path[i][j].setImageResource(R.drawable.spaceship2);
                    view_path[i][j].setVisibility(View.VISIBLE);
                }
                if (vals[i][j] == 8) {
                    view_path[i][j].setImageResource(R.drawable.spaceship3);
                    view_path[i][j].setVisibility(View.VISIBLE);
                }
                if (vals[i][j] == index_bone) {
                    view_path[i][j].setImageResource(R.drawable.img_bone);
                    view_path[i][j].setVisibility(View.VISIBLE);
                }
                if (vals[i][j] == IMG_dog) {
                    view_path[i][j].setImageResource(dog_id);
                    view_path[i][j].setVisibility(View.VISIBLE);
                }
            }
        }
        int curPos = getDogPosition();
        if(!sensors){
            if (curPos == 0) {
                panel_IMG_direction[0].setVisibility(View.INVISIBLE);
                panel_IMG_direction[1].setVisibility(View.VISIBLE);
            }
            if (curPos == 1) {
                panel_IMG_direction[0].setVisibility(View.VISIBLE);
                panel_IMG_direction[1].setVisibility(View.VISIBLE);
            }
            if (curPos == 2) {
                panel_IMG_direction[0].setVisibility(View.VISIBLE);
                panel_IMG_direction[1].setVisibility(View.VISIBLE);
            }
            if (curPos == 3) {
                panel_IMG_direction[0].setVisibility(View.VISIBLE);
                panel_IMG_direction[1].setVisibility(View.VISIBLE);
            }
            if (curPos == 4) {
                panel_IMG_direction[0].setVisibility(View.VISIBLE);
                panel_IMG_direction[1].setVisibility(View.INVISIBLE);
            }
        }

    }

    private int getDogPosition() {
        for (int i = 0; i < vals[index_PoodleRow].length; i++) {
            if (vals[index_PoodleRow][i] == IMG_dog)
                return i;
        }
        return -1;
    }

    private void moveDog(float direction) {
        int curPos = getDogPosition();
        vals[index_PoodleRow][curPos] = 0;
            if (direction == 1 && curPos < 4)
                curPos++;
            if(direction < 1 && curPos > 0)
                curPos--;
        vals[index_PoodleRow][curPos] = IMG_dog;
    }

    //---------Sensors----------
    private void initSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private final SensorEventListener accSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            DecimalFormat df = new DecimalFormat("##.##");
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if( x > 1.5)
                moveDog(-1);
            if(x < -1.5)
                moveDog(1);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(sensors)
            sensorManager.registerListener(accSensorEventListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(sensors)
            sensorManager.unregisterListener(accSensorEventListener);
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(300);
        }
    }

}

