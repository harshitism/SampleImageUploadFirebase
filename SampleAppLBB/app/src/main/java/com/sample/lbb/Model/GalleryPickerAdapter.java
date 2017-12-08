package com.sample.lbb.Model;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sample.lbb.R;
import com.sample.lbb.UploadActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by harshitgupta on 08/12/17.
 */

public class GalleryPickerAdapter extends RecyclerView.Adapter<GalleryPickerAdapter.MyViewHolder> {


    //define source of MediaStore.Images.Media, internal or external storage
    public static final Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final String[] projections = {MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.DISPLAY_NAME};
    private Context context;
    private LayoutInflater inflater;
    public static String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

    public static List<PhotosModel> data = new ArrayList<>();
    public TreeMap<Uri, Uri> selectedMap;
    private boolean sinlgleSelect;
    private int selectedCount = 0;


    public GalleryPickerAdapter(Context context) {
        this.context = context;
        selectedMap = new TreeMap<Uri, Uri>();
        inflater = LayoutInflater.from(context);
    }



    public void setData(List<PhotosModel> data) {
        GalleryPickerAdapter.data = data;
        this.notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View myView = inflater.inflate(R.layout.grid_item, parent, false);
        return new MyViewHolder(myView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final PhotosModel model = data.get(position);
        Glide.with(context).load(model.getImagePath()).into(holder.iv_grid);

        if (selectedMap.containsKey(Uri.parse(model.getImagePath()))) {
            holder.selectedLayout.setVisibility(View.VISIBLE);
        } else holder.selectedLayout.setVisibility(View.GONE);


        holder.iv_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedMap.containsKey(Uri.parse(model.getImagePath()))) {
                    holder.selectedLayout.setVisibility(View.GONE);
                    selectedMap.remove(Uri.parse(model.getImagePath()));
                    selectedCount--;
                    ((UploadActivity) context).showUpload(selectedCount);
                } else {
                    holder.selectedLayout.setVisibility(View.VISIBLE);
                    selectedMap.put(Uri.parse(model.getImagePath()), Uri.parse(model.getImagePath()));
                    selectedCount++;
                    ((UploadActivity) context).showUpload(selectedCount);

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        View row;
        ImageView iv_grid;
        TextView tv_grid;
        RelativeLayout selectedLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            row = itemView;
            iv_grid = (ImageView) row.findViewById(R.id.gv_image);
            selectedLayout = (RelativeLayout) row.findViewById(R.id.selected_layout);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PhotosData.dir) {
                        openBucketFragment();
                    } else {
                        openImageFragment();
                    }
                }
            });*/
        }

        // Do something when clicked on a bucket
        /*private void openBucketFragment() {

            Log.d("MyTag", "openBucketFragment()");

            Toast.makeText(context, "Clicked on the bucket at :" + getLayoutPosition(), Toast.LENGTH_LONG).show();
            Log.d("MyTag", "Bucket clicked");

            ImagesGridFragment fg = ImagesGridFragment.newInstance(getLayoutPosition());

            FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.view_holder, fg);
            //ft.add(R.id.view_holder, fg);
            ft.addToBackStack(null);
            ft.commit();
        }*/

        // Do something when clicked on an image in bucket
        /*private void openImageFragment() {

            ArrayList<String> paths = new ArrayList<>();
            for (int i = 0; i < data.size() - 1; i++) {
                paths.add(data.get(i).getImagePath());
                Log.d("Paths", paths.get(i));
            }

            Intent intent = new Intent(context, ImageViewActivity.class);
            intent.putStringArrayListExtra("paths", paths);
            context.startActivity(intent);
            *//*FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
            ImageViewActivity im = ImageViewActivity.newInstance(context, path);
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.view_holder, im, "ImageViewActivity");
            ft.addToBackStack("imageFragment");
            ft.commit();*//*
        }*/
    }


}
