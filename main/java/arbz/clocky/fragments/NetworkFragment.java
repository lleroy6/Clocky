package arbz.clocky.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import arbz.clocky.alarms.UpdateManager;
import arbz.clocky.callbacks.DownloadCallback;

public class NetworkFragment extends Fragment {
    public static final String TAG = "NetworkFragment";

    private DownloadCallback<DownloadTask.Result> mCallback;
    private DownloadTask mDownloadTask;
    private String mUrlString;

    public static NetworkFragment getInstance(FragmentManager fragmentManager, Context context) {
        NetworkFragment networkFragment = (NetworkFragment) fragmentManager.findFragmentByTag(NetworkFragment.TAG);

        if(networkFragment == null) {
            networkFragment = new NetworkFragment();
            networkFragment.mCallback = new UpdateManager(context);
            fragmentManager.beginTransaction()
                    .add(networkFragment, TAG)
                    .commit();
        }

        return networkFragment;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        mUrlString = mCallback.getSharedPreferences(AlarmConfigFragment.EDITOR_FILENAME).getString(AlarmConfigFragment.EDITOR_ICS_LINK_KEY, null);
        setRetainInstance(true);
        startDownload();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onDestroy() {
        cancelDownload();
        mCallback = null;
        super.onDestroy();
    }

    public void startDownload() {
        cancelDownload();
        mUrlString = mCallback.getSharedPreferences(AlarmConfigFragment.EDITOR_FILENAME).getString(AlarmConfigFragment.EDITOR_ICS_LINK_KEY, null);
        mDownloadTask = new DownloadTask(mCallback);
        mDownloadTask.execute(mUrlString);
    }

    public void cancelDownload() {
        if(mDownloadTask != null) {
            mDownloadTask.cancel(true);
        }
    }
}