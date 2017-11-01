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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import sushchak.bohdan.curabitur.R;
import sushchak.bohdan.curabitur.data.StaticVar;
import sushchak.bohdan.curabitur.data.UserDataSharedPreference;
import sushchak.bohdan.curabitur.model.Message;
import sushchak.bohdan.curabitur.model.Thread;
import sushchak.bohdan.curabitur.model.User;
import sushchak.bohdan.curabitur.utils.ImageUtils;

public class ChatActivity extends AppCompatActivity {

    public final static int VIEW_TYPE_YOU_MESSAGE = 0;
    public final static int VIEW_TYPE_FROM_MESSAGE = 1;

    private final String TAG = "ChatActivity";
    @BindView(R.id.rvChat) RecyclerView rvChat;
    private String idChat;



    @BindView(R.id.etMessage) EditText etMessage;

    private ArrayList<Message> messages;

    private ListMessageAdapter adapter;
    private LinearLayoutManager linearManager;

    protected User mUser;
    private User chatUser;

    @BindView(R.id.civ_Chat_avatar) CircleImageView civChatAvatar;
    @BindView(R.id.tv_Chat_Name) TextView tvChatName;
    @BindView(R.id.tv_User_Last_Seen) TextView tvUserLastSeen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarChatActivity);
        setSupportActionBar(toolbar);


        mUser = UserDataSharedPreference.getInstance(ChatActivity.this).getUserData();

        Log.d(TAG, mUser.toString());

        chatUser = new User();
        Intent intent = getIntent();
        idChat = intent.getStringExtra(StaticVar.STR_EXTRA_CHAT_ID);
        if(idChat == null){
            chatUser.setUserId(intent.getStringExtra(StaticVar.STR_EXTRA_CONTACT_ID));
            chatUser.setName(intent.getStringExtra(StaticVar.STR_EXTRA_CONTACT_NAME));
            createNewChat();
        }

        //getUserData();

        messages = new ArrayList<>();

        //etMessage = (EditText) findViewById(R.id.etMessage);
        //rvChat = (RecyclerView) findViewById(R.id.rvChat);

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

    private void viewUserData(){

        WeakReference<CircleImageView> reference = new WeakReference<>(civChatAvatar);
        ImageUtils.setUserAvatar(reference, chatUser, R.drawable.user_avatar_default);

        tvChatName.setText(chatUser.getName());
    }

    private void getUserData(){
        FirebaseDatabase.getInstance().getReference().child("users/" + chatUser.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    HashMap mapUserData = (HashMap) dataSnapshot.getValue();
                    String name = (String) mapUserData.get("name");
                    String avatar = (String) mapUserData.get("avatar");
                    String email = (String) mapUserData.get("email");
                    String phone = (String) mapUserData.get("phone");

                    chatUser.setName(name);
                    chatUser.setAvatar(avatar);
                    chatUser.setEmail(email);
                    chatUser.setPhone(phone);
                    viewUserData();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createNewChat(){
        Thread thread = new Thread();

        String threadId = FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/threads").push().getKey();
        thread.setThread_id(threadId);
        thread.setTitle_name(chatUser.getName());

        FirebaseDatabase.getInstance().getReference().child("users/" + mUser.getUserId() + "/threads").push().setValue(thread);

        HashMap<String, Object> details = new HashMap<>();
        details.put("creation_date", System.currentTimeMillis());
        details.put("creator_id", mUser.getUserId());

        FirebaseDatabase.getInstance().getReference().child("threads/" + thread.getThread_id() + "/details").setValue(details);

        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", mUser.getUserId());
        map.put("user_avatar", mUser.getAvatar());
        FirebaseDatabase.getInstance().getReference().child("threads/" + thread.getThread_id() + "/user").push().setValue(map);
        map = new HashMap<>();
        map.put("user_id", chatUser.getUserId());
        map.put("user_avatar", chatUser.getAvatar());
        FirebaseDatabase.getInstance().getReference().child("threads/" + thread.getThread_id() + "/user").push().setValue(map);


        thread.setTitle_name(mUser.getName());
        FirebaseDatabase.getInstance().getReference().child("users/" + chatUser.getUserId() + "/threads").push().setValue(thread);

        idChat = thread.getThread_id();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.chat_menu_search:{

                break;
            }
            case R.id.chat_menu_clear_history:{

                break;
            }
            case R.id.chat_menu_delete_chat:{

                break;
            }
            case R.id.chat_menu_mute_notifications:{

                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickSend(View view){

        final DatabaseReference  reference = FirebaseDatabase.getInstance().getReference();

        Log.d(TAG, mUser.toString());
        String content = etMessage.getText().toString().trim();
        if(content.length() > 0){
            etMessage.setText("");
            Message newMessage = new Message();
            newMessage.text = content;
            newMessage.idSender = mUser.getUserId();
            newMessage.timestamp = System.currentTimeMillis();
            final String key = reference.child("threads/" + idChat + "/messages").push().getKey();

            reference.child("threads/" + idChat + "/messages/" + key).setValue(newMessage);

            reference.child("threads/" + idChat + "/details").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null){
                        HashMap map = (HashMap) dataSnapshot.getValue();

                        map.put("idLastMessage", key);
                        reference.child("threads/" + idChat + "/details").setValue(map);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


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




