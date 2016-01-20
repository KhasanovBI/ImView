package jat.imview.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import jat.imview.R;

import jat.imview.model.Image;
import jat.imview.ui.view.TouchImageView;

public class ImageFragment extends Fragment {
    private static final String ARGUMENT_IMAGE = "argument_image";
    private Image image;
    private TouchImageView mTouchImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARGUMENT_IMAGE)) {
            image = (Image) getArguments().getSerializable(ARGUMENT_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        mTouchImageView = (TouchImageView) rootView.findViewById(R.id.image);
        final CircularProgressBar circularProgressBar = (CircularProgressBar) rootView.findViewById(R.id.preloader);
        final TextView textView = (TextView) rootView.findViewById(R.id.check_connection);
        String url = image.getFullNetpath();
        Log.d("MyImageURL", url);
        Picasso.with(getActivity())
                .load(url)
                .into(mTouchImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                mTouchImageView.setVisibility(View.VISIBLE);
                                textView.setVisibility(View.INVISIBLE);
                                circularProgressBar.setVisibility(View.INVISIBLE);
                                Log.d("MyImageLoading", "Success");
                            }

                            @Override
                            public void onError() {
                                textView.setVisibility(View.VISIBLE);
                                circularProgressBar.setVisibility(View.INVISIBLE);
                                Log.d("MyImageLoading", "Error");
                            }
                        }
                );
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public static ImageFragment newInstance(Image image) {
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_IMAGE, image);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
