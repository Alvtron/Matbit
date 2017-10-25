package net.r3dcraft.matbit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by unibl on 16.10.2017.
 */

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.NewsFeedViewHolder> {

    private List<NewsFeed> newsFeeds;
    private Context context;

    public NewsFeedAdapter(List<NewsFeed> newsFeeds, Context context) {
        this.newsFeeds = newsFeeds;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return newsFeeds.size();
    }

    @Override
    public void onBindViewHolder(NewsFeedViewHolder contactViewHolder, int i) {
        NewsFeed newsFeed = newsFeeds.get(i);
        contactViewHolder.vMainline.setText(newsFeed.getMainline());
        contactViewHolder.vUnderline.setText(newsFeed.getUnderline());
        //Glide.with(context).load(newsFeed.getPhoto_url()).into(contactViewHolder.vPhoto);
    }

    @Override
    public NewsFeedViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.feed_item, viewGroup, false);

        return new NewsFeedViewHolder(itemView);
    }

    public static class NewsFeedViewHolder extends RecyclerView.ViewHolder {
        protected TextView vMainline;
        protected TextView vUnderline;
        protected ImageView vPhoto;

        public NewsFeedViewHolder(View v) {
            super(v);
            vMainline = (TextView)  v.findViewById(R.id.feed_item_mainline);
            vUnderline = (TextView) v.findViewById(R.id.feed_item_underline);
            vPhoto = (ImageView) v.findViewById(R.id.feed_item_imageview);
        }
    }
}