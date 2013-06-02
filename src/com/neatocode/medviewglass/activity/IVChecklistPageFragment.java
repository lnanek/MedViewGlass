
package com.neatocode.medviewglass.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.neatocode.medviewglass.Constants;
import com.neatocode.medviewglass.R;

public class IVChecklistPageFragment extends Fragment {

    public static final String ARG_PAGE = "page";

    private int mPageNumber;
    
    private CheckBox mCheckBox;
    
    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static IVChecklistPageFragment create(int pageNumber) {
        IVChecklistPageFragment fragment = new IVChecklistPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public IVChecklistPageFragment() {
  
    }
    
    public void setCheckbox() {

		Log.i(Constants.LOG_TAG, "setCheckbox, thread = " + Thread.currentThread().getName());
    	if ( null != mCheckBox ) {
    		Log.i(Constants.LOG_TAG, "checkbox checked");
    	mCheckBox.setChecked(true);
    	} else {

    		Log.i(Constants.LOG_TAG, "checkbox null");
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
		Log.i(Constants.LOG_TAG, "onCreateView");
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_checklist_page, container, false);

        // Set the title view to show the page number.
        ((TextView) rootView.findViewById(android.R.id.text1)).setText(
        		IVChecklistActivity.TARGET_NAMES[mPageNumber]);
        
        mCheckBox = (CheckBox) rootView.findViewById(R.id.checkbox1);
        mCheckBox.setChecked(false);

        rootView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCheckBox.setChecked(true);
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
