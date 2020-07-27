package io.methinks.android.methinks_android_forum_sdk.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.util.ArrayList;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.methinks.android.methinks_android_forum_sdk.R;
import io.methinks.android.methinks_android_forum_sdk.activity.ForumCommentActivity;
import io.methinks.android.methinks_android_forum_sdk.activity.ForumPostCreateActivity;
import io.methinks.android.methinks_android_forum_sdk.activity.ForumPostUpdateActivity;


public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>{
    private ArrayList<FileDescriptor> imagesFD;
    public Activity activity;
    private String purpose;

    public ImagesAdapter(ArrayList<FileDescriptor> imagesFD, Activity activity, String purpose) {
        this.imagesFD = imagesFD;
        this.activity = activity;
        this.purpose = purpose;
    }

    @Override
    public ImagesAdapter.ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View imageView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_images_view, parent, false);
        return new ImagesViewHolder(imageView, activity, purpose);
    }

    @Override
    public void onBindViewHolder(ImagesViewHolder holder, int position) {
        holder.onBind(imagesFD.get(position));
    }

    @Override
    public int getItemCount() { return imagesFD.size(); }

    public static class ImagesViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout imageElement;
        public ImageView postImage;
        public ImageView imageRemove;
        public Activity activity;
        public FileDescriptor curImage;
        public Bitmap imageBitmap;
        public Display display;
        public Point size;
        public int deviceWidth;
        public int deviceHeight;
        public String purpose;

        public ImagesViewHolder (View itemView, Activity activity, String purpose) {
            super(itemView);

            this.activity = activity;
            this.imageElement = itemView.findViewById(R.id.image_element);
            this.postImage = itemView.findViewById(R.id.post_image);
            this.imageRemove = itemView.findViewById(R.id.image_remove);
            this.purpose = purpose;

            display = activity.getWindowManager().getDefaultDisplay();
            size = new Point();
            display.getSize(size);
            deviceWidth = size.x;
            deviceHeight = size.y;
        }

        void onBind(FileDescriptor imageFD) {
            new ImageConvertinThread(imageFD).run();
        }

        public Bitmap rotateBitmap(Bitmap source, float angle) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        }

        View.OnClickListener removeOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (purpose.equals("post")) {
                    int rmIndex = ForumPostCreateActivity.selectedImage.indexOf(curImage);
                    ForumPostCreateActivity.selectedImage.remove(rmIndex);
                    ForumPostCreateActivity.attachedImages.remove(rmIndex);
                    ForumPostCreateActivity.imageAdapter.notifyItemRemoved(rmIndex);
                } else if(purpose.equals("postUpdate")) {
                    int rmIndex = ForumPostUpdateActivity.selectedImage.indexOf(curImage);
                    ForumPostUpdateActivity.selectedImage.remove(rmIndex);
                    ForumPostUpdateActivity.attachedImages.remove(rmIndex + ForumPostUpdateActivity.imagesUrl.size());
                    ForumPostUpdateActivity.newImageAdapter.notifyItemRemoved(rmIndex);
                } else if(purpose.equals("comment")) {
                    int rmIndex = ForumCommentActivity.selectedImage.indexOf(curImage);
                    ForumCommentActivity.selectedImage.remove(rmIndex);
                    ForumCommentActivity.attachedImages.remove(rmIndex);
                    ForumCommentActivity.imageAdapter.notifyItemRemoved(rmIndex);
                }
            }
        };

        public void saveBitmapAsBase64(Bitmap imageBitmap, int width, int height) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            String imageBase64String = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            JSONArray size = new JSONArray();
            size.put(width);
            size.put(height);

            JSONObject imageData = new JSONObject();
            try {
                imageData.put("image", imageBase64String);
                imageData.put("size", size);
                if (purpose.equals("post")) {
                    imageData.put("sequence", ForumPostCreateActivity.attachedImages.length() + 1);
                    ForumPostCreateActivity.attachedImages.put(imageData);
                } else if (purpose.equals("postUpdate")) {
                    imageData.put("sequence", ForumPostUpdateActivity.attachedImages.length() + 1);
                    ForumPostUpdateActivity.attachedImages.put(imageData);
                } else if (purpose.equals("comment")) {
                    imageData.put("sequence", ForumCommentActivity.attachedImages.length() + 1);
                    ForumCommentActivity.attachedImages.put(imageData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        class ImageConvertinThread extends Thread {
            FileDescriptor imageFD;
            ImageConvertinThread(FileDescriptor imageFD) {
                this.imageFD = imageFD;
            }

            public void run() {
                curImage = imageFD;
                imageBitmap = BitmapFactory.decodeFileDescriptor(imageFD);

                if (deviceWidth < imageBitmap.getWidth()) {
                    imageBitmap = rotateBitmap(imageBitmap, 90);
                }

                postImage.setImageBitmap(imageBitmap);
                saveBitmapAsBase64(imageBitmap, imageBitmap.getWidth(), imageBitmap.getHeight());
                imageRemove.setOnClickListener(removeOnClickListener);
            }
        }
    }
}
