package com.free.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.free.exception.FCException;
import com.free.exception.FCNetworkException;

public class FCHttpClient {
	static private FCHttpClient fCHttpClient = null;

	private FCHttpClient() {

	}

	public void doPost(String url, Map<?, ?> params) throws FCException {

		// 构造HttpClient的实例
		HttpClient httpClient = new DefaultHttpClient();
		// 创建Post的实例
		HttpPost httpPost = new HttpPost(url);

		try {
			// 设置HttpPost数据
			if (params != null) {
				// 填入各个表单域的值
				JSONObject data = null;

				Set<?> sets = params.keySet();
				Object[] arr = sets.toArray();
				int mxsets = sets.size();
				if (mxsets > 0) {
					data = new JSONObject();
					for (int i = 0; i < mxsets; i++) {
						String key = (String) arr[i];
						String val = (String) params.get(key);
						data.put(key, val);
					}
					// 将表单的值放入httpPost中
					httpPost.setEntity(new StringEntity(data.toString()));
				}
			}
			// 执行httpPost
			HttpResponse response;
			response = httpClient.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			//Log.e("Code", "" + code);
			if (code == 200||code==403) {// 域名没备案，将会返回403，其实已经请求成功
				HttpEntity httpEntity = response.getEntity();
				if (httpEntity != null) {
					try {
						BufferedReader bf = new BufferedReader(
								new InputStreamReader(httpEntity.getContent(),
										"UTF-8"));
						StringBuilder sb = new StringBuilder();
						String line = null;
						while ((line = bf.readLine()) != null) {
							sb.append(line);
						}
						bf.close();
						if (sb.toString().equals("false")) {
							throw new FCException();
						}
						//Log.e("sb", sb.toString());
					} catch (Exception e) {
						throw new FCException();
					}
				}

			} else
				throw new FCException();

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}  catch (ConnectException e) {
			e.printStackTrace();
			throw new FCNetworkException("网络异常，请检查网络");
		} catch (Exception e) {
			e.printStackTrace();
			throw new FCException();
		}
	}

	public static FCHttpClient getInstance() {
		if (fCHttpClient == null) {
			fCHttpClient = new FCHttpClient();
		}
		return fCHttpClient;
	}
}
