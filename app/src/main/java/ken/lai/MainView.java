package ken.lai;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

// 对应游戏主界面，包含图片和绘制函数
public class MainView {
	Bitmap bg;
	Bitmap help;
	Bitmap start;
	Bitmap exit;
	float fWidthRate;
	float fHigthRate;
	
	// 初始化
	public void init( Resources res, int iCardWidth, int iCardHigth )
	{
		// 根据屏幕的宽度和高度，适当的来决定牌的宽度和高度
		fWidthRate = (float)(iCardWidth)/800;
		fHigthRate = (float)(iCardHigth)/480;
		
		bg    = CreateBitMap.scaleBitmap(res,R.drawable.bgm,   (int)(800*fWidthRate),(int)(480*fHigthRate));
		help  = CreateBitMap.scaleBitmap(res,R.drawable.bhelp, (int)(100*fWidthRate),(int)( 60*fHigthRate));
		start = CreateBitMap.scaleBitmap(res,R.drawable.bstart,(int)(100*fWidthRate),(int)( 60*fHigthRate));
		exit  = CreateBitMap.scaleBitmap(res,R.drawable.bexit, (int)(100*fWidthRate),(int)( 60*fHigthRate));
	}
	
	// 绘图函数
	public void onDrow( Canvas canvas )
	{
		canvas.drawColor(Color.BLACK);
		canvas.drawBitmap( bg, 0, 0, null );
		canvas.drawBitmap( help,  (int)(150*fWidthRate), (int)(340*fHigthRate), null );
		canvas.drawBitmap( start, (int)(350*fWidthRate), (int)(340*fHigthRate), null );
		canvas.drawBitmap( exit,  (int)(550*fWidthRate), (int)(340*fHigthRate), null );
	}
	
	// 触摸事件
	public int onTouch( MotionEvent event )
	{
		// 帮助
		if (event.getX()>(int)(150.0*fWidthRate) && event.getX()<(int)(250.0*fWidthRate)
                && event.getY()>(int)(340.0*fHigthRate) && event.getY()<(int)(400.0*fHigthRate)) {
			return 1;
        }
		// 开始，转到选择关卡界面
		if (event.getX()>(int)(350.0*fWidthRate) && event.getX()<(int)(450.0*fWidthRate)
                && event.getY()>(int)(340.0*fHigthRate) && event.getY()<(int)(400.0*fHigthRate)) {
            return 2;
        }
		// 退出
		if (event.getX()>(int)(550.0*fWidthRate) && event.getX()<(int)(650.0*fWidthRate)
                && event.getY()>(int)(340.0*fHigthRate) && event.getY()<(int)(400.0*fHigthRate)) {
			System.exit(0);
        }
        return 0;
	}
}
