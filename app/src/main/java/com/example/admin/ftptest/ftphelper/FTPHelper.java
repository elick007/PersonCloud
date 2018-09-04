package com.example.admin.ftptest.ftphelper;

import com.example.admin.ftptest.utils.MyLogger;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/5/2.
 */

public class FTPHelper {
    private static String hostName;
    private static String userName;
    private static String password;
    private static FTPClient ftpClient;
    private String currentPath = "";
    private static FTPHelper singleTon = null;
    private boolean isConnected = false;

    private FTPHelper() {
        hostName = ServiceState.host;
        userName = ServiceState.loginName;
        password = ServiceState.passwd;
        ftpClient = new FTPClient();
    }

    public static FTPHelper getInstance() {
        if (singleTon == null) {
            synchronized (FTPHelper.class) {
                if (singleTon == null) {
                    singleTon = new FTPHelper();
                }
            }
        }
        return singleTon;
    }

    /**
     * 打开FTP服务.
     *
     * @throws IOException
     */
    public boolean openConnect() {
        // 中文转码
        boolean flag = false;
        ftpClient.setControlEncoding("UTF-8");
        int reply; // 服务器响应值
        // 连接至服务器
        try {
            ftpClient.connect(hostName, 21);
            // 获取响应值
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                // 断开连接
                ftpClient.disconnect();
                MyLogger.d("Connect Fail");
                flag = false;
            }
            // 登录到服务器
            ftpClient.login(userName, password);
            // 获取响应值
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                // 断开连接
                ftpClient.disconnect();
                MyLogger.d("Connect Fail");
                flag = false;
            } else {
                // 获取登录信息
//            FTPClientConfig config = new FTPClientConfig(ftpClient.getSystemType().split(" ")[0]);
//            config.setServerLanguageCode("zh");
//            ftpClient.configure(config);
                // 使用被动模式设为默认
                ftpClient.enterLocalPassiveMode();
                // 二进制文件支持
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                MyLogger.d("Connect Success");
                flag = true;
                isConnected = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 关闭FTP服务
     **/
    public void closeConnect() throws IOException {
        if (ftpClient != null) {
            ftpClient.logout();
            ftpClient.disconnect();//断开连接
        }
    }

    /**
     * @return 判断是否连接
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 列出服务器指定路径下所有文件
     */
    public List<FTPFile> listFTPFile(String path) {
        List<FTPFile> lists = new ArrayList<>();
        try {
            ftpClient.changeWorkingDirectory(path);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            //遍历添加到集合
            for (FTPFile file : ftpFiles) {
                if (file.getName().equals("."))
                    continue;
                lists.add(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lists;
    }

    /**
     * 列出服务器路径下的文件夹
     *
     * @param path
     * @return
     */
    public List<FTPFile> listFTPDir(String path) {
        List<FTPFile> lists = new ArrayList<>();
        try {
            ftpClient.changeWorkingDirectory(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FTPFile[] ftpFiles = ftpClient.listDirectories();
            //遍历添加到集合
            for (FTPFile file : ftpFiles) {
                if (file.getName().equals("."))
                    continue;
                lists.add(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lists;
    }

    /**
     * 上传文件
     *
     * @param filePath    本地文件路径
     * @param currentPath 服务器路径
     * @return
     * @throws IOException
     */
    public boolean uploadFile(String filePath, String currentPath) {
        boolean flag = false;
        try {
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            // ftpClient.enterLocalPassiveMode();
            ftpClient.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE);
            ftpClient.changeWorkingDirectory(currentPath);
            File localFile = new File(filePath);
            if (localFile.exists() && localFile.isFile()) {
                InputStream inputStream = new FileInputStream(localFile);
                flag = ftpClient.storeFile(localFile.getName(), inputStream);
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除文件
     *
     * @param pathName 服务器文件路径
     * @return
     */
    public boolean deleteFile(String pathName) {
        boolean flag = false;
        try {
            flag = ftpClient.deleteFile(pathName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * @param pathName 删除文件夹里面全部文件
     * @return
     */
    public boolean removeDirectoryALLFile(String pathName) {

        FTPFile[] files = new FTPFile[0];
        try {
            files = ftpClient.listFiles(pathName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null != files && files.length > 2) {
            for (int i = 2; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    removeDirectoryALLFile(pathName + "/" + files[i].getName());
                    // 切换到父目录，不然删不掉文件夹
                    // ftpClient.changeWorkingDirectory(currentPath+"/"+pathName.substring(0, pathName.lastIndexOf("/")));
                    try {
                        ftpClient.removeDirectory(pathName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        ftpClient.deleteFile(pathName + "/" + files[i].getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

        }

        // 切换到父目录，不然删不掉文件夹
        try {
            ftpClient.removeDirectory(pathName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;

    }

    /**
     * 创建文件夹
     *
     * @param dirName
     * @return 0-创建成功  1-创建失败  2-文件夹已存在
     * @throws IOException
     */
    public int creatNewDir(String dirName) {
        FTPFile[] ftpDirs = new FTPFile[0];
        try {
            ftpDirs = ftpClient.listDirectories();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (FTPFile ftpDir : ftpDirs) {
            if (ftpDir.getName().equals(dirName))
                return 2;
        }
        boolean flag = false;
        try {
            flag = ftpClient.makeDirectory(dirName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (flag) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * 与mianactivity同步服务器当前path
     *
     * @param currentPath
     */
    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    /**
     * 重命名或移动文件
     *
     * @param from
     * @param to
     * @return
     */
    public boolean reNameOrMove(final String from, final String to) {
        boolean flag = false;

        try {
            flag = ftpClient.rename(from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return flag;
    }

}
