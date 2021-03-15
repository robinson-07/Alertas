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

        n = 0;
        if(rington_uri.equals("Ninguno") || alerta.getTimbres() < 1) {
            ringtone = null;
            n = -1;
        }else if(rington_uri.equals("")) {
            ringtone = RingtoneManager.getRingtone(this,
                    RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION));
        }else {
            ringtone = RingtoneManager.getRingtone(this, uri_tono);
        }
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
                }else if(ringtone!=null && !ringtone.isPlaying()){
                    ringtone.play();
                    n++;
                }
            }

            @Override
            public void onFinish() {
                if(!alerta.isNotificacion()){
                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                }
                stopSelf();
            }
        }.start();
    }
}
