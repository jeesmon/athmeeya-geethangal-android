package com.jeesmon.apps.ag.control;

import com.jeesmon.apps.ag.R;

import android.content.Context;
import android.graphics.Typeface;

public class FontControl {
	private static FontControl instance;
	
	private Typeface typeface;
	
	private FontControl(){		
	}
	
	public static FontControl getInstance(Context context) {
		if(instance == null) {
			instance = new FontControl();
			instance.typeface = Typeface.createFromAsset(context.getAssets(), context.getResources().getString(R.string.font_name));
		}
		
		return instance;
	}
	
	public Typeface getTypeface() {
		return this.typeface;
	}
}
