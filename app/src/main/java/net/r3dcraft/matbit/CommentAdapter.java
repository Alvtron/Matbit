package net.r3dcraft.matbit;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 26.10.2017.
 *
 * This Comment adapter extends the RecyclerView and keeps track of comments as well as sorting them
 * with a specified comparator. Adding and removing Comment-objects is goes automatic with this class.
 */
class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final static String TAG = "CommentAdapter";

    /**
     * This is a SortedList that stores and manages Comment-objects. This is made so that adding and
     * removing Comment-objects goes automatically, without any need to notify the adapter every time
     * the data-set is changed. This SortedList uses a custom comparator specified in the constructor
     * of this adapter.
     */
    private final SortedList<Comment> sortedCommentList = new SortedList<>(Comment.class, new SortedList.Callback<Comment>() {
        @Override
        public int compare(Comment a, Comment b) {
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
        public boolean areContentsTheSame(Comment oldItem, Comment newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Comment item1, Comment item2) {
            return item1.getUser() == item2.getUser();
        }
    });

    private static Context context;
    private String recipeID;
    private Comparator<Comment> comparator;

    /**
     *
     * Construct a CommentAdapter that keeps track of comments as well as sorting them with a
     * specified comparator
     *
     * @param context - the context of the Activity that constructs this adapter
     * @param recipeID - a recipe ID string to which recipe these comments belong to
     * @param comparator - a custom comparator that compares Comment-objects
     */
    public CommentAdapter(Context context, String recipeID, Comparator<Comment> comparator) {
        CommentAdapter.context = context;
        this.recipeID = recipeID;
        this.comparator = comparator;
    }

    /**
     * Add a new comment to this adapter. The data-set will be sorted automatically by the comparator.
     * If the comment already exists, it won't be added.
     *
     * @param comment - A Comment object
     */
    public void add(Comment comment) {
        sortedCommentList.add(comment);
    }

    /**
     * Remove a comment. The data-set will be sorted automatically by the comparator.
     * If the comment don't exist, nothing happens.
     *
     * @param comment - A Comment object
     */
    public void remove(Comment comment) {
        sortedCommentList.remove(comment);
    }

    /**
     * Add comments to this adapter. The data-set will be sorted automatically by the comparator.
     * If the some comments already exists, they won't be added.
     *
     * @param comments - A List of Comment objects
     */
    public void add(List<Comment> comments) {
        sortedCommentList.addAll(comments);
    }

    /**
     * Remove multiple comments. The data-set will be sorted automatically by the comparator.
     * If some of the comments don't exist, nothing happens.
     *
     * @param comments - A List of Comment objects
     */
    public void remove(List<Comment> comments) {
        sortedCommentList.beginBatchedUpdates();
        for (Comment model : comments) {
            sortedCommentList.remove(model);
        }
        sortedCommentList.endBatchedUpdates();
    }

    /**
     * Replace all Comment objects in this adapter. The data-set will be sorted automatically by the
     * comparator.
     *
     * @param comments - A List of Comment objects
     */
    public void replaceAll(List<Comment> comments) {
        sortedCommentList.beginBatchedUpdates();
        for (int i = sortedCommentList.size() - 1; i >= 0; i--) {
            final Comment model = sortedCommentList.get(i);
            if (!comments.contains(model)) {
                sortedCommentList.remove(model);
            }
        }
        sortedCommentList.addAll(comments);
        sortedCommentList.endBatchedUpdates();
    }

    /**
     * Delete all comments in the sortedList.
     */
    public void clear() {
        sortedCommentList.clear();
        notifyDataSetChanged();
    }

    /**
     *
     * @return the number of Comment objects stored in this adapter.
     */
    @Override
    public int getItemCount() {
        return sortedCommentList.size();
    }

    /**
     * This creates a new RecyclerView.ViewHolder and initializes private fields to be used by
     * RecyclerView.
     *
     * @param viewGroup
     * @param i
     * @return a CommentViewHolder that holds a inflated Comment layout.
     */
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.fragment_recipe_comment_item, viewGroup, false);

        return new CommentAdapter.CommentViewHolder(itemView);
    }

    /**
     * A CommentViewHolder class that holds and sets a comment layout.
     */
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_user; // Photo of the Author
        private TextView txt_text; // The comment
        private TextView txt_info; // Info for displaying post-date
        private ImageView img_edit; // Edit icon

        public CommentViewHolder(View view) {
            super(view);
            img_user = view.findViewById(R.id.fragment_recipe_comment_user_photo);
            txt_text = view.findViewById(R.id.fragment_recipe_comment_text);
            txt_info = view.findViewById(R.id.fragment_recipe_comment_info);
            img_edit = view.findViewById(R.id.fragment_recipe_comment_edit);
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method updates the
     * contents of the Comment view to reflect the Comment object at the given position.
     *
     * @param commentViewHolder - A CommentViewHolder class that holds and sets a comment layout.
     * @param i - The position of the Comment
     */
    @Override
    public void onBindViewHolder(final CommentAdapter.CommentViewHolder commentViewHolder, int i) {
        final Comment COMMENT = sortedCommentList.get(i); // Get Comment

        // Load and display Author photo
        MatbitDatabase.userPictureToImageView(COMMENT.getUser(), context, commentViewHolder.img_user);

        // Set comment text. Use HTML tags to add colors to the nickname
        commentViewHolder.txt_text.setText(Html.fromHtml("<b><font color='#c62828'>"
                + COMMENT.getUserNickname() + "</font></b> "
                + COMMENT.getComment()));

        // Set comment info (post date)
        commentViewHolder.txt_info.setText(COMMENT.getDatetimeCreated());

        // Set edit comment icon visibility
        if (MatbitDatabase.hasUser() && MatbitDatabase.getCurrentUserUID().equals(COMMENT.getUser()))
            commentViewHolder.img_edit.setVisibility(View.VISIBLE);
        else
            commentViewHolder.img_edit.setVisibility(View.INVISIBLE);

        // If user clicks on author profile picture, user is taken to author's profile.
        commentViewHolder.img_user.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MatbitDatabase.goToUser(context, COMMENT.getUser());
            }
        });

        // If user clicks on edit symbol, user is prompted with a dialog where
        // the user can change the comment.
        commentViewHolder.img_edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Show dialog box for next step
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                builder.setTitle(R.string.string_change_comment);

                // Set up dialog layout
                View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_layout_edit_comment, null);
                final AppCompatEditText input = view.findViewById(R.id.alert_dialog_layout_edit_comment_edittext);
                input.setText(COMMENT.getComment());
                builder.setView(view);
                builder.setIcon(R.drawable.icon_edit_black_24dp);
                // Set up the buttons
                builder.setPositiveButton(R.string.string_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Recipe.changeComment(recipeID, COMMENT.getKey(), input.getText().toString());
                    }
                });
                builder.setNegativeButton(R.string.string_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setNeutralButton(R.string.string_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean deletion_successful = Recipe.deleteComment(recipeID, COMMENT);
                        if (!deletion_successful)
                            Toast.makeText(context, R.string.error_something_went_wrong_comment_not_deleted, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, R.string.string_comment_is_deleted, Toast.LENGTH_SHORT).show();
                    }
                });
                // Show dialog
                builder.show();
            }
        });
    }
}