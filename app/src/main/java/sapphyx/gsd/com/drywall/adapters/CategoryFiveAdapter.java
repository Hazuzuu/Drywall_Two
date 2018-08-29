package sapphyx.gsd.com.drywall.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prof.rssparser.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import sapphyx.gsd.com.drywall.R;
import sapphyx.gsd.com.drywall.activity.DetailsActivity;
import sapphyx.gsd.com.drywall.util.AnimationHelper;
import sapphyx.gsd.com.drywall.util.PaletteTransformation;
import sapphyx.gsd.com.drywall.util.SettingsProvider;

public class CategoryFiveAdapter extends RecyclerView.Adapter<CategoryFiveAdapter.ViewHolder> {

    private ArrayList<Article> articles;

    private int rowLayout;
    private Context mContext;

    private boolean usePalette = true;

    public CategoryFiveAdapter(ArrayList<Article> list, int rowLayout, Context context) {

        this.articles = list;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }


    @Override
    public long getItemId(int item) {
        // TODO Auto-generated method stub
        return item;
    }

    public void clearData() {
        if (articles != null)
            articles.clear();
    }

    @Override
    public CategoryFiveAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new CategoryFiveAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CategoryFiveAdapter.ViewHolder viewHolder, final int position) {

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int imageWidth = (width / 2);

        SettingsProvider.get(mContext)
                .edit()
                .putInt("categoryFiveCount", getItemCount())
                .apply();

        viewHolder.title.setText(articles.get(position).getTitle());

        Picasso.with(mContext).setIndicatorsEnabled(false);

        Picasso.with(mContext)
                .load(articles.get(position).getImage())
                .resize(imageWidth, imageWidth)
                .centerCrop()
                .transform(PaletteTransformation.instance())
                .into(viewHolder.image,
                        new PaletteTransformation.PaletteCallback(viewHolder.image) {
                            @Override
                            public void onSuccess(Palette palette) {
                                if (usePalette) {
                                    if (palette != null) {
                                        Palette.Swatch wallSwatch = palette.getDominantSwatch();
                                        if (wallSwatch != null ) {
                                            viewHolder.titleBg.setBackgroundColor(wallSwatch.getRgb());
                                            viewHolder.titleBg.setAlpha((float) 0.5);
                                            viewHolder.title.setTextColor(wallSwatch.getBodyTextColor());
                                            viewHolder.title.setAlpha(1);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onError() {
                            }
                        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(mContext, DetailsActivity.class)
                        .putExtra("wall", articles.get(position).getImage())
                        .putExtra("wallTitle", articles.get(position).getTitle());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles == null ? 0 : articles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        LinearLayout titleBg;

        public ViewHolder(View itemView) {
            super(itemView);

            AnimationHelper.animateGroup(itemView.findViewById(R.id.card_image), itemView.findViewById(R.id.card_title), itemView.findViewById(R.id.title_bkg));

            title = itemView.findViewById(R.id.card_title);
            image = itemView.findViewById(R.id.card_image);
            titleBg = itemView.findViewById(R.id.title_bkg);
        }
    }
}