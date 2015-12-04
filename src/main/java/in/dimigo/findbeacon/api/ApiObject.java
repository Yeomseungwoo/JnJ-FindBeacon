package in.dimigo.findbeacon.api;

import com.google.gson.JsonObject;
import in.dimigo.findbeacon.util.Schema;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public class ApiObject implements ApiRequests{

    private static ApiObject instance;
    private static ApiRequests apiRequests;

    public static ApiObject getInstance(){

        if(instance == null) instance = new ApiObject();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Schema.RECO_API_ENDPOINT).build();
        apiRequests = restAdapter.create(ApiRequests.class);
        return instance;
    }

    @Override
    public void postDeviceId(String deviceId, Callback<JsonObject> callback){
        apiRequests.postDeviceId(deviceId, callback);
    }
}


interface ApiRequests {
    @POST("/uuid.php")
    @FormUrlEncoded
    void postDeviceId(
            @Field(Schema.DEVICE_ID_KEY) String uuid,
            Callback<JsonObject> callback);
}