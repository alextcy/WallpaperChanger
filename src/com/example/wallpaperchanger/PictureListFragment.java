package com.example.wallpaperchanger;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.R.string;
import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PictureListFragment extends ListFragment {
	
	public static final String PIC_FULL_PATH_KEY = "com.example.wallpaperchanger.pic_full_path";
	public static final String FOLDER_PATH_KEY = "com.example.wallpaperchanger.folder_path";
		
	
	ArrayList<Picture> picList;
	//private PictureAdapter adapter;
	private String defaultFolderPath; //путь к папке с фотками по умолчанию
	private String folderPath; //путь к папке с фотками
	
	
		
	//при первом запуске и повороте экрана
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.app_name);
		//фрагмент отвечает за показ меню
		setHasOptionsMenu(true);
		
		//Log.d("Files", "onCreate start");
		
		//путь к папке с картинками по умолчанию
		defaultFolderPath = Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DOWNLOADS;
				
		//если в настройках сохранен путь тогда используем его (хранит данные даже между ЗАПУСКАМИ)
		SharedPreferences preferences = getActivity().getSharedPreferences("myPref", 0);
		if( preferences.contains(FOLDER_PATH_KEY) == true ) {
			folderPath = preferences.getString(FOLDER_PATH_KEY, defaultFolderPath);
		} else {
			folderPath = defaultFolderPath;
		}
		
		//Log.d("Files", "Path: " + path);
		//Log.d("Files", "Environment getExternalStorageState: " + Environment.getExternalStorageState());
		
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
								
		//засунуть массив файлов в ArrayAdapter
		//ArrayAdapter<File> adapter = new ArrayAdapter<File>(getActivity(), android.R.layout.simple_list_item_1, pictures);
		
		//получить список файлов
		picList = getPicturesInFolder(folderPath);
		PictureAdapter adapter = new PictureAdapter(picList);
		setListAdapter(adapter);
		
		//фоновая сканирование папки с картинками и получение списка файлов
		//new FetchPicturesAsyncTask().execute(path);
	}
	
	
	@Override
	public void onResume()
	{
		super.onResume();
		//Log.d("Files", "onResume start");
		
		
	    /*if(path == null) {
	    	SharedPreferences preferences = getActivity().getSharedPreferences("myPref", 0);
			if( preferences.contains(FOLDER_PATH_KEY) == true ) {
				Log.d("Files", "onResume preferences.contains FOLDER_PATH_KEY");
				path = preferences.getString(FOLDER_PATH_KEY, defaultFolderPath);
			} else {
				Log.d("Files", "onResume preferences EMPTY");
				path = defaultFolderPath;
			}
	    }*/
	    
		//востановили путь к папке из глобальной области аргументов
		//path = getArguments().getString(FOLDER_PATH_KEY);
		//Log.d("Files", "path from arguments: " + path);
		
		
		//есть два варинта известить адаптер о том что список файлов изменился (и показать во VIEW)
		//ВАРИАНТ-1 (если список в свойстве класса и он уже асоциирован с адаптером)
		picList.clear(); 
		picList.addAll( getPicturesInFolder(folderPath) ); //заново просканировать содержимое папки
		((PictureAdapter)getListAdapter()).notifyDataSetChanged(); //сообщить что его нужно обновить во View
		
		
		//ВАРИАНТ-2 (напрямую работаем с содержимым адаптера)
		//получаем адаптер который асоциируется с PictureListFragment (в нашем случае это переменная adapter)
		//((PictureAdapter)getListAdapter()).clear(); //удалить весь список файлов из адаптера
		//((PictureAdapter)getListAdapter()).addAll( getPicturesInFolder(path) ); //заполнить его обновленным списком (заново сканили папку)
		//((PictureAdapter)getListAdapter()).notifyDataSetChanged(); //сообщить что его нужно обновить
	
		//фоновая сканирование папки с картинками и получение списка файлов
		//new FetchPicturesAsyncTask().execute(path);
	}
	
	//при сворачивании в трей или повороте экрана
	@Override
	public void onStop()
	{
		super.onStop();
		
		//Store ONLY private PRIMITIVE data in key-value pairs. http://developer.android.com/guide/topics/data/data-storage.html
		SharedPreferences preferences = getActivity().getSharedPreferences("myPref", 0);
		preferences.edit().putString(FOLDER_PATH_KEY, folderPath).commit();
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
	
	
	private class PictureAdapter extends ArrayAdapter<Picture> {
		
		public PictureAdapter( ArrayList<Picture> picList ) {
			super(getActivity(), 0, picList);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent )
		{
			Log.d("Files", "PictureAdapter getView");
			if(convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_picture, null);
			}
			
			Picture pic = getItem(position);
			
						
			TextView picResolutionView = (TextView)convertView.findViewById(R.id.pic_resolution);
			int[] picOriginalWidthAndHeight = pic.getOriginalWidthAndHeight();
			picResolutionView.setText( picOriginalWidthAndHeight[0] + " x " + picOriginalWidthAndHeight[1]);
			
			TextView picFileNameView = (TextView)convertView.findViewById(R.id.pic_file_name);
			picFileNameView.setText( pic.getFileName() );
			
			ImageView picImageView = (ImageView)convertView.findViewById(R.id.pic_image);
			
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.picImageView = picImageView;
			viewHolder.pic = pic;
			
			//Log.d("Files", "picImage ImageView width: "+ picImage.get);
			//picImage.setImageURI(Uri.fromFile(pic));
			
			//нужно ресайзить большие фотки иначе нехватит памяти
			//Bitmap myBitmap = pic.decodeSampledBitmapFromFile(100, 100);
			
			//-------------------------------------
			//получение размеров экрана телефона (фотки в списке будут на половину ширины)
			/*DisplayMetrics displayMetrics = new DisplayMetrics();
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
			picImageView.setImageBitmap(myBitmap);*/
			
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
			
			//new ProcessPictureAsyncTask().execute(viewHolder);
			//ресайз фоток и их рендеринг в фоновом потоке
			new ProcessPictureAsyncTask().execute(viewHolder);
			
			return convertView;
		}
		
				
	}
	
	class ViewHolder {
		public ImageView picImageView;
		public Picture pic;
		public Bitmap picBitmap;
	}
	
	//String входной параметр для doInBackground , Void, ArrayList<Picture> - тип результатат и входной параметр для onPostExecute 
	/*private class FetchPicturesAsyncTask extends AsyncTask<String, Void, ArrayList<Picture>>
	{
		
		@Override
		protected ArrayList<Picture> doInBackground(String... folderPath)
		{
			return getPicturesInFolder(folderPath[0]);
		}
		
		@Override
		protected void onPostExecute(ArrayList<Picture> pictureList)
		{
			picList = pictureList;
			//полученый массив файлов фоток засунуть в адаптер
			adapter = new PictureAdapter(picList);
			setListAdapter(adapter);
		}
	}*/
	
	private class ProcessPictureAsyncTask extends AsyncTask<ViewHolder, Void, ViewHolder>
	{
		//фоновый поток
		@Override
		protected ViewHolder doInBackground(ViewHolder... params)
		{
			ViewHolder viewHolder = params[0];
			
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
			
			viewHolder.picBitmap = viewHolder.pic.getResizedAndCropBitmap( picItemWidth, picItemHeight); // 200 x 100
			
			return viewHolder;
		}
		
		//основной поток (только тут можно менять внешний вид)
		@Override
		protected void onPostExecute(ViewHolder viewHolder)
		{
			viewHolder.picImageView.setImageBitmap( viewHolder.picBitmap );
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
		
		
		//Log.d("Files", "displayMetrics.widthPixels: "+ displayMetrics.widthPixels);
		//Log.d("Files", "displayMetrics.densityDpi: "+ displayMetrics.densityDpi);
		//Log.d("Files", "dp: "+ dp);
		
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
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.picture_list_fragment_menu, menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		//определяем по какому пункту меню кликали 
		switch (item.getItemId()) {
			case R.id.menu_item_select_folder:
				Toast.makeText(getActivity(), "Clicked on Select Folder", Toast.LENGTH_SHORT).show();
				
				break;
	
			default:
				super.onOptionsItemSelected(item);
				break;
		}
		
		return true;
	}
	
}
