package com.alertas.modelo;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;

public class RingtoneService extends Service  {
    private CountDownTimer count;
    private int n;
    private Ringtone ringtone;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand (Intent intent, int flags, int startId) {
        Bundle bundle = intent.getBundleExtra("bundle");
        Alerta alerta = (Alerta)bundle.getSerializable("alerta");
        play_tone(alerta);
        return Service.START_NOT_STICKY ;
    }

    public void onDestroy(){
        if(count!=null) count.cancel();
        if(ringtone!=null) ringtone.stop();
        super.onDestroy();
    }

    private void play_tone(final Alerta alerta){
        SharedPreferences pref_rington = this.getSharedPreferences("rington",   Context.MODE_PRIVATE);
        String rington_uri = pref_rington.getString("tono_uri", "");
        Uri uri_tono = Uri.parse(rington_uri);

        ringtone = RingtoneManager.getRingtone(this, uri_tono);
        n = 0;
        count = new CountDownTimer(100000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(n >= alerta.getTimbres() && !ringtone.isPlaying()){
                    if(count != null) count.cancel();
                    if(!alerta.isNotificacion()){
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
