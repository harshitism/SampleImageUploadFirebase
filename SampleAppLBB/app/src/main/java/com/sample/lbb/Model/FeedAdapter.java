package com.sample.lbb.Model;

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
import java.util.List;
import java.util.TreeMap;

/**
 * Created by harshitgupta on 08/12/17.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {


    private final Context context;
    private final LayoutInflater inflater;

    public FeedAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    public void setData(List<PhotosModel> data) {
        this.notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View myView = inflater.inflate(R.layout.grid_item, parent, false);
        return new MyViewHolder(myView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        /*final PhotosModel model = data.get(position);
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
        });*/


    }

    @Override
    public int getItemCount() {
        return 0;
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


    }


}
