package api;

import java.util.ArrayList;

import com.google.gson.*;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import main.GamePanel;
import piece.PieceData;
public interface APIService {
	String BASE_URL = "http://26.21.145.88:5000/";
	APIService apiService = new Retrofit.Builder()
			.baseUrl(BASE_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
			.create(APIService.class);
	@POST("game/save")
	Call<Void> saveGame(@Body ArrayList<PieceData> pieceDataList);
}	
