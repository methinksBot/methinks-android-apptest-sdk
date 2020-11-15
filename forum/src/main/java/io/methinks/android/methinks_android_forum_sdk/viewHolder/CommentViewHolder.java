package io.methinks.android.methinks_android_forum_sdk.viewHolder;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import io.methinks.android.methinks_android_forum_sdk.Global;
import io.methinks.android.methinks_android_forum_sdk.HttpManager;
import io.methinks.android.methinks_android_forum_sdk.Log;
import io.methinks.android.methinks_android_forum_sdk.R;
import io.methinks.android.methinks_android_forum_sdk.activity.ForumCommentActivity;
import io.methinks.android.methinks_android_forum_sdk.activity.ForumPostDetailActivity;
import io.methinks.android.methinks_android_forum_sdk.adapter.ImagesUrlAdapter;
import io.methinks.android.methinks_android_forum_sdk.util.HtmlUtil;
import io.methinks.android.methinks_android_forum_sdk.util.OnLinkClickListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static io.methinks.android.methinks_android_forum_sdk.Global.getImageId;

public class CommentViewHolder extends RecyclerView.ViewHolder implements OnLinkClickListener {
    private Activity activity;

    public String sectionId;
    public String postId;
    public String commentId;
    public String base;

    public ImageView commetProfile;
    public TextView commentCreator;
    public TextView commentCreatedAt;
    public TextView commentDesc;
    public ImageView commentLike;
    public TextView commentLikeCount;
    public ImageView commentReply;
    public TextView commentReplyCount;
    public ImageView commentDelete;

    public LinearLayout toCommentPointer;
    public LinearLayout toCommentPanel;
    public TextView toCommentCreator;
    public TextView toCommentText;

    public String likedUsers;

    public JSONArray attachments;

    private String imageAssets; // result of http call
    public ArrayList<String> imagesUrl;

    public RecyclerView imagePanel;
    public RecyclerView.Adapter imageAdapter;
    private RecyclerView.LayoutManager imageLayoutManager;



    public CommentViewHolder (View itemView, Activity activity, String base) {
        super(itemView);

        this.activity = activity;
        this.commetProfile = itemView.findViewById(R.id.comment_profile);
        this.commentCreator = itemView.findViewById(R.id.comment_creator);
        this.commentCreatedAt = itemView.findViewById(R.id.comment_created_time);
        this.commentDesc = itemView.findViewById(R.id.comment_desc);
        this.commentLike = itemView.findViewById(R.id.comment_like);
        this.commentLikeCount = itemView.findViewById(R.id.comment_like_count);
        this.commentReply = itemView.findViewById(R.id.comment_reply);
        this.commentReplyCount = itemView.findViewById(R.id.comment_reply_count);
        this.commentDelete = itemView.findViewById(R.id.comment_delete);
        this.imagePanel = itemView.findViewById(R.id.images_panel);
        this.toCommentPointer = itemView.findViewById(R.id.tocomment_pointer);
        this.toCommentPanel = itemView.findViewById(R.id.tocomment_panel);
        //this.toCommentCreator = itemView.findViewById(R.id.tocomment_creator);
        this.toCommentText = itemView.findViewById(R.id.tocomment_text);
        this.base = base;

        imageLayoutManager = new LinearLayoutManager(activity);

        imagePanel.setLayoutManager(imageLayoutManager);

        commentLike.setOnClickListener(likeClickListener);
        commentReply.setOnClickListener(commentClickListener);
        commentDelete.setOnClickListener(deleteClickListener);
    }

