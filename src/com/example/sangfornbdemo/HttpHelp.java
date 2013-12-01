package com.example.sangfornbdemo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author liyc
 * @time 2012-2-20 下午3:44:00
 * @annotation HTTP传输帮助类
 */

public class HttpHelp{

	public static final int CONNECTION_TIME_OUT_TIME = 30 * 1000;
	public static final String BACK_DEAL_ERR = "BACK_DEAL_ERR";
	public static final String INTERFACE_ERR = "INTERFACE_ERR";
	public static final String NO_SUCH_METHOD = "NO_SUCH_METHOD";
	public final static String RESPONSEINFO_SUCCESS = "responseinfo_success";
	public final static String RESPONSEINFO_FAILURE = "responseinfo_failure";
	public static final String CONNECTION_TIME_OUT = "connection timed out";//网络连接超时

	public static String getHttpBack(String seamurl) throws Exception{
//		LogHelp.LogI(seamurl);
		String seamStr = "";
		seamurl = seamurl.replaceAll(" ", "%20");
		HttpGet get = new HttpGet(seamurl);
		HttpClient httpClient = new DefaultHttpClient();
		try{
			HttpResponse httpResponse = httpClient.execute(get);
			HttpEntity entity = httpResponse.getEntity();
			if(entity!=null){
				InputStream in = entity.getContent();
				GZIPInputStream gunzip = new GZIPInputStream(in);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int n;
				while ((n = gunzip.read(buffer)) >= 0) {
					out.write(buffer, 0, n);
				}
				byte[] b = out.toByteArray();
				out.close();
				gunzip.close();
				in.close();
				seamStr = new String(b);
			}
			else{
				seamStr = CONNECTION_TIME_OUT;
			}
		}catch(Exception e){
//			LogHelp.Log2SDInfo("Can not connect server:" + seamurl);
			seamStr = CONNECTION_TIME_OUT;
		}
		return seamStr;
	}

	//	public static String getHttpBack(String seamurl) throws Exception {
	//
	//		LogHelp.LogI(seamurl);
	//
	//		String seamStr = "";
	//
	//		try {
	//			seamurl = seamurl.replaceAll(" ","%20");
	//			URL url = new URL(new String(seamurl.getBytes("utf-8"),"ISO-8859-1"));
	//
	//			HttpURLConnection httpURLConnection = (HttpURLConnection) url
	//					.openConnection();
	//			httpURLConnection.setConnectTimeout(CONNECTION_TIME_OUT_TIME);
	//
	//			//设置读取服务器超时的时间
	//			//httpURLConnection.setReadTimeout(CONNECTION_TIME_OUT_TIME);
	//
	//			if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	//				InputStream in = httpURLConnection.getInputStream();
	//				//解压GZIP
	//				GZIPInputStream gunzip = new GZIPInputStream(in);
	//
	//				ByteArrayOutputStream out = new ByteArrayOutputStream();
	//
	//				byte[] buffer = new byte[1024];
	//				int n;
	//				while ((n = gunzip.read(buffer)) >= 0) {
	//					out.write(buffer, 0, n);
	//				}
	//				byte[] b = out.toByteArray();
	//				out.close();
	//				gunzip.close();
	//				in.close();
	//				seamStr = new String(b);
	//
	//			}else{
	//				seamStr = CONNECTION_TIME_OUT;
	//			}
	//			httpURLConnection.disconnect();
	//		} catch (Exception e) {
	//			LogHelp.Log2SDInfo("Can not connect server : "+seamurl);
	//			seamStr =  CONNECTION_TIME_OUT;
	//		}
	//		MainService.check_bs(seamStr);
	//		return seamStr;
	//	}

