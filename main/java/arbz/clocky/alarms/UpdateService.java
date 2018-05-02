package arbz.clocky.alarms;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import arbz.clocky.fragments.AlarmConfigFragment;
import arbz.clocky.fragments.DownloadTask;

public class UpdateService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        String urlString = getSharedPreferences(AlarmConfigFragment.EDITOR_FILENAME, MODE_PRIVATE).getString(AlarmConfigFragment.EDITOR_ICS_LINK_KEY, null);

        if(urlString == null) {
            stopSelf();
        } else {
            DownloadTask downloadTask = new DownloadTask(new UpdateManager(this));
            downloadTask.execute(urlString);
        }

        return START_NOT_STICKY;
    }

    @Override @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }
}