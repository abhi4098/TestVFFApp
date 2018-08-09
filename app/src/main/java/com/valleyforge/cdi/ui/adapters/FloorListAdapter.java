package com.valleyforge.cdi.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.api.ApiAdapter;
import com.valleyforge.cdi.api.RetrofitInterface;
import com.valleyforge.cdi.generated.model.DynamicallyAddedView;
import com.valleyforge.cdi.generated.model.FloorDetailsResponse;
import com.valleyforge.cdi.generated.model.Floorslist;
import com.valleyforge.cdi.generated.model.Plist;
import com.valleyforge.cdi.generated.model.Room;
import com.valleyforge.cdi.generated.model.RoomsListResponse;
import com.valleyforge.cdi.generated.model.Roomslist;
import com.valleyforge.cdi.generated.model.SkipResponse;
import com.valleyforge.cdi.ui.activities.CompletedProjectsActivity;
import com.valleyforge.cdi.ui.activities.MeasurementGridActivity;
import com.valleyforge.cdi.ui.activities.ProjectDetailActivity;
import com.valleyforge.cdi.utils.LoadingDialog;
import com.valleyforge.cdi.utils.NetworkUtils;
import com.valleyforge.cdi.utils.PrefUtils;
import com.valleyforge.cdi.utils.SnakBarUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.valleyforge.cdi.api.ApiEndPoints.BASE_URL;

/**
 * Created by Abhinandan on 26/12/17.
 */

public class FloorListAdapter extends ArrayAdapter<Floorslist> {
    private RetrofitInterface.UserRoomListClient UserRoomAdapter;
    private RetrofitInterface.AddRoomClient AddRoomAdapter;

    private RetrofitInterface.SkipFloorClient SkipFloorAdapter;


    int groupid;
    ArrayList<Floorslist> floorslist;
    MeasurementGridActivity context;
    String generatedCode ;
    ArrayList<Roomslist> alrooms;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    String roomName,numberOfWindows,roomDescription,floorSkipComment;






    public FloorListAdapter(MeasurementGridActivity measurementGridActivity, int layout_dyanmically_generated, int floor_name, ArrayList<Floorslist> floorslist)
    {
        super(measurementGridActivity,layout_dyanmically_generated,floor_name,floorslist);
        groupid=layout_dyanmically_generated;
        this.context = measurementGridActivity;
        this.floorslist = floorslist;

    }


    // Hold views of the ListView to improve its scrolling performance
    static class ViewHolder {
        public TextView floorName;
        public CardView cvAddRoom;
        public Button btnSelectRoom;
        public Button btnSkipFloor;
        public TextView tvRoomsCount;
        public TextView tvRoomsCompletedCount;



        }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.e("abhi", "getView: ........................................" );
        View rowView = convertView;

        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.floorName= (TextView) rowView.findViewById(R.id.floor_name);
            viewHolder.cvAddRoom = (CardView) rowView.findViewById(R.id.add_room_cardview);
           viewHolder.btnSelectRoom = (Button) rowView.findViewById(R.id.select_button);
            //viewHolder.btnSelectRoom = (Button) rowView.findViewById(R.id.select_button);
            viewHolder.tvRoomsCount= (TextView) rowView.findViewById(R.id.rooms_count);
            viewHolder.tvRoomsCompletedCount= (TextView) rowView.findViewById(R.id.rooms_completed_count);
            viewHolder.btnSkipFloor= (Button) rowView.findViewById(R.id.skip_floor_btn);




            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        final Floorslist floorslist = getItem(position);
        final ViewHolder holder = (ViewHolder) rowView.getTag();

