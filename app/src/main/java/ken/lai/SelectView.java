package ken.lai;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class SelectView {
	Bitmap bg;
	Bitmap start;
	Bitmap back;
	Bitmap bpai;
	Bitmap hqdj;
	float fWidthRate;
	float fHigthRate;
	Resources res;
	
	Bitmap selectb;	
	Bitmap selectbd;
	Bitmap []select;
	Bitmap []bShow;
	
	public CaicaiView CCV;
	
	// 初始化
	public void init( CaicaiView ccv, Resources r, int iCardWidth, int iCardHigth )
	{
		CCV = ccv;
		// 根据屏幕的宽度和高度，适当的来决定牌的宽度和高度
		fWidthRate = (float)(iCardWidth)/800;
		fHigthRate = (float)(iCardHigth)/480;
		
		res = r;
		bg    = CreateBitMap.scaleBitmap(res,R.drawable.bg,    (int)(800*fWidthRate),(int)(480*fHigthRate));
		start = CreateBitMap.scaleBitmap(res,R.drawable.bspecial,(int)(100*fWidthRate),(int)( 60*fHigthRate));
		back  = CreateBitMap.scaleBitmap(res,R.drawable.back,  (int)(100*fWidthRate),(int)( 60*fHigthRate));
		bpai  = CreateBitMap.scaleBitmap(res,R.drawable.pai, (int)(100*fWidthRate),(int)(60*fHigthRate));
		hqdj  = CreateBitMap.scaleBitmap(res,R.drawable.hqdj, (int)(100*fWidthRate),(int)(60*fHigthRate));
		
		selectb  = CreateBitMap.scaleBitmap(res,R.drawable.selectb, (int)(70*fWidthRate),(int)(70*fHigthRate));
		selectbd = CreateBitMap.scaleBitmap(res,R.drawable.selectbd,(int)(70*fWidthRate),(int)(70*fHigthRate));
		
		// 关卡图片初始化
		select = new Bitmap[22];
		for ( int i=1; i<=21; i++ )
		{
			int indentify = res.getIdentifier("ken.lai:drawable/s"+i, null, null);
			select[i] = CreateBitMap.scaleBitmap(res,indentify,(int)(70*fWidthRate),(int)(70*fHigthRate));
		}
		
		// 成就图片初始化
		bShow = new Bitmap[8];
		for ( int i=1; i<=7; i++ )
		{
			int indentify = res.getIdentifier("ken.lai:drawable/q"+i, null, null);
			bShow[i] = CreateBitMap.scaleBitmap(res,indentify,(int)(300*fWidthRate),(int)(80*fHigthRate));
		}
		
		// 初始化画笔
		String familyName = "宋体";
	    Typeface font = Typeface.create(familyName,Typeface.BOLD);
	    p.setColor(Color.rgb(255,0,0));
	    p.setTypeface(font);
	    p.setTextSize(23*fWidthRate);
	    p.setAntiAlias(true);
	}
	
	Paint p = new Paint();
	
    // 绘制界面
	public void onDrow( Canvas canvas )
	{
		canvas.drawColor(Color.BLACK);
		canvas.drawBitmap( bg, 0, 0, null );
		canvas.drawBitmap( hqdj,  (int)(50*fWidthRate), (int)(340*fHigthRate), null );
		canvas.drawBitmap( bpai,  (int)(200*fWidthRate), (int)(340*fHigthRate), null );
		canvas.drawBitmap( start, (int)(400*fWidthRate), (int)(340*fHigthRate), null );
		canvas.drawBitmap( back,  (int)(600*fWidthRate), (int)(340*fHigthRate), null );
		
		canvas.drawText(CCV.cact.getString(R.string.highscore)+CCV.iMax,400*fWidthRate,420*fHigthRate,p);
		
		int iCnt = 1;
		for ( int i = 1; i <= 3; i++ )		
		{
			for ( int j = 1; j <= 7; j++ )	
			{
				if ( iCnt <= CCV.iSelectMax )
					canvas.drawBitmap(selectb, ((j-1)*105+35)*fWidthRate, ((i-1)*100+30)*fHigthRate, null);
				else	
					canvas.drawBitmap(selectbd, ((j-1)*105+35)*fWidthRate, ((i-1)*100+30)*fHigthRate, null);
				
				canvas.drawBitmap(select[iCnt], ((j-1)*105+35)*fWidthRate, ((i-1)*100+30)*fHigthRate, null);
				iCnt++;
			}
		}
		
		int iIdx = 0;
		if ( CCV.iTotal <= 50 )	// 幼儿园
        	iIdx=1;
	    else if ( CCV.iTotal <= 100 )	// 小学生
        	iIdx=2;
	    else if ( CCV.iTotal <= 200 )	// 初中生
        	iIdx=3;
	    else if ( CCV.iTotal <= 400 )	// 高中生
        	iIdx=4;
	    else if ( CCV.iTotal <= 600 )	// 大学生
        	iIdx=5;
	    else if ( CCV.iTotal <= 900 )	// 硕士
        	iIdx=6;
	    else if ( CCV.iTotal > 900 )	// 博士
        	iIdx=7;
        
        if ( iIdx != 0 )
        	canvas.drawBitmap(bShow[iIdx], 0, 400*fHigthRate, null);
	}
	
	// 触摸事件
	public int onTouch( MotionEvent event )
	{
		// 判断选择的是哪一关
		for ( int i = 1; i <= 21; i++ )
		{
			int x = (i-1)/7+1;	
			int y = (i-1)%7+1;	
			
			int left = (int)(((y-1)*(70+35)+35)*fWidthRate);
			int right = left+selectb.getWidth();
			int top = (int)(((x-1)*(70+30)+30)*fHigthRate);
			int bottom = top+selectb.getHeight();
			
			// 进入普通游戏界面
			if ( event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom )
			{
				if ( i<= CCV.iSelectMax )	// 只能选择解锁的关卡
				{
					CCV.iSelect = i;
					return 3;
				}
				return -1;
			}
		}
		
		// 免费获取道具
		if (event.getX() > (int)(50*fWidthRate) && event.getX() < (int)(150*fWidthRate)
                && event.getY() > (int)(340*fHigthRate) && event.getY() < (int)(400*fHigthRate)) {
			return 7;
        }
		// 通过摄像头定制游戏
		if (event.getX() > (int)(200*fWidthRate) && event.getX() < (int)(300*fWidthRate)
                && event.getY() > (int)(340*fHigthRate) && event.getY() < (int)(400*fHigthRate)) {
			return 8;
        }
		// 挑战
		if (event.getX() > (int)(400*fWidthRate) && event.getX() < (int)(500*fWidthRate)
                && event.getY() > (int)(340*fHigthRate) && event.getY() < (int)(400*fHigthRate)) {
            return 4;
        }
		// 返回，到主界面
		if (event.getX() > (int)(600*fWidthRate) && event.getX() < (int)(700*fWidthRate)
                && event.getY() > (int)(340*fHigthRate) && event.getY() < (int)(400*fHigthRate)) {
            return 0;
        }
        return -1;	// -1表示界面不用变化
	}
}
