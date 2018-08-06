package com.valleyforge.cdi.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.ui.activities.BLEInformationActivity;
import com.valleyforge.cdi.ui.activities.CompletedProjectsActivity;
import com.valleyforge.cdi.ui.activities.NavigationActivity;
import com.valleyforge.cdi.ui.activities.SearchDevicesActivity;

import java.util.ArrayList;

public class HLVAdapter extends RecyclerView.Adapter<HLVAdapter.ViewHolder>  {

    ArrayList<String> alName;
    Context context;

    public HLVAdapter(Context context, ArrayList<String> alName) {
        super();
        this.context = context;
        this.alName = alName;

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
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Log.e("abhi", "onBindViewHolder:............................................. " );
        viewHolder.test.setText(alName.get(i));
        viewHolder.cvHorizontalRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BLEInformationActivity.class);
               context.startActivity(i);
            }
        });

        /*viewHolder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    Toast.makeText(context, "#" + position + " - " + alName.get(position) + " (Long click)", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, MainActivity.class));
                } else {
                    Toast.makeText(context, "#" + position + " - " + alName.get(position), Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return alName.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {


        public TextView test;
        public CardView cvHorizontalRoom;
       // private ItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            test = (TextView) itemView.findViewById(R.id.test);
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
