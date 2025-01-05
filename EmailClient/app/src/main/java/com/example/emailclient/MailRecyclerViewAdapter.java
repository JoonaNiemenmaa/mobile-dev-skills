package com.example.emailclient;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MailRecyclerViewAdapter extends RecyclerView.Adapter<MailRecyclerViewAdapter.MailViewHolder> {

    private ArrayList<Email> emails;
    public MailRecyclerViewAdapter(ArrayList<Email> emails) {
        this.emails = emails;
    }

    public void updateData(ArrayList<Email> emails) {
        this.emails = emails;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mail_recyclerview_item, parent, false);
        return new MailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MailViewHolder holder, int position) {
        int pos = holder.getAdapterPosition();
        holder.getSenderText().setText(emails.get(pos).getSender());
        holder.getSubjectText().setText(emails.get(pos).getSubject());
        holder.getViewLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ViewMailActivity.class);
                intent.putExtra("com.example.emailclient.MAIL", emails.get(pos));
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    public static class MailViewHolder extends RecyclerView.ViewHolder {
        final private TextView sender_text, subject_text;
        final private FrameLayout view_layout;
        public MailViewHolder(@NonNull View itemView) {
            super(itemView);

            view_layout = itemView.findViewById(R.id.viewLayout);
            sender_text = itemView.findViewById(R.id.senderText);
            subject_text = itemView.findViewById(R.id.subjectText);

        }

        public TextView getSenderText() {
            return sender_text;
        }

        public TextView getSubjectText() {
            return subject_text;
        }

        public FrameLayout getViewLayout() {
            return view_layout;
        }
    }
}
