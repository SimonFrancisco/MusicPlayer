package francisco.simon.musicplayer.data.model

import com.google.gson.annotations.SerializedName

data class User(

    @SerializedName("id") var id: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("profilePicture") var profilePicture: String? = null,
    @SerializedName("createdAt") var createdAt: Long? = null,
    @SerializedName("updatedAt") var updatedAt: Long? = null

)