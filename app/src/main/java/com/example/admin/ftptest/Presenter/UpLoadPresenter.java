package com.example.admin.ftptest.Presenter;

import com.example.admin.ftptest.model.UploadTaskModel;
import com.example.admin.ftptest.utils.MyLogger;
import com.example.admin.ftptest.view.MyActivity;
import java.util.ArrayList;

public class UpLoadPresenter implements BasePresenter<Boolean> {
    private MyActivity myActivity;
    private ArrayList<String> list;
    private String currentPath;
    private int count;

    public UpLoadPresenter(MyActivity myActivity, ArrayList<String> list, String currentPath) {
        this.myActivity = myActivity;
        this.list = list;
        this.currentPath = currentPath;
    }


    public void doUploadImage() {
//        String imagePath = null;
//        Uri uri = data.getData();
//        if (DocumentsContract.isDocumentUri(myActivity, uri)) {
//            String docId = DocumentsContract.getDocumentId(uri);
//            if ("com.android.providers.media.documents".equals(uri != null ? uri.getAuthority() : null)) {
//                String id = docId.split(":")[1];//解析出数字格式ID
//                String selection = MediaStore.Images.Media._ID + "=" + id;
//                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
//            } else if ("com.android.provider.downloads.documents".equals(uri != null ? uri.getAuthority() : null)) {
//                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
//                imagePath = getImagePath(contentUri, null);
//            }
//        } else if ("content".equalsIgnoreCase(uri != null ? uri.getScheme() : null)) {
//            imagePath = getImagePath(uri, null);
//        } else if ("file".equalsIgnoreCase(uri != null ? uri.getScheme() : null)) {
//            imagePath = uri != null ? uri.getPath() : null;
//        }
//        Notification notification = new NotificationCompat.Builder(myActivity)
//                .setContentTitle(imagePath.substring((imagePath != null ? imagePath.lastIndexOf("/") : 0) + 1, (imagePath != null ? imagePath.length() : 0) - 1))
//                .setContentText("上传中")
//                .setSmallIcon(R.drawable.ic_file)
//                .build();
//        NotificationManager manager = (NotificationManager) myActivity.getSystemService(NOTIFICATION_SERVICE);
//        if (manager != null) {
//            manager.notify(1, notification);
//        }
//        for (int i=0;i<albumFileList.size();i++){
//            new UploadTaskModel(albumFileList.get(i).getPath(), currentPath, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }
        for (int i = 0; i < list.size(); i++) {
            MyLogger.e(list.get(i));
            new UploadTaskModel(list.get(i), currentPath, this).execute();
        }

    }


    @Override
    public void onModelSuccess(Boolean aBoolean) {
        if (aBoolean) {
            count++;
        }
        if (count == list.size()) {
            myActivity.showToast("上传成功");
            ListFilesPresenter listFilesPresenter = new ListFilesPresenter(myActivity, currentPath);
            listFilesPresenter.doListFiles();
        }
    }

    @Override
    public void onModelFail() {

    }
}
