package com.valleyforge.cdi.api;


import com.valleyforge.cdi.generated.model.DashboardDataResponse;
import com.valleyforge.cdi.generated.model.FloorDetailsResponse;
import com.valleyforge.cdi.generated.model.ForgotPasswordResponse;
import com.valleyforge.cdi.generated.model.Login;
import com.valleyforge.cdi.generated.model.LoginResponse;
import com.valleyforge.cdi.generated.model.MeasurementResponse;
import com.valleyforge.cdi.generated.model.ProjectDetailResponse;
import com.valleyforge.cdi.generated.model.ProjectListResponse;
import com.valleyforge.cdi.generated.model.RoomsListResponse;
import com.valleyforge.cdi.generated.model.SkipResponse;
import com.valleyforge.cdi.generated.model.UploadPhotoResponse;
import com.valleyforge.cdi.generated.model.WindowsListResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class RetrofitInterface {


    public interface UserLoginClient {
        @FormUrlEncoded
        @POST("loginApi")
        public Call<LoginResponse> userLogIn(
                @Field("email") String username,
                @Field("password") String password
        );
    }

    public interface UserDashboardClient {
        @FormUrlEncoded
        @POST("dashboardApi")
        public Call<DashboardDataResponse> userDashboardData(
                @Field("userid") String userid

        );
    }

    public interface UserForgotPasswordClient {
        @FormUrlEncoded
        @POST("forgotpwdApi")
        public Call<ForgotPasswordResponse> userForgotPasswordData(
                @Field("email") String email

        );
    }

    public interface UserProjectListClient {
        @FormUrlEncoded
        @POST("assignedprojectsApi")
        public Call<ProjectListResponse> userProjectListData(
                @Field("userid") String userid,
                @Field("project_status") String project_status

        );
    }



    public interface UserProjectDetailClient {
        @FormUrlEncoded
        @POST("projectdetailApi")
        public Call<ProjectDetailResponse> userProjectDetailData(
                @Field("id") String id

        );
    }

    public interface UserFloorDetailsClient {
        @FormUrlEncoded
        @POST("floordetailsApi")
        public Call<FloorDetailsResponse> userFloorDetailData(
                @Field("id") String id

        );
    }


    public interface UserRoomListClient {
        @FormUrlEncoded
        @POST("floorroomslistApi")
        public Call<RoomsListResponse> userRoomListData(
                @Field("floor_plan_id") int floor_plan_id

        );
    }

    public interface UserProfileDetailsClient {
        @FormUrlEncoded
        @POST("profileApi")
        public Call<LoginResponse> userProfileDetailData(
                @Field("userid") String userid

        );
    }



    public interface SkipFloorClient {
        @FormUrlEncoded
        @POST("skipFloorApi")
        public Call<SkipResponse> skipFloorData(
                @Field("floor_plan_id") int floor_plan_id,
                @Field("skip_reason") String skip_reason

        );
    }

    public interface WindowsListClient {
        @FormUrlEncoded
        @POST("windowsListApi")
        public Call<WindowsListResponse> windowsListData(
                @Field("floor_plan_id") int floor_plan_id,
                @Field("room_id") String room_id

        );
    }


    public interface SkipRoomClient {
        @FormUrlEncoded
        @POST("skipRoomApi")
        public Call<SkipResponse> skipRoomData(
                @Field("floor_plan_id") String floor_plan_id,
                @Field("skip_reason") String skip_reason,
                @Field("room_id") String room_id

        );
    }



    public interface UpdateProfileDetailsClient {
        @FormUrlEncoded
        @POST("updateprofileApi")
        public Call<LoginResponse> updateProfileDetailData(
                @Field("userid") String userid,
                @Field("context_id") String context_id,
                @Field("name") String name,
                @Field("email") String email,
                @Field("phone") String phone,
                @Field("address") String address


        );
    }

    public interface DeletePhotoClient {
        @FormUrlEncoded
        @POST("deleteimageApi")
        public Call<LoginResponse> deleteImageData(
                @Field("imageid") String imageid,
                @Field("window_id") String window_id,
                @Field("image_type") String image_type



        );
    }




    public interface AddRoomClient {
        @FormUrlEncoded
        @POST("addRoomApi")
        public Call<RoomsListResponse> addRoomDataData(
                @Field("room_name") String room_name,
                @Field("floor_plan_id") int floor_plan_id,
                @Field("no_of_windows") String no_of_windows,
                @Field("room_status") int room_status,
                @Field("room_desc") String room_desc



        );
    }

    public interface MeasurementDataClient {
        @FormUrlEncoded
        @POST("addmeasurementsApi")
        public Call<MeasurementResponse> measurementData(
                @Field("floor_plan_id") String floor_plan_id,
                @Field("floor_room_id") String floor_room_id,
                @Field("window_name") String window_name,
                @Field("wall_width") String wall_width,
                @Field("width_left_window") String width_left_window,
                @Field("ib_width_window") String ib_width_window,
                @Field("ib_length_window") String ib_length_window,
                @Field("width_right_window") String width_right_window,
                @Field("length_ceil_flr") String length_ceil_flr,
                @Field("pocket_depth") String pocket_depth,
                @Field("carpet_inst") String carpet_inst,
                @Field("window_status") String window_status,
                @Field("window_approval") String window_approval,
                @Field("window_id") String window_id,
                @Field("userid") String userid



        );
    }

    /*public interface uploadPhotosClient {
        @Multipart
        @POST("imageuploadApi")
        Call<UploadPhotoResponse> uploadImageData(@Part MultipartBody.Part profilepic , @Part("profilepic") RequestBody name);

    }*/

    public interface uploadPhotosClient {
        @Multipart
        @POST("imageuploadApi")
        public Call<UploadPhotoResponse> uploadImageData(
                @Part("userid") RequestBody userid,
                @Part("measurement_id") RequestBody measurement_id,
                @Part MultipartBody.Part file,
                @Part("image_type") RequestBody image_type);
    }

    public interface uploadProfilePhotoClient {
        @Multipart
        @POST("profileimageupdateApi")
        public Call<UploadPhotoResponse> uploadProfileData(
                 @Part MultipartBody.Part file,
                 @Part("userid") RequestBody userid);
    }

  /*  public interface UserLoginClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<LoginResponse> userLogIn(
                @Field("username") String username,
                @Field("password") String password,
                @Field("type") String type);
    }

    public interface UserProfileClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<ProfileResponse> userProfile(
                @Field("username") String username,
                @Field("user_type") String userType,
                @Field("id") String id,
                @Field("type") String type);
    }

    public interface UserChangePasswordClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<ChangePasswordResponse> userChangePassword(
                @Field("username") String username,
                @Field("oldpass") String oldpass,
                @Field("newpass") String newpass,
                @Field("type") String type);
    }

    public interface UserSubmitComplaintClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<SubmitComplaintResponse> userSubmitComplaint(
                @Field("userid") String userId,
                @Field("projectid") String projectid,
                @Field("complaintstypeid") String complaintstypeid,
                @Field("remarks") String remarks,
                @Field("type") String type);
    }

    public interface UserCompaintListClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<NewComplaintResponse> userNewComplaintList(
                @Field("type") String type);
    }


    public interface UserTechCompaintListClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<NewComplaintResponse> userTechComplaintList(
                @Field("type") String type,
                @Field("tech_userid") String tech_userid);
    }

    public interface UserRejectedCompaintListClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<RejectedComplaintListResponse> userRejectedComplaintList(
                @Field("type") String type);
    }

    public interface UserCompaintTypeClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<ComplaintTypeDropdown> userComplaintTypeList(
                @Field("type") String type);
    }

    public interface SparePartsPendingClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<SparePartsPendingResponse> sparePartsPendingList(
                @Field("type") String type);
    }

    public interface SparePartsRequestClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<SparePartsRequestResponse> sparePartsRequestList(
                @Field("type") String type);
    }


    public interface SparePartsRequestByTechPartnerClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<SparePartsRequestResponse> sparePartsRequestBtPartnerList(
                @Field("complaintstypeid") String complaintstypeid,
                @Field("type") String type);
    }
    public interface SparePartsToBeClosedTodayClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<SparePartsRequestResponse> sparePartsRequestList(
                @Field("type") String type);
    }

    public interface AdminDataClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<DashboardDataResponse> DashboardDataList(
                @Field("type") String type);
    }

    public interface TechnicalPartnerClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<TechnicalPartnerListResponse> TechPartnerList(
                @Field("type") String type,
                @Field("id") String id);
    }

    public interface AdminApproveCompaintClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<ApproveComplaintResponse> AdminApproval(
                @Field("id") String id,
                @Field("admin_status") String admin_status,
                @Field("type") String type);
    }

    public interface AdminApprovesparePartClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<SparePartAdminAproveResponse> AdminSparePartApproval(
                @Field("sparepart_requestid") String sparepart_requestid,
                @Field("type") String type);
    }


    public interface TotalConsumerClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<TotalConsumerListResponse> totalConsumer(
                @Field("id") String id,
                @Field("user_type") String user_type,
                @Field("type") String type);
    }

    public interface TotalConsumerCountForClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<ConsumerCountClientResponse> totalConsumerCountClient(
                @Field("id") String id,
                @Field("type") String type);
    }

    public interface AssignTechPartnerClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<AssignComplaintResponse> AssignTechPartner(
                @Field("tech_userid") String tech_userid,
                @Field("complaintstypeid") String complaintstypeid,
                @Field("admin_id") String admin_id,
                @Field("type") String type);
    }

    public interface TechnicalPartnerFunctionClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<TechnicalPartnerFunctionResponse> technicalPartnerFunction(
                @Field("tech_userid") String tech_userid,
                @Field("complaintstypeid") String complaintstypeid,
                @Field("type") String type);
    }

    public interface UserSubmitSparePartClient {
        @Multipart
        @POST("query.php")
        public Call<SubmitComplaintResponse> userSubmitSparePart(
                @Part("sparepartid") RequestBody sparepartid,
                @Part("technicalpartnerid") RequestBody technicalpartnerid,
                @Part("complainid") RequestBody complainid,
                @Part("quantity") RequestBody quantity,
                @Part MultipartBody.Part request_image,
                //@Part MultipartBody.Part requestimage,
                @Part("type") RequestBody type);
    }

    public interface PreventiveMaintainanceClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<PreventiveMaintainanceResponse> preventiveMaintainanceList(
                @Field("userid") String userid,
                @Field("type") String type);
    }

    public interface SendTokenToServerClient {
        @FormUrlEncoded
        @POST("query.php")
        public Call<SendTokenToServerResponse> sendTokenToServer(
                @Field("userid") String userid,
                @Field("token_id") String token_id,
                @Field("type") String type);
    }

    public interface updateProfilePicClient {
        @Multipart
        @POST("query.php")
        Call<ResponseBody> uploadImageData(@Part MultipartBody.Part profilepic,
                                           @Part("request_image") RequestBody name);

    }*/
}
