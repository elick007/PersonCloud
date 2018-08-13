package com.example.admin.ftptest;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.ftptest.adapter.FileAdapter;
import com.example.admin.ftptest.ftphelper.FTPHelper;
import com.example.admin.ftptest.ftphelper.CallBackListener;
import com.example.admin.ftptest.ftphelper.ServiceState;
import com.example.admin.ftptest.myview.CopyMoveDialog;
import com.example.admin.ftptest.myview.SortWayPopup;
import com.example.admin.ftptest.utils.SortWayFuntion;
import com.example.admin.ftptest.view.LoginActivity;

import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener, CallBackListener {
    @BindView(R.id.userName)
    TextView userName;//用户名控件
    @BindView(R.id.userImage)
    ImageView loginImage;//用户图标
    @BindView(R.id.refresh)
    ImageView refreshImage;//刷新图标
    @BindView(R.id.menu)
    ImageView menuImage;//菜单图标
    @BindView(R.id.nav_view)
    NavigationView navigationView;//拉出菜单
    @BindView(R.id.drawer_view)
    DrawerLayout drawerLayout;
    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    View toolBar;//toolbar
    //长按弹出框及子view
    @BindView(R.id.long_click_toolbar)
    View longClickToolbar;
    @BindView(R.id.long_click_bottom)
    View longClickBottom;
    @BindView(R.id.long_click_select_count)
    TextView longClickFileCount;
    @BindView(R.id.long_click_check_all)
    ViewGroup longClickChooseAll;
    @BindView(R.id.long_click_check_cancel)
    ViewGroup longClickCancelAll;
    @BindView(R.id.long_click_delete)
    ViewGroup longClickDelete;
    @BindView(R.id.long_click_check_move)
    ViewGroup longClickMove;
    @BindView(R.id.long_click_check_rename)
    ViewGroup longClickRename;
    @BindView(R.id.long_click_check_copy)
    ViewGroup longClickCopy;
    @BindView(R.id.long_click_check_download)
    ViewGroup longClockDownload;
    private TextView footerRefreshTime;//footer显示更新时间
    private long exitTime = 0;//按键时间间隔
    private FTPHelper ftpHelper;
    private List<FTPFile> list = new ArrayList<>();
    private FileAdapter fileAdapter;//适配器
    private boolean isSuccess = false;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//转化时间为yyyy-MM-dd HH:mm:ss格式
    private StringBuilder currentPath = new StringBuilder();//路径名，当前为根目录
    private SortWayPopup sortWayPopup;
    private boolean isShowCheck = false;//是否显示checkBox
    private List<String> checkList = new ArrayList<>();//记录checkbox选择状态位置
    // @BindView(R.id.nav_userName) TextView navUserName;
    private SortWayFuntion sortWayFuntion = new SortWayFuntion();//排序方法
    //handler更新ui

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        private MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                // ...
                switch (msg.what) {
                    case 1:  //移动文件成功
                        activity.dismissLongClickPopup();
                        activity.showPathFiles();
                        break;
                    case 3: //重命名文件成功
                        activity.dismissLongClickPopup();
                        activity.showPathFiles();
                        break;
                }
            }
        }
    }

    private final MyHandler handler = new MyHandler(this);

    //记录文件排序方式
    enum FileSortWay {
        ASC_BY_FILENAME, DESC_BY_TIME, DESC_BY_FILENAME, ASC_BY_FILESIZE, DESC_BY_FILESIZE
    }

    public static final int CHOOSE_PHOTO = 2;//启动相册参数
    private FileSortWay fileSortWay = FileSortWay.ASC_BY_FILENAME;//默认为按文件名升序

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initRV();
        initRVListener();
    }

    private void initView() {

        //toolbar设置点击监听
        loginImage.setOnClickListener(this);
        refreshImage.setOnClickListener(this);
        menuImage.setOnClickListener(this);

        longClickChooseAll.setOnClickListener(this);
        longClickCancelAll.setOnClickListener(this);
        longClickDelete.setOnClickListener(this);
        longClickMove.setOnClickListener(this);
        longClickRename.setOnClickListener(this);
        longClickCopy.setOnClickListener(this);
        longClockDownload.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.nav_upload:
                        showToast("click upload");
                        break;
                    case R.id.nav_upload_image:
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        } else {
                            openAlbum();
                        }
                        break;
                    case R.id.nav_newDir:
