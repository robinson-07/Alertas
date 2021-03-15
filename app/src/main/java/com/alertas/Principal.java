package com.alertas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.alertas.modelo.AlertAdapter;
import com.alertas.modelo.Alerta;
import com.alertas.modelo.RingtoneService;

import java.io.File;
import java.util.ArrayList;

public class Principal extends AppCompatActivity {

    private static Principal instance;
    private AlertAdapter whatever;
    private ListView listViewAlerts;
    private ArrayList<Alerta> listAlerts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titulo_activity);
        setTitle(" ");

        stopService(new Intent(this, RingtoneService.class));

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.alertas.ESTABLECER_ALERTA");
        sendBroadcast(broadcastIntent);

        instance = this;
        whatever = new AlertAdapter(Principal.this, listAlerts);
        listViewAlerts = findViewById(R.id.list_alertsP);
        listViewAlerts.setAdapter(whatever);

        listViewAlerts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    modificarAlerta(listAlerts.get(position));
                } catch (Exception e) {}
            }
        });

        File dir = new File(this.getFilesDir(), "alertas");
        if(!dir.exists()) dir.mkdirs();
        File[] files = dir.listFiles();
        for(File file : files){
            Alerta alerta = new Alerta();
            alerta = alerta.getAlerta(this, file.getName());
            mostrarAlertas(alerta);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        stopService(new Intent(this, RingtoneService.class));
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
                configurarTono();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static Principal getInstance() {
        return instance;
    }

    //--------------------------------
    public void agregarAlerta(View v1) {
        LayoutInflater inflater = this.getLayoutInflater();
        final View Vi = inflater.inflate(R.layout.dialog_add_alarm, null);
        Alerta alerta = new Alerta();
        dialogoAlertas(Vi, alerta);
    }

    private void modificarAlerta(Alerta alerta) {
        LayoutInflater inflater = this.getLayoutInflater();
        final View Vi = inflater.inflate(R.layout.dialog_add_alarm, null);

        EditText hora = Vi.findViewById(R.id.dialogo_hora);
        EditText minutos = Vi.findViewById(R.id.dialogo_minutos);
        EditText titulo = Vi.findViewById(R.id.dialogo_titulo);
        EditText timbres = Vi.findViewById(R.id.dialogo_num_timbres);
        Switch notificacion = Vi.findViewById(R.id.dialogo_switch_notificacion);
        Switch repetir = Vi.findViewById(R.id.dialogo_switch_repetir);
        CheckBox dom = Vi.findViewById(R.id.dialogo_check_do);
        CheckBox lun = Vi.findViewById(R.id.dialogo_check_lu);
        CheckBox mar = Vi.findViewById(R.id.dialogo_check_ma);
        CheckBox mie = Vi.findViewById(R.id.dialogo_check_mi);
        CheckBox jue = Vi.findViewById(R.id.dialogo_check_ju);
        CheckBox vie = Vi.findViewById(R.id.dialogo_check_vi);
        CheckBox sab = Vi.findViewById(R.id.dialogo_check_sa);

        hora.setText(""+alerta.getHora());
        minutos.setText("" + alerta.getMinuto());
        titulo.setText(alerta.getTitulo());
        timbres.setText(""+alerta.getTimbres());
        notificacion.setChecked(alerta.isNotificacion());
        repetir.setChecked(alerta.isRepetir());
        dom.setChecked(alerta.isDomingo());
        lun.setChecked(alerta.isLunes());
        mar.setChecked(alerta.isMartes());
        mie.setChecked(alerta.isMiercoles());
        jue.setChecked(alerta.isJueves());
        vie.setChecked(alerta.isViernes());
        sab.setChecked(alerta.isSabado());

        dialogoAlertas(Vi, alerta);
    }

    public void dialogoAlertas(final View view, final Alerta alerta) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog alertDetalles = builder.create();

        Spinner spinner = view.findViewById(R.id.spinner_am_pm);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.am_pm_Array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (alerta.getAmPm() != null && alerta.getAmPm().equals("PM")) spinner.setSelection(1);
        else  spinner.setSelection(0);

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
                        guardarAlerta(view, alerta);
                        alertDetalles.cancel();
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

    private void guardarAlerta(View Vi, Alerta alerta) {
        EditText titulo = Vi.findViewById(R.id.dialogo_titulo);
        EditText timbres = Vi.findViewById(R.id.dialogo_num_timbres);
        Switch notificacion = Vi.findViewById(R.id.dialogo_switch_notificacion);
        Switch repetir = Vi.findViewById(R.id.dialogo_switch_repetir);
        EditText hora = Vi.findViewById(R.id.dialogo_hora);
        EditText minutos = Vi.findViewById(R.id.dialogo_minutos);
        CheckBox dom = Vi.findViewById(R.id.dialogo_check_do);
        CheckBox lun = Vi.findViewById(R.id.dialogo_check_lu);
        CheckBox mar = Vi.findViewById(R.id.dialogo_check_ma);
        CheckBox mie = Vi.findViewById(R.id.dialogo_check_mi);
        CheckBox jue = Vi.findViewById(R.id.dialogo_check_ju);
        CheckBox vie = Vi.findViewById(R.id.dialogo_check_vi);
        CheckBox sab = Vi.findViewById(R.id.dialogo_check_sa);
        Spinner spinner = Vi.findViewById(R.id.spinner_am_pm);

        String Hora = hora.getText().toString();
        String Minutos = minutos.getText().toString();
        if (Hora.isEmpty() || Minutos.isEmpty()) {
            Toast.makeText(this, "Datos incompletos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!dom.isChecked() && !lun.isChecked() && !mar.isChecked() && !mie.isChecked() &&
                !jue.isChecked() && !vie.isChecked() && !sab.isChecked()) {
            Toast.makeText(this, "Debes elegir uno o varios dias", Toast.LENGTH_SHORT).show();
            return;
        }
        int hora2 = Integer.parseInt(Hora);
        int minutos2 = Integer.parseInt(Minutos);
        if (hora2 > 12 || hora2 <= 0) {
            Toast.makeText(instance, "la hora está fuera de rango", Toast.LENGTH_LONG).show();
            return;
        }
        if (minutos2 > 59 || minutos2 < 0) {
            Toast.makeText(instance, "los minutos están fuera de rango", Toast.LENGTH_LONG).show();
            return;
        }

        if(alerta.getId_alerta()==null){
            alerta.setId_alerta("" + System.currentTimeMillis());
            alerta.setActiva(true);
        }
        alerta.setTitulo(titulo.getText().toString());
        alerta.setHora(hora2);
        alerta.setMinuto(minutos2);
        if(timbres.getText().toString().isEmpty()) alerta.setTimbres(1);
        else alerta.setTimbres(Integer.parseInt(timbres.getText().toString()));
        alerta.setNotificacion(notificacion.isChecked());
        alerta.setRepetir(repetir.isChecked());
        alerta.setDomingo(dom.isChecked());
        alerta.setLunes(lun.isChecked());
        alerta.setMartes(mar.isChecked());
        alerta.setMiercoles(mie.isChecked());
        alerta.setJueves(jue.isChecked());
        alerta.setViernes(vie.isChecked());
        alerta.setSabado(sab.isChecked());
        alerta.setAmPm(spinner.getSelectedItem().toString());

        alerta.saveAlerta(this);
        mostrarAlertas(alerta);
        //-------------------  establecer Alerta -------------------
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.alertas.ESTABLECER_ALERTA");
        sendBroadcast(broadcastIntent);
        Toast.makeText(this, "Alerta establecida", Toast.LENGTH_LONG).show();
    }

    public void mostrarAlertas(Alerta alerta) {
        for(Alerta alerta1 : listAlerts){
            if(alerta1.getId_alerta().equals(alerta.getId_alerta())){
                int index = listAlerts.indexOf(alerta1);
                listAlerts.set(index, alerta);
                whatever.notifyDataSetChanged();
                return;
            }
        }
        listAlerts.add(alerta);
        whatever.notifyDataSetChanged();
    }

    public void estadoAlerta(int posicion) {
        Alerta alerta = listAlerts.get(posicion);
        if(alerta.isActiva()) {
            alerta.setActiva(false);
            Toast.makeText(this, "Alerta desactivada", Toast.LENGTH_SHORT).show();
        }else  {
            alerta.setActiva(true);
            Toast.makeText(this, "Alerta Activada", Toast.LENGTH_SHORT).show();
        }
        alerta.saveAlerta(this);
        listAlerts.set(posicion, alerta);
        if (whatever != null) whatever.notifyDataSetChanged();
        //-------------------  establecer Alerta -------------------
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.alertas.ESTABLECER_ALERTA");
        sendBroadcast(broadcastIntent);
    }

    public void eliminarAlerta(int posicion) {
        Alerta alerta = listAlerts.get(posicion);
        alerta.deleteAlerta(this);
        listAlerts.remove(posicion);

        if (whatever != null) whatever.notifyDataSetChanged();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.alertas.ESTABLECER_ALERTA");
        sendBroadcast(broadcastIntent);
    }

    public void teclado_off(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //------------------------------  tonos de Alerta --------------------------------
    public void configurarTono(){
        SharedPreferences pref_rington = getSharedPreferences("rington", Context.MODE_PRIVATE);
        String ringtonUri = pref_rington.getString("tono_uri", "");

        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Tono de alertas");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        if (ringtonUri.equals("")) {
            ringtonUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION).toString();
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(ringtonUri));
        }else if(ringtonUri.equals("Ninguno")){
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
        }else{
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(ringtonUri));
        }
        startActivityForResult(intent, 5);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent){
        if (resultCode == Activity.RESULT_OK && requestCode == 5){
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null ){
                SharedPreferences pref_rington = getSharedPreferences("rington", Context.MODE_PRIVATE);
                pref_rington.edit().putString("tono_uri", uri.toString()).apply();
            }else{
                SharedPreferences pref_rington = getSharedPreferences("rington", Context.MODE_PRIVATE);
                pref_rington.edit().putString("tono_uri", "Ninguno").apply();
            }
        }
    }

}