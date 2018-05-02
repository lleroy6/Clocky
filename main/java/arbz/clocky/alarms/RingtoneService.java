package arbz.clocky.alarms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import arbz.clocky.R;
import arbz.clocky.fragments.AlarmConfigFragment;

public class RingtoneService extends Service {
    public static String CUSTOM_ACTION = "arbz.clocky.RINGTONE_NOTIFICATION";

    private Ringtone mRingtone;
    private Vibrator mVibrator;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        if(startId == 1) {
            SharedPreferences sharedPreferences = getSharedPreferences(AlarmConfigFragment.EDITOR_FILENAME, MODE_PRIVATE);
            final boolean ring = sharedPreferences.getBoolean(AlarmConfigFragment.EDITOR_RINGTONE_NOTMUTE_KEY, true);
            final boolean vib = sharedPreferences.getBoolean(AlarmConfigFragment.EDITOR_RINGTONE_VIBRATOR_KEY, true);

            if(ring) {
                String ringtoneStr = sharedPreferences.getString(AlarmConfigFragment.EDITOR_RINGTONE_KEY, null);
                Uri uri;

                if(ringtoneStr == null) {
                    uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                } else {
                    uri = Uri.parse(ringtoneStr);
                }

                AudioAttributes ringtoneAudioAttrs = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();

                mRingtone = RingtoneManager.getRingtone(getBaseContext(), uri);
                mRingtone.setAudioAttributes(ringtoneAudioAttrs);
                mRingtone.play();
            }

            if(vib) {
                mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                mVibrator.vibrate(new long[]{500, 500}, 0);
            }

            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(ring) mRingtone.stop();
                    if(vib) mVibrator.cancel();

                    LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(mBroadcastReceiver);
                    stopSelf();
                }
            };

            LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(CUSTOM_ACTION));

            String courseLabel = intent.getStringExtra("courseTime");
            int requestCode = intent.getIntExtra("requestCode", 0);

            PendingIntent pendingIntent = PendingIntent.getService(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(getBaseContext(), NotificationCompat.CATEGORY_ALARM)
                    .setContentTitle("Alarm title ")
                    .setContentText(courseLabel)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setDeleteIntent(pendingIntent)
                    .setContentIntent(pendingIntent)
                    .setFullScreenIntent(null, true)
                    .setAutoCancel(true)
                    .build();

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0, notification);
            startForeground(0, notification);

        } else {
            Intent newIntent = new Intent(CUSTOM_ACTION);

            LocalBroadcastManager.getInstance(this).sendBroadcast(newIntent);
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    @Override @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }
}