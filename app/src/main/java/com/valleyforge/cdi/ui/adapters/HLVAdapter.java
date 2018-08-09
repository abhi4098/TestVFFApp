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
import android.widget.TextView;
import android.widget.Toast;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.api.ApiAdapter;
import com.valleyforge.cdi.api.RetrofitInterface;
import com.valleyforge.cdi.generated.model.Room;
import com.valleyforge.cdi.generated.model.Roomslist;
import com.valleyforge.cdi.generated.model.SkipResponse;
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

public class HLVAdapter extends RecyclerView.Adapter<HLVAdapter.ViewHolder>  {
    MeasurementGridActivity context;
    LayoutInflater inflater;
    int GroupPosition,ListPosition;
    ArrayList<Roomslist> alRooms;
    private RetrofitInterface.SkipRoomClient SkipRoomAdapter;
    String roomSkipComment ;


    public HLVAdapter(Context Context, ArrayList<Roomslist> alRooms) {
        super();
       this.context = (MeasurementGridActivity) Context;
        this.alRooms = alRooms;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.e("abhi", "onCreateViewHolder:>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. "  );
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.horizontal_list_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        Log.e("abhi", "onBindViewHolder:............................................. " +alRooms.get(i).getRoomName());
        viewHolder.tvRoomName.setText(alRooms.get(i).getRoomName());
        viewHolder.tvWindowsCount.setText(alRooms.get(i).getNoOfWindows());
        if (alRooms.get(i).getRoomStatus() == 1)
        {
            viewHolder.cvHorizontalRoom.setCardBackgroundColor(Color.parseColor("#048700"));
        }
        else
        {
            viewHolder.cvHorizontalRoom.setCardBackgroundColor(Color.parseColor("#252525"));

        }
        viewHolder.cvHorizontalRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("abhi", "onClick: onbind............................................" +alRooms.get(i).getRoomName() );

                skipPopUp(v,viewHolder,alRooms.get(i).getFloorPlanId(),alRooms.get(i).getId(),alRooms.get(i).getRoomName());


            }
        });


    }

    private void skipPopUp(View v, final ViewHolder viewHolder, final Integer floorPlanId, final Integer id, final String roomName) {
        LayoutInflater inflater = context.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_skip_room, null);
        final Button measureBtn = alertLayout.findViewById(R.id.measure_btn);
        final Button skipBtn = alertLayout.findViewById(R.id.skip_button);
        final TextView tvPopUpHeader = alertLayout.findViewById(R.id.popup_header);

        tvPopUpHeader.setText(roomName);



        Log.e("abhi", "skipPopUp: .........floorplanid" + floorPlanId + "roomId" + id  );

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Cancel clicked", Toast.LENGTH_SHORT).show();
               // holder.cvAddRoom.setCardBackgroundColor(Color.parseColor("#048700"));

            }
        });

        /*alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //String user = etUsername.getText().toString();
               // String pass = etEmail.getText().toString();
               // Toast.makeText(getBaseContext(), "Username: " + user + " Email: " + pass, Toast.LENGTH_SHORT).show();
            }
        });*/
        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.getWindow().setLayout(450, 320);
        measureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BLEInformationActivity.class);
                context.startActivity(i);
                dialog.cancel();
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(context, BLEInformationActivity.class);
                context.startActivity(i);*/
                dialog.cancel();
                addSkipRoomPopUp(v,viewHolder,floorPlanId,id,roomName);
            }
        });
    }

    private void addSkipRoomPopUp(View v, final ViewHolder viewHolder, final Integer floorPlanId, final Integer id, String roomName) {
        LayoutInflater inflater = context.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_skip_floor_or_room, null);
        final EditText etComment = alertLayout.findViewById(R.id.comment);
        final Button btnAddComment = alertLayout.findViewById(R.id.add_comment_btn);
        final TextView tvPopUpHeader = alertLayout.findViewById(R.id.popup_header);

        tvPopUpHeader.setText(roomName);



        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });


        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.getWindow().setLayout(800, 450);
        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomSkipComment = etComment.getText().toString();
                if (!roomSkipComment.equals("")&& roomSkipComment != null) {
                    setUpRestAdapter();
                    skipRoom(v,viewHolder,id,roomSkipComment,floorPlanId);
                    dialog.cancel();
                }
                else {
                    etComment.setError("Please Enter Reason For Skipping");
                }
            }
        });
    }


    private void setUpRestAdapter() {

        SkipRoomAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.SkipRoomClient.class, BASE_URL, context);
    }
    private void skipRoom(View v, ViewHolder viewHolder, Integer id, String roomSkipComment, Integer floorPlanId) {

        Log.e("abhi", "setRoomList: ........................" +id );
        LoadingDialog.showLoadingDialog(context,"Loading...");
        Call<SkipResponse> call = SkipRoomAdapter.skipRoomData(floorPlanId,roomSkipComment,id);
        if (NetworkUtils.isNetworkConnected(context)) {
            call.enqueue(new Callback<SkipResponse>() {

                @Override
                public void onResponse(Call<SkipResponse> call, Response<SkipResponse> response) {
                    if (response.isSuccessful()) {
                        if(response.body().getType() == 1) {


                            Toast.makeText(context,"Room Successfully skipped",Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(context,response.body().getMsg(),Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();

                    }
                }

                @Override
                public void onFailure(Call<SkipResponse> call, Throwable t) {
                    Log.e("abhi", "onResponse: error....................... "  );

                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            SnakBarUtils.networkConnected(context);
        }
    }



    @Override
    public int getItemCount() {
        return alRooms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {


        public TextView tvRoomName;
        public TextView tvWindowsCount;
        public CardView cvHorizontalRoom;


       // private ItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            tvRoomName = (TextView) itemView.findViewById(R.id.room_name);
            tvWindowsCount = (TextView) itemView.findViewById(R.id.window_count);
            cvHorizontalRoom = (CardView) itemView.findViewById(R.id.room_cardview);

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
