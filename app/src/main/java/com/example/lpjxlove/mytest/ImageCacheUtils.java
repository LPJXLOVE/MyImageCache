package com.example.lpjxlove.mytest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.*;
import java.lang.Thread;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by LPJXLOVE on 2016/6/15.
 */
public class ImageCacheUtils {
    private int newState;

    public OnLoading loading;
    private ExecutorService executorService;


    private static LruCache<String,Bitmap> MemoryCache;
    public   Bitmap bitmap;
    private static DiskLruCache diskLruCache;



    public ImageCacheUtils(Context context) {
        executorService=Executors.newFixedThreadPool(5);
        //初始化DiskCache
        try {
            diskLruCache=DiskLruCache.open(getDiskCacheDir(context, "DiskCache")
                    ,getAppVersion(context), 1,1024*1024*10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //初始化MemoryCache

        int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        MemoryCache=new LruCache<String, Bitmap>(memoryCacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }




    private File getDiskCacheDir(Context context, String uniqueName){
        String cacheDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                ||!Environment.isExternalStorageRemovable()){
            cacheDir=context.getApplicationContext().getCacheDir().getPath();

        }else {
            cacheDir=context.getCacheDir().getPath();
        }

        return new File(cacheDir+File.separator+uniqueName);
    }


    private int getAppVersion(Context context){
        try {
            PackageInfo info=context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 1;
    }


    /**
     * @param loading
     * 加载图片成功回调
     */
    public void setLoading(OnLoading loading) {
        this.loading = loading;
    }

    /**
     * @param key
     * @return
     *
     * 从内存加载图片
     */
    public Bitmap getBitmapFromMemoryCache(String key){


        return MemoryCache.get(key);
    }


    /**
     * @param key
     * @
     * 从磁盘加载
     */
    private Bitmap getBitmapFromDiskCache(String key){
        Bitmap CacheBitmap=null;
        try {
           DiskLruCache.Snapshot snapshot= diskLruCache.get(hashKeyForDisk(key));
            if (snapshot!=null){
                InputStream inputStream=snapshot.getInputStream(0);
                CacheBitmap=BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert CacheBitmap != null;
        MemoryCache.put(key,CacheBitmap);


        return CacheBitmap;
    }










    @Nullable
    private void getBitmapFromNewThread(final String key, final int postion) {
        final MyHandle myHandle=new MyHandle(this);

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {

                bitmap=getBitmapFromDiskCache(key);
                if (bitmap==null){

                    bitmap=getBitmapFromInternet(key);

                }


                Message m = myHandle.obtainMessage();
                m.obj = bitmap;
                m.arg1 = postion;
                myHandle.sendMessage(m);


            }
        });


        executorService.execute(thread);

    }





    public Bitmap getBitmapFromInternet(final String key){
        Bitmap NetBitmap=null;
        try {
            final URL url = new URL(key);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            NetBitmap = BitmapFactory.decodeStream(connection.getInputStream());


            if (NetBitmap != null && key != null) {


                String u = hashKeyForDisk(key);


                MemoryCache.put(key, NetBitmap);


                DiskLruCache.Editor editor = diskLruCache.edit(u);
                if (editor != null) {
                    OutputStream ops = editor.newOutputStream(0);

                    NetBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ops);

                    editor.commit();
                    diskLruCache.flush();
                    ops.close();

                }


            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return NetBitmap;
    }





















        static class MyHandle extends Handler{
            private WeakReference<ImageCacheUtils> Image;


            public MyHandle(ImageCacheUtils image) {
                Image =new WeakReference<ImageCacheUtils>(image);
            }

            @Override
            public void handleMessage(Message msg) {
                ImageCacheUtils imageCacheUtils=Image.get();
                if (imageCacheUtils!=null){

                    imageCacheUtils.bitmap= (Bitmap) msg.obj;
                    int position=msg.arg1;
                    imageCacheUtils.loading.OnLoadingSuccess(imageCacheUtils.bitmap,position);//回调通知TestAdapter数据变化了




                }


            }
        }




    public void LoadingImage(String key, int position){
        if (CheckHaveCache(key)){
            loading.OnLoadingSuccess(getBitmapFromMemoryCache(key),position);
            return;
        }

        getBitmapFromNewThread(key, position);

    }





    /**
     * @param key
     * @return
     * check cache in memory
     */
    private boolean CheckHaveCache(String key){

        Bitmap bitmap=getBitmapFromMemoryCache(key);

        return !(bitmap == null);
    }






    public  void CancelTask(){
        if (!executorService.isShutdown()){

            synchronized (this){

                executorService.shutdown();
            }
        }


    }



//MD5编码
    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }








}
