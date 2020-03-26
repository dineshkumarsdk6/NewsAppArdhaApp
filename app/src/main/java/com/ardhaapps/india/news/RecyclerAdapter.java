package com.ardhaapps.india.news;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<NewsPaper> arrayListNewspaper = new ArrayList<>();
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView title;
        ImageView imageView;
        View layout;
        CardView cardView;

        ViewHolder(View v) {
            super(v);
            layout = v;
            imageView = v.findViewById(R.id.imageview);
            title = v.findViewById(R.id.sub_header);
            cardView = v.findViewById(R.id.card);
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    RecyclerAdapter(Context mContext, ArrayList<NewsPaper> mArrayListNewspaper) {
        context = mContext;
        arrayListNewspaper = mArrayListNewspaper;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.newslayout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        NewsPaper newsPaper = arrayListNewspaper.get(position);
        holder.title.setText(newsPaper.getTitle());
        GlideApp.with(context)
                .load(newsPaper.getImageUrl())
                .placeholder(R.drawable.loading)
                .transition(DrawableTransitionOptions.withCrossFade()) //Optional
                .skipMemoryCache(false)  //No memory cache
                .diskCacheStrategy(DiskCacheStrategy.ALL)   //No disk cache
                // .onlyRetrieveFromCache(true)
                .into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "" + newsPaper.getTitle(), Toast.LENGTH_SHORT).show();

                Utililty.custom_tabs(context,newsPaper.getWebsiteLink());

                   /* Intent i = new Intent(Activity_maram_person.this, Activity_Maram_Person_Content.class);
                    i.putExtra("description", m.getNursery_descrption());
                    i.putExtra("title", m.getNursery_name());
                    i.putExtra("imgurl", m.getNursery_image());
                    i.putExtra("toolbar_name", "மரம் ஆர்வலர்கள்");
                    i.putExtra("id", m.getNursery_id());
                    i.putExtra("type", "tadam");
                    startActivity(i);*/

            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return arrayListNewspaper.size();
    }

}

