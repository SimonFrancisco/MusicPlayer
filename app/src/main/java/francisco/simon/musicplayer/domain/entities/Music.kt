package francisco.simon.musicplayer.domain.entities

data class Music(
    val id: Int,
    val imageUrl: String,
    val audioUrl: String,
    val author: Author,
    val lyrics:String,
    val isFavourite:Boolean
)