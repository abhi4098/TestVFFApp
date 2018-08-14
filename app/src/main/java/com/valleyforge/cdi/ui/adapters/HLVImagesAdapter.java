package com.valleyforge.cdi.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.valleyforge.cdi.R;
import com.valleyforge.cdi.api.ApiAdapter;
import com.valleyforge.cdi.api.RetrofitInterface;
import com.valleyforge.cdi.generated.model.ImageList;
import com.valleyforge.cdi.generated.model.Roomslist;
import com.valleyforge.cdi.generated.model.SkipResponse;
import com.valleyforge.cdi.generated.model.Windowslist;
import com.valleyforge.cdi.ui.activities.BLEInformationActivity;
import com.valleyforge.cdi.ui.activities.MeasurementGridActivity;
import com.valleyforge.cdi.utils.LoadingDialog;
import com.valleyforge.cdi.utils.NetworkUtils;
import com.valleyforge.cdi.utils.SnakBarUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.valleyforge.cdi.api.ApiEndPoints.BASE_URL;

public class HLVImagesAdapter extends RecyclerView.Adapter<HLVImagesAdapter.ViewHolder>  {
    BLEInformationActivity context;

    LayoutInflater inflater;

    ArrayList<ImageList> alImageList;
    String selectedImageType;


  //  private RetrofitInterface.SkipRoomClient SkipRoomAdapter;


    public HLVImagesAdapter(Context Context, ArrayList<ImageList> alImageList, String selectedImageType) {
        super();
        this.context = (BLEInformationActivity) Context;
        this.alImageList = alImageList;
        this.selectedImageType = selectedImageType;


    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.e("abhi", "onCreateViewHolder:>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. "  );
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.horizontal_images_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        Log.e("abhi", "onBindViewHolder:......................lis separator " +alImageList.get(i).getimageType() + "selectedImageType  " +selectedImageType );

           // viewHolder.tvRoomName.setText(alImageList.get(i).getimageType());
            Glide.with(context)
                    .load(alImageList.get(i).getimageUrl()) // image url
                    .placeholder(R.drawable.add_image) // any placeholder to load at start
                    .error(R.drawable.add_image)  // any image in case of error
                    .into(viewHolder.ivImageUploaded);






          /*  viewHolder.cvHorizontalRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context1 != null){
                        ((BLEInformationActivity)context1).measurementScreen(v,alWindows.get(i).getWindow());
                    }


                }
            });*/

        }







    @Override
    public int getItemCount() {

        return alImageList.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {


        public ImageView ivImageUploaded;


       // private ItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            ivImageUploaded = (ImageView) itemView.findViewById(R.id.ll_image_view);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }

       /* public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getPosition(), true);
            return true;
        }*/
    }

}
