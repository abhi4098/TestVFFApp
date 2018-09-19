package com.valleyforge.cdi.api;


import com.valleyforge.cdi.generated.model.AddFloorResponse;
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
import com.valleyforge.cdi.generated.model.SubmitWindowDetailResponse;
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
                @Field("userid") String userid,
                @Field("roleid") String roleid


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
                @Field("userid") String userid


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

    public interface AddFloorClient {
        @FormUrlEncoded
        @POST("addfloorplanApi")
        public Call<AddFloorResponse> addFloorData(
                @Field("floor_name") String floor_name,
                @Field("project_id") String project_id,
                @Field("userid") String userid


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

    public interface SubmitWindowsData {
        @FormUrlEncoded
        @POST("savemeasurementsfinalApi")
        public Call<SubmitWindowDetailResponse> windowDetail(
                @Field("floor_plan_id") String floor_plan_id,
                @Field("floor_room_id") String floor_room_id,
                @Field("window_id") String window_id,
                @Field("userid") String userid




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


}
