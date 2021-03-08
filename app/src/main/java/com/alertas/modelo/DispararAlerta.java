package com.alertas.modelo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.alertas.Principal;
import com.alertas.R;

import static android.app.Notification.VISIBILITY_PUBLIC;
import static android.content.Context.NOTIFICATION_SERVICE;

public class DispararAlerta extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals("com.alertas.DISPARAR_ALERTA")) {
            Bundle bundle = intent.getBundleExtra("bundle");
            if(bundle==null) return;
            Alerta alerta = (Alerta)bundle.getSerializable("alerta");
            if(alerta==null) return;
            reproducir_tono(context, alerta);
            notificacion(context, alerta);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    actualizarAlerta(context);
                }
            }, 1000);

        } else if (intent.getAction().equals("com.alertas.DETENER_ALERTA")) {
            context.stopService(new Intent(context, RingtoneService.class));
        }
    }

    private void actualizarAlerta(Context context){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.alertas.ESTABLECER_ALERTA");
        context.sendBroadcast(broadcastIntent);
    }

    private void reproducir_tono(Context context, Alerta alerta){
        Intent intent = new Intent(context, RingtoneService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("alerta", alerta);
        intent.putExtra("bundle", bundle);
        context.startService(intent);
    }

    public void notificacion(final Context context, Alerta alerta){
        int id_notificacion = 1;
        if(alerta.isNotificacion()){
            SharedPreferences pref_notificacion = context.getSharedPreferences("notificacion",   Context.MODE_PRIVATE);
            id_notificacion = pref_notificacion.getInt("id_notificacion", 2);
            if(id_notificacion<100){
                SharedPreferences.Editor editor = pref_notificacion.edit();
                editor.putInt("id_notificacion", id_notificacion+1);
                editor.apply();
            }else{
                pref_notificacion.edit().clear().apply();
            }
        }
        Intent mainActivity = new Intent(context, Principal.class);
        Intent broadcastIntentDelete = new Intent();
        broadcastIntentDelete.setAction("com.alertas.DETENER_ALERTA");
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntentDelete);

        PendingIntent pendingMainActivity = PendingIntent.getActivity(context, 0, mainActivity, 0);
        PendingIntent pendingIntent_delete =PendingIntent.getBroadcast(context, 0, broadcastIntentDelete, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder mBuilder;
        final NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        mBuilder =new NotificationCompat.Builder(context)
                .setContentIntent(pendingMainActivity)
                .setDeleteIntent(pendingIntent_delete)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setVisibility(VISIBILITY_PUBLIC)
                .setSmallIcon(R.mipmap.icono_app)
                .setContentTitle("Alerta")
                .setContentText(alerta.getTime()+" - "+alerta.getTitulo())
                .setAutoCancel(true);
        mNotifyMgr.notify(id_notificacion, mBuilder.build());
    }

}
