package com.digisoft.mma.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.digisoft.mma.DB.InitializeDatabase;
import com.digisoft.mma.R;
import com.digisoft.mma.activity.FullScreenActivity;
import com.digisoft.mma.model.Event;
import com.digisoft.mma.model.PhotoModel;
import com.digisoft.mma.util.CryptographyUtils;
import com.digisoft.mma.util.SharePreferenceHelper;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.digisoft.mma.util.AppConstant.PID;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder>{
    Context context;
    ArrayList<PhotoModel> singleRowArrayList;
    private SharePreferenceHelper mSharedPreferences;
    private GlideUrl glideUrl;
    private RequestOptions options;
    private String url;
    private String  pid;
    public PhotoAdapter(Context context, ArrayList<PhotoModel> singleRowArrayList, String pid) {
        this.context = context;
        this.singleRowArrayList = singleRowArrayList;
        mSharedPreferences = new SharePreferenceHelper(context);
        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        this.pid = pid;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_item, null);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        if(singleRowArrayList.get(i).getUid() == 1){
            myViewHolder.newsImage.setImageBitmap(CryptographyUtils.getDecodedBitmap(singleRowArrayList.get(i).getImage(),
                    pid));
        }else {
            myViewHolder.delete.setVisibility(View.GONE);
            Log.i("url",singleRowArrayList.get(i).getImage());
            url = "http://alb-java-apps-776075049.ap-southeast-1.elb.amazonaws.com:9000"+singleRowArrayList.get(i).getImage();
            glideUrl = new GlideUrl(url,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + mSharedPreferences.getToken())
                            .build());


            Glide.with(context)
                    .load(glideUrl)
                    .transition(withCrossFade())
                    .thumbnail(0.5f)
                    .apply(options)
                    .into(myViewHolder.newsImage);
        }

        myViewHolder.newsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullScreenActivity.class);
                Bundle extras = new Bundle();
                if(singleRowArrayList.get(i).getUid() == 1){
                    extras.putString("image", singleRowArrayList.get(i).getImage());
                    extras.putString(PID, pid);
                    extras.putBoolean("isRejected",false);
                }else {
                    extras.putString("image", singleRowArrayList.get(i).getImage());
                    extras.putBoolean("isRejected",true);
                }
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });

        myViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletedata(i,singleRowArrayList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return singleRowArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView newsImage,delete;
        TextView id;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImage = (ImageView) itemView.findViewById(R.id.newsImage);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            // id = (TextView) itemView.findViewById(R.id.id);
        }
    }



    public void deletedata(final int position, final ArrayList<PhotoModel> singleRowArrayList){
        new AlertDialog.Builder(context)
                .setIcon(R.drawable.warning)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want delete this photo?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /* This is where deletions should be handled */
                        singleRowArrayList.remove(position);
                        notifyItemRemoved(position);
                        notifyDataSetChanged();

                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private Bitmap getBitmapFromEncodedString(String encodedString){

        byte[] arr = Base64.decode(encodedString, Base64.URL_SAFE);

        Bitmap img = BitmapFactory.decodeByteArray(arr, 0, arr.length);

        return img;

    }
}
