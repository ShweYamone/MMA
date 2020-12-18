package com.freelance.solutionhub.mma.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.delegate.FirstStepPMFragmentCallback;
import com.freelance.solutionhub.mma.model.CheckListModel;
import com.freelance.solutionhub.mma.model.Event;
import com.freelance.solutionhub.mma.model.PhotoModel;

import java.util.ArrayList;

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.MyViewHolder>{
    Context context;

    ArrayList<CheckListModel> checkListModels;
    ArrayList<Event> checkListEvent;
    SQLiteDatabase db;
    public CheckListAdapter(Context context, ArrayList<CheckListModel> checkListModels) {
        this.context = context;
        this.checkListModels = checkListModels;
        checkListEvent = new ArrayList<>();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.check_list_item, null);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        CheckListModel checkListModel = checkListModels.get(i);
        Log.i("ArrayList",i+":");
        myViewHolder.bindView(checkListModel);
        checkListEvent.add(i*2,new Event("PM_CHECK_LIST_DONE",checkListModel.getId()+"",myViewHolder.checkList.isChecked()+""));
        checkListEvent.add((i*2)+1 ,  new Event("PM_CHECK_LIST_REMARK",checkListModel.getId()+""," "));
        myViewHolder.checkList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeValue(checkListModel.getId(),i,isChecked+"",true);
            }
        });
        myViewHolder.checkListValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    changeValue(checkListModel.getId(),i,s.toString(),false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
//        myViewHolder.delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deletedata(i,singleRowArrayList);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return checkListModels.size();
    }

    private void changeValue(int id, int position,String value,boolean checkBox){

        if(checkBox){
            checkListEvent.remove(position*2);
            checkListEvent.add((position*2), new Event("PM_CHECK_LIST_DONE",id+"",value));
        }else {
            checkListEvent.remove((position*2)+1);
            checkListEvent.add((position*2)+1, new Event("PM_CHECK_LIST_REMARK",id+"",value));
        }
    }
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public ArrayList<Event> getCheckListEvent(){
       return checkListEvent;
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
        }
    }
}
