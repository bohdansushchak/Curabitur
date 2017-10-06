package sushchak.bohdan.curabitur.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import sushchak.bohdan.curabitur.R;
import sushchak.bohdan.curabitur.data.StaticVar;
import sushchak.bohdan.curabitur.model.Contact;
import sushchak.bohdan.curabitur.model.Message;
import sushchak.bohdan.curabitur.model.Thread;

public class ChatActivity extends AppCompatActivity {

    public final static int VIEW_TYPE_YOU_MESSAGE = 0;
    public final static int VIEW_TYPE_FROM_MESSAGE = 1;

    //private String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private final String TAG = "ChatActivity";
    private RecyclerView rvChat;
    private String idChat;

    private Contact contact;

    private ImageButton btnSend;
    private EditText etMessage;

    private ArrayList<Message> messages;

    private ListMessageAdapter adapter;
    private LinearLayoutManager linearManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarChatActivity);
        setSupportActionBar(toolbar);

        contact = new Contact();
        Intent intent = getIntent();
        idChat = intent.getStringExtra(StaticVar.STR_EXTRA_CHAT_ID);
        if(idChat == null){
            contact.contactId = intent.getStringExtra(StaticVar.STR_EXTRA_CONTACT_ID);
            contact.name = intent.getStringExtra(StaticVar.STR_EXTRA_CONTACT_NAME);
            createNewChat();
        }

        messages = new ArrayList<>();

        btnSend = (ImageButton) findViewById(R.id.btnSend);
        etMessage = (EditText) findViewById(R.id.etMessage);
        rvChat = (RecyclerView) findViewById(R.id.rvChat);


        adapter = new ListMessageAdapter(ChatActivity.this, messages);
        linearManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
        rvChat.setLayoutManager(linearManager);
        rvChat.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("threads/" + idChat + "/messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                    Message message = new Message();
                    message.idSender = (String) mapMessage.get("idSender");
                    message.text = (String) mapMessage.get("text");
                    message.timestamp = (Long) mapMessage.get("timestamp");
                    messages.add(message);
                    Log.d(TAG, message.toString());
                    adapter.notifyDataSetChanged();
                    linearManager.scrollToPosition(messages.size() - 1);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createNewChat(){
        Thread thread = new Thread();

        thread.thread_id = FirebaseDatabase.getInstance().getReference().child("users/" + StaticVar.currentUser.contactId + "/threads").push().getKey();

        thread.title_name = contact.name;
        FirebaseDatabase.getInstance().getReference().child("users/" + StaticVar.currentUser.contactId + "/threads").push().setValue(thread);

        HashMap<String, Object> details = new HashMap<>();
        details.put("creation_date", System.currentTimeMillis());
        details.put("creator_id", StaticVar.currentUser.contactId);

        FirebaseDatabase.getInstance().getReference().child("threads/" + thread.thread_id + "/details").setValue(details);

        thread.title_name = StaticVar.currentUser.name;
        FirebaseDatabase.getInstance().getReference().child("users/" + contact.contactId + "/threads").push().setValue(thread);

        idChat = thread.thread_id;
    }

    public void onClickSend(View view){
        String content = etMessage.getText().toString().trim();
        if(content.length() > 0){
            etMessage.setText("");
            Message newMessage = new Message();
            newMessage.text = content;
            //TODO: change it
            newMessage.idSender = StaticVar.currentUser.contactId;
            newMessage.timestamp = System.currentTimeMillis();
            FirebaseDatabase.getInstance().getReference().child("threads/" + idChat + "/messages").push().setValue(newMessage);
        }
    }
}


    class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private Context context;
        private ArrayList<Message> messages;

        public ListMessageAdapter(Context context, ArrayList<Message> messages){
            this.context = context;
            this.messages = messages;

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == ChatActivity.VIEW_TYPE_YOU_MESSAGE){
                View view = LayoutInflater.from(context).inflate(R.layout.item_msg_you, parent, false);
                return new ItemMessageUserHolder(view);
            }else if(viewType == ChatActivity.VIEW_TYPE_FROM_MESSAGE){
                View view = LayoutInflater.from(context).inflate(R.layout.item_msg_from, parent, false);
                return new ItemMessageFriendHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof ItemMessageUserHolder){
                ((ItemMessageUserHolder) holder).tvMessage.setText(messages.get(position).text);
                DateFormat df = new SimpleDateFormat("HH:mm:ss");
                String time = df.format(messages.get(position).timestamp);
                ((ItemMessageUserHolder) holder).tvTime.setText(time);
            } else if(holder instanceof ItemMessageFriendHolder){
                ((ItemMessageFriendHolder) holder).tvMessage.setText(messages.get(position).text);
                DateFormat df = new SimpleDateFormat("HH:mm:ss");
                String time = df.format(messages.get(position).timestamp);
                ((ItemMessageFriendHolder) holder).tvTime.setText(time);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return messages.get(position).idSender.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    ? ChatActivity.VIEW_TYPE_YOU_MESSAGE
                    : ChatActivity.VIEW_TYPE_FROM_MESSAGE;
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }

class ItemMessageUserHolder extends RecyclerView.ViewHolder {
    public TextView tvMessage;
    public TextView tvTime;

    public ItemMessageUserHolder(View itemView) {
        super(itemView);
        tvMessage = (TextView) itemView.findViewById(R.id.tvMsgYou);
        tvTime = (TextView) itemView.findViewById(R.id.tvTimeYou);
    }
}

class ItemMessageFriendHolder extends RecyclerView.ViewHolder {
    public TextView tvMessage;
    public TextView tvTime;

    public ItemMessageFriendHolder(View itemView) {
        super(itemView);
        tvMessage = (TextView) itemView.findViewById(R.id.tvMsgFrom);
        tvTime = (TextView) itemView.findViewById(R.id.tvTimeFrom);
    }
}




