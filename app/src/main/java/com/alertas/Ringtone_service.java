package com.alertas;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;

public class Ringtone_service extends Service {
    private CountDownTimer count;
    private int n;
    private Ringtone ringtone;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand (Intent intent, int flags, int startId) {
        if(intent.hasExtra("numero_timbres")){
            int numero_timbres  = (int) intent.getExtras().get("numero_timbres");
            String notificacion = (String) intent.getExtras().get("notificacion");
            play_tone(numero_timbres, notificacion);
        }
        return Service.START_NOT_STICKY ;
    }

    public void onDestroy(){
        if(count!=null) count.cancel();
        if(ringtone!=null) ringtone.stop();
        super.onDestroy();
    }

    private void play_tone(final int numero_timbres, final String notificacion){
        SharedPreferences pref_rington = this.getSharedPreferences("rington",   Context.MODE_PRIVATE);
        String rington_uri = pref_rington.getString("tono_uri", "");
        Uri uri_tono = Uri.parse(rington_uri);

        ringtone = RingtoneManager.getRingtone(this, uri_tono);
        n = 0;
        count = new CountDownTimer(100000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(n >= numero_timbres && !ringtone.isPlaying()){
                    if(count != null) count.cancel();
                    if(!notificacion.equals("true")){
                        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(1);
                    }
                    stopSelf();
                }else if(!ringtone.isPlaying()){
                    ringtone.play();
                    n++;
                }
            }

            @Override
            public void onFinish() {}
        }.start();
    }
}
