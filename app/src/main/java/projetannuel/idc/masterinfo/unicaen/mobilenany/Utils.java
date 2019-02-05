package projetannuel.idc.masterinfo.unicaen.mobilenany;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import projetannuel.idc.masterinfo.unicaen.mobilenany.entities.ApiError;
import projetannuel.idc.masterinfo.unicaen.mobilenany.network.RetrofitBuilder;
import retrofit2.Converter;

public class Utils {

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
}
