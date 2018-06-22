package com.example.milesbot.easycompass;

import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Compass extends AppCompatActivity implements SensorEventListener {

    TextView txt_compass;
    int mAzimuth;
    private SensorManager mSensorManager;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    boolean haveSensor = false, haveSensor2 = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        txt_compass = (TextView) findViewById(R.id.txt_azimuth);

        start();
    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
        {
            SensorManager.getRotationMatrixFromVector(rMat, sensorEvent.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;

        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            System.arraycopy(sensorEvent.values, 0, mLastAccelerometer, 0, sensorEvent.values.length);
            mLastAccelerometerSet = true;
        }
        else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            System.arraycopy(sensorEvent.values, 0, mLastMagnetometer, 0, sensorEvent.values.length);
            mLastMagnetometerSet = true;
        }

        if (mLastAccelerometerSet && mLastMagnetometerSet)
        {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        mAzimuth = Math.round(mAzimuth);

        String direction = "NW";

        if (mAzimuth >= 340 || mAzimuth <= 10)
        {
            direction = "N";
        }
        if (mAzimuth < 340 && mAzimuth > 280)
        {
            direction = "NW";
        }
        if (mAzimuth <= 280 && mAzimuth > 210)
        {
            direction = "W";
        }
        if (mAzimuth <= 210 && mAzimuth > 180)
        {
            direction = "SW";
        }
        if (mAzimuth <= 180 && mAzimuth > 140)
        {
            direction = "S";
        }
        if (mAzimuth <= 140 && mAzimuth > 100)
        {
            direction = "SE";
        }
        if (mAzimuth <= 100 && mAzimuth > 50)
        {
            direction = "E";
        }
        if (mAzimuth <=50 && mAzimuth > 10)
        {
            direction = "NE";
        }

        txt_compass.setText(mAzimuth + "° " + direction);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //Grants access to the sensors
    public void start()
    {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null)
        {
            if ((mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) ||
                    (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null))
            {
                noSensorsAlert();
            }
            else
            {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        }
        else
        {
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void noSensorsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device cannot support the compass!")
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    public void stop()
    {
        if (haveSensor && haveSensor2)
        {
            mSensorManager.unregisterListener(this, mAccelerometer);
            mSensorManager.unregisterListener(this, mMagnetometer);
        }
        else
        {
            if  (haveSensor)
            {
                mSensorManager.unregisterListener(this, mRotationV);
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        stop();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        start();
    }
}

