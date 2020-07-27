package io.methinks.android.methinks_android_forum_sdk.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Base64;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import io.methinks.android.methinks_android_forum_sdk.HttpManager;
import io.methinks.android.methinks_android_forum_sdk.Log;
import io.methinks.android.methinks_android_forum_sdk.R;
import io.methinks.android.methinks_android_forum_sdk.activity.ForumCommentActivity;
import io.methinks.android.methinks_android_forum_sdk.activity.ForumPostCreateActivity;
import io.methinks.android.methinks_android_forum_sdk.activity.ForumPostUpdateActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ImagesUrlAdapter extends RecyclerView.Adapter<ImagesUrlAdapter.ImagesUrlViewHolder>{
    private ArrayList<String> imagesUrl;
    private ArrayList<String> attachIds;
    public static Activity activity;

    public String postId;

    public Display display;
    public Point size;
    public int deviceWidth; //###
    public int deviceHeight;
    public String base;


    public ImagesUrlAdapter(ArrayList<String> imagesUrl, Activity activity) {
        this.imagesUrl = imagesUrl;
        this.activity = activity;
        display = activity.getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        deviceWidth = size.x;
        deviceHeight = size.y;
    }

    public ImagesUrlAdapter(ArrayList<String> imagesUrl, ArrayList<String> attachIds, Activity activity, String postId) {
        this.imagesUrl = imagesUrl;
        this.attachIds = attachIds;
        this.activity = activity;
        this.postId = postId;
        display = activity.getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        deviceWidth = size.x;
        deviceHeight = size.y;
    }

    @Override
    public ImagesUrlAdapter.ImagesUrlViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View imageUrlView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_images_view, parent, false);
        return new ImagesUrlViewHolder(imageUrlView, activity);
    }

    @Override
    public void onBindViewHolder(ImagesUrlViewHolder holder,int position) {
        if (attachIds == null)
            holder.onBind(imagesUrl.get(position));
        else {
            if (attachIds.size() > 0)
            holder.onBind(imagesUrl.get(position), attachIds.get(position));
        }
    }

    @Override
    public int getItemCount() { return imagesUrl.size(); }

    public class ImagesUrlViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout imageElement;
        public ImageView imageRemove;
        public Activity activity;
        public Bitmap imageBitmap;
        public ImageView postImage;
        public String imageUrl;
        public String attachId;


        public ImagesUrlViewHolder (View itemView, Activity activity) {
            super(itemView);

            this.activity = activity;
            this.imageElement = itemView.findViewById(R.id.image_element);
            this.imageRemove = itemView.findViewById(R.id.image_remove);
            this.postImage = itemView.findViewById(R.id.post_image);

            if (attachIds == null) {
                imageRemove.setVisibility(View.INVISIBLE);
            } else { // update
                imageRemove.setOnClickListener(removeOnClickListener);
            }
        }

        void onBind(String imageUrl) {
            this.imageUrl = imageUrl;

            new Thread() {
                public void run() {
                    ImageConverting(imageUrl);
                }
            }.start();
        }

        void onBind(String imageUrl, String attachId) {
            this.imageUrl = imageUrl;
            this.attachId = attachId;
            new Thread() {
                public void run() {
                    ImageConverting(imageUrl);
                }
            }.start();
        }

        public void ImageConverting(String imageUrlString) {
            URL imageUrl = null;
            String encoded = null;
            //Log.d("URL: " + imageUrlString + "/ " + deviceWidth);
            try {
                imageUrl = new URL(imageUrlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection conn = null;

            try {
                conn = (HttpURLConnection) imageUrl.openConnection();
                conn.setDoInput(true);
                conn.setConnectTimeout(8000);
                conn.connect();
                InputStream is = conn.getInputStream();
                imageBitmap = BitmapFactory.decodeStream(is);

                    //saveExistedImage();

                conn.disconnect();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postImage.setImageBitmap(imageBitmap);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void saveExistedImage() {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            String encoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            //ForumPostUpdateActivity.selectedImage.add(null);

            JSONArray size = new JSONArray();
            size.put(imageBitmap.getWidth());
            size.put(imageBitmap.getHeight());

            JSONObject imageData = new JSONObject();
            try {
                imageData.put("size", size);
                imageData.put("sequence", ForumPostUpdateActivity.attachedImages.length() + 1);
                imageData.put("image", encoded);
                ForumPostUpdateActivity.attachedImages.put(imageData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        View.OnClickListener removeOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // anyway attachments from ImagesUrlAdapter is posed on front of attachedImages array.
                int rmIndex = ForumPostUpdateActivity.imagesUrl.indexOf(imageUrl);
                //ForumPostUpdateActivity.attachedImages.remove(rmIndex);
                ForumPostUpdateActivity.imagesUrl.remove(rmIndex);
                //Log.d("delete attachment index: " + rmIndex + "/" + ForumPostUpdateActivity.attachedImages);

                ForumPostUpdateActivity.imageAdapter.notifyItemRemoved(rmIndex);

                new HttpManager().deleteForumPostAttachment(postId, attachId, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) { e.printStackTrace(); }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String res = response.body().string();
                        Log.d("Delete Attachments Result: " + res);
                        try {
                            JSONObject result = new JSONObject(res);
                            if (!result.has("result")) {
                                Log.e("No result on delete attachments !!!");
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
    }
}

