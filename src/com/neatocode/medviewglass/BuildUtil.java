package com.neatocode.medviewglass;

import android.os.Build;

public class BuildUtil {
	
	public static boolean isGlass() {
		return (null != Build.DEVICE && Build.DEVICE.toUpperCase().startsWith("GLASS"))
				|| (null != Build.MODEL && Build.MODEL.toUpperCase().startsWith("GLASS"))
				|| (null != Build.PRODUCT && Build.PRODUCT.toUpperCase().startsWith("GLASS"))
				;
	}

	public static boolean isKindle() {
		return null != Build.PRODUCT && Build.PRODUCT.toUpperCase().startsWith("KINDLE")
				;
	}
}
