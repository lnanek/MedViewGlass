
package com.neatocode.medviewglass.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.neatocode.medviewglass.R;

public class FluChecklistPageFragment extends Fragment {

    public static final String ARG_PAGE = "page";

    private int mPageNumber;
    
    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static FluChecklistPageFragment create(int pageNumber) {
        FluChecklistPageFragment fragment = new FluChecklistPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FluChecklistPageFragment() {
    }
    
    public void setCheckbox() {
    	View v = getView();
    	setCheckbox(v);
    }
    
    public static void setCheckbox(View v) {
    	if ( v instanceof CheckBox) {
    		((CheckBox)v).setChecked(true);
    	}
    	if ( v instanceof ViewGroup) {
    		int count = ((ViewGroup) v).getChildCount();
    		for(int i = 0 ; i < count; i++) {
    			View child = ((ViewGroup) v).getChildAt(i);
    			setCheckbox(child);
    		}
    	}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_checklist_page, container, false);

        // Set the title view to show the page number.
        ((TextView) rootView.findViewById(android.R.id.text1)).setText(
        		FluChecklistActivity.TARGET_NAMES[mPageNumber]);
        
        final CheckBox box = (CheckBox) rootView.findViewById(R.id.checkbox1);
        box.setChecked(false);
        
        rootView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		       box.setChecked(true);
			}
		});
        

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
