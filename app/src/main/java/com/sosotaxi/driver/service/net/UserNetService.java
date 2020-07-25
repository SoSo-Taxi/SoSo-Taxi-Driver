/**
 * @Author 范承祥
 * @CreateTime 2020/7/23
 * @UpdateTime 2020/7/25
 */
package com.sosotaxi.driver.service.net;

import android.util.Pair;

import com.google.gson.Gson;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.model.Driver;
import com.sosotaxi.driver.model.DriverVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ResponseCache;

import okhttp3.Response;

/**
 * 用户网络服务
 */
public class UserNetService extends BaseNetService{
    /**
     * 查询司机信息
     * @param driver 司机
     * @return
     * @throws IOException 输入输出异常
     * @throws JSONException JSON解析异常
     */
    public static Pair<Boolean,String> queryDriver(Driver driver) throws IOException, JSONException {
        String username=driver.getUserName();
        String token=driver.getToken();
        if(username==null||username.isEmpty()){
            return new Pair<>(false,"用户名为空");
        }

        StringBuffer urlBuffer=new StringBuffer();
        urlBuffer.append(Constant.QUERY_USER_URL);
        urlBuffer.append(username);
        String url= urlBuffer.toString();
        Pair<Response,String> result=get(url);

        Response response=result.first;
        String data=result.second;

        // 结果处理
        if(response.code()==200){
            JSONObject jsonObject=new JSONObject(data);
            int code=jsonObject.getInt("code");
            String message=jsonObject.getString("msg");
            return code==200?new Pair<Boolean, String>(true,data):new Pair<Boolean, String>(false,data);
        }
        return new Pair<>(false,"连接失败");
    }
}
