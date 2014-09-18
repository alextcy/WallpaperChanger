package com.example.wallpaperchanger.folderpicker;

import java.io.File;

public class Folder {

	private File dir; 
	private String folderName;
	private String folderPath;
	
	public Folder(File directory) 
	{
		dir = directory;
		
		folderName = directory.getName();
		folderPath = directory.getAbsolutePath();
	}

	public String getFolderName() {
		return folderName;
	}

	
	public String getFolderPath() {
		return folderPath;
	}
	
	public boolean hasParent()
	{
		if(dir.getParent() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getParentPath()
	{
		return dir.getParent();
	}
	
	public String toString()
	{
		return folderName;
	}
}
