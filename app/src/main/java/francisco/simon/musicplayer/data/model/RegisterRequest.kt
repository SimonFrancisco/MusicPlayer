package francisco.simon.musicplayer.data.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("email") var email: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("name") var name: String? = null

)