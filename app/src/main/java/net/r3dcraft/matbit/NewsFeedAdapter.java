package net.r3dcraft.matbit;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 16.10.2017.
 */

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.NewsFeedViewHolder> {

    private final static String TAG = "NewsfeedAdapter";

    private final SortedList<NewsFeed> sortedNewsfeedList = new SortedList<>(NewsFeed.class, new SortedList.Callback<NewsFeed>() {
        @Override
        public int compare(NewsFeed a, NewsFeed b) {
            return comparator.compare(a, b);
        }

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(NewsFeed oldItem, NewsFeed newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(NewsFeed item1, NewsFeed item2) {
            return item1.getTitle() == item2.getTitle();
        }
    });

    private static Context context;
    private String recipeID;
    private Comparator<NewsFeed> comparator;

    public NewsFeedAdapter(Context context, Comparator<NewsFeed> comparator) {
        this.context = context;
        this.recipeID = recipeID;
        this.comparator = comparator;
    }

    public void setComparator(Comparator<NewsFeed> comparator) {
        this.comparator = comparator;
    }

    public void add(NewsFeed newsFeed) {
        sortedNewsfeedList.add(newsFeed);
    }

    public void remove(NewsFeed newsFeed) {
        sortedNewsfeedList.remove(newsFeed);
    }

    public void add(List<NewsFeed> newsFeeds) {
        sortedNewsfeedList.addAll(newsFeeds);
    }

    public void remove(List<NewsFeed> newsFeeds) {
        sortedNewsfeedList.beginBatchedUpdates();
        for (NewsFeed newsFeed : newsFeeds) {
            sortedNewsfeedList.remove(newsFeed);
        }
        sortedNewsfeedList.endBatchedUpdates();
    }

    public void replaceAll(List<NewsFeed> newsFeeds) {
        sortedNewsfeedList.beginBatchedUpdates();
        for (int i = sortedNewsfeedList.size() - 1; i >= 0; i--) {
            final NewsFeed newsFeed = sortedNewsfeedList.get(i);
            if (!newsFeeds.contains(newsFeed)) {
                sortedNewsfeedList.remove(newsFeed);
            }
        }
        sortedNewsfeedList.addAll(newsFeeds);
        sortedNewsfeedList.endBatchedUpdates();
    }

    @Override
    public int getItemCount() {
        return sortedNewsfeedList.size();
    }

    @Override
    public NewsFeedAdapter.NewsFeedViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.activity_main_feed_item, viewGroup, false);

        return new NewsFeedAdapter.NewsFeedViewHolder(itemView);
    }

    public static class NewsFeedViewHolder extends RecyclerView.ViewHolder {
        private NewsFeed newsFeed;
        private ImageView img_thumbnail;
        private ImageView img_featured_image;
        private TextView txt_title;
        private TextView txt_time;
        private TextView txt_text;

        public NewsFeedViewHolder(View view) {
            super(view);
            img_thumbnail = (ImageView) view.findViewById(R.id.feed_item_thumbnail);
            img_featured_image = (ImageView) view.findViewById(R.id.feed_item_featured_image);
            txt_title = (TextView) view.findViewById(R.id.feed_item_title);
            txt_time = (TextView) view.findViewById(R.id.feed_item_time);
            txt_text = (TextView) view.findViewById(R.id.feed_item_text);
        }
    }

    @Override
    public void onBindViewHolder(final NewsFeedAdapter.NewsFeedViewHolder newsFeedViewHolder, int i) {
        final NewsFeed NEWSFEED = sortedNewsfeedList.get(i);
        final String MAINLINE = NEWSFEED.getTitle();
        newsFeedViewHolder.newsFeed = NEWSFEED;

        if (!NEWSFEED.getUrl_thumbnail().equals("") || NEWSFEED.getUrl_thumbnail() != null)
            Glide.with(context)
                    .load(NEWSFEED.getUrl_thumbnail())
                    .into(newsFeedViewHolder.img_thumbnail);
        if (!NEWSFEED.getUrl_featured_image().equals("") || NEWSFEED.getUrl_featured_image() != null)
            Glide.with(context)
                    .load(NEWSFEED.getUrl_featured_image())
                    .into(newsFeedViewHolder.img_featured_image);

        newsFeedViewHolder.txt_title.setText(NEWSFEED.getTitle());
        newsFeedViewHolder.txt_time.setText(DateUtility.dateToTimeText(NEWSFEED.getDate()) + " siden");
        newsFeedViewHolder.txt_text.setText(NEWSFEED.getText());
    }
}