package com.neatocode.medviewglass.model;

import java.util.ArrayList;
import java.util.List;

import com.neatocode.medviewglass.BuildUtil;
import com.neatocode.medviewglass.R;
import com.neatocode.medviewglass.activity.CheckoutActivity;
import com.neatocode.medviewglass.activity.EnhancementActivity;
import com.neatocode.medviewglass.activity.FluChecklistActivity;
import com.neatocode.medviewglass.activity.IVChecklistActivity;
import com.neatocode.medviewglass.activity.OverlayActivity;
import com.neatocode.medviewglass.activity.OverlayCameraActivity;
import com.neatocode.medviewglass.activity.RunTestActivity;

public class SlideActivityOption {
	
	public static List<SlideActivityOption> OPTIONS = new ArrayList<SlideActivityOption>();
	static {
		if ( BuildUtil.isGlass() ) {		
			OPTIONS.add(new SlideActivityOption(R.drawable.overlay_menu_icon, "Vein Overlay", OverlayActivity.class));
		}
		if ( !BuildUtil.isGlass() ) {		
			OPTIONS.add(new SlideActivityOption(R.drawable.overlay_menu_icon, "Vein Overlay Camera", OverlayCameraActivity.class));
		}
		OPTIONS.add(new SlideActivityOption(R.drawable.highlight_menu_icon, "Vein Highlighting", EnhancementActivity.class));
		OPTIONS.add(new SlideActivityOption(R.drawable.blue_checkmark_menu_icon, "IV Checklist", IVChecklistActivity.class));
		OPTIONS.add(new SlideActivityOption(R.drawable.blue_checkmark_menu_icon, "Flu Shot Checklist", FluChecklistActivity.class));
		if ( !BuildUtil.isGlass() ) {
			OPTIONS.add(new SlideActivityOption(R.drawable.paypal_menu_icon, "Checkout Patient", CheckoutActivity.class));
			OPTIONS.add(new SlideActivityOption(R.drawable.explain_menu_icon, "Explain Help", RunTestActivity.class));
			
		}
	}
		
	public int icon;
	
	public String name;

	public Class<?> activity;

	public SlideActivityOption(int icon, String name, Class<?> activity) {
		this.icon = icon;
		this.name = name;
		this.activity = activity;
	}
	
}
