package com.neatocode.medviewglass.model;

import com.neatocode.medviewglass.R;

public class Overlay {
	
	public static Overlay[] OVERLAYS = new Overlay[] {
		new Overlay(R.drawable.overlay_vein1, "accessory cephalic vein"),
		new Overlay(R.drawable.overlay_vein1_reversed, "accessory cephalic vein"),
		new Overlay(R.drawable.overlay_vein1_and_arm, "accessory cephalic vein"),
		new Overlay(R.drawable.overlay_vein2, "basilic vein"),
		new Overlay(R.drawable.overlay_vein2_reversed, "basilic vein"),
		new Overlay(R.drawable.overlay_vein2_and_arm, "basilic vein"),
		new Overlay(R.drawable.overlay_vein3, "intermediate antebrachial vein"),
		new Overlay(R.drawable.overlay_vein3_reversed, "intermediate antebrachial vein"),
		new Overlay(R.drawable.overlay_vein3_and_arm, "intermediate antebrachial vein"),
	};
	
	public int indicatorDrawableId;
	
	public String name;

	public Overlay(int indicatorDrawableId, String name) {
		this.indicatorDrawableId = indicatorDrawableId;
		this.name = name;
	}
	
}
