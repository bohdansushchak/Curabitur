package sushchak.bohdan.curabitur.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;

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

import sushchak.bohdan.curabitur.R;
import sushchak.bohdan.curabitur.model.Thread;



public class ThreadsFragment extends Fragment{

    private OnListFragmentInteractionListener mListener;

    private ArrayList<Thread> listThread;
    private MyThreadsRecyclerViewAdapter adapter;
    private final String TAG = "ThreadsFragment";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ThreadsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ThreadsFragment newInstance(int columnCount) {
        ThreadsFragment fragment = new ThreadsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
                        String idThread = ((HashMap) mapThreads.get(iterator.next().toString())).get("idthread").toString();
                        Thread thread = new Thread();
                        thread.idThread = idThread;
                        listThread.add(thread);
                        Log.d(TAG, idThread);
                    }
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (activity instanceof OnListFragmentInteractionListener) {
                mListener = (OnListFragmentInteractionListener) activity;
                Log.d(TAG, "context = OnListFragmentInteractionListener");
            } else {
                throw new RuntimeException(activity.toString()
                        + " must implement OnListFragmentInteractionListener");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Thread item);
    }

    public static class MyThreadsRecyclerViewAdapter extends RecyclerView.Adapter<MyThreadsRecyclerViewAdapter.ViewHolder> {

        private final List<Thread> mValues;
        private final OnListFragmentInteractionListener mListener;
        private final String TAG = "MyThreadsRecyclerViewAdapter";

        public MyThreadsRecyclerViewAdapter(List<Thread> items, OnListFragmentInteractionListener listener) {
            mValues = items;
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
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).idThread);
            //holder.mContentView.setText(mValues.get(position).content);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onListFragmentInteraction(holder.mItem);
                        Log.d(TAG, holder.mItem.idThread);
                    }
                    else Log.d(TAG, "mListener = null");
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Thread mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
