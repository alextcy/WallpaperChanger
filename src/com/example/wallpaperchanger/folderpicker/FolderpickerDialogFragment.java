package com.example.wallpaperchanger.folderpicker;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import com.example.wallpaperchanger.PictureFragment;
import com.example.wallpaperchanger.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FolderpickerDialogFragment extends DialogFragment
{
	public static final String FOLDER_SELECTED_KEY	= "com.example.wallpaperchanger.folderpicker.folder_selected";
	//final String[] items = new String[] { "Android", "iPhone", "WindowsMobile", "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2" };
		
	private String rootFolder = Environment.getExternalStorageDirectory().toString();
		
	//путь к выбраной папке в диалоге
	private String selectedFolder;
	
	
	private ArrayList<Folder> folderList;
	
	public FolderpickerDialogFragment() 
	{
				
	}
	
	/*public static FolderpickerDialogFragment newInstance(String currentFolderPath)
	{
		Bundle args = new Bundle();
		args.putString(FOLDER_SELECTED_KEY, currentFolderPath);
		
		FolderpickerDialogFragment fragment = new FolderpickerDialogFragment();
		fragment.setArguments(args);
		
		return fragment;
	}*/
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_folderpicker, null);
		
		folderList = getFolderList(rootFolder);
		selectedFolder = rootFolder;
		
		ListView list = (ListView) v.findViewById(R.id.folder_list);
		ArrayAdapter<Folder> adapter = new ArrayAdapter<Folder>(getActivity(), android.R.layout.simple_list_item_1, folderList);
		list.setAdapter(adapter);
		
		//отслеживаем клик по списку с директориями
		list.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view, int position,long id)
		    {
		        Folder fld = (Folder)parent.getAdapter().getItem(position);
		        if(fld.getFolderName().equals(".android_secure")) {
		        	return;
		        }

		        selectedFolder = fld.getFolderPath();
		    	
		    	folderList.clear();
		    	//TODO: нужно реализовать кастомный адаптер и кастомный вид эл-тов списка, где первый эл-т это линк на уровень вверх
		    	if(fld.hasParent() && !fld.getParentPath().equals("/mnt") ) {
		    		folderList.add(new Folder( new File( fld.getParentPath() ) ));
		    	}
		    	
		    	folderList.addAll( getFolderList(selectedFolder) );
		    	((ArrayAdapter<Folder>)parent.getAdapter()).notifyDataSetChanged();
		    	
		    	
		    }
		});
		
		return new AlertDialog.Builder(getActivity())
			.setView(v)
			.setTitle(R.string.select_folder)
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					sendResult(Activity.RESULT_OK);
				}
			})
			.create();
	}

	
	private ArrayList<Folder> getFolderList(String path)
	{
		File fh = new File(path);
		File files[] = fh.listFiles();
		
		ArrayList<Folder> folders = new ArrayList<Folder>();
		
		for ( File afile : files ) {
			
			if(afile.isDirectory() == true) {
				folders.add(new Folder(afile));
			}
		}
		
		return folders;
	}
	
	//вернуть выбраную папку в фрагмент (через интент) который вызвал этот: FolderpickerDialogFragment 
	private void sendResult(int resultCode)
	{
		if(getTargetFragment() == null) {
			return;
		}
		
		Intent i = new Intent();
		i.putExtra(FOLDER_SELECTED_KEY, selectedFolder);
		
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}
		
	
}
