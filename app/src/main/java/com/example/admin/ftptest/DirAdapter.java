package com.example.admin.ftptest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTPFile;

import java.util.List;

/**
 * Created by admin on 2018/6/6.
 */

public class DirAdapter extends RecyclerView.Adapter<DirAdapter.ViewHolder> {
    private List<FTPFile> list;
    private OnItemClickListener mOnItemClickListener;
    public DirAdapter(List<FTPFile> list) {
        this.list=list;
    }

    @Override
    public DirAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_dir_item, parent, false);
        final DirAdapter.ViewHolder holder = new DirAdapter.ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position=holder.getAdapterPosition();
                        mOnItemClickListener.onItemClick(view,position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(DirAdapter.ViewHolder holder, int position) {
        FTPFile dir = list.get(position);
        holder.imageView.setImageResource(R.drawable.ic_directory);
        holder.dirName.setText(dir.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView dirName;
        private ViewHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.list_dir_item_image);
            dirName=itemView.findViewById(R.id.list_dir_item_dirName);
        }
    }
    public interface OnItemClickListener{
        void onItemClick(View itemView, int position);
    }
    public void setOnItemClickListener(DirAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
