package com.alertas;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import static android.app.Notification.VISIBILITY_PUBLIC;
import static android.content.Context.NOTIFICATION_SERVICE;

public class disparar_alarma extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.alertas.DISPARAR_ALARMA")) {
            String detalles = intent.getStringExtra("detalles");
            obtener_detalles(context, detalles);
        }
        else if (intent.getAction().equals("com.alertas.DETENER_ALARMA")) {
            context.stopService(new Intent(context, Ringtone_service.class));
        }
    }

    private void obtener_detalles(Context context, String detalles){
        String[] partes = detalles.split(",");
        String time = partes[0];
        String titulo = partes[1];
        int timbres = Integer.parseInt(partes[2]);
        String notificacion = partes[3];
        String repetir = partes[4];
        String id_alerta = partes[5];

        notificacion(context, time, titulo, notificacion);
        reproducir_tono(context, timbres, notificacion);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.alertas.ESTABLECER_ALARMA");
        broadcastIntent.putExtra("repetir", repetir);
        broadcastIntent.putExtra("id_alerta", id_alerta);
        context.sendBroadcast(broadcastIntent);
    }

    private void reproducir_tono(Context context, int numero_timbres, String notificacion){
        Intent intent = new Intent(context, Ringtone_service.class);
        intent.putExtra("numero_timbres", numero_timbres);
        intent.putExtra("notificacion", notificacion);
        context.startService(intent);
    }

    public void notificacion(final Context context, String time, String titulo, String notificacion){
        int id_notificacion = 1;
        if(notificacion.equals("true")){
            SharedPreferences pref_notificacion = context.getSharedPreferences("notificacion",   Context.MODE_PRIVATE);
            id_notificacion = pref_notificacion.getInt("id_notificacion", 2);
            if(id_notificacion<100){
                SharedPreferences.Editor editor = pref_notificacion.edit();
                editor.putInt("id_notificacion", id_notificacion+1);
                editor.commit();
            }else{
                pref_notificacion.edit().clear().apply();
            }
            if(titulo.contains("¬")) {
                titulo = titulo.replace("¬", ",");
            }
        }

        Intent mainActivity = new Intent(context, MainActivity.class);

        Intent broadcastIntentDelete = new Intent();
        broadcastIntentDelete.setAction("com.alertas.DETENER_ALARMA");
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
                .setContentText(time+" - "+titulo)
                .setAutoCancel(true);
        mNotifyMgr.notify(id_notificacion, mBuilder.build());
    }
}