package com.valleyforge.cdi.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.valleyforge.cdi.R;
import com.valleyforge.cdi.generated.model.Plist;
import com.valleyforge.cdi.ui.activities.ActivePendingActivity;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

/**
 * Created by Abhinandan on 26/12/17.
 */

public class ProjectListAdapter extends ArrayAdapter<Plist> {

    int groupid;
    ArrayList<Plist> projectList;
    Context context;
    String generatedCode ;

    public ProjectListAdapter(ActivePendingActivity activePendingActivityActivity, int layout_my_invoices, int invoice_name, ArrayList<Plist> projectList)
    {
        super(activePendingActivityActivity,layout_my_invoices,invoice_name,projectList);
        groupid=layout_my_invoices;
        this.context = activePendingActivityActivity;
        this.projectList = projectList;

    }


    // Hold views of the ListView to improve its scrolling performance
    static class ViewHolder {
        public TextView projectName;
        public TextView projectId;





    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.projectName= (TextView) rowView.findViewById(R.id.project_name);
            viewHolder.projectId= (TextView) rowView.findViewById(R.id.project_id);


            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        final Plist plist = getItem(position);
        final ViewHolder holder = (ViewHolder) rowView.getTag();

        if (plist !=null) {
            holder.projectName.setText(plist.getPname());
            holder.projectId.setText(plist.getProjectId());

           /* holder.invoiceName.setText(invoice.getCustomerName());
            holder.invoiceNum.setText(invoice.getInvoicenumber());
            holder.invoicePrice.setText(invoice.getAmount());
            holder.invoiceUpdateDate.setText(invoice.getUpdatedDate());*/
         /*   holder.generateCodeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    String itemId = invoice.getId();
                    String customerEmailId = PrefUtils.getEmail(getContext());
                    String action = "invoice";
                    String url1 = "https://www.payworks.bs/processor.html?product=";
                    String url2 = "&member=";
                    String url3 = "&action=";
                    String url4 = "&send=yes";
                    String baseUrl = url1.concat(itemId).concat(url2).concat(customerEmailId).concat(url3).concat(action).concat(url4);
                    OkHttpClient client = new OkHttpClient();
                    String urlTemplate = "http://tinyurl.com/api-create.php?url=%s";
                    String uri = String.format(urlTemplate, URLEncoder.encode(baseUrl));
                    Request request = new Request.Builder()
                            .url(uri)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                generatedCode = response.body().string();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        openDialog(generatedCode,view,invoice.getCustomerName());
                                    }
                                });


                            }
                            else
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(),"Error encountered", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    });



                }
            });*/




        }



        return rowView;
    }

   /* private void openDialog(final String generatedCode, View view, String name) {
        LayoutInflater li = LayoutInflater.from((Activity) view.getContext());
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                (Activity) view.getContext());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText ettinyUrl = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        final TextView entitiyName = (TextView) promptsView
                .findViewById(R.id.tv_Name);

        final TextView nameTag = (TextView) promptsView
                .findViewById(R.id.name_tag);

        nameTag.setText("Customer Name: ");
        entitiyName.setText(name);
        ettinyUrl.setText(generatedCode);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Share",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_TEXT, generatedCode);
                                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this invoice link!");
                                getContext().startActivity(Intent.createChooser(intent, "Share"));
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#1ea9e1"));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1ea9e1"));
    }*/


}
