package com.dam.t08p01.vista.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.t08p01.R;
import com.dam.t08p01.databinding.ContentRvProductosBinding;
import com.dam.t08p01.modelo.Producto;
import com.dam.t08p01.vista.fragmentos.BusProductosFragment;

import java.util.List;

public class AdaptadorProductos extends RecyclerView.Adapter<AdaptadorProductos.ProductoVH> {

    private List<Producto> mDatos;
    private int mItemPos;
    private final AdaptadorProductosInterface mCallback;

    public interface AdaptadorProductosInterface {
        void onItemClickRVProductos(int pos);
    }

    public AdaptadorProductos(BusProductosFragment context) {
        mDatos = null;
        mItemPos = -1;
        mCallback = context;
    }

    public void setDatos(List<Producto> datos) {
        mDatos = datos;
    }

    public int getItemPos() {
        return mItemPos;
    }

    public void setItemPos(int itemPos) {
        mItemPos = itemPos;
    }

    public Producto getItem(int pos) {
        return mDatos.get(pos);
    }

    @NonNull
    @Override
    public ProductoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_rv_productos, parent, false);
        return new ProductoVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoVH holder, int position) {
        if (mDatos != null) {
            holder.setItem(mDatos.get(position));
            holder.binding.llrvProductos.setActivated(mItemPos == position);
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

    public class ProductoVH extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final ContentRvProductosBinding binding;

        public ProductoVH(@NonNull View itemView) {
            super(itemView);
            binding = ContentRvProductosBinding.bind(itemView);
            binding.llrvProductos.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.rv_item_seleccionado));
            itemView.setOnClickListener(this);
        }

        private void setItem(@NonNull Producto producto) {

            binding.tvProductoRvId.setText(String.format(itemView.getContext().getResources().getString(R.string.tv_rv_Producto_Id), producto.getId()));
            binding.tvProductoRvNombre.setText(String.format(itemView.getContext().getResources().getString(R.string.tv_rv_Producto_Nombre), producto.getNombre()));
            binding.tvProductoRvFecAlta.setText(String.format(itemView.getContext().getResources().getString(R.string.tv_rv_Producto_FecAlta), producto.getFecAltaF()));
            binding.tvProductoRvCantidad.setText(String.format(itemView.getContext().getResources().getString(R.string.tv_rv_Producto_Cantidad), producto.getCantidad()));
//            binding.tvProductoRvIdAula.setText(String.format(itemView.getContext().getResources().getString(R.string.tv_rv_Producto_IdAula), producto.getIdAula()));
//            hacerBindingAAulaConObserver(producto);
            binding.tvProductoRvIdAula.setText(String.format(itemView.getContext().getResources().getString(R.string.tv_rv_Producto_IdAula), producto.getIdAula()));
            binding.tvProductoRvIdDpto.setText(String.format(itemView.getContext().getResources().getString(R.string.tv_rv_Producto_IdDpto), producto.getIdDpto()));
        }

//        private void hacerBindingAAulaConObserver(Producto producto) {
//            //Lo he intentado con view model pero no se que poner en el owner
////            AulasViewModel AulasVM = new ViewModelProvider(requireActivity()).get(AulasViewModel.class);
//            binding.tvProductoRvIdAula.setText(String.format(itemView.getContext().getResources().getString(R.string.tv_rv_Producto_IdAula), producto.getIdAula()));
//        }

        @Override
        public void onClick(View v) {
            int pos = getLayoutPosition();
            notifyItemChanged(mItemPos);
            mItemPos = (mItemPos == pos) ? -1 : pos;
            notifyItemChanged(mItemPos);
            if (mCallback != null) {
                mCallback.onItemClickRVProductos(mItemPos);
            }
        }
    }

}
