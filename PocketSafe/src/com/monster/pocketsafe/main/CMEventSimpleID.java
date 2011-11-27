package com.monster.pocketsafe.main;

public class CMEventSimpleID implements IMEventSimpleID {

	private TTypEvent mTyp;
	private int mId;
	
	public TTypEvent getTyp() {
		return mTyp;
	}

	public void setTyp(TTypEvent typ) {
		mTyp = typ;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

}
