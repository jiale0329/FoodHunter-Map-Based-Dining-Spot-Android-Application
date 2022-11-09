package com.example.testmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class BillRecordAdapter extends RecyclerView.Adapter<BillRecordAdapter.BillRecordHolder> {

    private List<BillRecord> mBillRecord;
    private Context context;

    public BillRecordAdapter(List<BillRecord>billRecord, Context c){
        mBillRecord = billRecord;
        context = c;
    }

    @NonNull
    @Override
    public BillRecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater
                .inflate(/*android.R.layout.simple_list_item_1*/
                        R.layout.list_item_bill_record, parent, false);
        return new BillRecordAdapter.BillRecordHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillRecordHolder holder, int position) {
        BillRecord billRecord = mBillRecord.get(position);
        holder.bindBillRecord(billRecord);
    }

    @Override
    public int getItemCount() { return mBillRecord.size();}

    public class BillRecordHolder extends RecyclerView.ViewHolder{

        public TextView mTvBillRecordTitle, mTvBillRecordDate, mTvBillRecordAmount;
        private BillRecord mBillRecord;

        public BillRecordHolder(@NonNull View itemView) {
            super(itemView);

            mTvBillRecordTitle = itemView.findViewById(R.id.list_item_bill_record_title);
            mTvBillRecordDate = itemView.findViewById(R.id.list_item_bill_record_dateTime);
            mTvBillRecordAmount = itemView.findViewById(R.id.list_item_bill_record_amount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ViewBillRecordDetails.class);
                    i.putExtra("billRecord_id", mBillRecord.getBillRecordId());
                    context.startActivity(i);
                    ((Activity)context).finish();
                }
            });
        }

        public void bindBillRecord(BillRecord billRecord){
            DecimalFormat df = new DecimalFormat("0.00");
            mBillRecord = billRecord;
            mTvBillRecordTitle.setText(mBillRecord.getBillRecordTitle());
            mTvBillRecordDate.setText(mBillRecord.getBillDate());
            mTvBillRecordAmount.setText("RM" + df.format(mBillRecord.getTotalAmount()));
        }
    }
}
