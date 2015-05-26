package com.example.torsh.myimageswipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import static com.example.torsh.myimageswipe.MainActivity.*;

/**
 * Created by torsh on 5/12/15.
 */
public class ImageGridDialogFragment extends DialogFragment implements View.OnClickListener{

    GridView gridView;
    private ImageAdapterGrid mAdapter;
    public FragmentManager fragmentManager;
    public UpdatePositionListener positionListenerI;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view_dialog_fragment = inflater.inflate(R.layout.frag_dialog_image_grid, null);
        // instantiating interface
        positionListenerI = (UpdatePositionListener) getActivity();

        getDialog().setTitle("Select Image");
        //setCancelable(false);
        fragmentManager = getFragmentManager();

        mAdapter = new ImageAdapterGrid(view_dialog_fragment.getContext());
        gridView = (GridView) view_dialog_fragment.findViewById(R.id.grid_view);
        gridView.setColumnWidth(10); // width in pixels
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(view_dialog_fragment.getContext(), "go to position "+String.valueOf(position), Toast.LENGTH_SHORT).show();
                positionListenerI.onNewPosition(position);
                dismiss();

            }
        });

        return view_dialog_fragment;
    }

    @Override
    public void onClick(View v) {
    }
}
