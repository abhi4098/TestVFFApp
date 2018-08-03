package com.valleyforge.cdi.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.generated.model.Plist;
import com.valleyforge.cdi.ui.activities.ActivePendingActivity;
import com.valleyforge.cdi.ui.activities.CompletedProjectsActivity;
import com.valleyforge.cdi.ui.activities.ProjectDetailActivity;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Abhinandan on 26/12/17.
 */

public class CompletedProjectListAdapter extends ArrayAdapter<Plist> {

    int groupid;
    ArrayList<Plist> projectList;
    CompletedProjectsActivity context;
    String generatedCode ;


    public CompletedProjectListAdapter(CompletedProjectsActivity activePending, int layout_my_invoices, int invoice_name, ArrayList<Plist> projectList)
    {
        super(activePending,layout_my_invoices,invoice_name,projectList);
        groupid=layout_my_invoices;
        this.context = activePending;
        this.projectList = projectList;

    }


    // Hold views of the ListView to improve its scrolling performance
    static class ViewHolder {
        public TextView projectName;
        public TextView projectId;
        public Button btnViewDetails;
        public TextView projectManager;


        }

    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.projectName= (TextView) rowView.findViewById(R.id.project_name);
            viewHolder.projectId= (TextView) rowView.findViewById(R.id.project_id);
            viewHolder.btnViewDetails= (Button) rowView.findViewById(R.id.view_details_button);
            viewHolder.projectManager= (TextView) rowView.findViewById(R.id.project_manager);



            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        final Plist plist = getItem(position);
        final ViewHolder holder = (ViewHolder) rowView.getTag();

        if (plist !=null) {
            holder.projectName.setText(plist.getPname());
            holder.projectId.setText(plist.getProjectId());
            holder.projectManager.setText(plist.getAssignPm());


            holder.btnViewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String itemID  = String.valueOf(plist.getId());
                    Intent i = new Intent(getContext(), ProjectDetailActivity.class);
                    i.putExtra("PROJECT_ID", itemID);
                    context.startActivity(i);
                }
            });





        }



        return rowView;
    }


}
