package com.valleyforge.cdi.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.valleyforge.cdi.R;
import com.valleyforge.cdi.generated.model.Plist;
import com.valleyforge.cdi.generated.tables.PListTable;
import com.valleyforge.cdi.ui.activities.ActivePendingActivity;
import com.valleyforge.cdi.ui.activities.BLEInformationActivity;
import com.valleyforge.cdi.ui.activities.CompletedProjectsActivity;
import com.valleyforge.cdi.ui.activities.MeasurementGridActivity;
import com.valleyforge.cdi.ui.activities.NavigationActivity;
import com.valleyforge.cdi.ui.activities.ProfileActivity;
import com.valleyforge.cdi.ui.activities.ProjectDetailActivity;
import com.valleyforge.cdi.ui.activities.ProjectSummaryActivity;
import com.valleyforge.cdi.ui.activities.SearchDevicesActivity;
import com.valleyforge.cdi.utils.PrefUtils;


import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;



public class ProjectListAdapter extends ArrayAdapter<PListTable> {

    int groupid;
    ArrayList<PListTable> projectList;
    ActivePendingActivity context;
    String generatedCode ;


    public ProjectListAdapter(ActivePendingActivity activePending, int layout_my_invoices, int invoice_name, ArrayList<PListTable> projectList)
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
        public ImageView ivPopUpMenu;
        public Button btnViewDetails;
        public Button btnContinueMeasurement;
        public Button btnCall;
        public ProgressBar pbPercentage;
        public TextView tvPercentageText;

        }

    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.projectName= (TextView) rowView.findViewById(R.id.project_name);
            viewHolder.projectId= (TextView) rowView.findViewById(R.id.project_id);
            viewHolder.ivPopUpMenu= (ImageView) rowView.findViewById(R.id.popup_menu_icon);
            viewHolder.btnViewDetails= (Button) rowView.findViewById(R.id.view_details_button);
            viewHolder.btnContinueMeasurement= (Button) rowView.findViewById(R.id.continue_measurement_button);
            viewHolder.btnCall= (Button) rowView.findViewById(R.id.call_button);
            viewHolder.pbPercentage= (ProgressBar) rowView.findViewById(R.id.progressBar4);
            viewHolder.tvPercentageText= (TextView) rowView.findViewById(R.id.percentage_text);



            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        final PListTable plist = getItem(position);
        final ViewHolder holder = (ViewHolder) rowView.getTag();

        if (plist !=null) {
            holder.projectName.setText(plist.pname);
            holder.projectId.setText(plist.projectId );
            holder.tvPercentageText.setText(plist.projectStatus);

            holder.pbPercentage.setProgress(Integer.parseInt(plist.projectPercentage));
            if (plist.projectStatus.equals("Submitted for Review"))
            {
                holder.btnContinueMeasurement.setText("Project Summary");
                holder.btnContinueMeasurement.setVisibility(View.GONE);

            }
            else
            {
                holder.btnContinueMeasurement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String itemID  = String.valueOf(plist.p_id);
                        PrefUtils.storeProjectId(itemID,context);
                        Intent i = new Intent(context, MeasurementGridActivity.class);
                        // i.putExtra("PROJECT_ID", itemID);
                        context.startActivity(i);
                    }
                });
            }

            holder.ivPopUpMenu.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    String itemID = String.valueOf(plist.p_id);

                    Context wrapper = new ContextThemeWrapper(getContext(), R.style.popupMenuStyle);
                    PopupMenu popup = new PopupMenu(wrapper, holder.ivPopUpMenu, Gravity.RIGHT);
                    try {
                        Field[] fields = popup.getClass().getDeclaredFields();
                        for (Field field : fields) {
                            if ("mPopup".equals(field.getName())) {
                                field.setAccessible(true);
                                Object menuPopupHelper = field.get(popup);
                                Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                                Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                                setForceIcons.invoke(menuPopupHelper, true);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //Inflating the Popup using xml file
                    popup.getMenuInflater()
                            .inflate(R.menu.popup_menu, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(
                                    getContext(),
                                    "You Clicked : " + item.getTitle(),
                                    Toast.LENGTH_SHORT
                            ).show();
                            return true;
                        }
                    });

                    popup.show(); //showing popup menu
                }

            });

            holder.btnViewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String itemID  = String.valueOf(plist.p_id);
                    PrefUtils.storeProjectId(itemID,context);
                    Intent i = new Intent(getContext(), ProjectDetailActivity.class);
                    i.putExtra("PROJECT_ID", itemID);
                    context.startActivity(i);
                }
            });





        }



        return rowView;
    }


}
