package com.example.admin.ftptest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTPFile;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by admin on 2018/5/5.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private List<FTPFile> mFiles;
    private SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//转化时间为yyyy-MM-dd HH:mm:ss格式
    private OnItemClickListener mOnItemClickListener;
    public static final int TYPE_HEADER = 0;//带有header
    public static final int TYPE_FOOTER = 1;//带有footer
    public static final int TYPE_NORMAL = 2;//不带header和footer
    public static final int TYPE_BACK = 3;//带返回和返回根目录
    private View mHeaderView;
    private View mFooterView;
    private View backLastView;
    private boolean showCheckBox;//是否是多选状态
    private SparseBooleanArray mCheckStates;//记录选择状态
    private boolean isCheckChange=true;
    public FileAdapter(List<FTPFile> mFiles) {
        this.mFiles = mFiles;
        mCheckStates = new SparseBooleanArray();
    }

    //设置headerview
    public void setHeaderView(View view) {
        this.mHeaderView = view;
        notifyItemInserted(0);
    }

    //设置footerview
    public void setFooterView(View view) {
        this.mFooterView = view;
        notifyItemInserted(getItemCount() - 1);
    }

    //返回CheckBox是否显示
    public boolean isShowCheckBox() {
        return showCheckBox;
    }

    //设置CheckBox显示状态
    public void setShowCheckBox(boolean showCheckBox) {
        this.showCheckBox = showCheckBox;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else if (position == 1 ) {
            return TYPE_BACK;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            // mHeaderView=LayoutInflater.from(parent.getContext()).inflate(R.layout.headerview_layout,parent,false);
            return new ViewHolder(mHeaderView);
        } else if (viewType == TYPE_FOOTER) {
            // mFooterView=LayoutInflater.from(parent.getContext()).inflate(R.layout.footerview_layout,parent,false);
            return new ViewHolder(mFooterView);
        } else if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        } else if (viewType == TYPE_BACK) {
            backLastView = LayoutInflater.from(parent.getContext()).inflate(R.layout.back_root_item_layout, parent, false);
            return new ViewHolder(backLastView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //头部绑定数据
        if (getItemViewType(position) == TYPE_HEADER) {
            if (mFiles.size() != 0) {
                holder.fileCount.setText(mFiles.size() - 1 + "个");
            }
            if (mOnItemClickListener != null) {
                holder.findFileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = holder.getLayoutPosition(); // 1
                        mOnItemClickListener.onItemClick(holder.findFileImage, position); // 2
                    }
                });
                holder.changeLayoutImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = holder.getLayoutPosition(); // 1
                        mOnItemClickListener.onItemClick(holder.changeLayoutImage, position); // 2
                    }
                });
                holder.sortFileWay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = holder.getLayoutPosition(); // 1
                        mOnItemClickListener.onItemClick(holder.sortFileWay, position); // 2
                    }
                });
                holder.sortFileWayImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = holder.getLayoutPosition(); // 1
                        mOnItemClickListener.onItemClick(holder.sortFileWayImage, position); // 2
                    }
                });
            }
            return;
        }
        if (getItemViewType(position) == TYPE_BACK) {
            holder.backLast.setText("返回上一层");
            if (mOnItemClickListener != null) {
                //为ItemView设置点击监听器
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition(); // 1
                        mOnItemClickListener.onItemClick(holder.itemView, position); // 2
                    }
                });
            }
            return;
        }
        //不是Header和footer就填充数据
        if (getItemViewType(position) == TYPE_NORMAL) {
            //记录CheckBox，防止复用错乱
            holder.checkBox.setTag(position);
            if (showCheckBox) {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(mCheckStates.get(position, false));
            } else {
                holder.checkBox.setVisibility(View.INVISIBLE);
                holder.checkBox.setChecked(false);
                mCheckStates.clear();//不是多选状态清除CheckBox记录
            }
            FTPFile ftpFile = mFiles.get(position - 1);
            if (ftpFile.isFile()) {
                int imageId=setFileImage(ftpFile);
                holder.fileTypeImage.setImageResource(imageId);}
                else {
                holder.fileTypeImage.setImageResource(R.drawable.ic_directory);
            }
                holder.fileOrDirName.setText(ftpFile.getName());
                holder.fileOrDirCurrentTime.setText(time.format(ftpFile.getTimestamp().getTime()));
                Long size = ftpFile.getSize();
                if (size < 1024) {
                    holder.fileSize.setText(String.valueOf(size) + " B ");
                } else if (1024 <= size && size < Math.pow(1024, 2)) {
                    holder.fileSize.setText(String.valueOf(size / 1024) + " KB ");
                } else {
                    double d=size / Math.pow(1024, 2);
                    DecimalFormat df   = new DecimalFormat("######0.00");
                    holder.fileSize.setText(String.valueOf(df.format(d)) + " MB ");
                }
                if (mOnItemClickListener != null) {
                    //为ItemView设置点击监听器
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //如果 是多选状态则进入多选点击操作

                            if (showCheckBox) {
                                holder.checkBox.setChecked(!holder.checkBox.isChecked());
                                return;
                            }
                            int position = holder.getLayoutPosition(); // 1
                            mOnItemClickListener.onItemClick(holder.itemView, position); // 2

                        }
                    });

                    //为ItemView设置长按监听器
                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            isCheckChange=true;//设置CheckBox为可监听状态
                            holder.checkBox.setChecked(!holder.checkBox.isChecked());
                            int position = holder.getLayoutPosition(); // 1
                            mOnItemClickListener.onItemLongClick(holder.itemView, position); // 2
                            return true;
                        }
                    });
                    holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (isCheckChange==false)
                                return;
                            int position = (int) compoundButton.getTag();
                            if (b) {
                                mCheckStates.put(position, true);
                            } else {
                                mCheckStates.delete(position);
                            }
                            int pos = holder.getLayoutPosition();
                            mOnItemClickListener.onCheckBoxChange(holder.itemView, pos, b);
                        }
                    });
                }
                return;

        }
        if (getItemViewType(position) == TYPE_FOOTER) {
            return;
        }

    }

    private int setFileImage(FTPFile ftpFile) {

        if (ftpFile.getName().toString().endsWith(".txt")){
            return R.drawable.txt;
        }else if (ftpFile.getName().toString().endsWith(".apk")){
            return R.drawable.apk;
        }else if (ftpFile.getName().toString().endsWith(".zip")||ftpFile.getName().toString().endsWith(".rar")||ftpFile.getName().toString().endsWith("gz")){
            return R.drawable.zip;
        }else if (ftpFile.getName().toString().endsWith(".jpg")||ftpFile.getName().toString().endsWith(".jpeg")||ftpFile.getName().toString().endsWith(".png")
                ||ftpFile.getName().toString().endsWith(".gif")||ftpFile.getName().toString().endsWith(".bpm")){
            return R.drawable.jpg;
        }else if (ftpFile.getName().toString().endsWith(".mp3")||ftpFile.getName().toString().endsWith(".wma")||ftpFile.getName().toString().endsWith(".wav")||
                ftpFile.getName().toString().endsWith(".aac")){
            return R.drawable.mp3;
        }else if (ftpFile.getName().toString().endsWith(".mp4")||ftpFile.getName().toString().endsWith(".avi")||ftpFile.getName().toString().endsWith(".3gp")||
                ftpFile.getName().toString().endsWith(".rmvb")||ftpFile.getName().toString().endsWith(".mpeg")){
            return R.drawable.video;
        }
        return R.drawable.unknowfile;
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null && mFooterView == null) {
            return mFiles.size();
        } else if (mHeaderView == null && mFooterView != null) {
            return mFiles.size() + 1;
        } else if (mHeaderView != null && mFooterView == null) {
            return mFiles.size() + 1;
        } else {
            return mFiles.size() + 2;
        }

    }

    public void showmCheckState() {
        Log.e("checkState", mCheckStates.toString());
    }

    public void setmCheckStates() {
        for (int i = 2; i <= mFiles.size(); i++) {
            mCheckStates.put(i, true);
        }
    }

    public void clearmCheckState() {
        mCheckStates.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //正常recyclerview包含的view
        ImageView fileTypeImage;
        TextView fileOrDirName;
        TextView fileOrDirCurrentTime;
        TextView fileSize;
        CheckBox checkBox;
        //headerview包含的view
        TextView sortFileWay;
        ImageView sortFileWayImage;
        TextView fileCount;
        ImageView findFileImage;
        ImageView changeLayoutImage;
        //back和backRoot的名称
        TextView backLast;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) {
                sortFileWay = itemView.findViewById(R.id.header_sort_way);
                sortFileWayImage = itemView.findViewById(R.id.header_sort_way_image);
                fileCount = itemView.findViewById(R.id.header_file_count);
                findFileImage = itemView.findViewById(R.id.header_search);
                changeLayoutImage = itemView.findViewById(R.id.header_change_layout);
                return;
            }
            if (itemView == mFooterView) {
                return;
            }
            if (itemView == backLastView) {
                backLast = itemView.findViewById(R.id.back_and_backRoot);
                return;
            } else {
                fileTypeImage = (ImageView) itemView.findViewById(R.id.fileTypeImage);
                fileOrDirName = (TextView) itemView.findViewById(R.id.fileOrDirName);
                fileOrDirCurrentTime = (TextView) itemView.findViewById(R.id.fileRecentTime);
                fileSize = (TextView) itemView.findViewById(R.id.fileSize);
                checkBox = (CheckBox) itemView.findViewById(R.id.choose_item);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);

        void onItemLongClick(View itemView, int position);

        void onCheckBoxChange(View itemView, int position, boolean b);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void clearmCheckStates() {
        mCheckStates.clear();
    }

    public void setIsOnCheckChange(boolean b){
        this.isCheckChange=b;
    }

}
