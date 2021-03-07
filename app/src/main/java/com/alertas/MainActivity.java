package com.alertas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //--------------- lista alertas --------------
    ArrayList<String> Id_alertas = new ArrayList<>();
    ArrayList<String> Time = new ArrayList<>();
    ArrayList<String> Dias = new ArrayList<>();
    ArrayList<String> Activa = new ArrayList<>();
    ArrayList<String> Titulo = new ArrayList<>();
    ArrayList<String> Timbres = new ArrayList<>();
    ArrayList<String> Notificacion = new ArrayList<>();
    ArrayList<String> Repetir = new ArrayList<>();
    //--------------- lista tonos ----------------
    ArrayList<String> tono = new ArrayList<>();
    ArrayList<String> uri = new ArrayList<>();
    ArrayList<String> RingtonSeleccionado = new ArrayList<>();

    int indice_list_rington = 0;
    MediaPlayer mp;
    //-------------------------------------------

    private AlertDialog alertDetalles, alertRingtons;
    private String spinnerAmPm = "AM";
    private static MainActivity instance;
    private list_alerts_adapter whatever;
    private Ringtons_adapter whatever_tono;
    private ListView listView, listView_tono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stopService(new Intent(this, Ringtone_service.class));
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titulo_activity);
        setTitle(" ");

        SharedPreferences pref_rington = getSharedPreferences("rington", Context.MODE_PRIVATE);
        String rington_uri = pref_rington.getString("tono_uri", "");
        if (rington_uri.equals("")) {
            Uri tono_default = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);
            pref_rington.edit().putString("tono_uri", tono_default.toString()).apply();
        }
        instance = this;

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.alertas.ESTABLECER_ALARMA");
        sendBroadcast(broadcastIntent);

        listView = findViewById(R.id.list_alerts);
        SharedPreferences alertas = this.getSharedPreferences("alertas", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = alertas.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            mostrar_alertas(entry.getKey(), entry.getValue().toString());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        stopService(new Intent(this, Ringtone_service.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tono_alertas:
                dialogo_ringtons();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public void add_alert(View v1) {
        LayoutInflater inflater = this.getLayoutInflater();
        final View Vi = inflater.inflate(R.layout.dialog_add_alarm, null);
        dialogo_alertas(Vi, "", "AM", "si");
    }

    private void spinner(View Vi, final String AmPm) {
        Spinner Spinner = Vi.findViewById(R.id.spinner_am_pm);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.am_pm_Array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner.setAdapter(adapter);

        if (AmPm.equals("PM")) Spinner.setSelection(1);

        Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                spinnerAmPm = selectedItemText;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerAmPm = AmPm;
            }
        });
    }

    private void save_date(View Vi, String id_alerta, String activa) {
        EditText E_titulo = Vi.findViewById(R.id.dialogo_titulo);
        EditText E_timbres = Vi.findViewById(R.id.dialogo_num_timbres);
        Switch E_notificacion = Vi.findViewById(R.id.dialogo_switch_notificacion);
        Switch E_repetir = Vi.findViewById(R.id.dialogo_switch_repetir);
        EditText Hora = Vi.findViewById(R.id.dialogo_hora);
        EditText Minutos = Vi.findViewById(R.id.dialogo_minutos);
        CheckBox dom = Vi.findViewById(R.id.dialogo_check_do);
        CheckBox lun = Vi.findViewById(R.id.dialogo_check_lu);
        CheckBox mar = Vi.findViewById(R.id.dialogo_check_ma);
        CheckBox mie = Vi.findViewById(R.id.dialogo_check_mi);
        CheckBox jue = Vi.findViewById(R.id.dialogo_check_ju);
        CheckBox vie = Vi.findViewById(R.id.dialogo_check_vi);
        CheckBox sab = Vi.findViewById(R.id.dialogo_check_sa);

        String hora = Hora.getText().toString();
        String minutos = Minutos.getText().toString();
        String titulo = E_titulo.getText().toString();

        if (titulo.contains(",")) {
            titulo = titulo.replace(",", "¬");
        }

        if (titulo.isEmpty()) titulo = " ";
        String timbres = E_timbres.getText().toString();
        if (timbres.isEmpty()) timbres = "1";
        String notificacion = "false";
        if (E_notificacion.isChecked()) notificacion = "true";
        String repetir = "false";
        if (E_repetir.isChecked()) repetir = "true";
        String dias = "";
        if (dom.isChecked()) dias = dias + "1-";
        if (lun.isChecked()) dias = dias + "2-";
        if (mar.isChecked()) dias = dias + "3-";
        if (mie.isChecked()) dias = dias + "4-";
        if (jue.isChecked()) dias = dias + "5-";
        if (vie.isChecked()) dias = dias + "6-";
        if (sab.isChecked()) dias = dias + "7-";

        if (hora.isEmpty() || minutos.isEmpty()) {
            Toast.makeText(this, "Datos incompletos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dias.equals("")) {
            Toast.makeText(this, "Debes elegir uno o varios dias", Toast.LENGTH_SHORT).show();
            return;
        }
        int hora2 = Integer.parseInt(hora);
        int minutos2 = Integer.parseInt(minutos);
        if (hora2 > 12 || hora2 <= 0) {
            Toast.makeText(instance, "la hora está fuera de rango", Toast.LENGTH_LONG).show();
            return;
        }
        if (minutos2 > 59 || minutos2 < 0) {
            Toast.makeText(instance, "los minutos están fuera de rango", Toast.LENGTH_LONG).show();
            return;
        }
        minutos = "" + minutos2;
        if (spinnerAmPm.equals("PM") && !hora.equals("12")) hora = "" + (hora2 + 12);
        if (spinnerAmPm.equals("AM") && hora.equals("12")) hora = "00";
        if (id_alerta.isEmpty()) {
            id_alerta = "" + System.currentTimeMillis();
        }
        String datos_alerta = "hora=" + hora + ",minuto=" + minutos + ",dias=" + dias + ",activa=" + activa +
                ",titulo=" + titulo + ",timbres=" + timbres + ",notificacion=" + notificacion + ",repetir=" +
                repetir + ",id_alerta=" + id_alerta;

        SharedPreferences alertas = this.getSharedPreferences("alertas", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = alertas.edit();
        editor.putString(id_alerta, datos_alerta);
        editor.apply();

        if (alertDetalles != null) alertDetalles.cancel();
        mostrar_alertas(id_alerta, datos_alerta);
        //-------------------  establecer alerta -------------------
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.alertas.ESTABLECER_ALARMA");
        sendBroadcast(broadcastIntent);
        Toast.makeText(this, "alerta establecida", Toast.LENGTH_LONG).show();
    }

    public void mostrar_alertas(String id_alerta, String datos_alerta) {
        String hora = "1", minuto = "00", dias = "1-", activa = "no", AmPm = "AM";
        String titulo = " ", timbres = "1", notificacion = "false", repetir = "false";
        int indice = 0;

        Map<String, String> map = get_map(datos_alerta);
        if (map.containsKey("hora")) hora = map.get("hora");
        if (map.containsKey("minuto")) minuto = map.get("minuto");
        if (map.containsKey("dias")) dias = map.get("dias");
        if (map.containsKey("activa")) activa = map.get("activa");
        if (map.containsKey("titulo")) titulo = map.get("titulo");
        if (map.containsKey("timbres")) timbres = map.get("timbres");
        if (map.containsKey("notificacion")) notificacion = map.get("notificacion");
        if (map.containsKey("repetir")) repetir = map.get("repetir");

        int hora2 = Integer.parseInt(hora);
        if (hora2 > 12) hora = "" + (hora2 - 12);
        if (hora2 >= 12) AmPm = "PM";
        if (hora.equals("00")) hora = "12";

        if (hora.length() == 1) hora = "0" + hora;
        if (minuto.length() == 1) minuto = "0" + minuto;
        String time = hora + ":" + minuto + " " + AmPm;

        if (titulo.contains("¬")) {
            titulo = titulo.replace("¬", ",");
        }

        if (Id_alertas.contains(id_alerta)) {
            indice = Id_alertas.indexOf(id_alerta);

            Time.set(indice, time);
            Dias.set(indice, dias);
            Titulo.set(indice, titulo);
            Timbres.set(indice, timbres);
            Notificacion.set(indice, notificacion);
            Repetir.set(indice, repetir);
            Activa.set(indice, activa);
        } else {
            Id_alertas.add(0, id_alerta);
            Time.add(0, time);
            Dias.add(0, dias);
            Titulo.add(0, titulo);
            Timbres.add(0, timbres);
            Notificacion.add(0, notificacion);
            Repetir.add(0, repetir);
            Activa.add(0, activa);
        }

        if (whatever == null) {
            whatever = new list_alerts_adapter(MainActivity.this, Id_alertas, Time, Dias, Activa, Titulo, Timbres, Notificacion, Repetir);
            listView.setAdapter(whatever);
        } else {
            whatever.notifyDataSetChanged();
        }
        clickAlerta();
    }

    public void estado_alerta(int posicion) {
        String id_alerta = Id_alertas.get(posicion);
        SharedPreferences alertas = this.getSharedPreferences("alertas", Context.MODE_PRIVATE);
        String datos_alerta = alertas.getString(id_alerta, "");
        String hora = "1", minuto = "00", dias = "1-";
        String titulo = " ", timbres = "1", notificacion = "false", repetir = "false";

        Map<String, String> map = get_map(datos_alerta);
        if (map.containsKey("hora")) hora = map.get("hora");
        if (map.containsKey("minuto")) minuto = map.get("minuto");
        if (map.containsKey("dias")) dias = map.get("dias");
        if (map.containsKey("titulo")) titulo = map.get("titulo");
        if (map.containsKey("timbres")) timbres = map.get("timbres");
        if (map.containsKey("notificacion")) notificacion = map.get("notificacion");
        if (map.containsKey("repetir")) repetir = map.get("repetir");

        if (Activa.get(posicion).equals("si")) {
            Activa.set(posicion, "no");
            datos_alerta = "hora=" + hora + ",minuto=" + minuto + ",dias=" + dias + ",activa=no" +
                    ",titulo=" + titulo + ",timbres=" + timbres + ",notificacion=" + notificacion + ",repetir=" +
                    repetir + ",id_alerta=" + id_alerta;
            Toast.makeText(this, "Alerta desactivada", Toast.LENGTH_SHORT).show();
        } else {
            Activa.set(posicion, "si");
            datos_alerta = "hora=" + hora + ",minuto=" + minuto + ",dias=" + dias + ",activa=si" +
                    ",titulo=" + titulo + ",timbres=" + timbres + ",notificacion=" + notificacion + ",repetir=" +
                    repetir + ",id_alerta=" + id_alerta;
            Toast.makeText(this, "Alerta activada", Toast.LENGTH_SHORT).show();
        }

        SharedPreferences.Editor editor = alertas.edit();
        editor.putString(id_alerta, datos_alerta);
        editor.commit();

        if (whatever != null) whatever.notifyDataSetChanged();
        //-------------------  establecer alerta -------------------
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.alertas.ESTABLECER_ALARMA");
        sendBroadcast(broadcastIntent);
    }

    public void eliminar_alerta(int posicion) {
        String id_alerta = Id_alertas.get(posicion);

        SharedPreferences datos = this.getSharedPreferences("alertas", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = datos.edit();
        editor.remove(id_alerta);
        editor.commit();

        Id_alertas.remove(posicion);
        Time.remove(posicion);
        Dias.remove(posicion);
        Activa.remove(posicion);
        Titulo.remove(posicion);
        Timbres.remove(posicion);
        Notificacion.remove(posicion);
        Repetir.remove(posicion);

        if (whatever != null) whatever.notifyDataSetChanged();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.alertas.ESTABLECER_ALARMA");
        sendBroadcast(broadcastIntent);
    }

    private Map get_map(String cadena) {
        String[] keyValuePairs = cadena.split(",");
        Map<String, String> map = new HashMap<>();

        for (String pair : keyValuePairs) {
            String[] entry = pair.split("=");
            map.put(entry[0], entry[1]);
        }
        return map;
    }

    private void clickAlerta() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    modificar_alerta(position);
                } catch (Exception e) {}
            }
        });
    }

    private void modificar_alerta(int position) {
        final String id_alerta = Id_alertas.get(position);
        int hora = 0, minutos = 0;
        String AmPm = "AM";
        String time = Time.get(position);
        String dias = Dias.get(position);
        String activa = Activa.get(position);
        String titulo = Titulo.get(position);
        String timbres = Timbres.get(position);
        String notificacion = Notificacion.get(position);
        String repetir= Repetir.get(position);

        if (time.contains(" ")) {
            String[] time_espacio = time.split(" ");
            AmPm = time_espacio[1];
            if (time_espacio[0].contains(":")) {
                String[] time_puntos = time_espacio[0].split(":");
                hora = Integer.parseInt(time_puntos[0]);
                minutos = Integer.parseInt(time_puntos[1]);
            } else {
                Toast.makeText(instance, "Ocurrio un error", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(instance, "Ocurrio un error", Toast.LENGTH_SHORT).show();
            return;
        }

        LayoutInflater inflater = this.getLayoutInflater();
        final View Vi = inflater.inflate(R.layout.dialog_add_alarm, null);

        EditText Hora = Vi.findViewById(R.id.dialogo_hora);
        EditText Minutos = Vi.findViewById(R.id.dialogo_minutos);
        EditText E_titulo = Vi.findViewById(R.id.dialogo_titulo);
        EditText E_timbres = Vi.findViewById(R.id.dialogo_num_timbres);
        Switch E_notificacion = Vi.findViewById(R.id.dialogo_switch_notificacion);
        Switch E_repetir = Vi.findViewById(R.id.dialogo_switch_repetir);
        CheckBox dom = Vi.findViewById(R.id.dialogo_check_do);
        CheckBox lun = Vi.findViewById(R.id.dialogo_check_lu);
        CheckBox mar = Vi.findViewById(R.id.dialogo_check_ma);
        CheckBox mie = Vi.findViewById(R.id.dialogo_check_mi);
        CheckBox jue = Vi.findViewById(R.id.dialogo_check_ju);
        CheckBox vie = Vi.findViewById(R.id.dialogo_check_vi);
        CheckBox sab = Vi.findViewById(R.id.dialogo_check_sa);

        Hora.setText("" + hora);
        Minutos.setText("" + minutos);
        E_titulo.setText(titulo);
        E_timbres.setText(timbres);
        if (notificacion.equals("true")) E_notificacion.setChecked(true);
        else E_notificacion.setChecked(false);

        if (repetir.equals("true")) E_repetir.setChecked(true);
        else E_repetir.setChecked(false);

        if (Dias.get(position).contains("-")) {
            String[] lista_dias = dias.split("-");
            for (String dia2 : lista_dias) {
                int dia = Integer.parseInt(dia2);
                if (dia == 1) dom.setChecked(true);
                if (dia == 2) lun.setChecked(true);
                if (dia == 3) mar.setChecked(true);
                if (dia == 4) mie.setChecked(true);
                if (dia == 5) jue.setChecked(true);
                if (dia == 6) vie.setChecked(true);
                if (dia == 7) sab.setChecked(true);
            }
        }

        dialogo_alertas(Vi, id_alerta, AmPm, activa);
    }

    public void dialogo_alertas(final View view, final String id_alerta, String AmPm, final String activa) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        alertDetalles = builder.create();
        spinner(view, AmPm);

        final LinearLayout linear_opciones = view.findViewById(R.id.dialogo_linear_opciones);
        Button Definir = view.findViewById(R.id.dialogo_definir);
        Button Cancelar = view.findViewById(R.id.dialogo_cancelar);
        TextView Opciones = view.findViewById(R.id.dialogo_opciones);

        linear_opciones.setVisibility(View.GONE);
        Opciones.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View VV) {
                        if (linear_opciones.getVisibility() == View.VISIBLE) {
                            linear_opciones.setVisibility(View.GONE);
                        } else {
                            linear_opciones.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );
        Definir.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View VV) {
                        save_date(view, id_alerta, activa);
                    }
                }
        );
        Cancelar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View Vi) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(Vi.getWindowToken(), 0);
                        alertDetalles.cancel();
                    }
                }
        );
        alertDetalles.show();
        alertDetalles.setCancelable(true);
    }

    public void teclado_off(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //------------------------------  tonos de alerta --------------------------------

    private void dialogo_ringtons() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View Vi2 = inflater.inflate(R.layout.dialogo_ringtons, null);
        builder.setView(Vi2);
        alertRingtons = builder.create();
        listaRingtons(Vi2);
        Button Aceptar = Vi2.findViewById(R.id.dialogo_ringtons_aceptar);
        Button Cancelar = Vi2.findViewById(R.id.dialogo_ringtons_cancelar);

        clickRington();
        Aceptar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View VV) {
                        SharedPreferences pref_rington = getSharedPreferences("rington", Context.MODE_PRIVATE);
                        pref_rington.edit().putString("tono_uri", uri.get(indice_list_rington)).apply();
                        if (mp != null) mp.stop();
                        alertRingtons.cancel();
                    }
                }
        );
        Cancelar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View Vi) {
                        if (mp != null) mp.stop();
                        alertRingtons.cancel();
                    }
                }
        );
        alertRingtons.show();
        alertRingtons.setCancelable(true);
    }

    private void listaRingtons(View Vi2) {
        tono.clear();
        uri.clear();
        RingtonSeleccionado.clear();

        SharedPreferences pref_rington = getSharedPreferences("rington", Context.MODE_PRIVATE);
        String rington_uri = pref_rington.getString("tono_uri", "");

        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_ALL);
        Cursor cursor = manager.getCursor();

        int i = 0;
        while (cursor.moveToNext()) {
            String tonoTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String tonoUri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX);

            if (tonoUri.equals(rington_uri)) {
                RingtonSeleccionado.add(i, "si");
                indice_list_rington = i;
            } else RingtonSeleccionado.add(i, "no");

            tono.add(i, tonoTitle);
            uri.add(i, tonoUri);
            i++;
        }
        whatever_tono = new Ringtons_adapter(MainActivity.this, tono, uri, RingtonSeleccionado);
        listView_tono = Vi2.findViewById(R.id.lista_ringtons);
        listView_tono.setAdapter(whatever_tono);
    }

    private void clickRington() {
        listView_tono.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (mp != null) mp.stop();
                    RingtonSeleccionado.set(indice_list_rington, "no");
                    RingtonSeleccionado.set(position, "si");
                    Uri uri_tono = Uri.parse(uri.get(position));
                    mp = MediaPlayer.create(getApplicationContext(), uri_tono);
                    mp.setLooping(false);
                    mp.start();
                    mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                        public void onSeekComplete(MediaPlayer mp) {
                            mp.stop();
                        }
                    });
                } catch (Exception e) {
                }

                indice_list_rington = position;
                if (whatever_tono != null) whatever_tono.notifyDataSetChanged();
            }
        });
    }

}