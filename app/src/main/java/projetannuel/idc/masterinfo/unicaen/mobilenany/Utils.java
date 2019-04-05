package projetannuel.idc.masterinfo.unicaen.mobilenany;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.ApiError;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.RetrofitBuilder;
import retrofit2.Converter;

public class Utils {
    public static final String BASE_URL = "http://10.188.109.155:8005/api/";//10.188.109.155 192.168.0.17 10.224.71.150 192.168.43.138
    public static final String PATH_URL = "http://10.188.109.155:8005/mes_images";

    public static ApiError convertErrors(ResponseBody response){

        ApiError apiError = null;
        Converter<ResponseBody, ApiError> converter = RetrofitBuilder.getRetrofit().responseBodyConverter(ApiError.class, new Annotation[0]);
        try {
            apiError = converter.convert(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return apiError;
    }

    public static String getImageCompleteUrl(String imageUrl) {
        String [] imgFolder = imageUrl.split("\\.");

        String path = PATH_URL+ "/" + imgFolder[0]+ "/" +imageUrl;
        return path;
    }
}