    @SuppressLint("ResourceAsColor")
    public void onBind(JSONObject commentObj) {
        try {
            commentId = commentObj.getString("objectId");
            JSONObject participant = commentObj.has("participant") ? commentObj.getJSONObject("participant") : null;
            JSONObject post = commentObj.getJSONObject("post");
            JSONObject section = commentObj.getJSONObject("section");

            postId = post.getString("objectId");
            sectionId = section.getString("objectId");

            if (commentObj.has("attachments")) {
                Log.d("call Attachments: " + commentObj.getJSONArray("attachments").toString());
                attachments = commentObj.getJSONArray("attachments");
            }

            // if current comment is belonged to another comment
            if (commentObj.has("toComment")) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) toCommentPointer.getLayoutParams();
                params.width = ActionBar.LayoutParams.WRAP_CONTENT;
                toCommentPointer.setLayoutParams(params);
                params = (LinearLayout.LayoutParams) toCommentPanel.getLayoutParams();
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                toCommentPanel.setLayoutParams(params);

                JSONObject toComment = commentObj.getJSONObject("toComment");
                Log.e("toComment: " + toComment);
                toCommentText.setText(toComment.getString("commentText"));
            }


            if (participant != null){
                commentCreator.setText(participant.has("forumNickName") ?
                        participant.getString("forumNickName") : "participant");

                commetProfile.setImageResource(participant.has("forumEmoji") ?
                        getImageId(activity, participant.getString("forumEmoji")) :
                        R.drawable.img_logoprofile);

                feedbackHandler(participant.getString("objectId"));
            } else {
                commentCreator.setText("Admin");

                commetProfile.setImageResource(R.drawable.img_logoprofile);

                feedbackHandler("admin");
            }


            commentCreatedAt.setText(passedTimeCalc(commentObj.getString("createdAt")));


            if (commentObj.has("likedUsers")) {
                if (commentObj.getJSONArray("likedUsers").length() > 0) {
                    commentLikeCount.setVisibility(View.VISIBLE);
                    commentLikeCount.setText(String.valueOf(commentObj.getJSONArray("likedUsers").length()));
                }
            } else { commentLikeCount.setText("0"); }

            if (commentObj.has("commentCount")) {
                if (commentObj.getInt("commentCount") > 0) {
                    commentReplyCount.setVisibility(View.VISIBLE);
                    commentReplyCount.setText(commentObj.getString("commentCount"));
                    commentReply.setActivated(true);
                }
            } else { commentReplyCount.setText("0"); }


            this.likedUsers = commentObj.has("likedUsers") ?
                    commentObj.getString("likedUsers") : null;

