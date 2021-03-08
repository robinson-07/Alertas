package com.alertas.modelo;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alertas.Principal;
import com.alertas.R;

import java.util.ArrayList;

public class AlertAdapter extends BaseAdapter{
    Activity context;
    ArrayList<Alerta> alertas;

    public AlertAdapter(Activity context, ArrayList<Alerta> alertas) {
        this.context = context;
        this.alertas = alertas;
    }

    @Override
    public int getCount(){
        return alertas.size(); // images array length
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.plantilla_lista_alertas, null);

        TextView TimeTextField = rowView.findViewById(R.id.plantilla_lista_time);
        TextView TituloTextField = rowView.findViewById(R.id.plantilla_lista_titulo);
        TextView TimbresTextField = rowView.findViewById(R.id.plantilla_lista_timbres);
        TextView doTextField = rowView.findViewById(R.id.plantilla_lista_do);
        TextView luTextField = rowView.findViewById(R.id.plantilla_lista_lu);
        TextView maTextField = rowView.findViewById(R.id.plantilla_lista_ma);
        TextView miTextField = rowView.findViewById(R.id.plantilla_lista_mi);
        TextView juTextField = rowView.findViewById(R.id.plantilla_lista_ju);
        TextView viTextField = rowView.findViewById(R.id.plantilla_lista_vi);
        TextView saTextField = rowView.findViewById(R.id.plantilla_lista_sa);

        final ImageButton Estado = rowView.findViewById(R.id.plantilla_list_estado);
        ImageButton Eliminar = rowView.findViewById(R.id.plantilla_list_eliminar);
        ImageView NotificacionImage = rowView.findViewById(R.id.plantilla_lista_notificacion);
        ImageView RepetirImage = rowView.findViewById(R.id.plantilla_lista_repetir);

        Alerta alerta = alertas.get(position);

        TimeTextField.setText(alerta.getTime());
        TituloTextField.setText(alerta.getTitulo());
        TimbresTextField.setText("" + alerta.getTimbres());

        if(alerta.isDomingo()) doTextField.setTextColor(Color.parseColor("#0BBE10"));
        if(alerta.isLunes()) luTextField.setTextColor(Color.parseColor("#0BBE10"));
        if(alerta.isMartes()) maTextField.setTextColor(Color.parseColor("#0BBE10"));
        if(alerta.isMiercoles()) miTextField.setTextColor(Color.parseColor("#0BBE10"));
        if(alerta.isJueves()) juTextField.setTextColor(Color.parseColor("#0BBE10"));
        if(alerta.isViernes()) viTextField.setTextColor(Color.parseColor("#0BBE10"));
        if(alerta.isSabado()) saTextField.setTextColor(Color.parseColor("#0BBE10"));

        if(alerta.isNotificacion()) NotificacionImage.setImageResource(R.drawable.notificacion_on);
        else NotificacionImage.setImageResource(R.drawable.notificacion_off);

        if(alerta.isRepetir()) RepetirImage.setImageResource(R.drawable.repetir_on);
        else RepetirImage.setImageResource(R.drawable.repetir_off);

        if(alerta.isActiva()) {
            Estado.setImageResource(R.drawable.alerta_activa);
        }
        else {
            Estado.setImageResource(R.drawable.alerta_inactiva);
        }

        Estado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Principal.getInstance()!=null){
                    Principal.getInstance().estadoAlerta(position);
                }
            }
        });

        Eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Principal.getInstance()!=null){
                    Principal.getInstance().eliminarAlerta(position);
                }
            }
        });

        return rowView;
    }
}
