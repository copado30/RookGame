//package edu.up.cs301.rook;
//
//import android.app.Service;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.os.IBinder;
//
//public class BackgroundMusic extends Service {
//    MediaPlayer mediaPlayer;
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mediaPlayer = MediaPlayer.create(this, R.raw.jazz);
//        mediaPlayer.setLooping(true); // Set looping
//        mediaPlayer.setVolume(0.5f, 0.5f); // Set volume (float values between 0.0 and 1.0)
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        mediaPlayer.start();
//        return START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//        }
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//    }
//}