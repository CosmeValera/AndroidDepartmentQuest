package com.dam.t08p01.vista.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.t08p01.R;
import com.dam.t08p01.databinding.ContentRvRegistroBinding;

import java.util.List;

public class AdaptadorRegistro extends RecyclerView.Adapter<AdaptadorRegistro.RegistroVH> {

    private List<String> mDatos;

    public AdaptadorRegistro() {
        mDatos = null;
    }

    public void setDatos(List<String> datos) {
        mDatos = datos;
    }

    @NonNull
    @Override
    public AdaptadorRegistro.RegistroVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_rv_registro, parent, false);
        return new RegistroVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorRegistro.RegistroVH holder, int position) {
        if (mDatos != null) {
            holder.setItem(mDatos.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (mDatos != null) {
            return mDatos.size();
        } else {
            return 0;
        }
    }

    public static class RegistroVH extends RecyclerView.ViewHolder {

        private final ContentRvRegistroBinding binding;

        public RegistroVH(@NonNull View itemView) {
            super(itemView);
            binding = ContentRvRegistroBinding.bind(itemView);
        }

        private void setItem(String item) {
            binding.tvRegistroRvItem.setText(item);
        }
    }

}
