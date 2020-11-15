package io.methinks.android.methinks_android_forum_sdk.adapter;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import io.methinks.android.methinks_android_forum_sdk.Global;
import io.methinks.android.methinks_android_forum_sdk.Log;
import io.methinks.android.methinks_android_forum_sdk.R;
import io.methinks.android.methinks_android_forum_sdk.activity.ForumPostDetailActivity;
import io.methinks.android.methinks_android_forum_sdk.activity.ForumProfileCreateActivity;

import static io.methinks.android.methinks_android_forum_sdk.Global.getImageId;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>  {
    private JSONArray postData;
    public Activity activity;

    public PostAdapter(JSONArray postData, Activity activity) {
        this.postData = postData;
        this.activity = activity;
    }

    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View postView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_view, parent, false);
        return new PostViewHolder(postView, activity);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        try {
            JSONObject post;
            if(Global.type == Global.Type.Patcher) {
                post = postData.getJSONObject(position);
            } else {
                post = postData.getJSONObject(position).getJSONObject("post");
            }
            holder.onBind(post);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {return postData.length(); }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public ImageView profile;
        public JSONObject campaign;
        public TextView userName;
        public TextView createdAt;
        public TextView postTitle;
        public TextView postDesc;
        public TextView likeCount;
        public TextView commentCount;
        public TextView post_is_new;
        public Activity activity;
        public String postId;
        public String sectionId;
        public LinearLayout postPanel;
        public String profileIconString;

        public ArrayList<String> attachments;
        public String likedUsers;

        public String postTextWithHtml;


        public PostViewHolder(View itemView, Activity activity) {
            super(itemView);

            this.activity = activity;
            this.profile = itemView.findViewById(R.id.post_profile);
            this.userName = itemView.findViewById(R.id.post_creator);
            this.createdAt = itemView.findViewById(R.id.post_created_time);
            this.postTitle = itemView.findViewById(R.id.post_title);
            this.postDesc = itemView.findViewById(R.id.post_desc);
            this.likeCount = itemView.findViewById(R.id.post_like_count);
            this.commentCount = itemView.findViewById(R.id.post_comment_count);
            this.post_is_new = itemView.findViewById(R.id.post_is_new);
            this.postPanel = itemView.findViewById(R.id.post_panel);
            this.attachments = new ArrayList<>();
        }

        void onBind(JSONObject post) {
            try {
                postId = post.getString("objectId");
                sectionId = post.getJSONObject("section").getString("objectId");
                if (post.has("attachments")) {
                    String attachmentsSource = post.getString("attachments").replaceAll("[\\[\\](){}]", "");
                    for (String attachment : attachmentsSource.split(",")) {
                        String singleAttachment = attachment.replaceAll("\"", "");
                        attachments.add(singleAttachment);
                    }
                } else {
                    attachments = new ArrayList<>();
                }
                if (post.has("likedUsers")) {
                    likedUsers = post.getString("likedUsers");
                }

                // If post writer is admin
                if (post.getString("writeUserType").equals("admin")) {
                    Log.d("You are Admin ");
                    userName.setText(R.string.patcher_text_admin);
                    profile.setImageResource(R.drawable.img_logoprofile);
                } else {
                    // If post writer is thinker (participant is existed in object)
                    Log.d("You are Participant ");

                    JSONObject participant = post.getJSONObject("participant");
                    userName.setText(participant.getString("forumNickName"));
                    profileIconString = participant.getString("forumEmoji");
                    profile.setImageResource(getImageId(activity, profileIconString));
                }

                createdAt.setText(passedTimeCalc(post.getString("createdAt")));
                postTitle.setText(post.getString("postTitle"));

                postTextWithHtml = post.getString("postText");
                String purePostText = post.getString("postText").replaceAll("\\<.*?\\>", "");
                //Spanned descHtml = Html.fromHtml(post.getString("postText"));
                if (purePostText.length() > 80) {
                    purePostText = purePostText.substring(0, 80);
                    purePostText += "...";
                }
                postDesc.setText(purePostText);

                if (post.has("likedUsers")) {
                    likeCount.setVisibility(View.VISIBLE);
                    likeCount.setText(String.valueOf(post.getJSONArray("likedUsers").length()));
                } else {
                    likeCount.setText("0");
                }
                if (post.has("commentCount")) {
                    if (post.getInt("commentCount") > 0) {
                        commentCount.setVisibility(View.VISIBLE);
                        commentCount.setText(post.getString("commentCount"));
                    } else {
                        commentCount.setText("0");
                    }
                } else { commentCount.setText("0"); }


                postPanel.setOnClickListener(postOnClickListener);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        View.OnClickListener postOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.e("Pre Counter: " + likeCount.getText() + "/" + commentCount.getText());
                if (Global.isUserAllocated) {
                    Intent intent = new Intent(activity, ForumPostDetailActivity.class);
                    intent.putExtra("postId", postId);
                    intent.putExtra("sectionId", sectionId);
                    intent.putExtra("userName", userName.getText());
                    intent.putExtra("profile", profileIconString);
                    intent.putExtra("postTitle", postTitle.getText());
                    intent.putExtra("postText", postTextWithHtml);
                    intent.putExtra("likeCount", likeCount.getText());
                    intent.putExtra("commentCount", commentCount.getText());
                    intent.putExtra("createdAt", createdAt.getText());
                    intent.putExtra("attachments", attachments);
                    intent.putExtra("likedUsers", likedUsers);
                    activity.startActivity(intent);
                } else {
                    // go to profileCreate Activity
                    Log.d("No user set profile");
                    Intent intent = new Intent(activity, ForumProfileCreateActivity.class);
                    activity.startActivity(intent);
                }
            }
        };


        /**
         *   passed time calculator for 'createdAt' and 'post_is_new'
         *   'post_is_new' is shown up when post's createdAt is lower than 1 day
         * */
        String passedTimeCalc(String createdAt) {
            SimpleDateFormat sdf = null;
            Log.d(createdAt);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            } else { return "ago"; }

            try {
                Date createTime =  sdf.parse(createdAt);

                String current = sdf.format(new Date());
                Date currentTime = sdf.parse(current);

                long timeDiffer = currentTime.getTime() - createTime.getTime();
                Log.d("createdAt: " + createTime.getTime() + "\n" + "current: " + currentTime.getTime());
                Log.d("Time differ: " + timeDiffer);

                if (timeDiffer < 60000) {
                    post_is_new.setVisibility(View.VISIBLE);
                    return (timeDiffer / 1000) + " sec ago";
                } else if (timeDiffer < 3600000) {
                    post_is_new.setVisibility(View.VISIBLE);
                    return (timeDiffer / 60000) + " mins ago";
                } else if (timeDiffer < 86400000) {
                    post_is_new.setVisibility(View.VISIBLE);
                    return (timeDiffer / 3600000) + " hours ago";
                } else if (timeDiffer < 2592000000L) {
                    return (timeDiffer / 86400000) + " days ago";
                } else if (timeDiffer < 31104000000L) {
                    return (timeDiffer / 2592000000L) + " months ago";
                } else {
                    return (timeDiffer / 31104000000L) + " years ago";
                }

            }catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
