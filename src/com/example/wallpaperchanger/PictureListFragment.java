package com.example.wallpaperchanger;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PictureListFragment extends ListFragment {
	
	public static final String PIC_FULL_PATH_KEY = "com.example.wallpaperchanger.pic_full_path";
		
	
	ArrayList<Picture> picList;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.app_name);
		
		//получить список файлов
		//filesInFolder
		String path = Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DOWNLOADS;
		
		Log.d("Files", "Path: " + path);
		Log.d("Files", "Environment getExternalStorageState: " + Environment.getExternalStorageState());
		
		/*File fh = new File(path);
		//список файлов только с расширением *.jpg и *.jpeg
		File pictures[] = fh.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return (filename.endsWith(".jpg")||filename.endsWith(".jpeg")); 
			}
		});
		
		ArrayList<Picture> picList = new ArrayList<Picture>();
		for(int i=0; i<pictures.length; i++) {
			picList.add( new Picture(pictures[i]) );
		}*/
		
		picList = getPicturesInFolder(path);
		
		Log.d("Files", "pictures count: " + picList.size());
		
		
		
								
		//засунуть массив файлов в ArrayAdapter
		//ArrayAdapter<File> adapter = new ArrayAdapter<File>(getActivity(), android.R.layout.simple_list_item_1, pictures);
		//PictureAdapter adapter = new PictureAdapter(pictures);
		PictureAdapter adapter = new PictureAdapter(picList);
		
		setListAdapter(adapter);
	}
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		Picture pic = (Picture)getListAdapter().getItem(position);
		
		//Intent intent = new Intent(getActivity(), PictureActivity.class);
		Intent intent = new Intent(getActivity(), PicturePagerActivity.class); //ViewPager
		intent.putExtra(PIC_FULL_PATH_KEY, pic.getFullPath());
		intent.putExtra(PicturePagerActivity.PIC_POSITION, position); //номер позиции картинки на которую кликнули (чтобы потом показать ее во ViewPager)
		
		startActivity(intent);
		
		//Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
		
		
		
		//Log.d("Files", "onListItemClick");
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Log.d("Files", "onResume start");
		
		//нужно заново просканировать директорию и ообновить содержимое ArrayList<Picture> picList
		String path = Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DOWNLOADS;

		
		//есть два варинта известить адаптер о том что список файлов изменился (и показать во VIEW)
		//ВАРИАНТ-1 (если список в свойстве класса и он уже асоциирован с адаптером)
		picList.clear(); 
		picList.addAll( getPicturesInFolder(path) );
		((PictureAdapter)getListAdapter()).notifyDataSetChanged(); //сообщить что его нужно обновить во View
		
		//ВАРИАНТ-2 (напрямую работаем с содержимым адаптера)
		//получаем адаптер который асоциируется с PictureListFragment (в нашем случае это переменная adapter)
		//((PictureAdapter)getListAdapter()).clear(); //удалить весь список файлов из адаптера
		//((PictureAdapter)getListAdapter()).addAll( getPicturesInFolder(path) ); //заполнить его обновленным списком (заново сканили папку)
		//((PictureAdapter)getListAdapter()).notifyDataSetChanged(); //сообщить что его нужно обновить
		
	}
	
	
	private class PictureAdapter extends ArrayAdapter<Picture> {
		
		public PictureAdapter( ArrayList<Picture> picList ) {
			super(getActivity(), 0, picList);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent )
		{
			if(convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_picture, null);
			}
			
			Picture pic = getItem(position);
			
						
			TextView picResolution = (TextView)convertView.findViewById(R.id.pic_resolution);
			int[] picOriginalWidthAndHeight = pic.getOriginalWidthAndHeight();
			picResolution.setText( picOriginalWidthAndHeight[0] + " x " + picOriginalWidthAndHeight[1]);
			
			TextView picFileName = (TextView)convertView.findViewById(R.id.pic_file_name);
			picFileName.setText( pic.getFileName() );
			
			ImageView picImage = (ImageView)convertView.findViewById(R.id.pic_image);
			//Log.d("Files", "picImage ImageView width: "+ picImage.get);
			//picImage.setImageURI(Uri.fromFile(pic));
			
			//нужно ресайзить большие фотки иначе нехватит памяти
			//Bitmap myBitmap = pic.decodeSampledBitmapFromFile(100, 100);
			
			//получение размеров экрана телефона (фотки в списке будут на половину ширины)
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			
						
			int picItemWidth, picItemHeight;
			
			if(displayMetrics.widthPixels < displayMetrics.heightPixels) {
				//portrait
				picItemWidth = displayMetrics.widthPixels/2;
				picItemHeight = displayMetrics.heightPixels/6;
			} else {
				//landscape
				picItemWidth = displayMetrics.widthPixels/2;
				picItemHeight = displayMetrics.heightPixels/3;
			}
			
			Bitmap myBitmap = pic.getResizedAndCropBitmap( picItemWidth, picItemHeight); // 200 x 100
			
			Log.d("Files", "pic <Picture> byte count: "+ myBitmap.getByteCount());
			Log.d("Files", "myBitmap resolution: " + myBitmap.getWidth() + "x" + myBitmap.getHeight());
			
			picImage.setImageBitmap(myBitmap);
			
			//---------------------------------------
			
			/*Bitmap myBitmap = decodeSampledBitmapFromFile(pic.getAbsolutePath(), 100, 100);
			Log.d("Files", "pic byte count: "+ myBitmap.getByteCount());
			picImage.setImageBitmap(myBitmap);*/
			
			
			/*BitmapFactory.Options opts=new BitmapFactory.Options();
			opts.inSampleSize = calculateInSampleSize(opts, 100, 100);
			opts.inJustDecodeBounds = true;
			Log.d("Files", "pictures inSampleSize: "+ opts.inSampleSize);
			
			Bitmap myBitmap = BitmapFactory.decodeFile(pic.getAbsolutePath());
			Log.d("Files", "pic byte count: "+ myBitmap.getByteCount());
			myBitmap.recycle();*/
			
			/*
			Bitmap myBitmap = BitmapFactory.decodeFile(pic.getAbsolutePath());
			//Bitmap myBitmapResized = Bitmap.createScaledBitmap(myBitmap, 100, 100, true);
			
			//myBitmap.recycle();
			//picImage.setImageBitmap(myBitmapResized);
			
			picImage.setImageBitmap(myBitmap);
			myBitmap.recycle();*/
			
			return convertView;
		}
		
		
	}
	
	
	private ArrayList<Picture> getPicturesInFolder(String path)
	{
		File fh = new File(path);
		//список файлов только с расширением *.jpg и *.jpeg
		File pictures[] = fh.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return (filename.endsWith(".jpg")||filename.endsWith(".jpeg")); 
			}
		});
		
		ArrayList<Picture> picList = new ArrayList<Picture>();
		for(int i=0; i<pictures.length; i++) {
			picList.add( new Picture(pictures[i]) );
		}
		
		return picList;
	}
	
	
	private float dpFromPx(float px, DisplayMetrics displayMetrics)
	{
	    //return px / this.getContext().getResources().getDisplayMetrics().density;
		
		//DisplayMetrics displayMetrics = new DisplayMetrics();
		//return px / displayMetrics.density;
		float dp = px / (displayMetrics.densityDpi / 160f); 
		
		
		Log.d("Files", "displayMetrics.widthPixels: "+ displayMetrics.widthPixels);
		Log.d("Files", "displayMetrics.densityDpi: "+ displayMetrics.densityDpi);
		Log.d("Files", "dp: "+ dp);
		
		return dp;
	}
	
	private float pxFromDp(float dp, DisplayMetrics displayMetrics)
	{
	    //return dp * this.getContext().getResources().getDisplayMetrics().density;
		//DisplayMetrics displayMetrics = new DisplayMetrics();
		//return dp * displayMetrics.density;
		
		float px = dp * (displayMetrics.densityDpi / 160f);
		
		return px;
	}
	
}
