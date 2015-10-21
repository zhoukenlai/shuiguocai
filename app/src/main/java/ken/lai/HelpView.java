package ken.lai;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

public class HelpView {
	Bitmap bg;
	Bitmap back;
	float fWidthRate;
	float fHigthRate;
	Resources res;
	
	public CaicaiView CCV;
	
	// 初始化
	public void init( CaicaiView ccv, Resources r, int iCardWidth, int iCardHigth )
	{
		CCV = ccv;
		// 根据屏幕的宽度和高度，适当的来决定牌的宽度和高度
		fWidthRate = (float)(iCardWidth)/800;
		fHigthRate = (float)(iCardHigth)/480;
		
		res = r;
		bg    = CreateBitMap.scaleBitmap(res,R.drawable.help,  (int)(800*fWidthRate),(int)(480*fHigthRate));
		back  = CreateBitMap.scaleBitmap(res,R.drawable.back,  (int)(100*fWidthRate),(int)( 60*fHigthRate));
		
	}
	
    // 绘制界面
	public void onDrow( Canvas canvas )
	{
		canvas.drawColor(Color.BLACK);
		canvas.drawBitmap( bg, 0, 0, null );
		canvas.drawBitmap( back,  (int)(550*fWidthRate), (int)(340*fHigthRate), null );
	}
	
	// 触摸事件
	public int onTouch( MotionEvent event )
	{
		// 返回，到主界面
		if (event.getX() > (int)(550*fWidthRate) && event.getX() < (int)(650*fWidthRate)
                && event.getY() > (int)(340*fHigthRate) && event.getY() < (int)(400*fHigthRate)) {
            return 0;
        }
        return -1;	// -1表示界面不用变化
	}
}
