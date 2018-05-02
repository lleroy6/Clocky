package arbz.clocky.fragments;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.SparseArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import arbz.clocky.alarms.ICSParser;
import arbz.clocky.callbacks.DownloadCallback;

public class DownloadTask extends AsyncTask<String, Integer, DownloadTask.Result> {
    private DownloadCallback<Result> mCallback;

    public DownloadTask(DownloadCallback<Result> callback) {
        setCallback(callback);
    }

    void setCallback(DownloadCallback<Result> callback) {
        mCallback = callback;
    }

    public class Result {
        public SparseArray<Date> mResultValue;
        public Exception mException;

        Result(SparseArray<Date> resultValue) {
            mResultValue = resultValue;
        }
        Result(Exception exception) {
            mException = exception;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        mCallback.onProgressUpdate(values[0]);
    }

    @Override
    protected void onPreExecute() {
        if(mCallback != null) {
            mCallback.beginDownloading();
            NetworkInfo networkInfo = mCallback.getActiveNetworkInfo();

            if(networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI &&
                            networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                mCallback.finishDownloading(null);
                cancel(true);
            }
        }
    }

    @Override
    protected DownloadTask.Result doInBackground(String... urls) {
        Result result = null;

        if(!isCancelled() && urls != null && urls.length > 0) {
            String urlString = urls[0];

            try {
                URL url = new URL(urlString);
                SparseArray<Date> resultList = downloadUrl(url);

                if(resultList != null) {
                    result = new Result(resultList);
                } else {
                    throw new IOException("No response received");
                }
            } catch (Exception e) {
                result = new Result(e);
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(Result result) {
        if(result != null && mCallback != null) {
            mCallback.finishDownloading(result);
        }
    }

    private SparseArray<Date> downloadUrl(URL url) throws IOException, ParseException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        SparseArray<Date> result = null;

        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);

            int responseCode = connection.getResponseCode();

            if(responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            stream = connection.getInputStream();
            publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS);

            if(stream != null) {
                publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS);
                result = ICSParser.getDateList(stream);
                publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS);

            }
        } finally {
            if(stream != null) stream.close();
            if(connection != null) connection.disconnect();
        }

        return result;
    }
}