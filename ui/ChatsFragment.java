package sushchak.bohdan.curabitur.ui;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import sushchak.bohdan.curabitur.R;
import sushchak.bohdan.curabitur.model.Message;
import sushchak.bohdan.curabitur.model.Thread;
import sushchak.bohdan.curabitur.model.ThreadData;
import sushchak.bohdan.curabitur.utils.ChatUtils;
import sushchak.bohdan.curabitur.utils.ImageUtils;


public class ChatsFragment extends Fragment {

    public static final String TAG = "ChatsFragment";

    private ThreadFragmentInteractionListener mListener;

    private ArrayList<ThreadData> listDataThread;
    private MyThreadsRecyclerViewAdapter adapter;

    private DatabaseReference reference;

    private ArrayList<Thread> listThread;

    public ChatsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listDataThread = new ArrayList<>();
        listThread = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference();
        getListThread();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_threads_list, container, false);

        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            adapter = new MyThreadsRecyclerViewAdapter(ChatsFragment.this, listDataThread, mListener);
            recyclerView.setAdapter(adapter);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_chat_list));
            recyclerView.addItemDecoration(dividerItemDecoration);
        }
        return view;
    }

    private void getListThread(){

        reference.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/threads").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    HashMap mapThreads = (HashMap) dataSnapshot.getValue();
                    Iterator iterator = mapThreads.keySet().iterator();
                    while (iterator.hasNext()){
                        HashMap mapThread = (HashMap) mapThreads.get(iterator.next());
                        String idThread =  mapThread.get("thread_id").toString();
                        String threadName = mapThread.get("title_name").toString();
                        Thread thread = new Thread();
                        thread.setThread_id(idThread);
                        thread.setTitle_name(threadName);
                        listThread.add(thread);
                    }
                    getUserIdFromChats();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getUserIdFromChats(){

        for (final Thread thread : listThread){
            reference.child("threads/" + thread.getThread_id() + "/user").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        HashMap mapUsers = (HashMap) dataSnapshot.getValue();
                        Iterator iterator = mapUsers.keySet().iterator();
                        final ThreadData threadData = new ThreadData(thread);
                        while (iterator.hasNext()) {
                            HashMap map = (HashMap) mapUsers.get(iterator.next());
                            String userId = (String) map.get("user_id");

                            threadData.listUserId.add(userId);

                        }
                        reference.child("threads/" + thread.getThread_id() + "/details").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue() != null){
                                    HashMap mapDetails = (HashMap) dataSnapshot.getValue();
                                    String textLastMessage;
                                    long timeLastMessage;
                                    try {
                                        textLastMessage = (String) mapDetails.get("textLastMessage");
                                        timeLastMessage = (Long) mapDetails.get("timeLastMessage");
                                    }catch (NullPointerException e){
                                        textLastMessage = "";
                                        timeLastMessage = 0;
                                    }

                                    Message lastMessage = new Message();
                                    lastMessage.text = textLastMessage;
                                    lastMessage.timestamp = timeLastMessage;

                                    threadData.setLastMessage(lastMessage);

                                    Log.d(TAG, "ThreadData: " + threadData.toString());
                                    listDataThread.add(threadData);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
            if (activity instanceof ThreadFragmentInteractionListener) {
                mListener = (ThreadFragmentInteractionListener) activity;
            } else {
                throw new RuntimeException(activity.toString()
                        + " must implement ThreadFragmentInteractionListener");
            }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ThreadFragmentInteractionListener {
        int CLICK = 1;
        int LONG_CLICK = 2;

        void threadFragmentInteractionClick(ThreadData item, int clickType);
    }

    public static class MyThreadsRecyclerViewAdapter extends RecyclerView.Adapter<MyThreadsRecyclerViewAdapter.ViewHolder> {

        private final List<ThreadData> threadList;
        private final ThreadFragmentInteractionListener mListener;
        private final String TAG = "MyThreadsRecyclerViewAdapter";
        private Fragment mFragment;

        public MyThreadsRecyclerViewAdapter(Fragment fragment, List<ThreadData> items, ThreadFragmentInteractionListener listener) {
            this.mFragment = fragment;
            this.threadList = items;
            this.mListener = listener;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_chat, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final ThreadData thread = threadList.get(position);
            holder.tvChatName.setText(thread.getTitle_name());
            holder.tvLastMessage.setText(thread.getLastMessage().text);

            if(thread.getLastMessage().timestamp != 0) {
                DateFormat df = new SimpleDateFormat("HH:mm");
                String time = df.format(thread.getLastMessage().timestamp);
                holder.tvTimeLastMessage.setText(time);
            }else {
                holder.tvTimeLastMessage.setText("");
            }


            WeakReference<CircleImageView> reference = new WeakReference<CircleImageView>(holder.civChatAvatar);
            for (String userId: thread.listUserId) {
                if(!userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    ImageUtils.setUserAvatar(reference, null, userId, R.drawable.user_avatar_default);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.threadFragmentInteractionClick(thread, ThreadFragmentInteractionListener.CLICK);
                        Log.d(TAG, thread.getThread_id());
                    }
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new BottomSheet.Builder(mFragment.getActivity()).sheet(R.menu.main_sheet_chat).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case R.id.sheet_pin_to_top:
                                {
                                    break;
                                }
                                case R.id.sheet_clear_history:
                                {
                                    break;
                                }
                                case R.id.sheet_delete:
                                {
                                    ChatUtils.deleteChatInUser(thread);
                                    threadList.remove(position);
                                    notifyDataSetChanged();
                                    break;
                                }
                            }

                        }
                    }).show();

                    return true;
                }
            });
        }



        @Override
        public int getItemCount() {
            return threadList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.civChatAvatar) CircleImageView civChatAvatar;
            @BindView(R.id.tvChatName) TextView tvChatName;
            @BindView(R.id.tvLastMessage) TextView tvLastMessage;
            @BindView(R.id.tvTimeLastMessage) TextView tvTimeLastMessage;
            @BindView(R.id.tvCountMessages) TextView tvCountMessages;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

        }
    }
}
