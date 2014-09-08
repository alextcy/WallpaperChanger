package com.example.wallpaperchanger;



import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

public class PictureActivity extends Activity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture);
		
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
		if(fragment == null) {
			
			String pictureFullPath = getIntent().getStringExtra(PictureFragment.PIC_FULL_PATH_KEY);
			//fragment = new PictureFragment();
			//инстанцируем фрагмент и передаем в него данные полученые от PictureListActivity
			fragment = PictureFragment.newInstance(pictureFullPath);
			
			fm.beginTransaction()
				.add(R.id.fragmentContainer, fragment)
				.commit();
		}
		
		
		
		
	}
}
