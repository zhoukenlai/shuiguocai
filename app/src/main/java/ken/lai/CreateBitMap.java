package ken.lai;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

// 生成合适大小的图片
public class CreateBitMap {

	// 根据最终要显示的图片大小，计算合适的采样值，主要是节省内存，防止内存溢出错误
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	// 生成合适大小的图片
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		options.inScaled = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	// 将图片缩放到指定分辨率
	public static Bitmap scaleBitmap(Resources res, int resId, int reqWidth,
			int reqHeight) {
		Bitmap oldBitmap;
		oldBitmap = decodeSampledBitmapFromResource(res, resId,
				reqWidth, reqHeight);
		int width = oldBitmap.getWidth();
		int height = oldBitmap.getHeight();

		float fW1 = (float) reqWidth / ((float) width);
		float fH1 = (float) reqHeight / ((float) height);

		Matrix matrix = new Matrix();
		matrix.postScale(fW1, fH1);

		Bitmap newBitmap = Bitmap.createBitmap(oldBitmap, 0, 0, width, height,
				matrix, true);
		oldBitmap = null;
		return newBitmap;
	}
	
	// 从存储卡加载的
	
	// 生成合适大小的图片
	public static Bitmap decodeSampledBitmapFromResource(String spath, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		//BitmapFactory.decodeResource(res, resId, options);
		BitmapFactory.decodeFile(spath,options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		options.inScaled = false;
		return BitmapFactory.decodeFile(spath,options);
	}

	// 将图片缩放到指定分辨率
	public static Bitmap scaleBitmap(String spath, int reqWidth,
			int reqHeight) {
		//Log.d("11111111", Environment.getExternalStorageDirectory().getPath());
		Bitmap oldBitmap = decodeSampledBitmapFromResource(
				Environment.getExternalStorageDirectory().getPath()+"/caicai/"+spath,
				reqWidth, reqHeight);
		if ( oldBitmap == null ) return null;
		int width = oldBitmap.getWidth();
		int height = oldBitmap.getHeight();

		float fW1 = (float) reqWidth / ((float) width);
		float fH1 = (float) reqHeight / ((float) height);

		Matrix matrix = new Matrix();
		matrix.postScale(fW1, fH1);

		return Bitmap.createBitmap(oldBitmap, 0, 0, width, height,
				matrix, true);
	}
}