	public static byte[] getHttpBackByte(String seamurl) throws Exception {
//		LogHelp.LogI(seamurl);
		byte[] b;
		try {
			seamurl = seamurl.replaceAll(" ","%20");
			URL url = new URL(new String(seamurl.getBytes("utf-8"),"ISO-8859-1"));
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setConnectTimeout(CONNECTION_TIME_OUT_TIME);
			if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream in = httpURLConnection.getInputStream();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int n;
				while ((n = in.read(buffer)) >= 0) {
					out.write(buffer, 0, n);
				}
				b = out.toByteArray();
			}else{
				b = null;
			}
			httpURLConnection.disconnect();
		} catch (Exception e) {
//			LogHelp.Log2SDInfo("Can not connect server:"+seamurl);
			b = null;
		}
		return b;
	}

	public static String getHttpBackNoZipNoCheck(String seamurl) throws Exception {
//		LogHelp.LogI(seamurl);
		String seamStr = "";

		try {

			seamurl = seamurl.replaceAll(" ","%20");

			URL url = new URL(new String(seamurl.getBytes("utf-8"),"ISO-8859-1"));

			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setConnectTimeout(CONNECTION_TIME_OUT_TIME);

			if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

				InputStream in = httpURLConnection.getInputStream();

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int n;
				while ((n = in.read(buffer)) >= 0) {
					out.write(buffer, 0, n);
				}
				byte[] b = out.toByteArray();
				out.close();
				in.close();
				seamStr = new String(b);
			}else{
				seamStr = CONNECTION_TIME_OUT;
			}
			httpURLConnection.disconnect();
		} catch (Exception e) {
//			LogHelp.Log2SDInfo("Can not connect server:"+seamurl);
			seamStr =  CONNECTION_TIME_OUT;
		}
		return seamStr;
	}

	public static String getHttpBackNoZipCheck(String seamurl) throws Exception {
//		LogHelp.LogI(seamurl);
		String seamStr = "";
		try {
			seamurl = seamurl.replaceAll(" ","%20");
			URL url = new URL(new String(seamurl.getBytes("utf-8"),"ISO-8859-1"));

			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setConnectTimeout(CONNECTION_TIME_OUT_TIME);
			if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream in = httpURLConnection.getInputStream();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int n;
				while ((n = in.read(buffer)) >= 0) {
					out.write(buffer, 0, n);
				}
				byte[] b = out.toByteArray();
				out.close();
				in.close();
				seamStr = new String(b);
			}else{
				seamStr = CONNECTION_TIME_OUT;
			}
			httpURLConnection.disconnect();
		} catch (Exception e) {
//			LogHelp.Log2SDInfo("Can not connect server:"+seamurl);
			seamStr =  CONNECTION_TIME_OUT;
		}
		return seamStr;
	}

	public static String getHttpBackWithNOCheck(String seamurl) throws Exception {
//		LogHelp.LogI(seamurl);
		String seamStr = "";
		try {
			URL url = new URL(new String(seamurl.getBytes("utf-8"),"ISO-8859-1"));
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setConnectTimeout(CONNECTION_TIME_OUT_TIME);
			if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// 解压缩GZIP
				InputStream in = httpURLConnection.getInputStream();
				GZIPInputStream gunzip = new GZIPInputStream(in);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int n;
				while ((n = gunzip.read(buffer)) >= 0) {
					out.write(buffer, 0, n);
				}
				byte[] b = out.toByteArray();
				out.close();
				gunzip.close();
				in.close();
				seamStr = new String(b);
			}else{
				seamStr = CONNECTION_TIME_OUT;
			}
			httpURLConnection.disconnect();
		} catch (Exception e) {
//			LogHelp.Log2SDInfo("Can not connect server:"+seamurl);
			seamStr = CONNECTION_TIME_OUT;
		}
		return seamStr;
	}

	public static String getHttpBackEncodeGBK(String seamurl) throws Exception {
//		LogHelp.LogI(seamurl);
		String seamStr = "";
		try {
			URL url = new URL(new String(seamurl.getBytes("utf-8"),"ISO-8859-1"));
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setConnectTimeout(CONNECTION_TIME_OUT_TIME);
			if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// 解压缩GZIP
				InputStream in = httpURLConnection.getInputStream();
				GZIPInputStream gunzip = new GZIPInputStream(in);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int n;
				while ((n = gunzip.read(buffer)) >= 0) {
					out.write(buffer, 0, n);
				}
				byte[] b = out.toByteArray();
				out.close();
				gunzip.close();
				in.close();
				seamStr = new String(b,"gbk");
			}else{
				seamStr = CONNECTION_TIME_OUT;
			}
			httpURLConnection.disconnect();
		} catch (Exception e) {
//			LogHelp.Log2SDInfo("Can not connect server:"+seamurl);
			seamStr = CONNECTION_TIME_OUT;
		}
		return seamStr;
	}

	public static String post(String actionUrl, Map<String, String> params,
			Map<String, File> files)throws IOException{

		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(30 * 1000); //缓存的最长时间
		conn.setDoInput(true);//允许输入
		conn.setDoOutput(true);//允许输出
		conn.setUseCaches(false); //不允许使用缓存
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
				+ ";boundary=" + BOUNDARY);

		// 首先组拼文本类型的参数
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}
		DataOutputStream outStream = new DataOutputStream(conn
				.getOutputStream());
		outStream.write(sb.toString().getBytes());

		// 发送文件数据
		if (files != null)
			for (Map.Entry<String, File> file : files.entrySet()) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1
				.append("Content-Disposition: form-data; name=\"file\"; filename=\""
						+ URLEncoder.encode(file.getKey(),"utf-8") + "\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());
				InputStream is = new FileInputStream(file.getValue());

				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				is.close();
				outStream.write(LINEND.getBytes());
			}

		//请求结束标志
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();

		String seamStr = "";
		// 得到响应码
		if (conn.getResponseCode()==HttpURLConnection.HTTP_OK) {
			InputStream in = conn.getInputStream();
			GZIPInputStream gunzip = new GZIPInputStream(in);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[256];
			int n;
			while ((n = gunzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
			byte[] b = out.toByteArray();
			out.flush();
			out.close();
			gunzip.close();
			in.close();
			seamStr = new String(b);
		} else {
			seamStr = CONNECTION_TIME_OUT;
		}
		outStream.close();
		conn.disconnect();
		return seamStr;
	}

	/**
	 * 邮件发送post到后台
	 */
	public static String postToseam(String urlstr, String username, String title,
			String msgTo, String msgNameTo, String chaosongTo,
			String chaosongNameTo, String content, String haveattach,
			String mailguid) {
		String seamStr = "";
		try {
			URL url = new URL(urlstr);
			// 使用HttpURLConnection打开连接
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			urlConn.setConnectTimeout(CONNECTION_TIME_OUT_TIME);
			// 因为这个是post请求,设立需要设置为true
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			// 设置以POST方式
			urlConn.setRequestMethod("POST");
			// Post 请求不能使用缓存
			urlConn.setUseCaches(false);
			urlConn.setInstanceFollowRedirects(true);
			// 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
			urlConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
			// 要注意的是connection.getOutputStream会隐含的进行connect。
			urlConn.connect();
			// DataOutputStream流
			DataOutputStream dos = new DataOutputStream(urlConn
					.getOutputStream());
			// 要上传的参数
			String contentStr = "username="
					+ URLEncoder.encode(username, "utf-8");
			contentStr += "&title=" + URLEncoder.encode(title, "utf-8");
			contentStr += "&msgTo=" + URLEncoder.encode(msgTo, "utf-8");
			contentStr += "&msgNameTo=" + URLEncoder.encode(msgNameTo, "utf-8");
			contentStr += "&chaosongTo="
					+ URLEncoder.encode(chaosongTo, "utf-8");
			contentStr += "&chaosongNameTo="
					+ URLEncoder.encode(chaosongNameTo, "utf-8");
			contentStr += "&content=" + URLEncoder.encode(content, "utf-8");
			contentStr += "&haveattach="
					+ URLEncoder.encode(haveattach, "utf-8");
			contentStr += "&mailguid=" + URLEncoder.encode(mailguid, "utf-8");
			// 将要上传的内容写入流中
			dos.writeBytes(contentStr);
			// 刷新、关闭
			dos.flush();
			dos.close();
			if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream in = urlConn.getInputStream();
				GZIPInputStream gunzip = new GZIPInputStream(in);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[256];
				int n;
				while ((n = gunzip.read(buffer)) >= 0) {
					out.write(buffer, 0, n);
				}
				byte[] b = out.toByteArray();
				gunzip.close();
				in.close();
				seamStr = new String(b);
			} else {
				seamStr = CONNECTION_TIME_OUT;
			}
		} catch (Exception e) {
			seamStr = CONNECTION_TIME_OUT;
		}
		return seamStr;
	}
	public static String postFileServlet(String urlstr, String srcPath) {
		String twoHyphens = "--";
		String boundary = "******";
		String end = "\r\n";
		String seamStr = null;
		try {
			System.out.println(srcPath);
			URL url = new URL(urlstr);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
			httpURLConnection.setRequestProperty("enctype", "multipart/form-data");
			DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition:form-data;name=\"file\"; filename=\""
					+ srcPath.substring(srcPath.lastIndexOf("/") + 1)
					+ "\"" + end);
			dos.writeBytes(end);

			FileInputStream fis = new FileInputStream(srcPath);
			byte[] buffer = new byte[8192]; // 8k
			int count = 0;
			while ((count = fis.read(buffer)) != -1) {
				dos.write(buffer, 0, count);

			}
			fis.close();

			dos.writeBytes(end);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
			dos.flush();
			dos.close();


			if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream in = httpURLConnection.getInputStream();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer1 = new byte[256];
				int n;
				while ((n = in.read(buffer1)) >= 0) {
					out.write(buffer1, 0, n);
				}
				byte[] b = out.toByteArray();
				in.close();
				seamStr = new String(b);
				System.out.println(seamStr);
			} else {
				seamStr = CONNECTION_TIME_OUT;
			}
		} catch (MalformedURLException e) {
			seamStr = CONNECTION_TIME_OUT;
		} catch (IOException e) {
			seamStr = CONNECTION_TIME_OUT;
		}
		return seamStr;
	}
}
