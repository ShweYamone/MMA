package com.freelance.solutionhub.mma.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.FullScreenActivity;
import com.freelance.solutionhub.mma.activity.PMActivity;
import com.freelance.solutionhub.mma.delegate.FirstStepPMFragmentCallback;
import com.freelance.solutionhub.mma.model.PhotoModel;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder>{
    Context context;
    ArrayList<PhotoModel> singleRowArrayList;
    SQLiteDatabase db;
    public PhotoAdapter(Context context, ArrayList<PhotoModel> singleRowArrayList) {
        this.context = context;
        this.singleRowArrayList = singleRowArrayList;

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
        myViewHolder.newsImage.setImageBitmap(getBitmapFromEncodedString(singleRowArrayList.get(i).getImage()));

        myViewHolder.newsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullScreenActivity.class);

                Bundle extras = new Bundle();
                extras.putParcelable("imagebitmap", getBitmapFromEncodedString(singleRowArrayList.get(i).getImage()));
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });
        //  myViewHolder.id.setText(singleRowArrayList.get(i).uid);
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
                .setIcon(R.drawable.defaultimage)
                .setTitle("Delete result")
                .setMessage("Are you sure you want delete this result?")
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