//                        final NewDirDialog newDirDialog = new NewDirDialog(MainActivity.this, R.layout.new_dir_dialog_layout,
//                                new int[]{R.id.dialog_cancel, R.id.dialog_sure});
//                        NewDirDialog.OnCenterItemClickListener dialogListener = new NewDirDialog.OnCenterItemClickListener() {
//                            @Override
//                            public void OnCenterItemClick(NewDirDialog dialog, View view) {
//                                switch (view.getId()) {
//                                    case R.id.dialog_sure:
//                                        EditText editText = newDirDialog.findViewById(R.id.edit_new_dir);
//                                        final String newDirName = editText.getText().toString().trim();
//                                        new Thread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                try {
//                                                    switch (ftpHelper.creatNewDir(newDirName)) {
//                                                        case 0:
//                                                            showToast("创建成功");
//                                                            showPathFiles();
//                                                            break;
//                                                        case 1:
//                                                            showToast("创建失败");
//                                                            break;
//                                                        case 2:
//                                                            showToast("文件夹已存在");
//                                                            break;
//                                                    }
//                                                } catch (IOException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        }).start();
//                                        break;
//                                    default:
//                                        break;
//                                }
//                            }
//                        };
//                        newDirDialog.setOnCenterItemClickListener(dialogListener);
//                        newDirDialog.show();
                        break;

                }
                return true;
            }
        });
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    showToast("You denied the permission");
                }
                break;
            default:
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    String imagePath = null;
                    Uri uri = data.getData();
                    if (DocumentsContract.isDocumentUri(this, uri)) {
                        String docId = DocumentsContract.getDocumentId(uri);
                        if ("com.android.providers.media.documents".equals(uri != null ? uri.getAuthority() : null)) {
                            String id = docId.split(":")[1];//解析出数字格式ID
                            String selection = MediaStore.Images.Media._ID + "=" + id;
                            imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                        } else if ("com.android.provider.downloads.documents".equals(uri != null ? uri.getAuthority() : null)) {
                            Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                            imagePath = getImagePath(contentUri, null);
                        }
                    } else if ("content".equalsIgnoreCase(uri != null ? uri.getScheme() : null)) {
                        imagePath = getImagePath(uri, null);
                    } else if ("file".equalsIgnoreCase(uri != null ? uri.getScheme() : null)) {
                        imagePath = uri != null ? uri.getPath() : null;
                    }
                    Notification notification = new NotificationCompat.Builder(this)
                            .setContentTitle(imagePath.substring((imagePath != null ? imagePath.lastIndexOf("/") : 0) + 1, (imagePath != null ? imagePath.length() : 0) - 1))
                            .setContentText("上传中")
                            .setSmallIcon(R.drawable.ic_file)
                            .build();
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.notify(1, notification);
                    }
                    //new UploadTaskModel(ftpHelper, imagePath, currentPath.toString(), this).execute();
                }
                break;
            default:
                break;
        }
    }

    private String getImagePath(Uri externalContentUri, String seletion) {
        String path = null;
        //通过Uri和selection来获取真实的图片地址
        Cursor cursor = getContentResolver().query(externalContentUri, null, seletion, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void initRV() {
        //绑定recyclerview
        //recyclerView = findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        fileAdapter = new FileAdapter(list);
        recyclerView.setAdapter(fileAdapter);//设置适配器
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//设置分割线
        //设置headerview和footerview
        View headerView = LayoutInflater.from(this).inflate(R.layout.headerview_layout, recyclerView, false);
        fileAdapter.setHeaderView(headerView);
        View footerView = LayoutInflater.from(this).inflate(R.layout.footerview_layout, recyclerView, false);
        footerRefreshTime = footerView.findViewById(R.id.footer_fresh_time);
        fileAdapter.setFooterView(footerView);
        //显示登录的用户名
        if (ServiceState.loginName == null) {
            userName.setText("未登录");
       }
//       else {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    ftpHelper = new FTPHelper(ServiceState.host, ServiceState.loginName, ServiceState.passwd);
//                    try {
//                        isSuccess = ftpHelper.openConnect();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    //登录成功则更新列表
//                    if (isSuccess) {
//                        showPathFiles();
//                    }
//                }
//            }).start();
//            userName.setText(ServiceState.loginName);
//            View navHeader = navigationView.getHeaderView(0);//获取navigationView头部布局
//            TextView navUserName = navHeader.findViewById(R.id.nav_userName);
//            navUserName.setText(ServiceState.loginName);//设置navigationView用户名
//        }
    }


    private void initRVListener() {
        //实现Recyclerview点击事件
        fileAdapter.setOnItemClickListener(new FileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                //    headerview里查询点击监听
                ImageView findFile = findViewById(R.id.header_search);
                ImageView changeLayout = findViewById(R.id.header_change_layout);
                TextView headerSortWay = findViewById(R.id.header_sort_way);
                ImageView headerSortWayImage = findViewById(R.id.header_sort_way_image);
                if (itemView == findFile) {
                    showToast("click findfile");
                    return;
                }
                //header里改变布局监听
                if (itemView == changeLayout) {
                    showToast("click changelayout");
                    return;
                }
                //点击是header排序类型就弹出popupWindow
                if (itemView == headerSortWay || itemView == headerSortWayImage) {
                    // popupWindow.showAsDropDown(headerSortWay, -15, 0);
                    //sortWayPopup = new SortWayPopup(MainActivity.this, itemsOnClick);
                    sortWayPopup.showAsDropDown(headerSortWay, -15, 0);
                    return;
                }
                try {
                    if (list.get(position - 1).isFile()) {
                        if (isShowCheck) {
                            return;
                        }
                        return;
                    }
                    //点击返回上一层
                    if (position == 1) {
                        if (!currentPath.toString().equals("/")) {
                            currentPath.delete(currentPath.lastIndexOf("/"), currentPath.length());
                            showPathFiles();
                            ftpHelper.setCurrentPath(currentPath.toString());
                            showToast(currentPath.toString());
                        }
                        return;
                    }
                    //点击的是文件夹便列出文件夹里的文件
                    if (list.get(position - 1).isDirectory()) {
                        if (!isShowCheck) {
                            if (currentPath.toString().equals("/")) {
                                currentPath.append(list.get(position - 1).getName());
                            } else {
                                currentPath.append("/").append(list.get(position - 1).getName());
                            }
                            showPathFiles();
                            ftpHelper.setCurrentPath(currentPath.toString());
                            showToast(currentPath.toString());
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onItemLongClick(View itemView, int position) {
                if (!isShowCheck) {
                    showLongClickPopup();
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckBoxChange(View itemView, int position, boolean b) {
                if (b) {
                    if (checkList != null) {
                        if (!checkList.contains(String.valueOf(position))) {
                            checkList.add(String.valueOf(position));
                        }
                    }
                } else {
                    try {
                        checkList.remove(String.valueOf(position));
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    if (checkList.size() == 0) {
                        dismissLongClickPopup();
                        fileAdapter.notifyDataSetChanged();
                    }
                }
                longClickFileCount.setText("已选中" + (checkList != null ? checkList.size() : 0) + "个");
                Log.e("have", checkList.toString());
            }
        });
    }

    //弹出排序view点击事件
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            TextView sortWay = findViewById(R.id.header_sort_way);
            sortWayPopup.dismiss();
            switch (view.getId()) {
                case R.id.desc_by_time:
                    fileSortWay = FileSortWay.DESC_BY_TIME;//更改标记按时间降序
                    sortWay.setText("按时间降序");
                    break;
                case R.id.asc_by_fileName:
                    fileSortWay = FileSortWay.DESC_BY_FILENAME;//更改标记按文件名降序
                    sortWay.setText("文件名降序");
                    break;
                case R.id.asc_by_fileSize:
                    fileSortWay = FileSortWay.ASC_BY_FILESIZE;//更改标记按文件大小升序
                    sortWay.setText("按大小升序");
                    break;
                case R.id.desc_by_fileSize:
                    fileSortWay = FileSortWay.DESC_BY_FILESIZE;//更改标记按文件大小降序
                    sortWay.setText("按大小降序");
                    break;
            }
            showPathFiles();//重新进入当前文件夹刷新列表
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //拉开DrawerLayout
            case R.id.menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            //实现登录头像点击登录事件
            case R.id.userImage:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;

            //刷新列表
            case R.id.refresh:
                if (!isSuccess) {
                    showToast("当前未登录，不可操作");
                    return;
                } else {
                    Date date = new Date(System.currentTimeMillis());
                    footerRefreshTime.setText("列表更新于" + simpleDateFormat.format(date));
                    showPathFiles();
                    //fileAdapter.notifyDataSetChanged();
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    showToast("刷新成功");
                }
                break;
            //全选列表
            case R.id.long_click_check_all:
                // fileAdapter.setIsOnCheckChange(false);//取消CheckBox监听   、、取消之后再点击单独挑选不行，不够好
                for (int i = 2; i <= list.size(); i++) {
                    if (!checkList.contains(String.valueOf(i))) {
                        checkList.add(String.valueOf(i));
                    }
                }
                longClickFileCount.setText("已选中" + checkList.size() + "个");
                //mChckStates和checkList记为全选
                fileAdapter.setmCheckStates();
                fileAdapter.notifyDataSetChanged();
                break;
            //取消全选
            case R.id.long_click_check_cancel:
                fileAdapter.setIsOnCheckChange(false);//取消CheckBox监听
                dismissLongClickPopup();
                fileAdapter.notifyDataSetChanged();
                break;
            //删除选择文件
            case R.id.long_click_delete:
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("删除");
                alertDialog.setMessage("删除这" + checkList.size() + "个文件");
                alertDialog.setCancelable(false);
                fileAdapter.setIsOnCheckChange(false);//设置CheckBox不响应监听
                alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("文件删除中");
                        progressDialog.show();
                        for (String listPosition : checkList) {
                            final FTPFile ftpFile = list.get(Integer.valueOf(listPosition) - 1);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (ftpFile.isFile()) {
                                        if (ftpHelper.deleteFile(ftpFile.getName())) {
                                            showToast(ftpFile.getName() + "删除成功");
                                        } else {
                                            showToast(ftpFile.getName() + "删除失败");
                                        }
                                    } else {
                                        ftpHelper.removeDirectoryALLFile(ftpFile.getName());
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissLongClickPopup();
                                            showPathFiles();
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                            }).start();

                        }
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        fileAdapter.setIsOnCheckChange(true);//设置CheckBox响应监听
                    }
                });
                alertDialog.show();
                break;
            //移动操作
            case R.id.long_click_check_move:
                fileAdapter.setIsOnCheckChange(false);//取消CheckBox监听
                final CopyMoveDialog copyMoveDialog = new CopyMoveDialog(this, new CopyMoveDialog.ChooseListener() {
                    @Override
                    public void chooseConfirm(final String path) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (String position : checkList) {
                                    //FTPHelper.reNameOrMove(currentPath.toString() + "/" + list.get(Integer.parseInt(position) - 1).getName(), path + "/" + list.get(Integer.parseInt(position) - 1).getName());
                                }
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            }
                        }).start();

                    }

                    @Override
                    public void chooseCancel() {
                        fileAdapter.setIsOnCheckChange(true);
                    }
                });
                copyMoveDialog.setCancelable(false);
                copyMoveDialog.show();
                break;
            //重命名操作
            case R.id.long_click_check_rename:
