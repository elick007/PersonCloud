package com.example.admin.ftptest.view;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.admin.ftptest.BaseActivity;
import com.example.admin.ftptest.Presenter.ListFilesPresenter;
import com.example.admin.ftptest.Presenter.OperatePresenter;
import com.example.admin.ftptest.Presenter.UpLoadPresenter;
import com.example.admin.ftptest.adapter.FileAdapter;
import com.example.admin.ftptest.Presenter.LoginPresenter;
import com.example.admin.ftptest.R;
import com.example.admin.ftptest.ftphelper.FTPHelper;
import com.example.admin.ftptest.ftphelper.ServiceState;
import com.example.admin.ftptest.myview.NewDirDialog;
import com.example.admin.ftptest.myview.RenameDialog;
import com.example.admin.ftptest.myview.SortWayPopup;
import com.example.admin.ftptest.utils.AnimatorUtil;
import com.example.admin.ftptest.utils.MyLogger;

import org.apache.commons.net.ftp.FTPFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.iwf.photopicker.PhotoPicker;

public class MyActivity extends BaseActivity implements BaseView, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.userName)
    TextView userName;//用户名控件
    @BindView(R.id.userImage)
    ImageView loginImage;//用户图标
    @BindView(R.id.refresh)
    ImageView refreshImage;//刷新图标
    @BindView(R.id.download_manage)
    ImageView downloadManager;
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
    private List<FTPFile> list = new ArrayList<>();
    private FileAdapter fileAdapter;//适配器
    private boolean isSuccess = false;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//转化时间为yyyy-MM-dd HH:mm:ss格式
    private StringBuilder currentPath = new StringBuilder();//路径名，当前为根目录
    private SortWayPopup sortWayPopup;
    private boolean isShowCheck = false;//是否显示checkBox
    private List<String> checkList = new ArrayList<>();//记录checkbox选择状态位置
    public static final int CHOOSE_PHOTO = 2;//启动相册参数
    public static final int LOGIN_ACT=3;//启动登录活动界面
    private boolean safeClick;//防止多次点击
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.bind(this);
        initView();
        initRV();
        initRVListener();
        MyLogger.setDebug();
    }

    private void initView() {
        //toolbar设置点击监听
        loginImage.setOnClickListener(this);
        refreshImage.setOnClickListener(this);
        menuImage.setOnClickListener(this);
        //弹出框点击事件
        longClickChooseAll.setOnClickListener(this);
        longClickCancelAll.setOnClickListener(this);
        longClickDelete.setOnClickListener(this);
        longClickMove.setOnClickListener(this);
        longClickRename.setOnClickListener(this);
        longClickCopy.setOnClickListener(this);
        longClockDownload.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        //显示登录的用户名,更新recyclerview
        if (ServiceState.loginName == null) {
            userName.setText("未登录");
        }
    }

    private void initRV() {
        //绑定recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        fileAdapter = new FileAdapter(list);
        recyclerView.setAdapter(fileAdapter);//设置适配器
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//设置分割线
        //设置headerview和footerview
        View headerView = LayoutInflater.from(this).inflate(R.layout.headerview_layout, recyclerView, false);
        fileAdapter.setHeaderView(headerView);
        View footerView = LayoutInflater.from(this).inflate(R.layout.footerview_layout, recyclerView, false);
        fileAdapter.setFooterView(footerView);
        footerRefreshTime = footerView.findViewById(R.id.footer_fresh_time);
    }

    //记录文件排序方式
   public enum FileSortWay {
        ASC_BY_FILENAME, DESC_BY_TIME, ASC_BY_FILESIZE, DESC_BY_FILESIZE
    }

    private void initRVListener() {
        //实现Recyclerview点击事件
        fileAdapter.setOnItemClickListener(new FileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                switch (itemView.getId()){
                    case R.id.header_search:   //查找
                        showToast("click find");
                        return;
                    case R.id.header_change_layout: //改變rv布局
                        showToast("click change");
                        return;
                    case R.id.header_sort_way_viewGroup:
                        TextView sortWay = findViewById(R.id.header_sort_way);
                        sortWayPopup = new SortWayPopup(MyActivity.this, sortWay,list);
                        sortWayPopup.showAsDropDown(itemView, -15, 0);
                        return;
                }
                try {
                    //点击返回上一层
                    if (position == 1) {
                        if (!currentPath.toString().isEmpty()) {
                            currentPath.delete(currentPath.lastIndexOf("/"), currentPath.length());
                            ListFilesPresenter listFilesPresenter=new ListFilesPresenter(MyActivity.this,new String(currentPath));
                            listFilesPresenter.doListFiles();
                            showToast(currentPath.toString());
                        }
                        return;
                    }

                    if (list.get(position - 1).isFile()) {
                        if (isShowCheck) {
                            return;
                        }
                        return;
                    }
                    //点击的是文件夹便列出文件夹里的文件
                    if (list.get(position - 1).isDirectory()) {
                        if (!isShowCheck&&safeClick) {
                            if (currentPath.toString().equals("/")) {
                                currentPath.append(list.get(position - 1).getName());
                            } else {
                                safeClick=false;
                                currentPath.append("/").append(list.get(position - 1).getName());
                            }
                            FTPHelper.getInstance().setCurrentPath(currentPath.toString());
                            ListFilesPresenter listFilesPresenter=new ListFilesPresenter(MyActivity.this,new String(currentPath));
                            listFilesPresenter.doListFiles();
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
                    showAnimator();
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
                        dissmissAnimator();
                    }
                }
                longClickFileCount.setText("已选中" + (checkList != null ? checkList.size() : 0) + "个");
                Log.e("have", checkList.toString());
            }
        });
    }


    @Override
    public void showRV(List<FTPFile> lists) {
        safeClick=true;
        list.clear();
        list.addAll(lists);
        fileAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDialog(Dialog dialog) {
        dialog.show();
    }

    @Override
    public void dismissDialog(Dialog dialog) {
        dialog.dismiss();
    }

    @Override
    public void showPopupWindow() {
    }

    @Override
    public void showAnimator() {
        isShowCheck = true;
        FileAdapter.setShowCheckBox(true);
        //FileAdapter.setIsOnCheckChange(true);//取消CheckBox监听
        fileAdapter.notifyDataSetChanged();
        //隐藏toolbar，显示Longclick弹出
        toolBar.setVisibility(View.GONE);
        AnimatorUtil.startAnimator(longClickToolbar,-longClickToolbar.getHeight(),0f);
        AnimatorUtil.startAnimator(longClickBottom,longClickBottom.getHeight(),0f);
    }

    @Override
    public void dissmissAnimator() {
        isShowCheck = false;
        FileAdapter.setShowCheckBox(false);
        FileAdapter.setIsOnCheckChange(true);//设置CheckBox监听
        fileAdapter.clearmCheckStates();
        //fileAdapter.notifyDataSetChanged();
        checkList.clear();
        toolBar.setVisibility(View.VISIBLE);
        AnimatorUtil.dismissAnimator(longClickToolbar,0f,-longClickToolbar.getHeight());
        AnimatorUtil.dismissAnimator(longClickBottom,0f,longClickToolbar.getHeight());
    }

    @Override
    public void showToast(String mes) {
        Toast.makeText(this, mes, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginSuccessInit() {
        userName.setText(ServiceState.loginName);
        View navHeader = navigationView.getHeaderView(0);//获取navigationView头部布局
        TextView navUserName = navHeader.findViewById(R.id.nav_userName);
        navUserName.setText(ServiceState.loginName);//设置navigationView用户名
        ListFilesPresenter listFilesPresenter=new ListFilesPresenter(this,"/");
        listFilesPresenter.doListFiles();
    }

    @Override
    public void onPresenterSuccess() {

    }

    @Override
    public void onPresenterFail() {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        OperatePresenter operatePresenter=new OperatePresenter(MyActivity.this);
        switch (v.getId()) {
            case R.id.menu:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.userImage:
                //startActivity(new Intent(MyActivity.this, LoginActivity.class));
                startActivityForResult(new Intent(MyActivity.this, LoginActivity.class),LOGIN_ACT);
                break;
            case R.id.refresh:
                if (FTPHelper.getInstance().isConnected()){
                    final ProgressDialog progressDialog=new ProgressDialog(MyActivity.this);
                    progressDialog.setMessage("刷新中...");
                    showDialog(progressDialog);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 800);
                    ListFilesPresenter listFilesPresenter=new ListFilesPresenter(MyActivity.this,currentPath.toString());
                    listFilesPresenter.doListFiles();
                    Date date = new Date(System.currentTimeMillis());
                    footerRefreshTime.setText("列表更新于"+simpleDateFormat.format(date));
                }else {
                    showToast("当前未登录");
                }
                break;
            case R.id.download_manage:
                break;
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
                FileAdapter.setIsOnCheckChange(false);//取消CheckBox监听
                dissmissAnimator();
                fileAdapter.notifyDataSetChanged();
                break;
            case R.id.long_click_delete:
                operatePresenter.doDeleteFile(checkList,list);
                break;
            case R.id.long_click_check_move:
                operatePresenter.doRemoveFiles(checkList,list);
                break;
            case R.id.long_click_check_rename:
                if (checkList.size()>1){
                    showToast("暂不支持多个文件重命名");
                }else {
                    RenameDialog renameDialog=new RenameDialog(MyActivity.this,list.get(Integer.valueOf(checkList.get(0))-1).getName());
                    showDialog(renameDialog);
                }
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PhotoPicker.REQUEST_CODE:
                if (resultCode==RESULT_OK&&data!=null){
                    ArrayList<String> arrayList=data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    Log.e("photo picker", arrayList.toString());
                    new UpLoadPresenter(this,arrayList,currentPath.toString()).doUploadImage();
                }
                break;
            case LOGIN_ACT:
                if (resultCode==RESULT_OK){
                    new LoginPresenter(this);
                }
                break;
        }
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawers();
        switch (item.getItemId()) {
            case R.id.nav_upload_image:
                PhotoPicker.builder()
                        .setShowCamera(true)
                        .start(MyActivity.this);
                break;
            case R.id.nav_newDir:
                NewDirDialog newDirDialog=new NewDirDialog(MyActivity.this);
                showDialog(newDirDialog);
        }
        return false;
    }

}
