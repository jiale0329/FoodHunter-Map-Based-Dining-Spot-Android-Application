package com.example.testmap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SimilarityCalculationAdapter extends RecyclerView.Adapter<SimilarityCalculationAdapter.SimilarityCalculationHolder> {

    private List<DiningSpot> mDiningSpotList;
    private Context context;
    private Uri mImage;

    public SimilarityCalculationAdapter(List<DiningSpot>diningSpot, Uri image, Context c){
        mDiningSpotList = diningSpot;
        mImage = image;
        context = c;
    }

    @NonNull
    @Override
    public SimilarityCalculationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater
                .inflate(/*android.R.layout.simple_list_item_1*/
                        R.layout.list_item_similarity_calculation, parent, false);
        return new SimilarityCalculationAdapter.SimilarityCalculationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarityCalculationHolder holder, int position) {
        DiningSpot diningSpot = mDiningSpotList.get(position);

        try {
            holder.bindDiningSpot(diningSpot, position, holder.itemView);
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.itemView.setTag(diningSpot.getmId());
    }

    @Override
    public int getItemCount() { return mDiningSpotList.size();}

    public class SimilarityCalculationHolder extends RecyclerView.ViewHolder{

        public TextView mTvDiningSpotName, mTvSimilarityCalculation;
        private DiningSpot mDiningSpot, temp;
        private Bitmap bmpSelected, bmpRetrieved;
        private Mat srcSelected, srcRetrieved;
        StorageReference storageReference;

        public SimilarityCalculationHolder(@NonNull View itemView) {
            super(itemView);

            mTvDiningSpotName = itemView.findViewById(R.id.list_item_similarity_title);
            mTvSimilarityCalculation = itemView.findViewById(R.id.list_item_similarity_calculation);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ViewRestaurantProfile.class);
                    i.putExtra("restaurant_id", mDiningSpot.getmId());
                    context.startActivity(i);
                }
            });
        }

        public void bindDiningSpot(DiningSpot diningSpot, int position, View itemView) throws IOException {
            mDiningSpot = diningSpot;
            mTvDiningSpotName.setText(mDiningSpot.getmName());

            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + mDiningSpot.getmPictureUrl());
            try {
                File localfile = File.createTempFile("tempfile", ".jpg");
                ref.getFile(localfile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                bmpRetrieved = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "FAIL TO RETRIEVE", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            bmpSelected = MediaStore.Images.Media.getBitmap(context.getContentResolver(), mImage);
            Bitmap bmp = bmpSelected.copy(Bitmap.Config.ARGB_8888, true);
            srcSelected = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC2);
            Utils.bitmapToMat(bmp, srcSelected);

            new CountDownTimer(5000, 1000) {

                public void onTick(long millisUntilFinished) {
                    // You don't need anything here
                }

                public void onFinish() {
                    Bitmap bmp2 = bmpRetrieved.copy(Bitmap.Config.ARGB_8888, true);
                    srcRetrieved = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC2);
                    Utils.bitmapToMat(bmp2, srcRetrieved);

                    Mat hsvBase = new Mat(), hsvTest1 = new Mat();
                    Imgproc.cvtColor( srcSelected, hsvBase, Imgproc.COLOR_BGR2HSV );
                    Imgproc.cvtColor( srcRetrieved, hsvTest1, Imgproc.COLOR_BGR2HSV );

                    Mat histBase = new Mat();
                    Mat histTest1 = new Mat();

                    int hist_bins = 30;           //number of histogram bins
                    int hist_range[]= {0,180};//histogram range
                    MatOfFloat ranges = new MatOfFloat(0f, 256f);
                    MatOfInt histSize = new MatOfInt(25);

                    Imgproc.calcHist(Arrays.asList(srcSelected), new MatOfInt(0), new Mat(), histBase, histSize, ranges);
                    Imgproc.calcHist(Arrays.asList(srcRetrieved), new MatOfInt(0), new Mat(), histTest1, histSize, ranges);

                    double res = Imgproc.compareHist(histBase, histTest1, Imgproc.CV_COMP_CORREL);
                    String z = "" + res*100 + "%";
                    mTvSimilarityCalculation.setText(z);

                    if ((res*100) > 95){
                        temp = diningSpot;
                        mDiningSpotList.clear();
                        mDiningSpotList.add(diningSpot);
                        notifyDataSetChanged();
                    }
                }
            }.start();
        }
    }
}

