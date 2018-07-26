
package com.valleyforge.cdi.generated.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login {

    public static class LoginDetails {

        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("password")
        @Expose
        private String password;



        public LoginDetails(String email, String password) {
            this.email = email;
            this.password = password;
        }




    }

    }


