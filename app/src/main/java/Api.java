import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public class Api {
    public static String BASE_URL = "https://simplifiedcoding.net/demos/";

    @GET("marvel")
    Call<List<Results>> getSuperHeroes() {
        return null;
    }
}
