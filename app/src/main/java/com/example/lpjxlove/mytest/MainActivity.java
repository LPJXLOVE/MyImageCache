package com.example.lpjxlove.mytest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.lpjxlove.mytest.Adapter.Test_Adapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recycler;
    public ImageView iv;
    private Button b;
    private List<String> url;
    private String i="http://p3.so.qhimg.com/t016aaf73f3ce2df2ad.jpg";
    private String i1="http://f.hiphotos.baidu.com/image/h%3D300/sign=e50211178e18367ab28979dd1e738b68/0b46f21fbe096b63a377826e04338744ebf8aca6.jpg";
    private String i2="http://img5.imgtn.bdimg.com/it/u=717857214,2829749621&fm=21&gp=0.jpg";
    private String i3="http://img4.imgtn.bdimg.com/it/u=819265564,3078214620&fm=21&gp=0.jpg";
    private String i4="http://dl.bizhi.sogou.com/images/2013/03/26/334124.jpg";
    private String i5="http://www.deskcar.com/desktop/fengjing/2013812103350/11.jpg";
    private String i6="http://img.article.pchome.net/00/46/22/89/pic_lib/wm/kuanping012.jpg";
    private String i7="http://b.hiphotos.baidu.com/zhidao/pic/item/f9dcd100baa1cd119b739dafbb12c8fcc2ce2d7c.jpg";
    private String i8="http://p0.so.qhimg.com/t019a79d2c5d5db2a40.jpg";
    private String i9="http://e.hiphotos.baidu.com/zhidao/pic/item/5366d0160924ab18eb02b75e35fae6cd7b890b46.jpg";
    private ExecutorService executorService;
    private Handler handler;
    private ImageCacheUtils utils;
    private Test_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*iv= (ImageView) findViewById(R.id.imageView);
        b= (Button) findViewById(R.id.button);*/
        handler=new Handler(this);

   /*     b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap= utils.getBitmap(i);
                if (bitmap==null){
                    Log.i("test","为空");
                }



            }
        });*/
        recycler= (RecyclerView) findViewById(R.id.recycle);
        LinearLayoutManager m=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recycler.setLayoutManager(m);
        recycler.setHasFixedSize(true);
        url=new ArrayList();
        url.add(i1);
        url.add(i);
        url.add(i2);
        url.add(i3);
        url.add(i4);
        url.add(i5);
        url.add(i6);
        url.add(i7);
        url.add(i8);
        url.add(i9);
        adapter= new Test_Adapter(url,this);
        recycler.setAdapter(adapter);
        adapter.setRecyclerView(recycler);




    }




    static class Handler extends android.os.Handler{
        private WeakReference<MainActivity> mainActivityWeakReference;

        public Handler(MainActivity mainActivity) {
            this.mainActivityWeakReference = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity a=mainActivityWeakReference.get();
            if (a!=null){
                Bitmap b= (Bitmap) msg.obj;
                a.iv.setImageBitmap(b);
            }



        }
    }



}
