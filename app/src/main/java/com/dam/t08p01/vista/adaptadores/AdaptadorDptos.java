package com.dam.t08p01.vista.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.t08p01.R;
import com.dam.t08p01.databinding.ContentRvDptosBinding;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.vista.fragmentos.BusDptosFragment;

import java.util.List;

public class AdaptadorDptos extends RecyclerView.Adapter<AdaptadorDptos.DptoVH> {

    private List<Departamento> mDatos;
    private int mItemPos;
    private final AdaptadorDptosInterface mCallback;

    public interface AdaptadorDptosInterface {
        void onItemClickRVDptos(int pos);
    }

    public AdaptadorDptos(BusDptosFragment context) {
        mDatos = null;
        mItemPos = -1;
        mCallback = context;
    }

    public void setDatos(List<Departamento> datos) {
        mDatos = datos;
    }

    public int getItemPos() {
        return mItemPos;
    }

    public void setItemPos(int itemPos) {
        mItemPos = itemPos;
    }

    public Departamento getItem(int pos) {
        return mDatos.get(pos);
    }

    @NonNull
    @Override
    public AdaptadorDptos.DptoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_rv_dptos, parent, false);
        return new DptoVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorDptos.DptoVH holder, int position) {
        if (mDatos != null) {
            holder.setItem(mDatos.get(position));
            holder.binding.llrvDptos.setActivated(mItemPos == position);
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

    public class DptoVH extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final ContentRvDptosBinding binding;

        public DptoVH(@NonNull View itemView) {
            super(itemView);
            binding = ContentRvDptosBinding.bind(itemView);
            binding.llrvDptos.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.rv_item_seleccionado));
            itemView.setOnClickListener(this);
        }

        private void setItem(@NonNull Departamento dpto) {
            binding.tvDptoRvId.setText(String.format(itemView.getContext().getResources().getString(R.string.tv_rv_Dpto_Id), dpto.getId()));
            binding.tvDptoRvNombre.setText(String.format(itemView.getContext().getResources().getString(R.string.tv_rv_Dpto_Nombre), dpto.getNombre()));
        }

        @Override
        public void onClick(View v) {
            int pos = getLayoutPosition();
            notifyItemChanged(mItemPos);
            mItemPos = (mItemPos == pos) ? -1 : pos;
            notifyItemChanged(mItemPos);
            if (mCallback != null) {
                mCallback.onItemClickRVDptos(mItemPos);
            }
        }
    }

}
