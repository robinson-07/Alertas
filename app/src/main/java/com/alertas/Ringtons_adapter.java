package com.alertas;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class Ringtons_adapter extends BaseAdapter {
    Activity context;
    ArrayList<String> tono;
    ArrayList<String> uri;
    ArrayList<String> RingtonSeleccionado;

    public Ringtons_adapter(Activity context, ArrayList<String> tonoParam, ArrayList<String> uriParam,
                            ArrayList<String> RingtonSeleccionadoParam){
        this.context=context;
        this.tono = tonoParam;
        this.uri = uriParam;
        this.RingtonSeleccionado = RingtonSeleccionadoParam;
    }

    @Override
    public int getCount(){
        return tono.size(); // images array length
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
        View rowView=inflater.inflate(R.layout.plantilla_lista_ringtons, null);

        TextView tonoTextField = rowView.findViewById(R.id.plantilla_lista_ringtons_nombre);
        ImageButton circulo = rowView.findViewById(R.id.plantilla_lista_ringtons_circulo);

        tonoTextField.setText(tono.get(position));

        if(RingtonSeleccionado.get(position).equals("si")) {
            circulo.setImageResource(R.drawable.circulo_verde);
        }

        return rowView;
    }
}
