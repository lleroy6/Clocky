package arbz.clocky.callbacks;

import android.content.SharedPreferences;
import android.net.NetworkInfo;

public interface DownloadCallback<T> {
    interface Progress {
        int ERROR = -1;
        int CONNECT_SUCCESS = 0;
        int GET_INPUT_STREAM_SUCCESS = 1;
        int PROCESS_INPUT_STREAM_IN_PROGRESS = 2;
        int PROCESS_INPUT_STREAM_SUCCESS = 3;
        int WRITING_IN_PROGRESS = 4;
        int WRITING_SUCCESS = 5;
        int WRITING_FAIL = 6;
    }

    NetworkInfo getActiveNetworkInfo();
    SharedPreferences getSharedPreferences(String fileName);
    void beginDownloading();
    void onProgressUpdate(int progressCode);
    void finishDownloading(T result);
}
