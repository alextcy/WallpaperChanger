package com.example.wallpaperchanger;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

public class Picture {

	private String fullPath;
	private String fileName;
	
	public Picture(File pic) {
		fullPath = pic.getAbsolutePath();
		fileName = pic.getName();
	}
	
	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
	public int[] getOriginalWidthAndHeight()
	{
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(fullPath, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;
	    
	    return new int[] { photoW, photoH };
	}
	
	public Bitmap getResizedAndCropBitmap(int targetW, int targetH) {

	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    //inJustDecodeBounds = true <-- will not load the bitmap into memory
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(fullPath, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
	    //Log.d("Files", "pictures scaleFactor: "+ scaleFactor);
	    //Log.d("Files", "pictures photoW: "+ photoW);
	    //Log.d("Files", "pictures targetW: "+ targetW);
	    //Log.d("Files", "pictures photoH: "+ photoH);
	    //Log.d("Files", "pictures targetH: "+ targetH);
	    
	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    Bitmap bitmap = BitmapFactory.decodeFile(fullPath, bmOptions);
	    
	    //crop image to targetW and targetH because resize is not strong 
	    bitmap = ThumbnailUtils.extractThumbnail(bitmap, targetW, targetH, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
	    
	    return(bitmap);
	}
	



	
	/*public int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {

	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;

	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }

	    return inSampleSize;
	}
	
	public Bitmap decodeSampledBitmapFromFile( int reqWidth, int reqHeight) {
		
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(fullPath, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    Log.d("Files", "pictures inSampleSize: "+ options.inSampleSize);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(fullPath,options);
	}*/
	

}
