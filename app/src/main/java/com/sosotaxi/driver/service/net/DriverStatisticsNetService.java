/**
 * @Author 屠天宇
 * @CreateTime 2020/7/25
 * @UpdateTime 2020/7/25
 */

package com.sosotaxi.driver.service.net;

import android.util.Pair;

import com.google.gson.Gson;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.model.Driver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class DriverStatisticsNetService extends BaseNetService{

    public static int postDriverStatistics(Driver driver) throws IOException, JSONException {
        if (driver == null){
            return -1;
        }
        Gson gson = new Gson();
        String json = gson.toJson(driver);

        System.out.println(json);
        // POST请求
        Pair<Response,String> result = post(Constant.POST_DRIVER_STATISTICS_URL,json);
        Response response=result.first;
        String data=result.second;

        if (response.code() == 200){
            JSONObject jsonObject = new JSONObject(data);
            int code=jsonObject.getInt("code");
            if (code == 200){
                JSONObject  object = jsonObject.getJSONObject("data");
                System.out.println(object);
                int serviceScore = object.getInt("serviceScore");
                System.out.println(serviceScore);
                return serviceScore;
            }else {
                System.out.println(code);
            }
        }{
            System.out.println(response.code());
        }
        return -1;
    }
}
