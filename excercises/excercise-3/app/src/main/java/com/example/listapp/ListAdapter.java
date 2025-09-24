package com.example.listapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private String[] names;
    private String[] prices;
    private String[] descriptions;

    public ListAdapter(String[] names, String[] prices, String[] descriptions) {
        this.names = names;
        this.prices = prices;
        this.descriptions = descriptions;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_layout, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.getNameText().setText(names[position]);
        holder.getPriceText().setText(prices[position]);
        holder.getDescriptionText().setText(descriptions[position]);
    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        final private TextView name_text;
        final private TextView description_text;
        final private TextView price_text;
        final private FrameLayout view_layout;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            view_layout = itemView.findViewById(R.id.viewLayout);
            name_text = itemView.findViewById(R.id.itemNameText);
            description_text = itemView.findViewById(R.id.itemDescriptionText);
            price_text = itemView.findViewById(R.id.itemPriceText);

            view_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ImageActivity.class);
                    intent.putExtra("ITEM_NAME", name_text.getText().toString());
                    v.getContext().startActivity(intent);
                }
            });
        }

        public TextView getNameText() {
            return name_text;
        }

        public TextView getDescriptionText() {
            return description_text;
        }

        public TextView getPriceText() {
            return price_text;
        }
    }
}
