package com.alertas;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class establecer_alarmas extends BroadcastReceiver {

    private List<Long> list_date_millis = new ArrayList<>();
    private List<String> list_detalles = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            actualizar_alerta(context);
        }
        if (intent.getAction().equals("com.alertas.ESTABLECER_ALARMA")) {
            if(intent.hasExtra("repetir") && intent.hasExtra("id_alerta")){
                String repetir = intent.getStringExtra("repetir");
                String id_alerta = intent.getStringExtra("id_alerta");
                verificar_repetir(context, repetir, id_alerta);
            }
            actualizar_alerta(context);
        }
    }

    private void verificar_repetir(Context context, String repetir, String id_alerta){
        if(repetir.equals("true")) return;
        SharedPreferences alertas = context.getSharedPreferences("alertas",   Context.MODE_PRIVATE);
        String datos_alerta = alertas.getString(id_alerta, "");
        if(datos_alerta.isEmpty()) return;
        Map<String,String> map = get_map(datos_alerta);

        int hora = 0, minuto = 0;
        String dias = "0", titulo = " ", timbres = "1", notificacion = "false";


        if (map.containsKey("hora")) hora = Integer.parseInt(map.get("hora"));
        if (map.containsKey("minuto")) minuto = Integer.parseInt(map.get("minuto"));
        if (map.containsKey("dias")) dias = map.get("dias");
        if (map.containsKey("titulo")) titulo = map.get("titulo");
        if (map.containsKey("timbres")) timbres = map.get("timbres");
        if (map.containsKey("notificacion")) notificacion = map.get("notificacion");

        if (dias.contains("-")){
            String[] lista_dias = dias.split("-");
            long date_now = System.currentTimeMillis();

            for(String dia2 : lista_dias){
                int dia = Integer.parseInt(dia2);
                long date_millis = get_date_millis(dia,hora,minuto);
                if(date_millis - date_now > 0){
                    return;
                }
            }
        }

        datos_alerta = "hora=" + hora + ",minuto=" + minuto + ",dias=" + dias + ",activa=no" +
                ",titulo=" + titulo + ",timbres=" + timbres + ",notificacion=" + notificacion + ",repetir=false" +
                ",id_alerta=" + id_alerta;

        SharedPreferences.Editor editor = alertas.edit();
        editor.putString(id_alerta, datos_alerta);
        editor.commit();

        if(MainActivity.getInstance()!=null){
            MainActivity.getInstance().mostrar_alertas(id_alerta, datos_alerta);
        }
    }

    private void actualizar_alerta(Context context){
        list_date_millis.clear();
        list_detalles.clear();
        SharedPreferences alertas = context.getSharedPreferences("alertas",   Context.MODE_PRIVATE);
        Map<String, ?> allEntries = alertas.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            add_date_millis_to_list(entry.getValue().toString(), context);
        }

        int size = list_date_millis.size();
        if(size>0){
            long time_alarm = Collections.min(list_date_millis);
            int indice = list_date_millis.indexOf(time_alarm);
            String detalles = list_detalles.get(indice);
            establecer_alerta(context, time_alarm, detalles);
        }else {
            cancelar_alerta(context);
        }
    }

    private void establecer_alerta(Context context, long time_alarm, String detalles){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.alertas.DISPARAR_ALARMA");
        broadcastIntent.putExtra("detalles", detalles);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time_alarm, alarmIntent);
        } else if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.KITKAT) {
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, time_alarm, alarmIntent);
        } else {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, time_alarm, alarmIntent);
        }
    }

    private void cancelar_alerta(Context context){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.alertas.DISPARAR_ALARMA");
        PendingIntent alarmIntent =PendingIntent.getBroadcast(context, 0, broadcastIntent, 0);
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(alarmMgr!= null && alarmIntent!=null) {
            alarmMgr.cancel(alarmIntent);
        }
    }

    //----------------------------------------------------------------------------------------------

    private void add_date_millis_to_list(String datos_alerta, Context context){
        Map<String,String> map = get_map(datos_alerta);
        int hora2 = 0, minuto2 = 0;
        String dias = "0", titulo = " ", timbres = "1", notificacion = "false", repetir = "false", id_alerta = "", AmPm = "AM";

        if (map.containsKey("activa")) {
            if(!map.get("activa").equals("si")) return;
        }
        if (map.containsKey("hora")) hora2 = Integer.parseInt(map.get("hora"));
        if (map.containsKey("minuto")) minuto2 = Integer.parseInt(map.get("minuto"));
        if (map.containsKey("dias")) dias = map.get("dias");
        if (map.containsKey("titulo")) titulo = map.get("titulo");
        if (map.containsKey("timbres")) timbres = map.get("timbres");
        if (map.containsKey("notificacion")) notificacion = map.get("notificacion");
        if (map.containsKey("repetir")) repetir = map.get("repetir");
        if (map.containsKey("id_alerta")) id_alerta = map.get("id_alerta");
        //------------------------------------ detalles ----------------------------------
        int hora3 = hora2;
        int minuto3 = minuto2;
        if(hora3 >= 12) AmPm = "PM";
        if(hora3 > 12) hora3 = (hora3-12);
        if(hora3 == 0) hora3 = 12;

        String hora = "00", minuto = "00";
        if(hora3 < 10) hora = "0"+hora3;
        else hora = ""+hora3;
        if(minuto3 < 10) minuto = "0"+minuto3;
        else minuto = ""+minuto3;

        String detalles = hora+":"+minuto+" "+AmPm+","+titulo+","+timbres+","+notificacion+","+repetir+","+id_alerta;
        //--------------------------------------------------------------------------------

        if (dias.contains("-")){
            String[] lista_dias = dias.split("-");
            for(String dia2 : lista_dias){
                int dia = Integer.parseInt(dia2);
                long date_millis = get_date_millis(dia,hora2,minuto2);
                update_y_add_date_millis(date_millis, detalles);
            }
        }
    }

    private void update_y_add_date_millis(long date_millis, String detalles){
        long date_now = System.currentTimeMillis();

        if(date_millis - date_now > 0){
            list_date_millis.add(date_millis);
            list_detalles.add(detalles);
            return;
        }
        while(date_millis - date_now <= 0){
            date_millis = date_millis + 604800000;
        }
        list_date_millis.add(date_millis);
        list_detalles.add(detalles);
        return;
    }

    private long get_date_millis(int dia, int hora, int minuto){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if(dia != 0) calendar.set(Calendar.DAY_OF_WEEK, dia);
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minuto);
        calendar.set(Calendar.SECOND, 00);

        return calendar.getTimeInMillis();
    }

    private Map get_map(String cadena){
        String[] keyValuePairs = cadena.split(",");
        Map<String,String> map = new HashMap<>();

        for(String pair : keyValuePairs){
            String[] entry = pair.split("=");
            map.put(entry[0], entry[1]);
        }
        return map;
    }
}