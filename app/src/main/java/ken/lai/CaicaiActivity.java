package ken.lai;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import com.adsmogo.adview.AdsMogoLayout;
import com.adsmogo.util.AdsMogoLayoutPosition;
import com.adsmogo.util.AdsMogoSize;
import com.baidu.appx.BDBannerAd;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class CaicaiActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		Random rdm = new Random(System.currentTimeMillis());
		iRdm = rdm.nextInt(2); 
		
		// 取参数以及保存游戏参数
		SharedPreferences settings = this.getSharedPreferences("CAICAI",0);
		iiID = settings.getInt("iID", 111);
		iiSelectMax = settings.getInt("iSelectMax", 1);
		iiTotal = settings.getInt("iTotal", 0);
		iiMax = settings.getInt("iMax", 0);

        AsyGetInfo();
		
		CCV = new CaicaiView(this,this);
		LayoutParams ccvPar = new
		LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT );
		
		adsMogoLayoutCode = new AdsMogoLayout(this, "0282aeb5a37b487191d1bbef27fbfd12",
				AdsMogoLayoutPosition.CENTER_BOTTOM,AdsMogoSize.AdsMoGoBanner,false);
		adsMogoLayoutCode.setAdsMogoListener(null);
		
		top = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		top.addRule(RelativeLayout.CENTER_HORIZONTAL);
		top.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		
		setContentView(R.layout.game);
		parent = (RelativeLayout)findViewById(R.id.layout_parent);
		parent.addView(CCV,ccvPar);
		
		// 创建广告视图
		// 发布时请使用正确的ApiKey和广告位ID
		// 此处ApiKey和推广位ID均是测试用的
		// 您在正式提交应用的时候，请确认代码中已经更换为您应用对应的Key和ID
		// 具体获取方法请查阅《百度开发者中心交叉换量产品介绍.pdf》
		bannerAdView = new BDBannerAd(this, 
				"pYeSl3eGD0ohYaTVz2NvnPsY","cP8uENWzpOn4i034F0kZVWST");

		// 设置横幅广告展示尺寸，如不设置，默认为SIZE_FLEXIBLE;
		bannerAdView.setAdSize(BDBannerAd.SIZE_320X50);
		
    }
    
    public int iRdm = 1;
    public int iiID = 0;
    public int iiSelectMax = 0;
    public int iiTotal = 0;
    public int iiMax = 0;
    public String sAdFlag = "";

    public void AsyGetInfo() {
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            String stringUrl = "http://zhkl.sinaapp.com/caicai";
            new DownloadWebpageText().execute(stringUrl);
        }
    }

    public class DownloadWebpageText extends AsyncTask {
        @Override
        protected String doInBackground(Object[] params) {
            try {
                return downloadUrl(params[0].toString());
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(Object results)
        {
            sAdFlag = results.toString();
        }
    }

    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        String sResult;
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            is = conn.getInputStream();
            sResult = readIt(is, len);
        } finally {
            if (is != null) {
                is.close();
            }
        }

//        NameValuePair pair1 = new BasicNameValuePair("a",""+iiSelectMax);
//        NameValuePair pair2 = new BasicNameValuePair("b",""+iiTotal);
//        NameValuePair pair3 = new BasicNameValuePair("c",""+iiMax);
//        NameValuePair pair4 = new BasicNameValuePair("d",""+iiID);

//        try
//        {
//            URL url = new URL(myurl);
//            HttpURLConnection conn1 = (HttpURLConnection) url.openConnection();
//            conn1.setReadTimeout(10000 /* milliseconds */);
//            conn1.setConnectTimeout(15000 /* milliseconds */);
//            conn1.setRequestMethod("POST");
//            conn1.setDoOutput(true);
//            conn1.setDoInput(true);
//            conn1.connect();
//
//            String para = "a=213";
//            PrintWriter pw = new PrintWriter(conn1.getOutputStream());
//            pw.print(para);
//            pw.flush();
//            pw.close();
//
//            is = conn1.getInputStream();
//            String sResult1 = readIt(is, 500);
//            Log.d("XSXS", sResult1);
//        }
//        catch (Exception e)
//        {
//            Log.d("XSXS", e.toString());
//            e.printStackTrace();
//        }

        return sResult;
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException {
        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        int i = reader.read(buffer);
        if ( i>1 ) i--;
        return new String(buffer,0,i);
    }
        
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
        	CCV.backView();
        	return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    // 界面
    public CaicaiView CCV;
    public RelativeLayout parent;
    public LayoutParams top;
    
    public AdsMogoLayout adsMogoLayoutCode;
    public BDBannerAd bannerAdView;
	    
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
    protected void onDestroy() {
    	super.onDestroy();
	}
    
    // 取具体的广告视图
    public View getAdView()
    {
    	if ( sAdFlag.isEmpty() )
    		sAdFlag = "2";
    	if ( sAdFlag.substring(0,1).equals("1") )
    	{
    		return adsMogoLayoutCode;
    	}
    	
    	if ( sAdFlag.substring(0,1).equals("3") && iRdm==1 )
    	{
    		return adsMogoLayoutCode;
    	}
    	
    	return bannerAdView;
    }
}