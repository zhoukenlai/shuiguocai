package ken.lai;

import java.io.IOException;
import java.util.Random;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class GameView {
	
	// 牌种类 以及 游戏总布局
	Bitmap[] card;
	CGuessUnit[] game;
	
	// 相关游戏资源定义
	Bitmap bmCardBg;
	Bitmap bmCardSelect;
	Bitmap bmDian;
	Bitmap bg;
	Bitmap daoju;
	Bitmap df;
	Bitmap db;
	Bitmap dd;
	Bitmap reset;
	Bitmap back;
	Bitmap open;
	Bitmap close;
	Bitmap timebar;
	Bitmap ice;
	Bitmap timeleft;
	Bitmap over1;
	Bitmap over2;
	Bitmap bfeng;
	
	public CaicaiView CCV;
	
	// 长宽比率
	float fWidthRate;
	float fHigthRate;
	
	int iCardWidth;		// 牌的宽度
	int iCardHigth;		// 牌的高度
	int iShowFlag = 1;	// 1 展示在底部 0展示在顶部
	int iRows = 4;		// 布局为几行
	int iCols = 6;		// 布局为几列
	int iCardCnt = 6;
	int iBlank = 3;		// 牌之间的间隔
	int ads = 70;		// 广告区高度
	int ctrl = 150;		// 控制区宽度
	
	// 设置牌的行列
	public void setPara(int ShowFlag,int row,int col,int CardCnt,int adss)
	{
		iShowFlag = ShowFlag;
		iRows = row;
		iCols = col;
		iCardCnt = CardCnt;
		ads = adss;
	}
	
	// 先从存储卡加载图片，没找到则取默认
	public void CreateBM( Resources res, int i )
	{
		card[i] = CreateBitMap.scaleBitmap("card"+i+".jpg",(int)(iCardWidth*fWidthRate),(int)(iCardHigth*fHigthRate));
		if ( card[i] == null )
		{
			int indentify = res.getIdentifier("ken.lai:drawable/card"+i, null, null);
			card[i] = CreateBitMap.scaleBitmap(res,indentify,(int)(iCardWidth*fWidthRate),(int)(iCardHigth*fHigthRate));
		}
	}
	
	Random rdm = new Random(System.currentTimeMillis());
	int timemax = 60;
	int iDian = 0;
	int iCnt = 0;	// 控制选中的两张牌的闪烁
	long itimeleft = timemax;
	int iNowSucc = 0;
	Resources m_res;
	
	// 洗牌
	public void reset()
	{
		bResetOK = false;
		iSelectCard1 = -1;
		iSelectCard2 = -1;
		iOver = 0;
		itimeleft = timemax;
		timelast = System.currentTimeMillis();
		iNowSucc = 0;
		iSucc = 0;
		iClick = 0;
		iHeihei = 0;
		
		// 根据关卡挑战游戏布局难道
		if ( CCV.iSelect <= 3 )
		{
			iRows = 4;
			iCols = 6;
			iCardCnt = 6;
		}
		else if ( CCV.iSelect <= 8 )
		{
			iRows = 4;
			iCols = 7;
			iCardCnt = 7;
		}
		else if ( CCV.iSelect <= 12 )
		{
			iRows = 4;
			iCols = 8;
			iCardCnt = 8;
		}
		else if ( CCV.iSelect <= 18 )
		{
			iRows = 5;
			iCols = 8;
			iCardCnt = 9;
		}
		else
		{
			iRows = 5;
			iCols = 8;
			iCardCnt = 10;
		}
		
		if ( model == 1 )
		{
			iCardCnt = 10;
			iRows = 4;
			iCols = 7;
		}
		
		iCardWidth = (800-ctrl-iBlank*(iCols+1))/iCols;
		iCardHigth = (480-ads-iBlank*(iRows+1))/iRows;
		bmCardSelect = CreateBitMap.scaleBitmap(m_res,R.drawable.select,(int)(iCardWidth*fWidthRate),(int)(iCardHigth*fHigthRate));
		bmCardBg     = CreateBitMap.scaleBitmap(m_res,R.drawable.cardbg,(int)(iCardWidth*fWidthRate),(int)(iCardHigth*fHigthRate));
		
		// 因为图片的大小变化了，需要重新生成
		for ( int i=1; i<=10; i++ )
		{
			CreateBM( m_res, i );
		}
		
		int tmp[] = new int[iCardCnt+1];
		for ( int i=0; i<iCardCnt+1; i++ )
			tmp[i] = 0;
		for( int i=1; i<=iRows; i++ )
		{
			for ( int j=1; j<=iCols; j++ )
			{
				int iSeq = (i-1)*iCols + j;
				game[iSeq] = new CGuessUnit();
				boolean bFind = false;
				if ( iSeq>((iRows*iCols)-iCardCnt) )
				{
					for ( int itmp=1; itmp<iCardCnt+1; itmp++ )
					{
						if ( tmp[itmp]%2 == 1 )
						{
							//game[iSeq].card = card[itmp];
							game[iSeq].card = itmp;
							tmp[itmp]++;
							bFind = true;
							break;
						}
					}
				}

				if ( !bFind )
				{
					int iRand = Math.abs(rdm.nextInt())%iCardCnt+1;
					tmp[iRand]++;
					//game[iSeq].card = card[iRand];
					game[iSeq].card = iRand;
				}
				
				// 布局中一张牌的坐标，点击事件中根据这个来判断
				int iBlankTmp = (int)((iBlank)*fHigthRate);
				game[iSeq].iLeft = (j-1)*((int)(iCardWidth*fWidthRate)+iBlankTmp)+iBlankTmp;
				game[iSeq].iRight = game[iSeq].iLeft + (int)(iCardWidth*fWidthRate);
				game[iSeq].iTop = (i-1)*((int)(iCardHigth*fHigthRate)+iBlankTmp)+iBlankTmp
						+(int)(ads*fHigthRate)*iShowFlag;
				game[iSeq].iBottom = game[iSeq].iTop + (int)(iCardHigth*fHigthRate);
			}
		}
		bResetOK = true;
	}
	
	// 初始化
	int m_iWidthT;
	int m_iHigthT;
	public void init( CaicaiView ccv,Resources res, int iWidthT, int iHigthT )
	{
		CCV = ccv;
		m_iWidthT = iWidthT;
		m_iHigthT = iHigthT;
		m_res = res;
		// 根据屏幕的宽度和高度，适当的来决定牌的宽度和高度
		fWidthRate = (float)(iWidthT)/800;
		fHigthRate = (float)(iHigthT)/480;
		
		// 背景按钮等
		bmDian       = CreateBitMap.scaleBitmap(res,R.drawable.dian,(int)(800*fWidthRate),(int)(480*fHigthRate));
		bg           = CreateBitMap.scaleBitmap(res,R.drawable.bg,(int)(800*fWidthRate),(int)(480*fHigthRate));
		daoju        = CreateBitMap.scaleBitmap(res,R.drawable.daoju,(int)(70*fWidthRate),(int)(200*fHigthRate));
		df           = CreateBitMap.scaleBitmap(res,R.drawable.df,(int)(55*fWidthRate),(int)(60*fHigthRate));
		db           = CreateBitMap.scaleBitmap(res,R.drawable.db,(int)(55*fWidthRate),(int)(60*fHigthRate));
		dd           = CreateBitMap.scaleBitmap(res,R.drawable.dd,(int)(55*fWidthRate),(int)(60*fHigthRate));
		reset        = CreateBitMap.scaleBitmap(res,R.drawable.reset,(int)(100*fWidthRate),(int)(60*fHigthRate));
		back         = CreateBitMap.scaleBitmap(res,R.drawable.back, (int)(100*fWidthRate),(int)(60*fHigthRate));
		open         = CreateBitMap.scaleBitmap(res,R.drawable.open, (int)(100*fWidthRate),(int)(60*fHigthRate));
		close        = CreateBitMap.scaleBitmap(res,R.drawable.close,(int)(100*fWidthRate),(int)(60*fHigthRate));
		timebar      = CreateBitMap.scaleBitmap(res,R.drawable.timebar,(int)(12*fWidthRate),(int)(470*fHigthRate));
		timeleft     = CreateBitMap.scaleBitmap(res,R.drawable.timeleft,(int)(29*fWidthRate),(int)(32*fHigthRate));
		over1        = CreateBitMap.scaleBitmap(res,R.drawable.over1,(int)(200*fWidthRate),(int)(150*fHigthRate));
		over2        = CreateBitMap.scaleBitmap(res,R.drawable.over2,(int)(200*fWidthRate),(int)(150*fHigthRate));
		bfeng = CreateBitMap.scaleBitmap(res,R.drawable.feng,(int)(25*fWidthRate),(int)(25*fHigthRate));
		ice = CreateBitMap.scaleBitmap(res,R.drawable.ice,(int)(12*fWidthRate),(int)(470*fHigthRate));
		
		// 初始化所有牌资源
		card = new Bitmap[11];
		
		// 初始化牌布局,5*8为最大布局
		game = new CGuessUnit[5*8+1];
		reset();
	}

	// 展示游戏界面
	public void onDrow( Canvas canvas )
	{
		if ( !bResetOK )
			return;

		canvas.drawColor(Color.BLACK);
		canvas.drawBitmap( bg, 0, 0, null );
		if ( iDian>0 && iDian%4==3 )
		{
			canvas.drawBitmap( bmDian, 0, 0, null );
		}
		canvas.drawBitmap( daoju, (int)(705*fWidthRate), (int)(80*fHigthRate), null );
		canvas.drawBitmap( df, (int)(713*fWidthRate), (int)(89*fHigthRate), null );
		canvas.drawBitmap( db, (int)(713*fWidthRate), (int)(151*fHigthRate), null );
		canvas.drawBitmap( dd, (int)(713*fWidthRate), (int)(212*fHigthRate), null );
		canvas.drawBitmap( reset, (int)(690*fWidthRate), (int)(290*fHigthRate), null );
		canvas.drawBitmap( back,  (int)(690*fWidthRate), (int)(360*fHigthRate), null );
		
		if ( bBing )
			canvas.drawBitmap( ice,  (int)(665*fWidthRate), (int)(5*fHigthRate), null );
		else
			canvas.drawBitmap( timebar,  (int)(665*fWidthRate), (int)(5*fHigthRate), null );
		int tmp = 470-(int)(((float)(itimeleft)/60)*470);
		canvas.drawBitmap( timeleft,  (int)(658*fWidthRate), (int)(tmp*fHigthRate), null );
		
		if ( CCV.bYinxiao )
			canvas.drawBitmap( close,  (int)(690*fWidthRate), (int)(420*fHigthRate), null );
		else
			canvas.drawBitmap( open,  (int)(690*fWidthRate), (int)(420*fHigthRate), null );
		
		Paint pp = new Paint();
		String familyName = "宋体";
        Typeface font = Typeface.create(familyName,Typeface.BOLD);
        pp.setColor(Color.rgb(4,66,7));
        pp.setAntiAlias(true);//抗锯齿
        
        // 道具显示区域
        pp.setTextSize(15*fWidthRate);
        pp.setColor(Color.rgb(255,0,0));
		canvas.drawText(""+String.format("%02d",CCV.iDaojuF),732*fWidthRate,125*fHigthRate,pp);
		canvas.drawText(""+String.format("%02d",CCV.iDaojuB),732*fWidthRate,185*fHigthRate,pp);
		canvas.drawText(""+String.format("%02d",CCV.iDaojuD),732*fWidthRate,245*fHigthRate,pp);
			
		pp.setTypeface(font);
        pp.setTextSize(25*fWidthRate);
        if ( model != 1 )
        	canvas.drawText(CCV.cact.getString(R.string.guan)+String.format("%02d",CCV.iSelect),695*fWidthRate,45*fHigthRate,pp);
        else
        	canvas.drawText(CCV.cact.getString(R.string.score)+iNowSucc,695*fWidthRate,45*fHigthRate,pp);
        
        if ( iDian>0 && iDian%4==3 )
		{
        	return;
		}

        for( int i=1; i<=iRows*iCols; i++ )
		{	
			// 道具电
			if ( iDian>0 && iDian%4!=3 )
			{
				//pp.setAlpha(80);
				canvas.drawBitmap( card[game[i].card], game[i].iLeft, game[i].iTop, pp );
				continue;
			}
						
			if ( game[i].istate == 0 )	// 原始状态
			{
				canvas.drawBitmap( bmCardBg, game[i].iLeft, game[i].iTop, null );
			}
			else if ( game[i].istate == 2 )
			{
				canvas.drawBitmap( card[game[i].card], game[i].iLeft, game[i].iTop, null );
			}
			else if ( game[i].istate == 3 )
			{
				canvas.drawBitmap( card[game[i].card], game[i].iLeft, game[i].iTop, null );
				canvas.drawBitmap( bfeng, game[i].iLeft, game[i].iTop, null );
			}
		}
        
        // 是否需要闪烁
		if ( iCnt > 0 )
		{
			if ( iCnt%3==2 )
			{
				if ( iSelectCard1 != -1 )
					canvas.drawBitmap( card[game[iSelectCard1].card], game[iSelectCard1].iLeft, game[iSelectCard1].iTop, null );
				if ( iSelectCard2 != -1 )
					canvas.drawBitmap( card[game[iSelectCard2].card], game[iSelectCard2].iLeft, game[iSelectCard2].iTop, null );
			}
		}
		else if ( iSelectCard2 != -1 )
		{
			iSelectCard1=-1;
			iSelectCard2=-1;
		}
		
		if ( iSelectCard1 != -1 )
			canvas.drawBitmap( bmCardSelect, game[iSelectCard1].iLeft, game[iSelectCard1].iTop, null );
		
        if ( iOver != 0 )
		{
			if ( iOver == 1 )
				canvas.drawBitmap( over1, 300*fWidthRate, 165*fHigthRate, null );
			if ( iOver == 2 )
				canvas.drawBitmap( over2, 300*fWidthRate, 165*fHigthRate, null );
			
			if ( model == 1 )
			{
				pp.setTextSize(23 * fWidthRate);
				canvas.drawText(CCV.cact.getString(R.string.newscore) + iNowSucc, 330 * fWidthRate, 195 * fHigthRate, pp);
			}
		}
		
		// 挑战时，控制先显示一下原来的翻开的图片，然后再重新生成图片再闪烁
		if ( model == 1 && iSelectCard2 != -1 && game[iSelectCard2].istate==2 && iCnt<=5 )
		{
			int iRand = Math.abs(rdm.nextInt())%iCardCnt+1;
			//game[iSelectCard1].card = card[iRand];
			game[iSelectCard1].card = iRand;
			iRand = Math.abs(rdm.nextInt())%iCardCnt+1;
			//game[iSelectCard2].card = card[iRand];
			game[iSelectCard2].card = iRand;
			game[iSelectCard1].istate = 0;
			game[iSelectCard2].istate = 0;
		}
	}
	
	int iOver = 0;	// 0 未结束；1 成功过关；2 时间到
	int iShowAd = 0; 	// 0未展示；1展示
	int iHeihei = 0;
	boolean bResetOK = false;
	public void checkover()
	{
		if ( !bResetOK )
			return;
		
		if ( itimeleft <= 0 )
		{
			itimeleft = 0;
			iOver = 2;
		}
		for ( int i=1; i<=iRows*iCols; i++ )
        {
            if ( game[i].istate != 2 )
            	return;
        }
		iOver = 1;
	}
	
	int iSelectCard1 = -1;
	int iSelectCard2 = -1;
	int model = 1;
	int iClick = 0;
	int iSucc = 0;
	// 游戏操作
	public int onTouch( MotionEvent event )
	{
		// 重玩
		if (event.getX()>(int)(690.0*fWidthRate) && event.getX()<(int)(790.0*fWidthRate)
                && event.getY()>(int)(290.0*fHigthRate) && event.getY()<(int)(350.0*fHigthRate)) {
            reset();
            return -1;
        }
		
		// 返回，到选择界面
		if (event.getX()>(int)(690.0*fWidthRate) && event.getX()<(int)(790.0*fWidthRate)
                && event.getY()>(int)(360.0*fHigthRate) && event.getY()<(int)(420.0*fHigthRate)) {
            return 2;
        }
		
		// 音效控制
		if (event.getX()>(int)(690.0*fWidthRate) && event.getX()<(int)(790.0*fWidthRate)
                && event.getY()>(int)(430.0*fHigthRate) && event.getY()<(int)(490.0*fHigthRate)) {

			CCV.bYinxiao = !CCV.bYinxiao;

            if ( CCV.bjyx != null && CCV.bYinxiao )
			{
				try {
					CCV.bjyx.prepare();
				} catch (IOException e) {
					e.printStackTrace();
				}
				CCV.bjyx.start();
			}
            if ( CCV.bjyx != null && !CCV.bYinxiao )
			{
            	CCV.bjyx.pause();
			}
			return -1;
        }
		
		if ( iOver != 0 )
        {
            //10-80  100 - 130 105-180
            if ( event.getX() > 310*fWidthRate && event.getX() < 380*fWidthRate
                    && event.getY() > 265*fHigthRate && event.getY() < 295*fHigthRate ) {
            	if ( iOver == 1 )
            		return 2;	// 返回到选择关卡界面
            	else
            	{
            		reset();
                    return -1;
            	}
            }
            if ( event.getX() > 405*fWidthRate && event.getX() < 480*fWidthRate
                    && event.getY() > 265*fHigthRate && event.getY() < 295*fHigthRate ) {
            	if ( iOver == 1 )	// 过关
            	{
            		if ( CCV.iSelect < 21 )
            			CCV.iSelect++;
            		else
            			return 2;
            		if ( CCV.iDaojuB < 5 && Math.abs(rdm.nextInt())%10==1 )
            			CCV.iDaojuB++;
            		if ( CCV.iDaojuD < 5 && Math.abs(rdm.nextInt())%10==1 )
            			CCV.iDaojuD++;
            		if ( CCV.iDaojuF < 5 && Math.abs(rdm.nextInt())%10==1 )
            			CCV.iDaojuF++;
            		CCV.SavePara();
            		reset();
                    return -1;
            	}
            	else
            	{
	            	return 7;
            	}
            }
            return -1;
        }
		
		for ( int i = 1; i <= iRows*iCols; i++ )
        {
            if ( event.getX() > game[i].iLeft && event.getX() < game[i].iRight
                    && event.getY() > game[i].iTop && event.getY() < game[i].iBottom )
            {
            	if ( game[i].istate == 2 )
            	{
            		break;
            	}
            		
            	if ( iSelectCard1 == -1 || iSelectCard2!=-1 )	// 重新开始选择第一张牌
            	{
            		if ( model == 1 )
            		{
						int iRand = Math.abs(rdm.nextInt())%iCardCnt+1;
						if ( iSelectCard1 != -1 && game[iSelectCard1].istate == 2 )
            			{
            				game[iSelectCard1].card = iRand;
            				game[iSelectCard1].istate = 0;
            			}
            			if ( iSelectCard2 != -1 && game[iSelectCard2].istate == 2 )
            			{
            				game[iSelectCard2].card = iRand;
            				game[iSelectCard2].istate = 0;
            			}
            		}
        			
            		iSelectCard1 = i;
            		iSelectCard2 = -1;
            		iCnt = 0;
	                break;
            	}
            	else if ( iSelectCard1 != i )// 选中了一张，选择第二张不一样的时候
            	{
            		//if ( game[i].card.iCardID == game[iSelectCard1].card.iCardID )
            		if ( game[i].card == game[iSelectCard1].card )
            		{
            			if ( CCV.yesyx!=null && CCV.bYinxiao )
                			CCV.yesyx.start();
            			game[i].istate = 2;
            			game[iSelectCard1].istate = 2;
            			CCV.iTotal++;//累积一分
            			
            			if ( model != 1 )
            				iSelectCard1 = -1;
            			else
            			{
            				iNowSucc++;
            				iSelectCard2 = i;
            				iCnt = 7;
            			}
            			// 游戏是否结束
            			checkover(); 
            			iSucc++;
            		}
            		else
            		{
            			if ( CCV.noyx!=null && CCV.bYinxiao )
                			CCV.noyx.start();
            			iSelectCard2 = i;
            			iCnt = 5;
            			iClick++;
            		}
            	}
            	break;
            }
        }
		
		// 道具显示区域
		
		if (event.getX()>(int)(705.0*fWidthRate) && event.getX()<(int)(775.0*fWidthRate)
                && event.getY()>(int)(80.0*fHigthRate) && event.getY()<(int)(140.0*fHigthRate)) {
			if ( CCV.iDaojuF>0 )
			{
				daojufeng();
				CCV.iDaojuF--;
			}
        }
		
		if (event.getX()>(int)(705.0*fWidthRate) && event.getX()<(int)(775.0*fWidthRate)
                && event.getY()>(int)(150.0*fHigthRate) && event.getY()<(int)(210.0*fHigthRate)) {
			if ( CCV.iDaojuB>0 )
			{
				bBing = true;
				iBing = System.currentTimeMillis();
				CCV.iDaojuB--;
			}
        }
		
		if (event.getX()>(int)(705.0*fWidthRate) && event.getX()<(int)(775.0*fWidthRate)
                && event.getY()>(int)(220.0*fHigthRate) && event.getY()<(int)(280.0*fHigthRate)) {
			if ( CCV.iDaojuD>0 )
			{
				iDian = 15;
				CCV.iDaojuD--;
			}
        }
		
		int i;
		for( i=1; i<=iCols; i++ )
		{	
			if ( game[i].istate != 2 )
				break;
		}
		//Log.d("aaaaaaaaa", ""+i);
		if ( i>iCols )
		{
			iHeihei = 1;
		}
		return -1;
	}
	
	public void daojufeng()
	{
		int iCnt = Math.abs(rdm.nextInt())%3+2;
		int iPos = Math.abs(rdm.nextInt())%10+1;
		for ( int i = iPos; i <= iRows*iCols && iCnt>0; i++ )
        {
			if ( game[i].istate==0 && Math.abs(rdm.nextInt())%2==1 )
			{	
				game[i].istate = 3;
				iCnt--;
			}
        }
		for ( int i = iPos-1; i >= 1 && iCnt>0; i-- )
        {
			if ( game[i].istate==0 && Math.abs(rdm.nextInt())%2==1 )
			{	
				game[i].istate = 3;
				iCnt--;
			}
        }
	}
	
	//Calendar calendar = Calendar.getInstance();  
	long timelast;
	long iBing = 0;
	boolean bBing = false;
	// 可能有线程不安全，到时再考虑
	public void Ctrl()
	{
		if ( iOver != 0 )
			return;
		
		// 游戏是否结束
		checkover(); 
		
		if ( iOver != 0 && model == 1 && iNowSucc>CCV.iMax )
		{
			CCV.iMax = iNowSucc;
		}
		
		if ( iOver == 1 && model == 0 )	// 成功过关
		{
			CCV.iSelectMax++;
		}
		
		if ( iOver != 0 )	// 游戏结束，保存参数
			CCV.SavePara();
		
		long timenow = System.currentTimeMillis();
		
		if ( bBing && (timenow-iBing) >= 5000 )
		{
			bBing = false;
			timemax += 5;
		}

		if ( !bBing )
		{
			itimeleft = timemax-(timenow-timelast)/1000;
		}
		// 此处更新一些控制效果的变量
		if ( iCnt>0 )
			iCnt--;
		if ( iDian >= 1 )
			iDian--;
	}
}
