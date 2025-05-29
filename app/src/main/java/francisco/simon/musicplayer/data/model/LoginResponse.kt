package francisco.simon.musicplayer.data.model

import com.google.gson.annotations.SerializedName


data class LoginResponse(

    @SerializedName("token") var token: String? = null,
    @SerializedName("user") var user: User? = User()

)