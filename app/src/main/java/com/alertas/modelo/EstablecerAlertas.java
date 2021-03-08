package com.alertas.modelo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.alertas.Principal;

import java.io.File;

public class EstablecerAlertas extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            actualizarAlerta(context);
        }else if (intent.getAction().equals("com.alertas.ESTABLECER_ALERTA")) {
            actualizarAlerta(context);
        }
    }

    private void actualizarAlerta(Context context){
        long dateNow = System.currentTimeMillis();
        Alerta nextAlert = new Alerta();
        nextAlert.setNextDateMillis(dateNow*100);
        File dir = new File(context.getFilesDir(), "alertas");
        if(!dir.exists()) dir.mkdirs();
        File[] files = dir.listFiles();
        for(File file : files){
            Alerta alerta = new Alerta();
            alerta = alerta.getAlerta(context, file.getName());
            if(!alerta.isRepetir() && (alerta.getNextDateMillis() >= alerta.getLimitDateMillis())) {
                alerta.setActiva(false);
                alerta.saveAlerta(context);
                if(Principal.getInstance()!=null){
                    Principal.getInstance().mostrarAlertas(alerta);
                }
            }else if(alerta.isActiva() && (alerta.getNextDateMillis() < nextAlert.getNextDateMillis())){
                nextAlert = alerta;
            }
        }

        if(nextAlert.getId_alerta() != null){
            long timeAlert = nextAlert.getNextDateMillis();
            Intent broadcastIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("alerta", nextAlert);
            broadcastIntent.putExtra("bundle", bundle);
            broadcastIntent.setAction("com.alertas.DISPARAR_ALERTA");
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeAlert, alarmIntent);
            } else if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.KITKAT) {
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, timeAlert, alarmIntent);
            } else {
                alarmMgr.set(AlarmManager.RTC_WAKEUP, timeAlert, alarmIntent);
            }
        }else{  //---------------------  eliminar cualquier alerta establecida -------
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("com.alertas.DISPARAR_ALERTA");
            PendingIntent alarmIntent =PendingIntent.getBroadcast(context, 0, broadcastIntent, 0);
            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            if(alarmMgr!= null && alarmIntent!=null) {
                alarmMgr.cancel(alarmIntent);
            }
        }
    }
}
