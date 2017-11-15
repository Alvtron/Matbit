package net.r3dcraft.matbit;

import android.content.Context;
import android.content.DialogInterface;
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
 */

class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private final static String TAG = "CommentAdapter";

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

    public CommentAdapter(Context context, String recipeID, Comparator<Comment> comparator) {
        this.context = context;
        this.recipeID = recipeID;
        this.comparator = comparator;
    }

    public void setComparator(Comparator<Comment> comparator) {
        this.comparator = comparator;
    }

    public void add(Comment comment) {
        sortedCommentList.add(comment);
    }

    public void remove(Comment comment) {
        sortedCommentList.remove(comment);
    }

    public void add(List<Comment> comments) {
        sortedCommentList.addAll(comments);
    }

    public void remove(List<Comment> comments) {
        sortedCommentList.beginBatchedUpdates();
        for (Comment model : comments) {
            sortedCommentList.remove(model);
        }
        sortedCommentList.endBatchedUpdates();
    }

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

    @Override
    public int getItemCount() {
        return sortedCommentList.size();
    }

    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.fragment_recipe_comment_item, viewGroup, false);

        return new CommentAdapter.CommentViewHolder(itemView);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        protected Comment comment;
        protected String USER_UID;
        protected ImageView img_user;
        protected TextView txt_text;
        protected TextView txt_info;
        protected ImageView img_edit;

        public CommentViewHolder(View view) {
            super(view);
            img_user = (ImageView) view.findViewById(R.id.fragment_recipe_comment_user_photo);
            txt_text = (TextView) view.findViewById(R.id.fragment_recipe_comment_text);
            txt_info = (TextView) view.findViewById(R.id.fragment_recipe_comment_info);
            img_edit = (ImageView) view.findViewById(R.id.fragment_recipe_comment_edit);
        }
    }

    @Override
    public void onBindViewHolder(final CommentAdapter.CommentViewHolder commentViewHolder, int i) {
        final Comment COMMENT = sortedCommentList.get(i);
        final String USER_UID = COMMENT.getUser();
        commentViewHolder.comment = COMMENT;

        MatbitDatabase.getUser(USER_UID).addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String USERNAME = dataSnapshot.child("nickname").getValue(String.class);

                MatbitDatabase.userPictureToImageView(COMMENT.getUser(), context, commentViewHolder.img_user);

                commentViewHolder.txt_text.setText(Html.fromHtml("<b><font color='#c62828'>"
                        + USERNAME + "</font></b> "
                        + COMMENT.getComment()));

                commentViewHolder.txt_info.setText(COMMENT.getDatetimeCreated());

                if (MatbitDatabase.hasUser())
                    if (MatbitDatabase.getCurrentUserUID().equals(USER_UID))
                        commentViewHolder.img_edit.setVisibility(View.VISIBLE);

                commentViewHolder.img_user.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        MatbitDatabase.goToUser(context, USER_UID);
                    }
                });

                commentViewHolder.img_edit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Show dialog box for next step
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                        builder.setTitle("Endre kommentar");

                        // Set up dialog layout
                        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_layout_edit_comment, null);
                        final AppCompatEditText input = (AppCompatEditText) view.findViewById(R.id.alert_dialog_layout_edit_comment_edittext);
                        input.setText(COMMENT.getComment());
                        builder.setView(view);

                        // Set up the buttons
                        builder.setPositiveButton("Lagre", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "[Under construction]", Toast.LENGTH_SHORT).show();
                                //txt_text.setText(input.getText().toString());
                                //Recipe.changeComment(recipeUID, COMMENT_UID, input.getText().toString());
                            }
                        });
                        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Get user:onCancelled", databaseError.toException());
            }
        });
    }
}