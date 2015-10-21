package ken.lai;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DaojuActivity extends Activity implements
		SurfaceHolder.Callback {

	public Button bOK;
	public Button bBack;
	public EditText TV;
	String sNote;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.daoju);
		TV = (EditText) findViewById(R.id.in);
		bOK = (Button) findViewById(R.id.bOK);
		bOK.setOnClickListener(new OKLintener());
		bBack = (Button) findViewById(R.id.bBack);
		bBack.setOnClickListener(new BackLintener());
		
//		int points=OffersManager.getPoints(this);
//		sNote = TV.getText().toString();
//		TV.setText(sNote+points);
		
	}
	
	void GetDaoju()
	{		
		SharedPreferences settings = this.getSharedPreferences("CAICAI",0);
		int iDaojuF = settings.getInt("iDaojuF",3);
		int iDaojuD = settings.getInt("iDaojuD",3);
		int iDaojuB = settings.getInt("iDaojuB",3);
		//long ltime  = settings.getLong("ltime",0);
		String sDaoju2 = settings.getString("sDaoju2","");
		String sDaoju5 = settings.getString("sDaoju5","");
		
		sNote = TV.getText().toString();
		if ( !sNote.equals("2011") && !sNote.equals("2321")
				&& !sNote.equals("2761") && !sNote.equals("5888") && !sNote.equals("5868") )
		{
			Toast.makeText(this, R.string.noted, Toast.LENGTH_LONG).show();
			return;
		}
		
		int iAdd = 0;
		if ( sNote.substring(0,1).equals("2") )
		{
			if ( sDaoju2.indexOf(sNote)!=-1 )
			{
				Toast.makeText(this, R.string.noted, Toast.LENGTH_LONG).show();
				return;
			}
			sDaoju2 += sNote;
			iAdd = 3;
		}
		else if ( sNote.substring(0,1).equals("5") )
		{
			if ( sDaoju2.indexOf(sNote)!=-1 )
			{
				Toast.makeText(this, "购买码不正确，请添加微信号“kkll2017”通过红包购买道具", Toast.LENGTH_LONG).show();
				return;
			}
			else {
				sDaoju5 += sNote;
				iAdd = 10;
			}
		}
		
		//long timenow = System.currentTimeMillis();
		//if ( (timenow-ltime)>24*60*60*1000 )
		//{
			SharedPreferences.Editor editor = settings.edit();
	
	        editor.putInt("iDaojuF", iDaojuF+iAdd);
	        editor.putInt("iDaojuD", iDaojuD+iAdd);
	        editor.putInt("iDaojuB", iDaojuB+iAdd);
	        editor.putString("sDaoju2", sDaoju2);
	        editor.putString("sDaoju5", sDaoju5);
	        //editor.putLong("ltime", timenow);
	
	        editor.apply();
	        Toast.makeText(this, "购买道具成功，祝你玩得愉快", Toast.LENGTH_LONG).show();
	        //Toast.makeText(this, ""+timenow+"|"+ltime, Toast.LENGTH_LONG).show();
		//}
		//else
		//{
		//	Toast.makeText(this, "一天最多只能免费获取一次道具，请明天再来", Toast.LENGTH_LONG).show();
		//}
	}
	
	class OKLintener implements OnClickListener
    {
		@Override
		public void onClick(View arg0) {
			GetDaoju();
		}
	}
	
//	class WallLintener implements OnClickListener
//    {
//		@Override
//		public void onClick(View arg0) {
//			OffersManager.setAppSid("a5e189cd");
//			OffersManager.setAppSec("a5e189cd");
//			OffersManager.showOffers(DaojuActivity.this);
//		}
//	}

	class BackLintener implements OnClickListener
    {
		@Override
		public void onClick(View arg0) {
			exit();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	void exit()
	{
		Intent intent = new Intent(this,CaicaiActivity.class);
		startActivity(intent);
		DaojuActivity.this.finish();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
        	exit();
        	return true;
			//return super.onKeyDown(keyCode, event);
        }
        else 
        {
        	return super.onKeyDown(keyCode, event);
        }
    }
	
	public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
	 
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
}
