package sushchak.bohdan.curabitur.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import sushchak.bohdan.curabitur.R;
import sushchak.bohdan.curabitur.data.StaticVar;
import sushchak.bohdan.curabitur.model.Message;

public class ChatActivity extends AppCompatActivity {

    private final String TAG = "ChatActivity";
    private RecyclerView rvChat;
    private String idChat;

    private ImageButton btnSend;
    private EditText etMessage;

    private ListMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarChatActivity);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        idChat = intent.getStringExtra(StaticVar.STR_EXTRA_CHAT_ID);

        btnSend = (ImageButton) findViewById(R.id.btnSend);
        etMessage = (EditText) findViewById(R.id.etMessage);
        rvChat = (RecyclerView) findViewById(R.id.rvChat);

        FirebaseDatabase.getInstance().getReference().child("threads/" + idChat + "/messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                    Message message = new Message();
                    message.idSender = (String) mapMessage.get("idSender");
                    message.text = (String) mapMessage.get("text");
                    message.timestamp = (Long) mapMessage.get("timestamp");
                    Log.d(TAG, message.toString());
                    //adapter.notifyDataSetChanged();
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

    public void onClickSend(View view){
        String content = etMessage.getText().toString().trim();
        if(content.length() > 0){
            etMessage.setText("");
            Message newMessage = new Message();
            newMessage.text = content;
            //TODO: change it
            newMessage.idSender = FirebaseAuth.getInstance().getCurrentUser().getUid();
            newMessage.timestamp = System.currentTimeMillis();
            FirebaseDatabase.getInstance().getReference().child("threads/" + idChat + "/messages").push().setValue(newMessage);
        }
    }
}


    class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

class ItemMessageUserHolder extends RecyclerView.ViewHolder {
    public TextView txtContent;
    public CircleImageView avata;

    public ItemMessageUserHolder(View itemView) {
        super(itemView);
        //txtContent = (TextView) itemView.findViewById(R.id.textContentUser);
        //avata = (CircleImageView) itemView.findViewById(R.id.imageView2);
    }
}

class ItemMessageFriendHolder extends RecyclerView.ViewHolder {
    public TextView txtContent;
    public CircleImageView avata;

    public ItemMessageFriendHolder(View itemView) {
        super(itemView);
        //txtContent = (TextView) itemView.findViewById(R.id.textContentFriend);
        //avata = (CircleImageView) itemView.findViewById(R.id.imageView3);
    }
}




