package sushchak.bohdan.curabitur.ui;


import android.app.Activity;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sushchak.bohdan.curabitur.R;
import sushchak.bohdan.curabitur.model.Thread;



public class ThreadsFragment extends Fragment {

    private final String TAG = "ThreadsFragment";

    private OnListFragmentInteractionListener mListener;

    private ArrayList<Thread> listThread;
    private MyThreadsRecyclerViewAdapter adapter;

    public ThreadsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listThread = new ArrayList<>();

        getListThread();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_threads_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            adapter = new MyThreadsRecyclerViewAdapter(listThread, mListener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    private void getListThread(){
        FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/threads").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    HashMap mapThreads = (HashMap) dataSnapshot.getValue();
                    Iterator iterator = mapThreads.keySet().iterator();
                    while (iterator.hasNext()){
                        HashMap threadData = (HashMap) mapThreads.get(iterator.next());
                        String idThread =  threadData.get("thread_id").toString();
                        String threadName = threadData.get("title_name").toString();
                        Thread thread = new Thread();
                        thread.setThread_id(idThread);
                        thread.setTitle_name(threadName);
                        listThread.add(thread);
                        Log.d(TAG, idThread);
                    }
                    Log.d(TAG, mapThreads.toString());
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
            if (activity instanceof OnListFragmentInteractionListener) {
                mListener = (OnListFragmentInteractionListener) activity;
            } else {
                throw new RuntimeException(activity.toString()
                        + " must implement OnListFragmentInteractionListener");
            }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Thread item);
    }


    public static class MyThreadsRecyclerViewAdapter extends RecyclerView.Adapter<MyThreadsRecyclerViewAdapter.ViewHolder> {

        private final List<Thread> listContact;
        private final OnListFragmentInteractionListener mListener;
        private final String TAG = "MyThreadsRecyclerViewAdapter";

        public MyThreadsRecyclerViewAdapter(List<Thread> items, OnListFragmentInteractionListener listener) {
            listContact = items;
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_threads, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Thread thread = listContact.get(position);
            holder.mIdView.setText(listContact.get(position).getTitle_name());
            //holder.mContentView.setText(listContact.get(position).content);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onListFragmentInteraction(thread);
                        Log.d(TAG, thread.getThread_id());
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return listContact.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.id) TextView mIdView;
            @BindView(R.id.content) TextView mContentView;
            //public Thread mItem;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                //mIdView = (TextView) view.findViewById(R.id.id);
                //mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }


        }
    }
}