//                final NewDirDialog renameDialog = new NewDirDialog(this, R.layout.rename_dialog_layout, new int[]{R.id.rename_dialog_cancel, R.id.rename_dialog_sure});
//                NewDirDialog.OnCenterItemClickListener listener = new NewDirDialog.OnCenterItemClickListener() {
//                    @Override
//                    public void OnCenterItemClick(NewDirDialog dialog, View view) {
//                        switch (view.getId()) {
//                            case R.id.rename_dialog_sure:
//                                if (checkList.size() != 1) {
//                                    showToast("不支持多个命名");
//                                } else {
//                                    fileAdapter.setIsOnCheckChange(false);
//                                    EditText renameEdit = renameDialog.findViewById(R.id.rename_dialog_newName_edit);
//                                    final String newName = renameEdit.getText().toString().trim();
//                                    new Thread(new Runnable() {
//                                        @Override
//                                        public void run() {
////                                            if (FTPHelper.reNameOrMove(list.get(Integer.valueOf(checkList.get(0)) - 1).getName(), newName)) {
////                                                Message message = new Message();
////                                                message.what = 3;
////                                                handler.sendMessage(message);
////                                            }
//                                        }
//                                    }).start();
//                                }
//                                renameDialog.dismiss();
//                                break;
//                            case R.id.rename_dialog_cancel:
//                                fileAdapter.setIsOnCheckChange(true);
//                                renameDialog.dismiss();
//                                break;
//                        }
//                    }
//                };
//                renameDialog.setOnCenterItemClickListener(listener);
//                renameDialog.show();
                break;
        }
    }

    /**
     * 主线程showToast
     *
     * @param msg 显示消息
     */
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 列出path路径下的全部文件与文件夹
     */
    public void showPathFiles() {
        //view.setClickable(false); // view设置监听后方法作废
//        new ListPathFileTask(ftpHelper, currentPath.toString(), this).execute();
    }

    /**
     * 长按弹出view菜单
     */
    public void showLongClickPopup() {
        isShowCheck = true;
        FileAdapter.setShowCheckBox(true);
        fileAdapter.notifyDataSetChanged();
        //隐藏toolbar，显示Longclick弹出
        toolBar.setVisibility(View.GONE);
        longClickToolbar.setVisibility(View.VISIBLE);
        longClickBottom.setVisibility(View.VISIBLE);
        longClickToolbar.post(new Runnable() {
            @Override
            public void run() {
                int height = longClickToolbar.getHeight();
                ObjectAnimator animator = ObjectAnimator.ofFloat(longClickToolbar, "translationY", -height, 0f);
                animator.setDuration(300);
                animator.start();
            }
        });
        longClickBottom.post(new Runnable() {
            @Override
            public void run() {
                int height = longClickBottom.getHeight();
                ObjectAnimator animator = ObjectAnimator.ofFloat(longClickBottom, "translationY", height, 0f);
                animator.setDuration(300);
                animator.start();
            }
        });
    }

    /**
     * 隐藏长按弹出菜单
     */
    public void dismissLongClickPopup() {
        isShowCheck = false;
        FileAdapter.setShowCheckBox(false);
        fileAdapter.clearmCheckStates();
        checkList.clear();
        //showPathFiles(currentPath);
        //fileAdapter.notifyDataSetChanged();
        longClickToolbar.clearAnimation();
        longClickBottom.clearAnimation();
        toolBar.setVisibility(View.VISIBLE);
        int height = longClickToolbar.getHeight();
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(longClickToolbar, "translationY", 0f, -height);
        animator1.setDuration(300);
        Animator.AnimatorListener dismissListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                longClickToolbar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        };
        animator1.addListener(dismissListener);
        animator1.start();


        ObjectAnimator animator = ObjectAnimator.ofFloat(longClickBottom, "translationY", 0f, height);
        animator.setDuration(300);
        Animator.AnimatorListener dismissListener1 = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                longClickBottom.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        };
        animator.addListener(dismissListener1);
        animator.start();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            removeALLActivity();
            System.exit(0);
        }
    }

    @Override
    public void getFTPFileList(List<FTPFile> ftpFileList) {
        if (list != null) {
            list.clear();
            switch (fileSortWay) {
                case DESC_BY_TIME:
                    list.addAll(sortWayFuntion.descByTime(ftpFileList));
                    break;
                case ASC_BY_FILENAME:
                    list.addAll(ftpFileList);
                    break;
                case DESC_BY_FILESIZE:
                    list.addAll(sortWayFuntion.descByFileSize(ftpFileList));
                    break;
                case ASC_BY_FILESIZE:
                    list.addAll(sortWayFuntion.ascByFileSize(ftpFileList));
                    break;
                case DESC_BY_FILENAME:
                    list.addAll(sortWayFuntion.descByName(ftpFileList));
                    break;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fileAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onFinsh(Boolean b) {
        if (b) {
            showToast("上传成功");
            showPathFiles();
        } else {
            showToast("上传失败");
        }
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(1);
        }
    }

}
