package io.methinks.android.apptest;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class HoverPopup {

    private Context context;
    protected LinearLayout hoverPopup;
    protected TextView bugReport, suggestion, cancel;
    protected RecyclerView eventRecyclerView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager layoutManager;
    public boolean isOpened;

    public HoverPopup(Context context) {
        this.context = context;
        init();
    }

    private void init(){
        hoverPopup = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.popup_hover, null);
        WindowManager.LayoutParams popupParam = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        hoverPopup.setLayoutParams(popupParam);

        bugReport = hoverPopup.findViewById(R.id.popup_hover_bug_report);
        suggestion = hoverPopup.findViewById(R.id.popup_hover_suggestion);
        cancel = hoverPopup.findViewById(R.id.popup_hover_cancel);
        eventRecyclerView = hoverPopup.findViewById(R.id.popup_hover_shootevent);

        eventRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(Global.applicationTracker.getTopActivity());
        eventRecyclerView.setLayoutManager(layoutManager);
    }

    public void setVisible(){
        isOpened = true;
        hoverPopup.setVisibility(View.VISIBLE);
    }

    public void setVisibleEvent() {
        eventRecyclerView.setVisibility(View.VISIBLE);
    }

    public void setInvisible(){
        hoverPopup.setVisibility(View.GONE);
    }

}
