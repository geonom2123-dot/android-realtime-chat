package com.example.fire_chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private List<ChatModel> chatList;

    public ChatAdapter(Context context, List<ChatModel> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatModel chat = chatList.get(position);

        holder.txtName.setText(chat.username);
        holder.txtLastMessage.setText(chat.lastMessage);

        // ΕΜΦΑΝΙΣΗ ΩΡΑΣ ΚΑΙ ΗΜΕΡΟΜΗΝΙΑΣ
        if (chat.lastTimestamp > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm (dd/MM/yyyy)", Locale.getDefault());
            String fullTimeStr = sdf.format(new Date(chat.lastTimestamp));
            holder.txtTime.setText(fullTimeStr);
        }

        //ΔΙΑΧΕΙΡΙΣΗ ΦΩΤΟΓΡΑΦΙΑΣ
        if (chat.profilePic != null && !chat.profilePic.isEmpty() && !chat.profilePic.equals("default")) {
            try {
                byte[] decodedString = Base64.decode(chat.profilePic, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Glide.with(context).load(decodedByte).circleCrop().into(holder.imgAvatar);
            } catch (Exception e) {
                holder.imgAvatar.setImageResource(R.drawable.baseline_account_circle_24);
            }
        } else {
            holder.imgAvatar.setImageResource(R.drawable.baseline_account_circle_24);
        }

        //ΚΛΙΚ ΓΙΑ ΕΙΣΟΔΟ ΣΤΟ CHAT
        holder.itemView.setOnClickListener(v -> {
            if (chat.receiverId != null) {
                Intent intent = new Intent(context, MainActivity7.class);
                intent.putExtra("uid", chat.receiverId);
                intent.putExtra("username", chat.username);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtLastMessage, txtTime;
        ImageView imgAvatar;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            txtTime = itemView.findViewById(R.id.txtTime);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}