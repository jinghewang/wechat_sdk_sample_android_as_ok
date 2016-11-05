package com.kanguo.hbd;


import org.json.JSONObject;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PayActivity extends Activity {
	
	private IWXAPI api;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay);
		
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.registerApp(Constants.APP_ID);


        Button appayBtn = (Button) findViewById(R.id.appay_btn);
		appayBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//String url = "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android";
                TextView orderid = (TextView) findViewById(R.id.tx_orderid);
                if (orderid.getText().length() ==0)
                    Toast.makeText(PayActivity.this,"请输入订单号",Toast.LENGTH_LONG);

				String url = "http://www.hbdworld.com.cn/v_4_app/wxpay/index?order_id="+ orderid.getText()  +"&token=a178c5bbd289510eefb02d0043b03d02";

				Button payBtn = (Button) findViewById(R.id.appay_btn);
				payBtn.setEnabled(false);
				Toast.makeText(PayActivity.this, "获取订单中...", Toast.LENGTH_SHORT).show();
		        try{
					byte[] buf = Util.httpGet(url);
					if (buf != null && buf.length > 0) {
						String content = new String(buf);
						Log.e("get server pay params:",content);
			        	JSONObject json = new JSONObject(content); 
						if(null != json && !json.has("retcode") ){
							PayReq req = new PayReq();
							json = json.getJSONObject("data");
							//req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
							req.appId			= json.getString("appId");
							req.partnerId		= json.getString("partnerId");
							req.prepayId		= json.getString("prepayId");
							req.nonceStr		= json.getString("nonceStr");
							req.timeStamp		= json.getString("timeStamp");
							req.packageValue	= json.getString("packageValue");
							req.sign			= json.getString("sign");
							req.extData			= "app data"; // optional
							Toast.makeText(PayActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
							// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
							api.sendReq(req);
						}else{
				        	Log.d("PAY_GET", "返回错误"+json.getString("retmsg"));
				        	Toast.makeText(PayActivity.this, "返回错误"+json.getString("retmsg"), Toast.LENGTH_SHORT).show();
						}
					}else{
			        	Log.d("PAY_GET", "服务器请求错误");
			        	Toast.makeText(PayActivity.this, "服务器请求错误", Toast.LENGTH_SHORT).show();
			        }
		        }catch(Exception e){
		        	Log.e("PAY_GET", "异常："+e.getMessage());
		        	Toast.makeText(PayActivity.this, "异常："+e.getMessage(), Toast.LENGTH_SHORT).show();
		        }
		        payBtn.setEnabled(true);
			}
		});


		Button checkPayBtn = (Button) findViewById(R.id.check_pay_btn);
		checkPayBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
				Toast.makeText(PayActivity.this, String.valueOf(isPaySupported), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
}
