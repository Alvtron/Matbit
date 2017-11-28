package net.r3dcraft.matbit;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
 *
 * This NewsFeed adapter extends the RecyclerView and keeps track of news feed items as well as
 * sorting them with a specified comparator. Adding and removing NewsFeed-objects goes automatic
 * with this class.
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
    private Comparator<NewsFeed> comparator;

    /**
     *
     * Construct a NewsFeedAdapter that keeps track of NewsFeed objects as well as sorting them with
     * the specified comparator.
     *
     * @param context - the context of the Activity that constructs this adapter
     * @param comparator - a custom comparator that compares NewsFeed-objects
     */
    public NewsFeedAdapter(Context context, Comparator<NewsFeed> comparator) {
        NewsFeedAdapter.context = context;
        this.comparator = comparator;
    }

    /**
     * Add a new NewsFeed to this adapter. The data-set will be sorted automatically by the comparator.
     * If the NewsFeed object already exists, it won't be added.
     *
     * @param newsFeed - A Comment object
     */
    public void add(NewsFeed newsFeed) {
        sortedNewsfeedList.add(newsFeed);
    }

    /**
     * Remove a NewsFeed object. The data-set will be sorted automatically by the comparator.
     * If the NewsFeed object don't exist, nothing happens.
     *
     * @param newsFeed - A Comment object
     */
    public void remove(NewsFeed newsFeed) {
        sortedNewsfeedList.remove(newsFeed);
    }

    /**
     * Add NewsFeed to this adapter. The data-set will be sorted automatically by the comparator.
     * If some of the NewsFeed objects already exists, they won't be added.
     *
     * @param newsFeeds - A List of NewsFeed objects
     */
    public void add(List<NewsFeed> newsFeeds) {
        sortedNewsfeedList.addAll(newsFeeds);
    }

    /**
     * Remove multiple NewsFeed objects. The data-set will be sorted automatically by the comparator.
     * If some of the NewsFeed objects don't exist, nothing happens.
     *
     * @param newsFeeds - A List of NewsFeed objects
     */
    public void remove(List<NewsFeed> newsFeeds) {
        sortedNewsfeedList.beginBatchedUpdates();
        for (NewsFeed newsFeed : newsFeeds) {
            sortedNewsfeedList.remove(newsFeed);
        }
        sortedNewsfeedList.endBatchedUpdates();
    }

    /**
     * Replace all NewsFeed objects in this adapter. The data-set will be sorted automatically by the
     * comparator.
     *
     * @param newsFeeds - A List of NewsFeed objects
     */
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

    /**
     * Erase all NewsFeed objects and notify the recycler list.
     */
    public void empty() {
        sortedNewsfeedList.clear();
        notifyDataSetChanged();
    }

    /**
     *
     * @return the number of Comment objects stored in this adapter.
     */
    @Override
    public int getItemCount() {
        return sortedNewsfeedList.size();
    }

    /**
     * This creates a new RecyclerView.ViewHolder and initializes private fields to be used by
     * RecyclerView.
     *
     * @param viewGroup
     * @param i
     * @return a NewsFeedViewHolder that holds an inflated NewsFeed-layout.
     */
    @Override
    public NewsFeedAdapter.NewsFeedViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.activity_main_feed_item, viewGroup, false);

        return new NewsFeedAdapter.NewsFeedViewHolder(itemView);
    }

    /**
     * A NewsFeedViewHolder class that holds and sets a NewsFeed-layout.
     */
    public static class NewsFeedViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_thumbnail;
        private ImageView img_featured_image;
        private TextView txt_title;
        private TextView txt_time;
        private TextView txt_text;

        public NewsFeedViewHolder(View view) {
            super(view);
            img_thumbnail = view.findViewById(R.id.feed_item_thumbnail);
            img_featured_image = view.findViewById(R.id.feed_item_featured_image);
            txt_title = view.findViewById(R.id.feed_item_title);
            txt_time = view.findViewById(R.id.feed_item_time);
            txt_text = view.findViewById(R.id.feed_item_text);
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method updates the
     * contents of the NewsFeed view to reflect the NewsFeed object at the given position.
     *
     * @param newsFeedViewHolder - A NewsFeedViewHolder object that holds and sets a NewsFeed layout.
     * @param i - The position of the NewsFeed object
     */
    @Override
    public void onBindViewHolder(final NewsFeedAdapter.NewsFeedViewHolder newsFeedViewHolder, int i) {
        final NewsFeed NEWSFEED = sortedNewsfeedList.get(i);

        // Load NewsFeed thumbnail from Matbit Storage
        if (NEWSFEED.getStorage_reference_thumbnail() != null)
            MatbitDatabase.downloadToImageView(NEWSFEED.getStorage_reference_thumbnail(), context, newsFeedViewHolder.img_thumbnail);
        // Load NewsFeed featured photo (if any) from Matbit Storage
        if (NEWSFEED.getStorage_reference_featured_image() != null)
            MatbitDatabase.downloadToImageView(NEWSFEED.getStorage_reference_thumbnail(), context, newsFeedViewHolder.img_featured_image);

        // Set text in NewsFeed layout with NewsFeed object
        newsFeedViewHolder.txt_title.setText(NEWSFEED.getTitle());
        newsFeedViewHolder.txt_time.setText(NEWSFEED.getSubtitle());
        newsFeedViewHolder.txt_text.setText(Html.fromHtml(NEWSFEED.getText()));

        // If user click this NewsFeed item in Recycler List, start the action associated with NewsFeed object.
        newsFeedViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(NEWSFEED.getAction());
            }
        });
    }
}