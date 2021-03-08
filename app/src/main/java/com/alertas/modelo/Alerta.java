package com.alertas.modelo;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Alerta implements Serializable {

    private static final long serialVersionUID = 4116381101167277453L;  // se usa en la serializacion

    private String id_alerta;
    private int hora;
    private int minuto;
    private String amPm;
    private String time;
    private boolean domingo;
    private boolean lunes;
    private boolean martes;
    private boolean miercoles;
    private boolean jueves;
    private boolean viernes;
    private boolean sabado;
    private String titulo;
    private int timbres;
    private long nextDateMillis;
    private long limitDateMillis;
    private boolean activa;
    private boolean notificacion;
    private boolean repetir;

    public Alerta(){

    }

    //------------------------------------  getters and setters-----------------
    public String getId_alerta() {
        return id_alerta;
    }

    public void setId_alerta(String id_alerta) {
        this.id_alerta = id_alerta;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }

    public String getAmPm() {
        return amPm;
    }

    public void setAmPm(String amPm) {
        this.amPm = amPm;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isDomingo() {
        return domingo;
    }

    public void setDomingo(boolean domingo) {
        this.domingo = domingo;
    }

    public boolean isLunes() {
        return lunes;
    }

    public void setLunes(boolean lunes) {
        this.lunes = lunes;
    }

    public boolean isMartes() {
        return martes;
    }

    public void setMartes(boolean martes) {
        this.martes = martes;
    }

    public boolean isMiercoles() {
        return miercoles;
    }

    public void setMiercoles(boolean miercoles) {
        this.miercoles = miercoles;
    }

    public boolean isJueves() {
        return jueves;
    }

    public void setJueves(boolean jueves) {
        this.jueves = jueves;
    }

    public boolean isViernes() {
        return viernes;
    }

    public void setViernes(boolean viernes) {
        this.viernes = viernes;
    }

    public boolean isSabado() {
        return sabado;
    }

    public void setSabado(boolean sabado) {
        this.sabado = sabado;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getTimbres() {
        return timbres;
    }

    public void setTimbres(int timbres) {
        this.timbres = timbres;
    }

    public long getNextDateMillis() {
        return nextDateMillis;
    }

    public void setNextDateMillis(long nextDateMillis) {
        this.nextDateMillis = nextDateMillis;
    }

    public long getLimitDateMillis() {
        return limitDateMillis;
    }

    public void setLimitDateMillis(long limitDateMillis) {
        this.limitDateMillis = limitDateMillis;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public boolean isNotificacion() {
        return notificacion;
    }

    public void setNotificacion(boolean notificacion) {
        this.notificacion = notificacion;
    }

    public boolean isRepetir() {
        return repetir;
    }

    public void setRepetir(boolean repetir) {
        this.repetir = repetir;
    }

    //---------------------------------------------------------------------
    private long get_nextDateMillis(){
        int hora2 = hora;
        if (amPm.equals("PM") && hora2 != 12) hora2 = hora2 + 12;
        if (amPm.equals("AM") && hora2 == 12) hora2 = 0;

        long date_now = System.currentTimeMillis();
        List<Long> list_date_millis = new ArrayList<>();
        List<Integer> list_dias = new ArrayList<>();

        if (domingo) list_dias.add(1);
        if (lunes) list_dias.add(2);
        if (martes) list_dias.add(3);
        if (miercoles) list_dias.add(4);
        if (jueves) list_dias.add(5);
        if (viernes) list_dias.add(6);
        if (sabado) list_dias.add(7);

        for(int dia : list_dias){
            //----------------  get Calendar Time -----
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            if(dia != 0) calendar.set(Calendar.DAY_OF_WEEK, dia);
            calendar.set(Calendar.HOUR_OF_DAY, hora2);
            calendar.set(Calendar.MINUTE, minuto);
            calendar.set(Calendar.SECOND, 00);

            long dateMillis = calendar.getTimeInMillis();
            //----------------------------------------------
            while(dateMillis - date_now <= 0){
                dateMillis = dateMillis + 604800000;
            }
            list_date_millis.add(dateMillis);
        }

        if(list_date_millis.size()>0){
            long time_alarm = Collections.min(list_date_millis);
            return time_alarm;
        }else {
            return 0;
        }
    }

    private String get_time(){
        String hora1 = "00", minuto1 = "00";
        if(hora < 10) hora1 = "0"+hora;
        else hora1 = ""+hora;
        if(minuto < 10) minuto1 = "0"+minuto;
        else minuto1 = ""+minuto;

        return hora1+":"+minuto1+" "+amPm;
    }

    //---------------------------------  trabajo con archivos en la memoria ----------------

    public void saveAlerta(Context context) {
        setLimitDateMillis(System.currentTimeMillis()+604800000);
        setNextDateMillis(get_nextDateMillis());
        setTime(get_time());

        try {
            setNextDateMillis(get_nextDateMillis());
            File dir = new File(context.getFilesDir(), "alertas");
            if(!dir.exists()) dir.mkdirs();
            File file = new File(dir, getId_alerta());

            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch (Exception e) {
            Toast.makeText(context, ""+e, Toast.LENGTH_LONG).show();
        }
    }

    public Alerta getAlerta(Context context, String id_alerta) {
        try {
            File dir = new File(context.getFilesDir(), "alertas");
            if(!dir.exists()) dir.mkdirs();
            File file = new File(dir, id_alerta);
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream is = new ObjectInputStream(fis);
            Alerta alerta = (Alerta)is.readObject();
            alerta.setNextDateMillis(alerta.get_nextDateMillis());
            fis.close();
            is.close();

            return alerta;
        } catch(Exception e) {
            Toast.makeText(context, ""+e, Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public void deleteAlerta(Context context) {
        try {
            File dir = new File(context.getFilesDir(), "alertas");
            if(!dir.exists()) dir.mkdirs();
            File file = new File(dir, id_alerta);
            file.delete();
        } catch(Exception e) {
            Toast.makeText(context, ""+e, Toast.LENGTH_LONG).show();
        }
    }

}
