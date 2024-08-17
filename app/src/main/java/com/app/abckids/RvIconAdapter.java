package com.app.abckids;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RvIconAdapter extends RecyclerView.Adapter<RvIconAdapter.ViewHolder> {
    List<Integer> iconItem = new ArrayList<>();
    Context context;
    MediaPlayer playerr;

    public RvIconAdapter(List<Integer> iconItem, Context context, MediaPlayer playerr) {
        this.iconItem = iconItem;
        this.context = context;
        this.playerr = playerr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_icon_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.imageView.setImageResource(iconItem.get(position));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (position) {
                    case 0:

                        Intent intent = new Intent(context, AlphaActivity.class);
                        intent.putExtra("type", ResourcePool.ICON1);
                        context.startActivity(intent);
                        playerr.pause();

                        break;
                    case 1:

                        Intent intent1 = new Intent(context, NumActivity.class);
                        intent1.putExtra("type", ResourcePool.ICON2);
                        context.startActivity(intent1);
                        playerr.pause();

                        break;
                    case 2:

                        Intent intent2 = new Intent(context, FruitsActivity.class);
                        intent2.putExtra("type", ResourcePool.ICON3);
                        context.startActivity(intent2);
                        playerr.pause();

                        break;
                    case 3:

                        Intent intent3 = new Intent(context, AnimalActivity.class);
                        intent3.putExtra("type", ResourcePool.ICON4);
                        context.startActivity(intent3);
                        playerr.pause();

                        break;
                    case 4:

                        Intent intent4 = new Intent(context, DaysActivity.class);
                        intent4.putExtra("type", ResourcePool.ICON5);
                        context.startActivity(intent4);
                        playerr.pause();

                        break;
                    case 5:

                        Intent intent5 = new Intent(context, MonthsActivity.class);
                        intent5.putExtra("type", ResourcePool.ICON6);
                        context.startActivity(intent5);
                        playerr.pause();

                        break;
                    case 6:

                        Intent intent6 = new Intent(context, DrawingActivity.class);
                        intent6.putExtra("type", ResourcePool.DRAWING_ALPHABET);
                        context.startActivity(intent6);
                        playerr.pause();

                        break;
                    case 7:
                        Intent intent7 = new Intent(context, DrawingActivity.class);
                        intent7.putExtra("type", ResourcePool.NUMBER);
                        context.startActivity(intent7);
                        playerr.pause();
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return iconItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iconImage);
        }
    }
}
