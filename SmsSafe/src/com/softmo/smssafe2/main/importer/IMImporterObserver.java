package com.softmo.smssafe2.main.importer;

public interface IMImporterObserver {
	public void importerStart();
	public void importerProgress(int perc);
	public void importerFinish();
	public void importerError(int err);

}
