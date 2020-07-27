package io.methinks.android.methinks_android_forum_sdk.adapter;

import android.app.Activity;
import android.content.Intent;
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

import io.methinks.android.methinks_android_forum_sdk.activity.ForumSectionActivity;
import io.methinks.android.methinks_android_forum_sdk.R;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {
    private JSONArray sectionsData;
    public Activity activity;

    public SectionAdapter(JSONArray sectionData, Activity activity) {
        this.sectionsData = sectionData;
        this.activity = activity;
    }

    @Override
    public SectionAdapter.SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View sectionView = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_item_view, parent, false);

        return new SectionViewHolder(sectionView, activity);
    }

    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position) {
        try {
            holder.onBind(sectionsData.getJSONObject(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return sectionsData.length();
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public TextView descView;
        public TextView postCountView;
        public String sectionId;
        public Activity activity;
        public ImageView enter;
        public LinearLayout sectionPanel;

        public SectionViewHolder (View itemView, Activity activity) {
            super(itemView);

            this.activity = activity;
            this.titleView = itemView.findViewById(R.id.section_title);
            this.descView = itemView.findViewById(R.id.section_desc);
            this.postCountView = itemView.findViewById(R.id.section_post_count);
            this.enter = itemView.findViewById(R.id.section_enter_button);
            this.sectionPanel = itemView.findViewById(R.id.section_panel);
        }

        void onBind(JSONObject section) {
            try {
                sectionId = section.getString("objectId");
                titleView.setText(section.getString("sectionName"));
                descView.setText(section.getString("sectionDesc"));

                if (section.has("postCount")) {
                    if (section.getInt("postCount") > 99)
                        postCountView.setText("99+");
                    else
                        postCountView.setText(section.getString("postCount"));
                } else {
                    postCountView.setText("0");
                }

                sectionPanel.setOnClickListener(sectionClickListener);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        View.OnClickListener sectionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ForumSectionActivity.class);
                intent.putExtra("sectionId", sectionId);
                activity.startActivity(intent);
            }
        };

    }
}
