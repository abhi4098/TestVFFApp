package com.valleyforge.cdi.ui.services;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.valleyforge.cdi.api.ApiAdapter;
import com.valleyforge.cdi.api.RetrofitInterface;
import com.valleyforge.cdi.generated.model.MeasurementResponse;
import com.valleyforge.cdi.generated.tables.MeasurementDetailTable;
import com.valleyforge.cdi.generated.tables.PListTable;
import com.valleyforge.cdi.ui.activities.BLEInformationActivity;
import com.valleyforge.cdi.utils.LoadingDialog;
import com.valleyforge.cdi.utils.NetworkUtils;
import com.valleyforge.cdi.utils.PrefUtils;
import com.valleyforge.cdi.utils.SnakBarUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.valleyforge.cdi.api.ApiEndPoints.BASE_URL;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {
    private static final String TAG = MyJobService.class.getSimpleName();
    boolean isWorking = false;
    boolean jobCancelled = false;
    private RetrofitInterface.MeasurementDataClient MeasurementAdapter;
    String wallWidth, widthLeftOfWindow, ibWidthOfWindow, ibLengthOfWindow, widthRightOfWindow, lengthCielFlr, pocketDepth, carpetInst, additionalData;
    String windowName, windowDescription,windowCompletionStatus,windowApprovalCheck,windowId;
    String floorPlanId, roomId;
    String jobId;




    // Called by the Android system when it's time to run the job
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d("abhi", "Job started!...." +jobParameters.getJobId());
        jobId = String.valueOf(jobParameters.getJobId());
        isWorking = true;
        // We need 'jobParameters' so we can call 'jobFinished'
        startWorkOnNewThread(jobParameters); // Services do NOT run on a separate thread

        return isWorking;
    }

    private void startWorkOnNewThread(final JobParameters jobParameters) {
        new Thread(new Runnable() {
            public void run() {
                doWork(jobParameters);
            }
        }).start();
    }


    private void setUpRestAdapter() {

        MeasurementAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.MeasurementDataClient.class, BASE_URL, this);

    }

    public static List<MeasurementDetailTable> getAll() {
        return new Select()
                .from(MeasurementDetailTable.class)
                .execute();
    }


    public void showProjectList()
    {
        List<MeasurementDetailTable> measurementDetailTable = getAll();
        Log.e("abhi", "showProjectList:...measurementDetailTables "+ measurementDetailTable.size() );
        //Adding all the items of the inventories to arraylist
        for (int i = 0; i < measurementDetailTable.size(); i++) {
           if (measurementDetailTable.get(i).windowId.equals(jobId))
           {
               floorPlanId = measurementDetailTable.get(i).floorPlanId ;
               roomId = measurementDetailTable.get(i).roomId ;
               windowName = measurementDetailTable.get(i).windowName ;
               wallWidth = measurementDetailTable.get(i).wallWidth ;

               widthLeftOfWindow =  measurementDetailTable.get(i).widthLeftWindow;
               ibWidthOfWindow =  measurementDetailTable.get(i).ibWidthWindow;
               ibLengthOfWindow = measurementDetailTable.get(i).ibLengthWindow ;
               widthRightOfWindow = measurementDetailTable.get(i).widthRightWindow ;
               lengthCielFlr = measurementDetailTable.get(i).lengthCeilFlr ;

               pocketDepth = measurementDetailTable.get(i).pocketDepth ;
               carpetInst = measurementDetailTable.get(i).carpetInst ;
               windowCompletionStatus =  measurementDetailTable.get(i).windowCompletionStatus;
               windowApprovalCheck = measurementDetailTable.get(i).windowApprovalCheck ;
               windowId =  measurementDetailTable.get(i).windowId ;
           }
        }


    }

    private void doWork(JobParameters jobParameters) {
        setUpRestAdapter();
        showProjectList();
        sendMeasurementData();


        Log.d("abhi", "Job finished!");
        isWorking = false;
        boolean needsReschedule = false;
        jobFinished(jobParameters, needsReschedule);
    }

    private void sendMeasurementData() {


        Call<MeasurementResponse> call = MeasurementAdapter.measurementData(floorPlanId, roomId, windowName, wallWidth, widthLeftOfWindow, ibWidthOfWindow, ibLengthOfWindow, widthRightOfWindow, lengthCielFlr,
                pocketDepth, carpetInst,windowCompletionStatus,windowApprovalCheck,windowId, PrefUtils.getUserId(this));
        if (NetworkUtils.isNetworkConnected(this)) {
            call.enqueue(new Callback<MeasurementResponse>() {

                @Override
                public void onResponse(Call<MeasurementResponse> call, retrofit2.Response<MeasurementResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getType() == 1) {
                            Log.e("abhi", "onResponse:........... " + response.body().getMsg());
                            for (int i=0; i<response.body().getMeasurementDetails().size(); i++)
                            {
                                windowId = String.valueOf(response.body().getMeasurementDetails().get(i).getId());
                            }


                            Toast.makeText(getApplicationContext(), response.body().getMsg(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), response.body().getMsg(), Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();

                    }
                }

                @Override
                public void onFailure(Call<MeasurementResponse> call, Throwable t) {
                    Log.e("abhi", "onResponse: error....................... ");

                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            //saveMeasurementDataInDb();
            SnakBarUtils.networkConnected(this);
            LoadingDialog.cancelLoading();
        }
    }

    // Called if the job was cancelled before being finished
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d("abhi", "Job cancelled before being completed.");
        jobCancelled = true;
        boolean needsReschedule = isWorking;
        jobFinished(jobParameters, needsReschedule);
        return needsReschedule;
    }
}