package com.example.wallpaperchanger;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;


import android.os.Bundle;
import android.os.Environment;
//import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

//project-properies/buidpath/extrenal jar/    /adt-bunsle/sdk/extras/support/v13/
//ViewPager может работать с нативными фрагментами только если использовать SDK-V13
//иначе только использовать support.v4 фрагменты

public class PicturePagerActivity extends Activity {

	public static final String PIC_POSITION = "com.example.wallpaperchanger.pic_position";
	
	private ViewPager picViewPager;
	private ArrayList<Picture> picList;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_picture_viewpager);
		
		//picViewPager = new ViewPager(this);
		//picViewPager.setId(R.id.viewPager);
		//setContentView(picViewPager);
		
		picViewPager = (ViewPager)findViewById(R.id.viewPager);
			
		picList = getPicturesInFolder( Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DOWNLOADS );
		
		FragmentManager fm = getFragmentManager();
		
		picViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
			
			@Override
            public int getCount() {
                return picList.size();
            }
            
            @Override
            public Fragment getItem(int pos) {
                Picture pic = picList.get(pos);
                return PictureFragment.newInstance(pic.getFullPath());
            }
		});
		
		//номер позиции картинки по которой кликнули в PictureListFragment, чтобы показать во ViewPager
		picViewPager.setCurrentItem( getIntent().getIntExtra(PIC_POSITION, 0) );
		
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
}
