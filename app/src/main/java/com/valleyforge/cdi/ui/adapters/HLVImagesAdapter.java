package com.valleyforge.cdi.ui.adapters;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.valleyforge.cdi.R;
import com.valleyforge.cdi.api.ApiAdapter;
import com.valleyforge.cdi.api.RetrofitInterface;
import com.valleyforge.cdi.generated.model.ImageList;
import com.valleyforge.cdi.generated.model.LoginResponse;
import com.valleyforge.cdi.ui.activities.BLEInformationActivity;
import com.valleyforge.cdi.utils.LoadingDialog;
import com.valleyforge.cdi.utils.NetworkUtils;
import com.valleyforge.cdi.utils.PrefUtils;
import com.valleyforge.cdi.utils.SnakBarUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.valleyforge.cdi.api.ApiEndPoints.BASE_URL;

public class HLVImagesAdapter extends RecyclerView.Adapter<HLVImagesAdapter.ViewHolder>  {
    BLEInformationActivity context;
    private RetrofitInterface.DeletePhotoClient DeletePhotoAdapter;


    LayoutInflater inflater;

    ArrayList<ImageList> alImageList;
    ArrayList<ImageList> alImageListToShow;
    String selectedImageType,windowId;


  //  private RetrofitInterface.SkipRoomClient SkipRoomAdapter;


    public HLVImagesAdapter(Context Context, ArrayList<ImageList> alImageList, String selectedImageType, String windowId) {
        super();
        this.context = (BLEInformationActivity) Context;
        this.alImageList = alImageList;
        this.selectedImageType = selectedImageType;
        this.windowId = windowId;


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
            viewHolder.ivCancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alImageList.get(i).getimageType();
                    alImageList.get(i).getImageId();
                    Log.e("abhi", "onClick: ................"+ alImageList.get(i).getImageId() );
                    setUpRestAdapter();
                    deletePhoto(alImageList.get(i).getimageType(),alImageList.get(i).getImageId());
                    ((BLEInformationActivity)context).updateCombinedImageListFromAdapter(alImageList.get(i).getImageId());
                    alImageList.remove(i);

                    notifyDataSetChanged();
                }
            });




        }

    private void deletePhoto(String imageType, String imageId) {
        LoadingDialog.showLoadingDialog(context,"Loading...");
        Log.e("abhi", "deletePhoto: .....................................imagetype " +imageType + " imageId" +imageId + " windowid" +windowId);
        Call<LoginResponse> call = DeletePhotoAdapter.deleteImageData(imageId,windowId,imageType);
        if (NetworkUtils.isNetworkConnected(context)) {
            call.enqueue(new Callback<LoginResponse>() {

                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        if(response.body().getType() == 1) {

                           /* for (int i=0; i<response.body().getData().size(); i++) {
                                Log.e("abhi.........      ", "onResponse: "+response.body().getData().get(i).getName() );
                                etUsername.setText(response.body().getData().get(i).getName());
                                etuserEmail.setText(response.body().getData().get(i).getEmail());
                                etUserAdd.setText(response.body().getData().get(i).getAddress());
                                etuserPhone.setText(response.body().getData().get(i).getPhone());
                            }
*/
                            Toast.makeText(context,response.body().getMsg(),Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(context,response.body().getMsg(),Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();

                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.e("abhi", "onResponse: error....................... "  );

                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            SnakBarUtils.networkConnected(context);
            LoadingDialog.cancelLoading();
        }
    }

    private void setUpRestAdapter() {
        DeletePhotoAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.DeletePhotoClient.class, BASE_URL, context);

    }




    @Override
    public int getItemCount() {

        return alImageList.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {


        public ImageView ivImageUploaded;
        public ImageView ivCancelBtn;


       // private ItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            ivImageUploaded = (ImageView) itemView.findViewById(R.id.ll_image_view);
            ivCancelBtn = (ImageView) itemView.findViewById(R.id.cancel_button);

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
