import com.example.collegelist.University
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface UniversityApiService {
    @GET("http://universities.hipolabs.com/search")
    suspend fun getAllUniversities(): MutableList<University>
    //abstract fun getAllUniversities(): Any
}

object UniversityApi {
    private const val BASE_URL = "http://universities.hipolabs.com/search/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val service: UniversityApiService = retrofit.create(UniversityApiService::class.java)
}
