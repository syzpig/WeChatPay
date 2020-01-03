package com.steven.wechatpay.util;

import com.steven.wechatpay.config.TransfersConfig;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @ClassName: HttpUtils
 * @Descripton: HttpUtils工具类
 * @Author: Steven_Deng
 * @Creation Date：2019年12月10日 19:03
 * @Version: 1.0V
 */
public class HttpUtils {

	private static final String DEFAULT_CHARSET = "UTF-8";

	/**链接超时时间3秒*/
	private static final int CONNECT_TIME_OUT = 5000;
	
	private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setConnectTimeout(CONNECT_TIME_OUT).build();

	/**微信支付ssl证书*/
	private static SSLContext wx_ssl_context = null;
	
	static{
		//该证书名字最好改为别人猜不到的
		Resource resource = new ClassPathResource("wx_apiclient_cert.p12");
		try {
			//FileInputStream inputStream = FileUtils.openInputStream(new File(TransfersDTO.api_secret_path));
			KeyStore keystore = KeyStore.getInstance("PKCS12");
			//证书密码
			char[] keyPassword = TransfersConfig.mch_id.toCharArray();
			keystore.load(resource.getInputStream(), keyPassword);
			wx_ssl_context = SSLContexts.custom().loadKeyMaterial(keystore, keyPassword).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @description 功能描述: get 请求
	 * @param url 请求地址
	 * @param params 参数
	 * @param headers headers参数
	 * @return 请求失败返回null
	 */
	public static String get(String url, Map<String, String> params, Map<String, String> headers) {

		CloseableHttpClient httpClient = null;
		if (params != null && !params.isEmpty()) {
			StringBuffer param = new StringBuffer();
			// 是否开始
			boolean flag = true;
			for (Entry<String, String> entry : params.entrySet()) {
				if (flag) {
					param.append("?");
					flag = false;
				} else {
					param.append("&");
				}
				param.append(entry.getKey()).append("=");
				
				try {
					param.append(URLEncoder.encode(entry.getValue(), DEFAULT_CHARSET));
				} catch (UnsupportedEncodingException e) {
					//编码失败
				}
			}
			url += param.toString();
		}

		String body = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = HttpClients.custom().setDefaultRequestConfig(REQUEST_CONFIG).build();
			HttpGet httpGet = new HttpGet(url);
			response = httpClient.execute(httpGet);
			body = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return body;
	}

	/**
	 * @description 功能描述: get 请求
	 * @param url 请求地址
	 * @return 请求失败返回null
	 */
	public static String get(String url) {
		return get(url, null);
	}

	/**
	 * @description 功能描述: get 请求
	 * @param url 请求地址
	 * @param params 参数
	 * @return 请求失败返回null
	 */
	public static String get(String url, Map<String, String> params) {
		return get(url, params, null);
	}

	/**
	 * @description 功能描述: post 请求
	 * @param url 请求地址
	 * @param params 参数
	 * @return 请求失败返回null
	 */
	public static String post(String url, Map<String, String> params) {
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nameValuePairs = new ArrayList<>();
		if (params != null && !params.isEmpty()) {
			for (Entry<String, String> entry : params.entrySet()) {
				nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}

		String body = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = HttpClients.custom().setDefaultRequestConfig(REQUEST_CONFIG).build();
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, DEFAULT_CHARSET));
			response = httpClient.execute(httpPost);
			body = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return body;
	}

	/**
	 * @description 功能描述: post 请求
	 * @param url 请求地址
	 * @param s 参数xml
	 * @return 请求失败返回null
	 */
	public static String post(String url, String s) {
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = new HttpPost(url);
		String body = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = HttpClients.custom().setDefaultRequestConfig(REQUEST_CONFIG).build();
			httpPost.setEntity(new StringEntity(s, DEFAULT_CHARSET));
			response = httpClient.execute(httpPost);
			body = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return body;
	}

	/**
	 * @description 功能描述: post https请求，服务器双向证书验证
	 * @param url 请求地址
	 * @param params 参数
	 * @return 请求失败返回null
	 */
	 public static String posts(String url, Map<String, String> params) {
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nameValuePairs = new ArrayList<>();
		if (params != null && !params.isEmpty()) {
			for (Entry<String, String> entry : params.entrySet()) {
				nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}

		String body = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = HttpClients.custom().setDefaultRequestConfig(REQUEST_CONFIG)
					.setSSLSocketFactory(getSSLConnectionSocket()).build();
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, DEFAULT_CHARSET));
			response = httpClient.execute(httpPost);
			body = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return body;
	}
	
	/**
	 * @description 功能描述: post https请求，服务器双向证书验证
	 * @param url 请求地址
	 * @param s 参数xml
	 * @return 请求失败返回null
	 */
	public static String posts(String url, String s) {
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = new HttpPost(url);
		String body = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = HttpClients.custom().setDefaultRequestConfig(REQUEST_CONFIG)
					.setSSLSocketFactory(getSSLConnectionSocket()).build();
			httpPost.setEntity(new StringEntity(s, DEFAULT_CHARSET)); 
			response = httpClient.execute(httpPost);
			body = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return body;
	}

	/**
	 * 获取ssl connection链接
	 * @return SSLConnectionSocketFactory
	 */
	private static SSLConnectionSocketFactory getSSLConnectionSocket() {
		return new SSLConnectionSocketFactory(wx_ssl_context, new String[] {"TLSv1", "TLSv1.1", "TLSv1.2"}, null,
				SSLConnectionSocketFactory.getDefaultHostnameVerifier());
	}
}