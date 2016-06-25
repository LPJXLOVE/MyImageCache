package com.example.lpjxlove.mytest.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.example.lpjxlove.mytest.ImageCacheUtils;
import com.example.lpjxlove.mytest.OnLoading;
import com.example.lpjxlove.mytest.R;

import java.util.List;

/**
 * Created by LPJXLOVE on 2016/6/14.
 */
public class Test_Adapter extends RecyclerView.Adapter<Test_Adapter.MyHolder> implements OnLoading {
    private List<String> data;
    private ImageCacheUtils utils;
    private RecyclerView recycle;



    public Test_Adapter(List<String> data,Context context) {
        this.data = data;
        utils=new ImageCacheUtils(context);
        utils.setLoading(this);


    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recycle=recyclerView;

       // LoadImage(0,1);//初始加载两张图片
        recyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                LinearLayoutManager m= (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstItem=m.findFirstVisibleItemPosition();
                int lastItem=m.findLastVisibleItemPosition();
                if (newState==RecyclerView.SCROLL_STATE_IDLE){
                   Log.i("test", "onScrollStateChanged: "+firstItem);
                   LoadImage(firstItem, lastItem);

               }else if (newState==RecyclerView.SCROLL_STATE_SETTLING){

                  /* utils.CancelTask();*/
               }


            }
        });
    }



    private void LoadImage(int firstItem, int lastItem) {
        for (int i=firstItem;i<lastItem+1;i++){

               utils.LoadingImage(data.get(i),i);


        }




    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder holder;
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_item,null,false);
        holder=new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        Bitmap b=utils.getBitmapFromMemoryCache(data.get(position));
        if (b != null) {
            holder.iv.setImageBitmap(b);
        }else {

            holder.iv.setImageResource(R.drawable.welcome);
        }
        holder.iv.setTag(data.get(position));

    }



    @Override
    public int getItemCount() {
        return data.size();
    }



    @Override
    public void OnLoadingSuccess(Bitmap bitmap,int position) {
    if (recycle.findViewHolderForAdapterPosition(position).itemView!=null);{

            ImageView iv= (ImageView) recycle.findViewHolderForAdapterPosition(position)
                    .itemView
                    .findViewWithTag(data.get(position));
            iv.setImageBitmap(bitmap);
        }



    }





    static class MyHolder extends RecyclerView.ViewHolder{
        public ImageView iv;
        public MyHolder(View itemView) {
            super(itemView);
            iv= (ImageView) itemView.findViewById(R.id.iv);
        }
    }




}
