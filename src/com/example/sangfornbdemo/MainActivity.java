package com.example.sangfornbdemo;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sangfor.vpn.IVpnDelegate;
import com.sangfor.vpn.SFException;
import com.sangfor.vpn.auth.SangforNbAuth;
import com.sangfor.vpn.common.VpnCommon;

public class MainActivity extends Activity implements View.OnClickListener,
		IVpnDelegate {
	private final static String TAG = "andli";

	private Button mLoginBtn = null;
	private Button mLogoutBtn = null;
	private Button mTestBtn = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 初始化非阻塞SangforNbAuth单例，需要在主线程处理
		try {
			SangforNbAuth.getInstance().init(this, this);
		} catch (SFException e) {
			e.printStackTrace();
		}

		// 开始初始化VPN
		if (initSslVpn() == false) {
			Log.e(TAG, "init ssl vpn fail.");
		}

		mLoginBtn = (Button) findViewById(R.id.LoginButton);
		mLogoutBtn = (Button) findViewById(R.id.LogoutButton);
		mTestBtn = (Button) findViewById(R.id.TestButton);
		mLoginBtn.setOnClickListener(this);
		mLogoutBtn.setOnClickListener(this);
		mTestBtn.setOnClickListener(this);

		Log.i(TAG, "version is " + SangforNbAuth.getInstance().vpnGetVersion());
	}

	@Override
	public void onDestroy() {
		SangforNbAuth.getInstance().vpnQuit();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (v.equals(mLoginBtn)) {
			doVpnLogin(IVpnDelegate.AUTH_TYPE_PASSWORD);
		} else if (v.equals(mLogoutBtn)) {
			SangforNbAuth.getInstance().vpnLogout();
		} else if (v.equals(mTestBtn)) {
			new TestThread().start();
		}
	}

	/**
	 * 开始初始化VPN，该初始化为异步接口，后续动作通过回调函数通知结果
	 * 
	 * @return 成功返回true，失败返回false，一般情况下返回true
	 */
	private boolean initSslVpn() {
		SangforNbAuth sfAuth = SangforNbAuth.getInstance();

		InetAddress iAddr = null;
		try {
			iAddr = InetAddress.getByName("221.224.118.92");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if (iAddr == null || iAddr.getHostAddress() == null) {
			Log.d(TAG, "vpn host error");
			return false;
		}
		long host = VpnCommon.ipToLong(iAddr.getHostAddress());
		int port = 443;

		if (sfAuth.vpnInit(host, port) == false) {
			Log.d(TAG, "vpn init fail, errno is " + sfAuth.vpnGeterr());
			return false;
		}

		Log.d(TAG, "current vpn status is " + sfAuth.vpnQueryStatus());

		return true;
	}

	/**
	 * 处理认证，通过传入认证类型（需要的话可以改变该接口传入一个hashmap的参数用户传入认证参数）.
	 * 也可以一次性把认证参数设入，这样就如果认证参数全满足的话就可以一次性认证通过，可见下面屏蔽代码
	 * 
	 * @param authType
	 *            认证类型
	 */
	private void doVpnLogin(int authType) {
		Log.d(TAG, "doVpnLogin authType " + authType);

		boolean ret = false;
		SangforNbAuth sForward = SangforNbAuth.getInstance();

		switch (authType) {
		case IVpnDelegate.AUTH_TYPE_CERTIFICATE:
			sForward.setLoginParam(IVpnDelegate.CERT_PASSWORD, "123456");
			sForward.setLoginParam(IVpnDelegate.CERT_P12_FILE_NAME,"/sdcard/csh/csh.p12");
			ret = sForward.vpnLogin(IVpnDelegate.AUTH_TYPE_CERTIFICATE);
			break;
		case IVpnDelegate.AUTH_TYPE_PASSWORD:
			sForward.setLoginParam(IVpnDelegate.PASSWORD_AUTH_USERNAME, "test");
			sForward.setLoginParam(IVpnDelegate.PASSWORD_AUTH_PASSWORD,
					"123456");
			ret = sForward.vpnLogin(IVpnDelegate.AUTH_TYPE_PASSWORD);
			break;
		default:
			Log.w(TAG, "default authType " + authType);
			break;
		}

		if (ret == true) {
			Log.i(TAG, "认证成功！");
			
			//访问接口
			String bs = null;
			try {
				bs = HttpHelp
						.getHttpBackNoZipNoCheck("http://192.168.0.220:8080/MobileOaInterface/updateclient/update.xml");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.i(TAG, "返回值="+bs);
		} else {
			Log.i(TAG, "fail to call login method");
		}
	}

	/**
	 * 初始化vpn、认证或者其它情况均会通过该接口回调，
	 * 
	 * @param vpnResult
	 *            vpn结果，返回的值定义在IVpnDelegate中，例如一下几种
	 * @param authType
	 *            当前认证类型，如果是初始化vpn则是默认认证类型，如果需要下一个认证，则是下一个认证类型，如果认证成功则是无效认证类型，
	 *            其它为无效值
	 */
	@Override
	public void vpnCallback(int vpnResult, int authType) {
		SangforNbAuth sfAuth = SangforNbAuth.getInstance();

		switch (vpnResult) {
		case IVpnDelegate.RESULT_VPN_INIT_FAIL:
			// 初始化vpn失败
			Log.i(TAG, "RESULT_VPN_INIT_FAIL, error is " + sfAuth.vpnGeterr());
			break;

		case IVpnDelegate.RESULT_VPN_INIT_SUCCESS:
			// 初始化vpn成功，接下来就需要开始认证工作了
			Log.i(TAG, "RESULT_VPN_INIT_SUCCESS, current vpn status is "
					+ sfAuth.vpnQueryStatus());

			break;

		case IVpnDelegate.RESULT_VPN_AUTH_FAIL:
			// 认证失败，有可能是传入参数有误，具体信息可通过sfAuth.vpnGeterr()获取
			Log.i(TAG, "RESULT_VPN_AUTH_FAIL, error is " + sfAuth.vpnGeterr());
			break;

		case IVpnDelegate.RESULT_VPN_AUTH_SUCCESS:
			// 认证成功，认证成功有两种情况，一种是认证通过，可以使用sslvpn功能了，另一种是前一个认证（如：用户名密码认证）通过，
			// 但需要继续认证（如：需要继续证书认证）
			if (authType == IVpnDelegate.AUTH_TYPE_NONE) {
				Log.i(TAG, "welcom to sangfor sslvpn!");
			} else {
				Log.i(TAG,
						"auth success, and need next auth, next auth type is "
								+ authType);

				doVpnLogin(authType);
			}
			break;
		case IVpnDelegate.RESULT_VPN_AUTH_LOGOUT:
			// 主动注销（自己主动调用logout接口）或者被动注销（通过控制台把用户踢掉）均会调用该接口
			Log.i(TAG, "RESULT_VPN_AUTH_LOGOUT");
			break;
		default:
			// 其它情况，不会发生，如果到该分支说明代码逻辑有误
			Log.i(TAG, "RESULT_VPN_AUTH_LOGOUT");
			break;
		}
	}

	/**
	 * 认证过程若需要图形校验码，则回调通告图形校验码位图，
	 * 
	 * @param data
	 *            图形校验码位图
	 */
	@Override
	public void vpnRndCodeCallback(byte[] data) {
		Log.d(TAG, "RndCode callback, the data is bitmap of rndCode.");
	}

	/** 测试TCP资源 */
	private class TestThread extends Thread {
		@Override
		public void run() {
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpConnectionParams.setConnectionTimeout(params, 3000);

			HttpPost post = new HttpPost("http://bbs");
			DefaultHttpClient client = new DefaultHttpClient(params);
			try {
				StringBuffer sb = new StringBuffer();
				HttpResponse response = client.execute(post);
				InputStream is = response.getEntity().getContent();
				int b = 0;
				// 顺序读取文件text里的内容并赋值给整型变量b,直到文件结束为止。
				while ((b = is.read()) != -1) {
					sb.append((char) b);
				}
				System.out.print(sb.toString());
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
