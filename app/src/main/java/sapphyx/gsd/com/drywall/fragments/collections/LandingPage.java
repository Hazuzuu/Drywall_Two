package sapphyx.gsd.com.drywall.fragments.collections;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sapphyx.gsd.com.drywall.R;

import static sapphyx.gsd.com.drywall.activity.MainActivityBase.ARGS_INSTANCE;

/**
 * Created by ry on 2/28/18.
 */

public class LandingPage extends Fragment {

    public LandingPage() {
    }

    public static LandingPage newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        LandingPage fragment = new LandingPage();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.collections_landing_page, container, false);
        setHasOptionsMenu(true);

        return v;
    }
}