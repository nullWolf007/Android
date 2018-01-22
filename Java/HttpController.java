package com.inspeeding.ys400.http;

import android.util.Log;

import com.inspeeding.ys400.common.ApiConfig;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


/**
 * 网络请求连接类
 *
 * @author wangkai
 *         2015年3月8日
 */
public class HttpController {

    private static final String TAG = "HttpController";
    private static HttpController httpController = null;
    private static OkHttpClient okHttpClient = null;

    private HttpController() {
        okHttpClient = new OkHttpClient.Builder().
                connectTimeout(60 * 1000, TimeUnit.MILLISECONDS).build();

    }

    public static HttpController getInstance() {
        if (httpController == null) {
            httpController = new HttpController();
        }
        return httpController;
    }

    public String getUrl(String urlType) {
        return ApiConfig.URL_HEAD + urlType;
    }

    public String[] getStrArr(String... params) {
        return params;
    }

    public String getJsonStr(Map<String, String> params) throws JSONException, UnsupportedEncodingException {

        if (params == null || params.size() < 1) {
            return null;
        }

        JSONObject sendObj = new JSONObject();
        Set<Map.Entry<String, String>> entries = params.entrySet();
        for (Map.Entry entry : entries) {
            String key = String.valueOf(entry.getKey());
            String values = String.valueOf(entry.getValue());
            sendObj.put(key, values);
        }

        return sendObj.toString();
    }

//	public String httpRequest(String urlStr, String sendParam) {
//		try {
//			HttpClient hc = new DefaultHttpClient();
//
//			HttpParams params = null;
//			params = hc.getParams();
//			HttpConnectionParams.setConnectionTimeout(params, 20000);  //连接超时
//			HttpConnectionParams.setSoTimeout(params, 20000); //响应超时
//
//			HttpPost hp = new HttpPost(urlStr);
//
//			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
//			parameters.add((new BasicNameValuePair("c", sendParam)));
//			HttpEntity entity = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
//			hp.setEntity(entity);
//			HttpResponse hr = hc.execute(hp);
//
//			/*
//				添加response状态码判断
//				崔晟 20170719
//			 */
//			if (hr.getStatusLine().getStatusCode() == 200) {
//				return EntityUtils.toString(hr.getEntity());
//			} else {
//				return null;
//			}
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//			return null;
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//			return null;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

    public String httpRequest(String urlStr, Map<String, String> params) {


        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            Set<Map.Entry<String, String>> entries = params.entrySet();
            for (Map.Entry entry : entries) {
                String key = String.valueOf(entry.getKey());
                String values = String.valueOf(entry.getValue());
                builder.add(key, values);
            }
        }

        Request request = new Request.Builder()
                .url(urlStr)
                .post(builder.build())
                .build();

        try {
            okhttp3.Response temp = okHttpClient.newCall(request).execute();
            if (temp.isSuccessful()) {
                //call string auto close body
                return temp.body().string();
            } else {
                temp.body().close();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.w(TAG, e.getMessage() == null ? " " : e.getMessage());
            return null;
        }

    }

    public String httpRequest(String urlStr, String sendParam) {
        RequestBody formBody = new FormBody.Builder()
                .add("c", sendParam)
                .build();

        Request request = new Request.Builder()
                .url(urlStr)
                .post(formBody)
                .build();

        try {
            okhttp3.Response temp = okHttpClient.newCall(request).execute();
            if (temp.isSuccessful()) {
                //call string auto close body
                return temp.body().string();
            } else {
                temp.body().close();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.w(TAG, e.getMessage() == null ? " " : e.getMessage());
            return null;
        }

    }


//		call.enqueue(new Callback() {
//			@Override
//			public void onFailure(Call call, IOException e) {
//
//			}
//
//			@Override
//			public void onResponse(Call call, Response response) throws IOException {
//				System.out.println();
//				return response.body().string();
//			}
//
//		});


//		try{
//			HttpClient hc = new DefaultHttpClient();
//
//			HttpParams params = null;
//			params = hc.getParams();
//			HttpConnectionParams.setConnectionTimeout(params, 20000);  //连接超时
//			HttpConnectionParams.setSoTimeout(params, 20000); //响应超时
//
//			HttpPost hp = new HttpPost(urlStr);
//
//			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
//			parameters.add((new BasicNameValuePair("c", sendParam)));
//			HttpEntity entity = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
//			hp.setEntity(entity);
//			HttpResponse hr = hc.execute(hp);
//
//			/*
//				添加response状态码判断
//				崔晟 20170719
//			 */
//			if (hr.getStatusLine().getStatusCode() == 200)
//			{
//				return EntityUtils.toString(hr.getEntity());
//			}
//			else
//			{
//				return null;
//			}
//		}catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//			return null;
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//			return null;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
}

