package ken.lai;

import java.io.IOException;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CaicaiView extends SurfaceView implements SurfaceHolder.Callback{

	public CaicaiView(Context context,CaicaiActivity cact) {
		super(context);
		getHolder().addCallback(this);
		this.cact = cact;
		
		// 读取系统保存的游戏参数
		SharedPreferences settings = cact.getSharedPreferences("CAICAI",0);
		iSelectMax = settings.getInt("iSelectMax", 1);
		iTotal = settings.getInt("iTotal", 0);
		iMax = settings.getInt("iMax", 0);
		iID = settings.getInt("iID", 0);
		if ( iID == 0 )
		{
			Random r = new Random();
			iID = (r.nextInt(8999)+1000)*100+r.nextInt(89)+10;
		}
		iDaojuF = settings.getInt("iDaojuF",1);
		iDaojuD = settings.getInt("iDaojuD",1);
		iDaojuB = settings.getInt("iDaojuB",1);
		
		// 背景音效，暂时不开放
		//bjyx = MediaPlayer.create(this.cact,R.raw.bjyx);
	    if ( bjyx != null )
	    {
	    	bjyx.setLooping(true);
	    }
	    
	    // 点击，图案不一致时
	    noyx = MediaPlayer.create(this.cact,R.raw.no);
	    if ( noyx != null )
	    {
	    	noyx.setLooping(false);
	    }
	    
	    // 点击，图案一致时
	    yesyx = MediaPlayer.create(this.cact,R.raw.yes);
	    if ( yesyx != null )
	    {
	    	yesyx.setLooping(false);
	    }
	}

	private TutorialThread thread;
	public CaicaiActivity cact;
	public int iSelect;		// 当前选择的关卡
	public int iSelectMax;	// 已经玩到多少关了
	public int iTotal;		// 总共的积分
	public int iMax;		// 挑战关的最高得分
	public int iID;
	public int iDaojuF;
	public int iDaojuD;
	public int iDaojuB;
	
	public MediaPlayer bjyx;
	public MediaPlayer yesyx;
	public MediaPlayer noyx;
	public boolean bYinxiao = true;	// 默认开启音效
	
	// 保存游戏参数
	public void SavePara()
	{
		SharedPreferences settings = cact.getSharedPreferences("CAICAI", 0);
        SharedPreferences.Editor editor = settings.edit();

        if ( iSelectMax<iSelect )
			iSelectMax = iSelect;
		editor.putInt("iSelectMax", iSelectMax);
        editor.putInt("iTotal", iTotal);
        editor.putInt("iMax", iMax);
        editor.putInt("iID", iID);
        editor.putInt("iDaojuF", iDaojuF);
        editor.putInt("iDaojuD", iDaojuD);
        editor.putInt("iDaojuB", iDaojuB);

        editor.apply();
	}
	
	// 所有的视图
	public int ViewType = 0;
	MainView MV;	// 主界面  		ViewType = 0
	HelpView HV;	// 帮助界面		ViewType = 1
	SelectView SV;	// 选择关卡界面	ViewType = 2
	GameView GV;	// 游戏界面		普通：ViewType = 3		挑战：ViewType = 4
	
	// 初始化界面，界面类型变化时都要初始化
	public void initView( int vType )
	{
		DisplayMetrics dm = new DisplayMetrics();
        this.cact.getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        if ( vType == 0 && MV == null )	// 主界面
		{
        	MV = new MainView();
			MV.init(getResources(),dm.widthPixels,dm.heightPixels);
		}
        if ( vType == 0 && HV == null )	// 主界面
		{
        	HV = new HelpView();
        	HV.init(this,getResources(),dm.widthPixels,dm.heightPixels);
		}
		if ( vType == 2 && SV == null )	// 选择关卡界面
		{
			SV = new SelectView();
			SV.init(this,getResources(),dm.widthPixels,dm.heightPixels);
		}
		if ( vType == 3 )	// 普通游戏界面
		{
			if ( GV == null )
			{
				GV = new GameView();
				GV.init(this,getResources(),dm.widthPixels,dm.heightPixels);
				GV.setPara(1,3,6,7,60);
			}
			GV.model = 0;
		}
		if ( vType == 4 )	// 挑战游戏界面
		{
			if ( GV == null )
			{
				GV = new GameView();
				GV.init(this,getResources(),dm.widthPixels,dm.heightPixels);
				GV.setPara(1,3,6,7,60);
			}
			GV.model = 1;
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initView(ViewType);
		this.thread = new TutorialThread(getHolder(),this);
		this.thread.setFlag(true);
		this.thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setFlag(false);
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException ignored) {
			}
		}
	}
	
	// 不同的VIEW切换
	public void changeView( int vType )
	{
		if ( ViewType == vType )
			return;
		
		int oldType = ViewType;
		
		// 初始化切换后的VIEW
		initView(vType);
		
		// 改变全局参数，改变后，绘图线程中会立即生效
		ViewType = vType;
		
		// 控制只有在游戏界面才播放背景音乐
		if ( ViewType == 3 || ViewType == 4 )
		{
			GV.reset();
			if ( bjyx != null && bYinxiao )
			{
				try {
					bjyx.prepare();
					bjyx.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else
		{
			if ( bjyx != null  && bYinxiao )
				bjyx.stop();
		}
		
		// 释放原来的VIEW资源
		delView(oldType);
	}
	
	// 返回上一个界面
	public void backView()
	{
		int vType = -1;
		if ( ViewType == 3 || ViewType == 4 )
		{
			if ( bjyx != null && bYinxiao )
			{
				bjyx.stop();
			}
			vType = 2;
		}
		if ( ViewType == 2 || ViewType == 1 )
			vType = 0;
		if ( ViewType == 0 )
		{
			System.exit(0);
			return;
		}
		
		int oldType = ViewType;
		
		// 初始化切换后的VIEW
		initView(vType);

		if ( GV!=null && GV.iShowAd==1 && vType == 2 && (ViewType == 3 || ViewType == 4) )	// 从游戏返回时
		{
			GV.iShowAd = 0;
			if ( cact.getAdView()!=null )
				cact.parent.removeView(cact.getAdView());
		}
		
		// 改变全局参数，改变后，绘图线程中会立即生效
		ViewType = vType;
		
		// 释放原来的VIEW资源
		delView(oldType);
	}
	
	public void delView( int vType )
	{
		if ( vType == 0 && MV != null )
		{
			MV=null;
			System.gc();
		}
		if ( vType == 1 && HV != null )
		{
			HV=null;
			System.gc();
		}
		if ( vType == 2 && SV != null )
		{
			SV=null;
			System.gc();
		}
		if ( (vType == 3||vType == 4) && GV != null )
		{
			GV=null;
			System.gc();
		}
	}
	
	public void onDraw1(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		if ( ViewType == 0 )
		{
			MV.onDrow(canvas);
		}
		if ( ViewType == 1 )
		{
			HV.onDrow(canvas);
		}
		if ( ViewType == 2 )
		{
			SV.onDrow(canvas);
		}
		if ( ViewType == 3 || ViewType == 4 )
		{
			GV.onDrow(canvas);
		}
		if ( ViewType == 8 )	// 拍照界面
		{
			Intent intent = new Intent(cact, StorageBoxActivity.class);
			cact.startActivity(intent);
			cact.finish();
			ViewType = 999;
		}
		if ( ViewType == 7 )	// 没费获取道具界面
		{
			Intent intent = new Intent(cact,DaojuActivity.class);
			cact.startActivity(intent);
			cact.finish();
			ViewType = 999;
		}
	}
	
	// 绘图 
	class TutorialThread extends Thread {

		private final SurfaceHolder surfaceHolder;
		CaicaiView CV;
		private boolean flag = false;

		public TutorialThread(SurfaceHolder surfaceHolder,CaicaiView cv) {
			this.surfaceHolder = surfaceHolder;
			this.CV = cv;
		}
		
		public void setFlag(boolean flag) {
			this.flag = flag;
		}
		
		public void run() {
			Canvas c;
			
			while (this.flag) {
				c = null;
				try {
					c = this.surfaceHolder.lockCanvas(null);
					if ( c != null ) {
						synchronized (this.surfaceHolder) {
							CV.onDraw1(c);
						}
					}
				} finally {
					if (c != null) {
						this.surfaceHolder.unlockCanvasAndPost(c);
					}
				}
				
				if ( GV!=null )
				{
					GV.Ctrl();
				}
				
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// 判断是否需要展示广告
	void Ad( int vType )
	{
		if ( GV == null || cact.getAdView() == null ) return;

		if ( (ViewType == 3 || ViewType == 4) && GV.iShowAd == 0 )
		{
			GV.iShowAd = 1;
			cact.parent.addView(cact.getAdView(),cact.top);
		}

		if ( vType == 2 && (ViewType == 3 || ViewType == 4) )	// 从游戏返回时
		{
			GV.iShowAd = 0;
			cact.parent.removeView(cact.getAdView());
		}
	}
	
	// 触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
        if ( event.getAction() == MotionEvent.ACTION_DOWN) {
        	onTouch(event);
        }
        return super.onTouchEvent(event);
    }
	
	public void onTouch(MotionEvent event) {
		int iRet = -1;
		if ( ViewType == 0 )
		{
			iRet = MV.onTouch(event);
		}
		if ( ViewType == 1 )
		{
			iRet = HV.onTouch(event);
		}
		if ( ViewType == 2 )
		{
			iRet = SV.onTouch(event);
		}
		if ( ViewType == 3 || ViewType == 4 )
		{
			iRet = GV.onTouch(event);
		}
		
		Ad(iRet);	// 暂时只想到此处判断是否显示广告
		
		if ( iRet != -1 )
		{	
			changeView(iRet);
		}
	}
}
