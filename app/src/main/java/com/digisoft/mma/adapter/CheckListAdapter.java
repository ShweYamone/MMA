package com.digisoft.mma.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.digisoft.mma.DB.InitializeDatabase;
import com.digisoft.mma.R;
import com.digisoft.mma.model.CheckListModel;

import java.util.ArrayList;

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.MyViewHolder>{
    Context context;

    ArrayList<CheckListModel> checkListModels;
    InitializeDatabase dbHelper;

    public CheckListAdapter(Context context, ArrayList<CheckListModel> checkListModels, InitializeDatabase dbHelper) {
        this.context = context;
        this.checkListModels = checkListModels;
        this.dbHelper = dbHelper;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.check_list_item, null);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {
        CheckListModel checkListModel = checkListModels.get(position);
        Log.i("ArrayList",position+":");
        myViewHolder.bindView(checkListModel);
        myViewHolder.checkList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkListModel.setMaintenanceDone(isChecked);
            }
        });
        myViewHolder.checkListValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //We will get data after text change, ignore this
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //We will get data after text change, ignore this
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    checkListModel.setMaintenanceRemark(s.toString());
                    Glide.with(context).load(R.drawable.ic_remark).into(myViewHolder.edit);
                } else {
                    Glide.with(context).load(R.drawable.ic_remark_blank).into(myViewHolder.edit);
                }

               // Toast.makeText(context, checkListModel.getId() + "" , Toast.LENGTH_SHORT).show();
            }
        });
        myViewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myViewHolder.linearLayout.getVisibility() == View.GONE){
                    myViewHolder.linearLayout.setVisibility(View.VISIBLE);
                }else {
                    myViewHolder.linearLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return checkListModels.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView checkListDesc;
        ImageView edit;
        CheckBox checkList;
        EditText checkListValue;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
             checkListDesc = itemView.findViewById(R.id.tv_check_list_desc);
             checkList = itemView.findViewById(R.id.cb_check);
             edit = itemView.findViewById(R.id.iv_edit);
             checkListValue = itemView.findViewById(R.id.et_remarks);
             linearLayout = itemView.findViewById(R.id.ll_whole_card_design);
        }

        public void bindView(CheckListModel checkListModel){
            checkListDesc.setText(checkListModel.getCheckDescription());
            checkListValue.setText(checkListModel.getMaintenanceRemark());
            if (checkListModel.isMaintenanceDone()) {
                checkList.setChecked(true);
            } else {
                checkList.setChecked(false);
            }
            if (!checkListValue.getText().toString().equals("")) {
                Glide.with(context).load(R.drawable.ic_remark).into(edit);
            } else {
                Glide.with(context).load(R.drawable.ic_remark_blank).into(edit);
            }
        }
    }
}
