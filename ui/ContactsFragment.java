package sushchak.bohdan.curabitur.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import sushchak.bohdan.curabitur.R;
import sushchak.bohdan.curabitur.model.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class ContactsFragment extends Fragment {

    private final String TAG = "ContactsFragment";

    private OnListFragmentInteractionListener mListener;

    private ArrayList<Contact> listContact;
    private MyContactsRecyclerViewAdapter adapter;
    private Context context;
    private HashMap<String, String> mapPhoneContacts;


    public ContactsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listContact = new ArrayList<>();
        getListContactFromPhone();
    }

    private void getListContactFromPhone() {
        mapPhoneContacts = new HashMap<>();

        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Email.DATA};

        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, null, null, null);
        if (cursor != null) {
            try {
                final int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int emailIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                String displayName, address;
                while (cursor.moveToNext()) {
                    displayName = cursor.getString(displayNameIndex);
                    address = cursor.getString(emailIndex);
                    mapPhoneContacts.put(address, displayName);
                }
            } finally {
                cursor.close();
            }
            checkContactInDB();
        }
    }

    private void checkContactInDB(){
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    HashMap mapContact = (HashMap) dataSnapshot.getValue();
                    Iterator iterator = mapContact.keySet().iterator();
                    while (iterator.hasNext()){
                        String contactId = iterator.next().toString();
                        String contactEmail = ((HashMap) mapContact.get(contactId)).get("email").toString();
                        if(mapPhoneContacts.get(contactEmail) != null) {
                            Contact newContact = new Contact();
                            newContact.contactId = contactId;
                            newContact.email = contactEmail;
                            newContact.name = mapPhoneContacts.get(contactEmail);
                            Log.d(TAG, newContact.toString());
                            listContact.add(newContact);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            adapter = new MyContactsRecyclerViewAdapter(listContact, mListener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
            if (context instanceof OnListFragmentInteractionListener) {
                this.context = context;
                mListener = (OnListFragmentInteractionListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnListFragmentInteractionListener");
            }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Contact item);
    }

    public static class MyContactsRecyclerViewAdapter extends RecyclerView.Adapter<MyContactsRecyclerViewAdapter.ViewHolder> {

        private final List<Contact> listContact;
        private final OnListFragmentInteractionListener mListener;

        public MyContactsRecyclerViewAdapter(List<Contact> items, OnListFragmentInteractionListener listener) {
            listContact = items;
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_contacts, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = listContact.get(position);
            holder.tvContactName.setText(listContact.get(position).name);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onListFragmentInteraction(holder.mItem);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return listContact.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tvContactName)
            TextView tvContactName;

            @BindView(R.id.civAvatar)
            CircleImageView civAvatar;

            Contact mItem;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                //tvContactName = (TextView) view.findViewById(R.id.tvContactName);
                //civAvatar = (CircleImageView) view.findViewById(R.id.civAvatar);
            }
        }
    }
}
