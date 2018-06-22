package com.idx.launcher.user.personal_center.order.recyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idx.launcher.LauncherApplication;
import com.idx.launcher.R;
import com.idx.launcher.user.personal_center.order.orderbean.Items;

import java.util.List;

/**
 * Created by ryan on 18-5-14.
 * Email: Ryan_chan01212@yeah.net
 */

public class DetailsRecyclerView extends RecyclerView.Adapter<DetailsRecyclerView.MyHolder> {

    private List<Items> items;
    private MyHolder holder;


    public DetailsRecyclerView(List<Items> items) {
        this.items = items;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(LauncherApplication.getInstance().getBaseContext()).inflate(R.layout.details_item,parent,false);
        holder = new MyHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.name.setText(String.valueOf(position + 1) + "、" + items.get(position).getName());

        holder.count.setText("×"+ items.get(position).getCount());

        double price = items.get(position).getPrice() / 100;
        holder.money.setText("¥" + price);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView count;
        TextView money;

        public MyHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.detail_name);
            count = itemView.findViewById(R.id.count);
            money = itemView.findViewById(R.id.money);
        }
    }

}
