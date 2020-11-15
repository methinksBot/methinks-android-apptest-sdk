package io.methinks.android.methinks_android_forum_sdk.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.methinks.android.methinks_android_forum_sdk.Global;
import io.methinks.android.methinks_android_forum_sdk.R;
import io.methinks.android.methinks_android_forum_sdk.viewHolder.CommentViewHolder;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    private JSONArray commentObjs;
    public static Activity activity;
    public String base;

    public CommentAdapter(JSONArray commentObjs, Activity activity, String base) {
        this.commentObjs = commentObjs;
        this.activity = activity;
        this.base = base;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View commentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_view, parent, false);
        return new CommentViewHolder(commentView, activity, base);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder,int position) {
        try {
            JSONObject comment;
            if(Global.type == Global.Type.Patcher) {
                comment = commentObjs.getJSONObject(position);
            } else {
                comment = commentObjs.getJSONObject(position).getJSONObject("comment");
            }
            holder.onBind(comment);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() { return commentObjs.length(); }


}


