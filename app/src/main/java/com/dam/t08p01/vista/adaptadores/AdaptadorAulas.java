package com.dam.t08p01.vista.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.t08p01.R;
import com.dam.t08p01.databinding.ContentRvAulasBinding;
import com.dam.t08p01.modelo.Aula;
import com.dam.t08p01.vista.fragmentos.BusAulasFragment;

import java.util.List;

public class AdaptadorAulas extends RecyclerView.Adapter<AdaptadorAulas.AulaVH> {

    private List<Aula> mDatos;
    private int mItemPos;
    private final AdaptadorAulasInterface mCallback;

    public interface AdaptadorAulasInterface {
        void onItemClickRVAulas(int pos);
    }

    public AdaptadorAulas(BusAulasFragment context) {
        mDatos = null;
        mItemPos = -1;
        mCallback = context;
    }

    public void setDatos(List<Aula> datos) {
        mDatos = datos;
    }

    public int getItemPos() {
        return mItemPos;
    }

    public void setItemPos(int itemPos) {
        mItemPos = itemPos;
    }

    public Aula getItem(int pos) {
        return mDatos.get(pos);
    }

    @NonNull
    @Override
    public AdaptadorAulas.AulaVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_rv_aulas, parent, false);
        return new AulaVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorAulas.AulaVH holder, int position) {
        if (mDatos != null) {
            holder.setItem(mDatos.get(position));
            holder.binding.llrvAulas.setActivated(mItemPos == position);
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

    public class AulaVH extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final ContentRvAulasBinding binding;

        public AulaVH(@NonNull View itemView) {
            super(itemView);
            binding = ContentRvAulasBinding.bind(itemView);
            binding.llrvAulas.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.rv_item_seleccionado));
            itemView.setOnClickListener(this);
        }

        private void setItem(@NonNull Aula aula) {
            binding.tvAulaRvIdDpto.setText(String.format(itemView.getContext().getResources().getString(R.string.tv_rv_Aula_IdDpto), aula.getIdDpto()));
            binding.tvAulaRvId.setText(String.format(itemView.getContext().getResources().getString(R.string.tv_rv_Aula_Id), aula.getId()));
            binding.tvAulaRvNombre.setText(String.format(itemView.getContext().getResources().getString(R.string.tv_rv_Aula_Nombre), aula.getNombre()));
        }

        @Override
        public void onClick(View v) {
            int pos = getLayoutPosition();
            notifyItemChanged(mItemPos);
            mItemPos = (mItemPos == pos) ? -1 : pos;
            notifyItemChanged(mItemPos);
            if (mCallback != null) {
                mCallback.onItemClickRVAulas(mItemPos);
            }
        }
    }

}
