package com.thfw.ui.dialog.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.ui.dialog.TDialog;

import java.util.List;

/**
 * @author Timmy
 * @time 2018/1/24 14:39
 * @GitHub https://github.com/Timmy-zzh/TDialog
 **/
public abstract class TBaseAdapter<T> extends RecyclerView.Adapter<BindViewHolder> {

    private final int layoutRes;
    private List<T> datas;
    private OnAdapterItemClickListener adapterItemClickListener;
    private TDialog dialog;

    public TBaseAdapter(@LayoutRes int layoutRes, List<T> datas) {
        this.layoutRes = layoutRes;
        this.datas = datas;
    }

    protected abstract void onBind(BindViewHolder holder, int position, T t);

    @Override
    public BindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BindViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false));
    }

    @Override
    public void onBindViewHolder(final BindViewHolder holder, final int position) {
        onBind(holder, position, datas.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterItemClickListener.onItemClick(holder, position, datas.get(position), dialog);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setTDialog(TDialog tDialog) {
        this.dialog = tDialog;
    }

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
        this.adapterItemClickListener = listener;
    }

    public interface OnAdapterItemClickListener<T> {
        void onItemClick(BindViewHolder holder, int position, T t, TDialog tDialog);
    }
}
