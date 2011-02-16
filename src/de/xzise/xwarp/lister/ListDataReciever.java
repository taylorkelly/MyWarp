package de.xzise.xwarp.lister;

public interface ListDataReciever {

	ListSection[] getListSections(int start, int length);
	
	int getSize();
	
}
