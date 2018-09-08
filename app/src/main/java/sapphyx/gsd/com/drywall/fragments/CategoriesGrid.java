package sapphyx.gsd.com.drywall.fragments;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import sapphyx.gsd.com.drywall.R;
import sapphyx.gsd.com.drywall.adapters.CategoriesSelector;
import sapphyx.gsd.com.drywall.adapters.CategoryItems;
import sapphyx.gsd.com.drywall.adapters.ProvidersAdapter;
import sapphyx.gsd.com.drywall.util.AppManager;
import sapphyx.gsd.com.drywall.util.GridSpacingItemDecoration;
import sapphyx.gsd.com.drywall.util.RoundDialogHelper;
import sapphyx.gsd.com.drywall.util.SettingsProvider;

import static sapphyx.gsd.com.drywall.activity.MainActivityBase.ARGS_INSTANCE;

/**
 * This fragment list all of the categories we have available to the user
 * Use the categories as a position item for inflating the individual fragments
 */

public class CategoriesGrid extends Fragment{

    private List<CategoryItems> categoriesList = new ArrayList<>();

    private int numColumns = 2;
    String cnlCount, gsdCount, clrCount, envCount, oemCount, ctyCount, lfeCount, pxlCount, ertCount;
    private String bm;

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private RecyclerView providerList, categoryList;
    private CategoriesSelector categoriesAdapter;
    private ProvidersAdapter providerAdapter;
    private GridLayoutManager mLayoutManager;

    public CategoriesGrid() {
    }

    public static CategoriesGrid newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        CategoriesGrid fragment = new CategoriesGrid();
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
        View v = inflater.inflate(R.layout.category_grid_layout, container, false);
        setHasOptionsMenu(true);

        categoryList = v.findViewById(R.id.category_list);
        providerList = v.findViewById(R.id.provider_list);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (!prefs.contains("CategoriesMsg")) {
            RoundDialogHelper alert = new RoundDialogHelper();
            alert.showCategoriesDialog(getActivity(), getResources().getString(R.string.first_categories_message));
        }

        //Categories Listview
        categoriesAdapter = new CategoriesSelector((ArrayList<CategoryItems>) categoriesList, getContext());

        mLayoutManager = new GridLayoutManager(getContext(), numColumns);
        categoryList.addItemDecoration(new GridSpacingItemDecoration(numColumns, dpToPx(6), true));
        categoryList.setAdapter(categoriesAdapter);
        categoryList.setHasFixedSize(true);
        categoryList.setLayoutManager(mLayoutManager);

        //Providers Listview
        providerAdapter = new ProvidersAdapter(getContext(), new AppManager(getContext()).getInstalledPackages());

        LinearLayoutManager providersManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        providerList.addItemDecoration(new GridSpacingItemDecoration(numColumns, dpToPx(0), false));
        providerList.setAdapter(providerAdapter);
        providerList.setHasFixedSize(true);
        providerList.setLayoutManager(providersManager);

        providerAdapter.notifyDataSetChanged();


        prepareCategoryData(v);

        return  v;
    }

    private void prepareCategoryData(View v) {

        cnlCount = String.valueOf(SettingsProvider.get(getContext()).getInt("categoryOneCount", 1));
        gsdCount = String.valueOf(SettingsProvider.get(getContext()).getInt("categoryTwoCount", 1));
        envCount = String.valueOf(SettingsProvider.get(getContext()).getInt("categoryThreeCount", 1));
        oemCount = String.valueOf(SettingsProvider.get(getContext()).getInt("categoryFourCount", 1));
        clrCount = String.valueOf(SettingsProvider.get(getContext()).getInt("categoryFiveCount", 1));
        ctyCount = String.valueOf(SettingsProvider.get(getContext()).getInt("categorySixCount", 1));
        lfeCount = String.valueOf(SettingsProvider.get(getContext()).getInt("categorySevenCount", 1));
        pxlCount = String.valueOf(SettingsProvider.get(getContext()).getInt("categoryEightCount", 1));
        ertCount = String.valueOf(SettingsProvider.get(getContext()).getInt("categoryNineCount", 1));

        CategoryItems CNL = new CategoryItems(v, "CNL PHOTOS", cnlCount, R.drawable.camera_gopro);
        categoriesList.add(CNL);

        CategoryItems GSD = new CategoryItems(v, "GOCALSD", gsdCount, R.drawable.cube_send);
        categoriesList.add(GSD);

        CategoryItems ENV = new CategoryItems(v, "ENVIRONMENT", envCount, R.drawable.image_area);
        categoriesList.add(ENV);

        CategoryItems OEM = new CategoryItems(v, "OEM", oemCount, R.drawable.cellphone_text);
        categoriesList.add(OEM);

        CategoryItems CLR = new CategoryItems(v, "COLORS", clrCount, R.drawable.palette);
        categoriesList.add(CLR);

        CategoryItems CTY = new CategoryItems(v, "CITYSCAPE", ctyCount, R.drawable.city_variant_outline);
        categoriesList.add(CTY);

        CategoryItems LFE = new CategoryItems(v, "LIFE", lfeCount, R.drawable.dog_side);
        categoriesList.add(LFE);

        CategoryItems PXL = new CategoryItems(v, "PIXEL", pxlCount, R.drawable.google_glass);
        categoriesList.add(PXL);

        CategoryItems ERT = new CategoryItems(v, "EARTH", ertCount, R.drawable.earth_box);
        categoriesList.add(ERT);

        categoriesAdapter.notifyDataSetChanged();


    }
}
