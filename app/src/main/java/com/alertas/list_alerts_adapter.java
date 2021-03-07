package com.alertas;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class list_alerts_adapter extends BaseAdapter {
    Activity context;
    ArrayList<String> Id_alertas;
    ArrayList<String> Time;
    ArrayList<String> Dias;
    ArrayList<String> Activa;
    ArrayList<String> Titulo;
    ArrayList<String> Timbres;
    ArrayList<String> Notificacion;
    ArrayList<String> Repetir;

    public list_alerts_adapter(Activity context, ArrayList<String> Id_alertasParam, ArrayList<String> TimeParam,
                             ArrayList<String> DiasParam, ArrayList<String> ActivaParam, ArrayList<String> TituloParam,
                               ArrayList<String> TimbresParam, ArrayList<String> NotificacionParam, ArrayList<String> RepetirParam){
        this.context=context;
        this.Id_alertas = Id_alertasParam;
        this.Time = TimeParam;
        this.Dias = DiasParam;
        this.Activa = ActivaParam;
        this.Titulo = TituloParam;
        this.Timbres = TimbresParam;
        this.Notificacion = NotificacionParam;
        this.Repetir = RepetirParam;
    }

    @Override
    public int getCount(){
        return Id_alertas.size(); // images array length
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


        TimeTextField.setText(Time.get(position));
        TituloTextField.setText(Titulo.get(position));
        TimbresTextField.setText(Timbres.get(position));

        if (Dias.get(position).contains("-")){
            String[] lista_dias = Dias.get(position).split("-");
            for(String dia2 : lista_dias){
                int dia = Integer.parseInt(dia2);
                if(dia==1) doTextField.setTextColor(Color.parseColor("#0BBE10"));
                else if(dia==2) luTextField.setTextColor(Color.parseColor("#0BBE10"));
                else if(dia==3) maTextField.setTextColor(Color.parseColor("#0BBE10"));
                else if(dia==4) miTextField.setTextColor(Color.parseColor("#0BBE10"));
                else if(dia==5) juTextField.setTextColor(Color.parseColor("#0BBE10"));
                else if(dia==6) viTextField.setTextColor(Color.parseColor("#0BBE10"));
                else if(dia==7) saTextField.setTextColor(Color.parseColor("#0BBE10"));
            }
        }

        if(Notificacion.get(position).equals("true")) NotificacionImage.setImageResource(R.drawable.notificacion_on);
        else NotificacionImage.setImageResource(R.drawable.notificacion_off);

        if(Repetir.get(position).equals("true")) RepetirImage.setImageResource(R.drawable.repetir_on);
        else RepetirImage.setImageResource(R.drawable.repetir_off);

        if(Activa.get(position).equals("si")) Estado.setImageResource(R.drawable.alerta_activa);
        else Estado.setImageResource(R.drawable.alerta_inactiva);

        Estado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.getInstance()!=null){
                    MainActivity.getInstance().estado_alerta(position);
                }
            }
        });

        Eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.getInstance()!=null){
                    MainActivity.getInstance().eliminar_alerta(position);
                }
            }
        });
        return rowView;
    }
}