        if (floorslist !=null) {
            holder.floorName.setText(floorslist.getFloor());
             holder.tvRoomsCount.setText(floorslist.getRoomscount());
            holder.tvRoomsCompletedCount.setText(floorslist.getCompletedroomscount());



            holder.cvAddRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.cvAddRoom.setCardBackgroundColor(Color.parseColor("#252525"));
                    addRoom(v,holder,floorslist.getId());
                }
            });


            holder.btnSelectRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //holder.cvAddRoom.setCardBackgroundColor(Color.parseColor("#252525"));

                    addRoomSelectPopUp(v,holder,floorslist.getId(),floorslist.getFloor());
                }
            });

            holder.btnSkipFloor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //holder.cvAddRoom.setCardBackgroundColor(Color.parseColor("#252525"));

                    skipFloorPopUp(v,holder,floorslist.getId(),floorslist.getFloor());
                }
            });



        }



        return rowView;
    }

    public  void skipFloorPopUp(View v, final ViewHolder holder, final int id, String floor)
    {

        LayoutInflater inflater = context.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_skip_floor_or_room, null);
        final EditText etComment = alertLayout.findViewById(R.id.comment);
        final Button btnAddComment = alertLayout.findViewById(R.id.add_comment_btn);
        final TextView tvPopUpHeader = alertLayout.findViewById(R.id.popup_header);

         tvPopUpHeader.setText(floor);

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
                floorSkipComment = etComment.getText().toString();
               if (!floorSkipComment.equals("")&& floorSkipComment != null) {
                   setUpRestAdapter();
                   skipFloor(v,holder,id,floorSkipComment);
                   dialog.cancel();
               }
               else {
                   etComment.setError("Please Enter Reason For Skipping");
               }
            }
        });
    }

    private void skipFloor(View v, ViewHolder holder, int id, String floorSkipComment) {
        Log.e("abhi", "setRoomList: ........................" +id );
        LoadingDialog.showLoadingDialog(context,"Loading...");
        Call<SkipResponse> call = SkipFloorAdapter.skipFloorData(id,floorSkipComment);
        if (NetworkUtils.isNetworkConnected(context)) {
            call.enqueue(new Callback<SkipResponse>() {

                @Override
                public void onResponse(Call<SkipResponse> call, Response<SkipResponse> response) {
                    if (response.isSuccessful()) {
                        if(response.body().getType() == 1) {

                            Toast.makeText(context,"Floor successfully skipped",Toast.LENGTH_SHORT).show();

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


    public  void addRoom(View v, final ViewHolder holder, final int id)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
            final Button addRoomBtn = alertLayout.findViewById(R.id.add_room_btn);

        final EditText etRoomName = alertLayout.findViewById(R.id.room_name);
        final EditText etNumberOfWindows = alertLayout.findViewById(R.id.number_of_windows);
        final EditText etRoomDescription = alertLayout.findViewById(R.id.room_description  );



            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
            // this is set the view from XML inside AlertDialog
            alert.setView(alertLayout);
            // disallow cancel of AlertDialog on click of back button and outside touch
            alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Cancel clicked", Toast.LENGTH_SHORT).show();
                holder.cvAddRoom.setCardBackgroundColor(Color.parseColor("#048700"));

            }
        });


            final AlertDialog dialog = alert.create();
            dialog.show();
            dialog.getWindow().setLayout(1000, 550);
            addRoomBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.cvAddRoom.setCardBackgroundColor(Color.parseColor("#048700"));
                    roomName =  etRoomName.getText().toString();
                    roomDescription = etRoomDescription.getText().toString();
                    numberOfWindows = etNumberOfWindows.getText().toString();
                    setUpRestAdapter();
                    if ((roomName != null && !roomName.equals(""))&&(numberOfWindows != null && !numberOfWindows.equals(""))) {
                        addRoomDetails(roomName, roomDescription, numberOfWindows, v, holder, id);
                        dialog.cancel();
                    }
                    else if (roomName == null || roomName.equals(""))
                    {
                        etRoomName.setError("Please Add Room Name");

                    }
                    else
                    {
                        etNumberOfWindows.setError("Please Add Windows Count");

                    }
                }
            });
        }

    private void addRoomDetails(String roomName, String roomDescription, String numberOfWindows, View v, ViewHolder holder, int id) {

        LoadingDialog.showLoadingDialog(context,"Loading...");
        Call<RoomsListResponse> call = AddRoomAdapter.addRoomDataData(roomName,id,numberOfWindows,0,roomDescription);
        if (NetworkUtils.isNetworkConnected(context)) {
            call.enqueue(new Callback<RoomsListResponse>() {

                @Override
                public void onResponse(Call<RoomsListResponse> call, Response<RoomsListResponse> response) {
                    if (response.isSuccessful()) {
                        if(response.body().getType().equals("1")) {
                            Log.e("abhi", "onResponse: success///////////////////////" );


                            Toast.makeText(context,"Add Room request send Successfully",Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(context,response.body().getMsg(),Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();

                    }
                }

                @Override
                public void onFailure(Call<RoomsListResponse> call, Throwable t) {
                    Log.e("abhi", "onResponse: error....................... " +t.getMessage()  );

                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            SnakBarUtils.networkConnected(context);
        }
    }



    private void setUpRestAdapter() {
        UserRoomAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.UserRoomListClient.class, BASE_URL, context);
        AddRoomAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.AddRoomClient.class, BASE_URL, context);
        SkipFloorAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.SkipFloorClient.class, BASE_URL, context);
    }


    public  void addRoomSelectPopUp(View v, final ViewHolder holder, int id, String floor)
    {

        LayoutInflater inflater = context.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_dialog_for_selecting_room, null);
        final TextView tvPopUpHeader = alertLayout.findViewById(R.id.popup_header);

        tvPopUpHeader.setText(floor);
        mRecyclerView= alertLayout.findViewById(R.id.recycler_view);
        setUpRestAdapter();
        setRoomList(id);


       /* final EditText etUsername = alertLayout.findViewById(R.id.et_username);
        final EditText etEmail = alertLayout.findViewById(R.id.et_email);
        final CheckBox cbToggle = alertLayout.findViewById(R.id.cb_show_pass);*/

       /* cbToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // to encode password in dots
                    etEmail.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // to display the password in normal text
                    etEmail.setTransformationMethod(null);
                }
            }
        });*/

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

      /*  alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //String user = etUsername.getText().toString();
               // String pass = etEmail.getText().toString();
               // Toast.makeText(getBaseContext(), "Username: " + user + " Email: " + pass, Toast.LENGTH_SHORT).show();
            }
        });*/
        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.getWindow().setLayout(1000, 500);
       /* addRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cvAddRoom.setCardBackgroundColor(Color.parseColor("#048700"));
                dialog.cancel();
            }
        });*/
    }

    private void setRoomList(int id) {
        Log.e("abhi", "setRoomList: ........................" +id );
        LoadingDialog.showLoadingDialog(context,"Loading...");
        Call<RoomsListResponse> call = UserRoomAdapter.userRoomListData(id);
        if (NetworkUtils.isNetworkConnected(context)) {
            call.enqueue(new Callback<RoomsListResponse>() {

                @Override
                public void onResponse(Call<RoomsListResponse> call, Response<RoomsListResponse> response) {
                    if (response.isSuccessful()) {
                        if(response.body().getType().equals("1")) {
                            Log.e("abhi", "onResponse: ....................."+response.body().getRoomslist().size() );
                            alrooms = new ArrayList<>();
                            for (int i=0; i<response.body().getRoomslist().size(); i++) {


                                Roomslist roomslist  = new Roomslist();
                                roomslist.setNoOfWindows(response.body().getRoomslist().get(i).getNoOfWindows());
                                roomslist.setRoomName(response.body().getRoomslist().get(i).getRoomName());
                                roomslist.setRoomStatus(response.body().getRoomslist().get(i).getRoomStatus());
                                roomslist.setFloorPlanId(response.body().getRoomslist().get(i).getFloorPlanId());
                                roomslist.setId(response.body().getRoomslist().get(i).getId());


                                alrooms.add(roomslist);


                                }

                            mRecyclerView.setHasFixedSize(true);
                            mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                            mRecyclerView.setLayoutManager(mLayoutManager);

                            mAdapter = new HLVAdapter(context, alrooms);
                            mRecyclerView.setAdapter(mAdapter);

                            Toast.makeText(context,response.body().getMsg(),Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(context,response.body().getMsg(),Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();

                    }
                }

                @Override
                public void onFailure(Call<RoomsListResponse> call, Throwable t) {
                    Log.e("abhi", "onResponse: error....................... "  );

                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            SnakBarUtils.networkConnected(context);
        }
    }
    }





