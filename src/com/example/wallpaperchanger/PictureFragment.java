package com.example.wallpaperchanger;

import java.io.File;
import java.io.IOException;

import android.app.Fragment;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
//import android.widget.Toast;
import android.widget.Toast;

public class PictureFragment extends Fragment {
	
	public static final String PIC_FULL_PATH_KEY = "com.example.wallpaperchanger.pic_full_path";
	
	Picture pic;
	
	//private String pictureFullPath;
	//File picFile = new File(pictureFullPat);
	
	public static PictureFragment newInstance(String pictureFullPath) 
	{
		Bundle args = new Bundle();
		args.putString(PIC_FULL_PATH_KEY, pictureFullPath);
		PictureFragment fragment = new PictureFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getActivity().setTitle("Picture for wallpaper");
		
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_picture, container, false);
		
		//String pictureFullPath = getActivity().getIntent().getStringExtra(PIC_FULL_PATH_KEY);
		//достаем данные которые пришли из PictureListActivity в PictureActivity а потом в PictureFragment
		String pictureFullPath = getArguments().getString(PIC_FULL_PATH_KEY);
		pic = new Picture(new File(pictureFullPath));
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		
		//Bitmap picBitmap = pic.decodeSampledBitmapFromFile(300, 300);
		int picBigWidth, picBigHeight;
		if(displayMetrics.widthPixels < displayMetrics.heightPixels) {
			//portrait
			picBigWidth = displayMetrics.widthPixels;
			picBigHeight = displayMetrics.heightPixels/3;
		} else {
			//landscape
			picBigWidth = (int)(displayMetrics.widthPixels * 0.75);
			picBigHeight = displayMetrics.heightPixels/2;
		}
		
		Bitmap picBitmap = pic.getResizedAndCropBitmap(picBigWidth, picBigHeight);
		Log.d("Files", "picBitmap resolution: " + picBitmap.getWidth() + "x" + picBitmap.getHeight());
		
		ImageView picImage = (ImageView)v.findViewById(R.id.picture_big);
		picImage.setImageBitmap( picBitmap );
		
				
		Button wallPaperButton = (Button)v.findViewById(R.id.set_as_wallpaper_button);
		wallPaperButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Log.d("Files", "pic absolute: " + pic.getFullPath());
				
				DisplayMetrics displayMetrics = new DisplayMetrics();
				getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
							
				
				WallpaperManager wallManager = WallpaperManager.getInstance(getActivity());
								
				Log.d("Files", "screen : " + displayMetrics.widthPixels + "x" + displayMetrics.heightPixels);
				
				//Bitmap picWallpaperBitmap = pic.decodeSampledBitmapFromFile(displayMetrics.widthPixels, displayMetrics.heightPixels);
				Bitmap picWallpaperBitmap = pic.getResizedAndCropBitmap(displayMetrics.widthPixels*2, displayMetrics.heightPixels);
				
				Log.d("Files", "picWallpaperBitmap resolution: " + picWallpaperBitmap.getWidth() + "x" + picWallpaperBitmap.getHeight());
				
				try {
					
					//НЕ ТРОГАТЬ ЭТУ ХУЙНЮ!!!!
					//wallManager.suggestDesiredDimensions(displayMetrics.widthPixels*2, displayMetrics.heightPixels);
					
					wallManager.setBitmap(picWallpaperBitmap);
			    } catch (IOException e) {
			        //Log.e(TAG, "Cannot set image as wallpaper", e);
			    	Toast.makeText(getActivity(), "Cannot set image as wallpaper", Toast.LENGTH_LONG).show();
			    }
				
				Toast.makeText(getActivity(), "Wallpaper set.", Toast.LENGTH_LONG).show();
			}
		});
		
		return v;
	}
	
	
	
}