            if (commentObj.has("isDeleted") && commentObj.getBoolean("isDeleted")) {
                commentDesc.setText(R.string.forum_deleted_comment);
                commentDesc.setTextColor(R.color.deleted_comment_color);
            } else {
                commentDesc.setText(HtmlUtil.fromHtml(commentObj.getString("commentText"), this));
                commentDesc.setMovementMethod(LinkMovementMethod.getInstance());
                commentDesc.setClickable(true);
                getAllPostAttachments();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getAllPostAttachments() {
        new Thread(new Runnable() {
            @Override
            public void run() { // TODO Auto-generated method stub
                if (attachments != null) {
                    imagesUrl = new ArrayList<>();

                    //Log.e(attachments.toString());

                    //for (String ele : attachments) { attachmentArray.put(ele); }

                    imageAssets = new HttpManager().getPostAttachments(attachments);
                    Log.d("Comment Images: " + imageAssets);
                    try {
                        JSONArray assetsArray = new JSONObject(imageAssets).getJSONArray("result");

                        // getting base64 string from url to keep images
                        for (int i = 0; i < assetsArray.length(); i++) {
                            JSONObject objectAsset = assetsArray.getJSONObject(i).getJSONObject("image");
                            imagesUrl.add(objectAsset.getString("url"));
                        }
                        setAttachments();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void setAttachments() {
        //Log.d(imagesUrl.toString());
        imageAdapter = new ImagesUrlAdapter(imagesUrl, activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //setAdapter
                imagePanel.setAdapter(imageAdapter);
            }
        });
    }

    public void feedbackHandler(String participantId) {
        //feedbackCounter();
        likeDetector();
        checkOwnedComment(participantId);
    }

    public void counterVisibility() {
        if (Integer.parseInt(commentLikeCount.getText().toString()) > 0) {
            commentLikeCount.setVisibility(View.VISIBLE);
        } else if (Integer.parseInt(commentLikeCount.getText().toString()) == 0)
            commentLikeCount.setVisibility(View.INVISIBLE);
        if (Integer.parseInt(commentReplyCount.getText().toString()) > 0) {
            commentReplyCount.setVisibility(View.VISIBLE);
        } else if (Integer.parseInt(commentReplyCount.getText().toString()) == 0)
            commentReplyCount.setVisibility(View.INVISIBLE);

    }

    public void likeDetector() {
        if (likedUsers != null) {
            //Log.e("likedUsers: " + likedUsers + "/" + Global.userObjectId);
            ArrayList<String> likedUserArray = new ArrayList<>();
            String likedUsersSource = likedUsers.replaceAll("[\\[\\](){}]", "");
            for (String user : likedUsersSource.split(",")) {
                likedUserArray.add(user.replaceAll("\"", ""));
            }
            Log.d(likedUserArray.toString());
            Log.d(likedUserArray.contains(Global.userObjectId) + "/" + commentLike.isActivated());
            if (likedUserArray.contains(Global.userObjectId) && !commentLike.isActivated()) {
                commentLike.setActivated(true);
            }
        }
    }

    public void restartActivity() {
        Intent intent = activity.getIntent();
        activity.finish();
        activity.startActivity(intent);
    }

    View.OnClickListener deleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new HttpManager().deleteForumComment(commentId, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {e.printStackTrace();}

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();
                    try {
                        Log.d("delete comment: " + res);
                        JSONObject result = new JSONObject(res);
                        if (!result.has("result")) {
                            Log.e("No result!!");
                            return;
                        }

                        restartActivity();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    View.OnClickListener commentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int commentCount;
            if (commentReplyCount.getText() != null) {
                commentCount = Integer.parseInt(commentReplyCount.getText().toString());
            } else {
                commentCount = 0;
            }
            Intent intent = new Intent(activity, ForumCommentActivity.class);
            intent.putExtra("postId", postId);
            intent.putExtra("toCommentId", commentId);
            intent.putExtra("sectionId", sectionId);
            intent.putExtra("commentCount", commentCount);
            intent.putExtra("base", "comment");
            activity.startActivity(intent);
        }
    };

    View.OnClickListener likeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean like = !commentLike.isActivated();
            new HttpManager().likeForumComment(commentId, like, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) { }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();
                    try {
                        JSONObject result = new JSONObject(res);
                        JSONObject comment = result.getJSONObject("result");
                        Log.d(comment.toString());

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //Log.e(comment.getJSONArray("likedUsers").toString());
                                    int likeCount =
                                            comment.has("likedUsers")
                                                    && comment.getJSONArray("likedUsers").length() > 0
                                            ? comment.getJSONArray("likedUsers").length()
                                            : 0;

                                    commentLikeCount.setText(String.valueOf(likeCount));
                                    counterVisibility();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    commentLike.setActivated(!commentLike.isActivated());
                }
            });
        }
    };

    public void checkOwnedComment(String participantId) {
        /** If current user is writer */
        //Log.d("pass:" + Global.forumNickName + "/" + commentCreator.getText());
        if (participantId.equals("admin")) {

        } else {
            if (Global.participantId.equals(participantId)) {
                if (base.equals("post"))
                    ForumPostDetailActivity.commentIcon.setActivated(true);
            } else {
                commentDelete.setVisibility(View.INVISIBLE);
            }
        }

    }

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
                return (timeDiffer / 1000) + " sec ago";
            } else if (timeDiffer < 3600000) {
                return (timeDiffer / 60000) + " mins ago";
            } else if (timeDiffer < 86400000) {
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

    @Override
    public void onLinkClick(@NotNull String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }
}