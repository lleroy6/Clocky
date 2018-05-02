package arbz.clocky;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;
import java.lang.*;

public class AccelerometerManager {
    interface AccelerometerListener {
        void onShakeActiveAlarms(float force);
        void onShakeUpdateAlarms(float force);
    }

    private static float limite_mouvement_reconnue = 20.0f;
    private static int interval_temps_min = 50;

    private static Sensor sensor;
    private static SensorManager sensorManager;
    private static AccelerometerListener listener;

    //Accelerometre supporté ou pas
    private static Boolean supported;

    //Accelerometre en fonctionnement ou pas
    private static boolean running = false;

    public static boolean isListening() {
        return running;
    }

    public static void stopListening() {
        running = false;
        try {
            if ( sensorEventListener != null && sensorManager != null) {
                sensorManager.unregisterListener(sensorEventListener);
            }
        } catch (Exception e) {
        }
    }

    public static boolean isSupported(Context context) {
        //Si l'accelerometre est supporté par l'appareil
        if (supported == null) {
            //On verifie le context
            if (context != null) {
                //Creation du sensor manager
                sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
                List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

                supported = new Boolean(sensors.size() > 0);
            } else {
                supported = Boolean.FALSE;
            }
        }
        return supported;
    }

    public static void startListening(AccelerometerListener accelerometerListener, Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        if (sensors.size() > 0) {
            //C'est l'accelerometre
            sensor = sensors.get(0);
            running = sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_GAME);
            listener = accelerometerListener;
        }
    }

    public static void startListening(AccelerometerListener accelerometerListener, Context context, int limite_mouv, int limite_temps) {
        AccelerometerManager.limite_mouvement_reconnue = limite_mouv;
        AccelerometerManager.interval_temps_min = limite_temps;
        startListening(accelerometerListener, context);
    }

    private static SensorEventListener sensorEventListener = new SensorEventListener() {

        private long now = 0;
        private long timeDiff = 0;
        private long lastUpdate = 0;
        private long lastShake = 0;

        private float x = 0;
        private float y = 0;
        private float z = 0;
        private float lastX = 0;
        private float lastY = 0;
        private float lastZ = 0;
        private float force = 0;

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {

            now = event.timestamp;

            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            if (lastUpdate == 0) {
                lastUpdate = now;
                lastShake = now;
                lastX = x;
                lastY = y;
                lastZ = z;

            } else {
                timeDiff = now - lastUpdate;

                if (timeDiff > 0) {

                    force = Math.abs(x + y + z - lastX - lastY - lastZ);

                    if (Float.compare(force, limite_mouvement_reconnue) > 0) {

                        if (now - lastShake >= interval_temps_min*1000000) {
                            if(Math.abs(x)>Math.abs(y)) {
                                listener.onShakeActiveAlarms(force);
                            }else {
                                listener.onShakeUpdateAlarms(force);
                            }
                            //float a = now - lastShake;
                            //float b = a - (interval*1000000);
                            //String txt = "x = "+x+" y= "+y+" z ="+z;
                            //Toast.makeText(context, txt, Toast.LENGTH_SHORT).show();


                        } else {
                            //Toast.makeText(context, "No Motion detected",Toast.LENGTH_SHORT).show();

                        }
                        lastShake = now;
                    }
                    lastX = x;
                    lastY = y;
                    lastZ = z;
                    lastUpdate = now;
                } else {
                    //Toast.makeText(context, "No Motion detected", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
