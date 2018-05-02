package arbz.clocky.alarms;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import arbz.clocky.MainActivity;
import arbz.clocky.R;
import arbz.clocky.callbacks.DownloadCallback;
import arbz.clocky.fragments.AlarmConfigFragment;
import arbz.clocky.fragments.DownloadTask;

import static android.support.v4.app.NotificationCompat.CATEGORY_PROGRESS;
import static android.support.v4.app.NotificationCompat.PRIORITY_DEFAULT;
import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

public class UpdateManager implements DownloadCallback<DownloadTask.Result> {
    private final int NOTIF_ID = 100;
    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private NetworkInfo mNetworkInfo;
    private SharedPreferences mSharedPreferences;

    public UpdateManager(Context context) {
        mContext = context;
        mNetworkInfo = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        mSharedPreferences = mContext.getSharedPreferences(AlarmConfigFragment.EDITOR_FILENAME, Context.MODE_PRIVATE);
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext, "DownloadNotifChan");
        mBuilder.setContentTitle("Mise à jour du calendrier")
                .setContentText("Téléchargement en cours")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_DEFAULT)
                .setVisibility(VISIBILITY_PUBLIC)
                .setCategory(CATEGORY_PROGRESS)
                .setAutoCancel(true)
                .setFullScreenIntent(PendingIntent.getService(mContext, 0, new Intent(), 0), true);

        mNotificationManager.cancel(NOTIF_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("DownloadNotifChan", "DownloadNotif", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("channel used for download progress");
            mNotificationManager.createNotificationChannel(channel);
        }

    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        return mNetworkInfo;
    }

    @Override
    public SharedPreferences getSharedPreferences(String fileName) {
        return mSharedPreferences;
    }

    @Override
    public void beginDownloading() {
        mBuilder.setProgress(100, 0, false);
        mNotificationManager.notify(NOTIF_ID, mBuilder.build());
    }

    @Override
    public void onProgressUpdate(int progressCode) {
        int progress = 0;
        String content = "";

        switch(progressCode) {
            case DownloadCallback.Progress.CONNECT_SUCCESS:
                progress = 10;
                content = "Connexion établie";
                break;

            case DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS:
                progress = 50;
                content = "Calendrier téléchargé";
                break;

            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                progress = 70;
                content = "Analyse du calendrier";
                break;

            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS:
                progress = 80;
                content = "Analyse terminée";
                break;

            case DownloadCallback.Progress.WRITING_IN_PROGRESS:
                progress = 90;
                content = "Sauvegarde en cours";
                break;

            case DownloadCallback.Progress.WRITING_SUCCESS:
                progress = 100;
                content = "Sauvegarde terminée";
        }

        mBuilder.setProgress(100, progress, false)
                .setContentText(content);
        mNotificationManager.notify(NOTIF_ID, mBuilder.build());
    }

    @Override
    public void finishDownloading(DownloadTask.Result result) {
        if(result == null) {
            Toast.makeText(mContext, "Problème de connectivité", Toast.LENGTH_SHORT).show();

            mBuilder.setContentText("Problème de connectivité")
                    .setProgress(0, 0, false);
            mNotificationManager.notify(NOTIF_ID, mBuilder.build());
            mNotificationManager.cancel(NOTIF_ID);

        } else if(result.mException != null) {
            Toast.makeText(mContext, result.mException.toString(), Toast.LENGTH_SHORT).show();

            mBuilder.setContentText("Problème de connectivité")
                    .setProgress(0, 0, false);
            mNotificationManager.notify(NOTIF_ID, mBuilder.build());

        } else if(result.mResultValue != null) {
            onProgressUpdate(DownloadCallback.Progress.WRITING_IN_PROGRESS);
            SparseArray<Date> mDateList = result.mResultValue;

            SimpleDateFormat dateFormatter = new SimpleDateFormat("EE dd/MM", Locale.getDefault());
            SimpleDateFormat hourFormatter = new SimpleDateFormat("hh:mm", Locale.getDefault());

            SharedPreferences.Editor editor = mSharedPreferences.edit();

            int alarmQuantity = mSharedPreferences.getInt(AlarmConfigFragment.EDITOR_ALARM_QTY_KEY, 1);

            for(int i = Calendar.MONDAY; i <= Calendar.FRIDAY; i++) {
                String dayKey = AlarmDay.ACTIVATED + i;
                boolean dayActivated = mSharedPreferences.getBoolean(dayKey, true);

                Date date = mDateList.get(i);

                editor.putString(AlarmDay.DATE + i, dateFormatter.format(date).replace(".", ""))
                        .putString(AlarmDay.HOUR + i, hourFormatter.format(date))
                        .putBoolean(dayKey, dayActivated);

                for (int j = 0; j < alarmQuantity; j++) {
                    String alarmKey = AlarmHour.ALARM + i + j;
                    boolean alarmActivated = mSharedPreferences.getBoolean(alarmKey, true);

                    editor.putBoolean(alarmKey, alarmActivated);
                }
            }

            if(editor.commit()) {
                try {
                    AlarmSetter.setAlarms(mContext);
                } catch (ParseException e) {
                    Toast.makeText(mContext, R.string.parsing_alarms_exception, Toast.LENGTH_SHORT).show();
                }
                onProgressUpdate(DownloadCallback.Progress.WRITING_SUCCESS);
                finishWriting(true);
            } else {
                onProgressUpdate(DownloadCallback.Progress.WRITING_FAIL);
                finishWriting(false);
            }
        }
    }

    private void finishWriting(boolean result) {
        mBuilder.setContentText("Mise à jour terminée")
                .setProgress(0, 0, false);
        mNotificationManager.notify(NOTIF_ID, mBuilder.build());
        mNotificationManager.cancel(NOTIF_ID);

        mBuilder.setFullScreenIntent(null, false);
        mNotificationManager.notify(NOTIF_ID, mBuilder.build());

        if(!result) {
            Toast.makeText(mContext, R.string.problem_when_writing, Toast.LENGTH_SHORT).show();
        }

        if(mContext instanceof MainActivity) {
            ((MainActivity) mContext).updateAlarms();
        }
    }
}
