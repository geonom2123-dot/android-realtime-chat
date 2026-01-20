package com.example.fire_chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    private Context context;
    private List<ModelOfMesseging> messageList;
    private String myUid;

    public MessageAdapter(Context context, List<ModelOfMesseging> messageList) {
        this.context = context;
        this.messageList = messageList;
        this.myUid = FirebaseAuth.getInstance().getUid();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).senderId.equals(myUid)) {
            return TYPE_SENT;
        } else {
            return TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.message_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.message_received, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelOfMesseging message = messageList.get(position);

        // ΤΡΟΠΟΠΟΙΗΣΗ: Προσθήκη ημερομηνίας σε παρένθεση (π.χ. 14:30 (24/12/2025))
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm (dd/MM/yyyy)", Locale.getDefault());
        String timeStr = sdf.format(new Date(message.timestamp));

        if (holder instanceof SentViewHolder) {
            SentViewHolder sentHolder = (SentViewHolder) holder;
            sentHolder.txtMsg.setText(message.message);
            sentHolder.txtTime.setText(timeStr);
        } else {
            ReceivedViewHolder receivedHolder = (ReceivedViewHolder) holder;
            receivedHolder.txtMsg.setText(message.message);
            receivedHolder.txtTime.setText(timeStr);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class SentViewHolder extends RecyclerView.ViewHolder {
        TextView txtMsg, txtTime;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMsg = itemView.findViewById(R.id.text_message);
            txtTime = itemView.findViewById(R.id.txtMessageTime);
        }
    }

    class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView txtMsg, txtTime;
        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMsg = itemView.findViewById(R.id.text_message);
            txtTime = itemView.findViewById(R.id.txtMessageTime);
        }
    }
}