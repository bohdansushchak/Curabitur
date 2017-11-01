package sushchak.bohdan.curabitur.utils;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;

import sushchak.bohdan.curabitur.model.Thread;
import sushchak.bohdan.curabitur.model.User;

public class ChatUtils {

    public static void deleteChatInUser(final Thread thread){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference.child(User.KEY_USERS + "/" + userId + "/" + Thread.KEY_THREAD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    HashMap mapThreads = (HashMap) dataSnapshot.getValue();
                    Iterator iterator = mapThreads.keySet().iterator();
                    while (iterator.hasNext()){
                        String keyThread = (String) iterator.next();
                        HashMap map = (HashMap) mapThreads.get(keyThread);
                        String threadId = (String) map.get("thread_id");
                        if(threadId.equals(thread.getThread_id())){
                            reference.child(User.KEY_USERS + "/" + userId + "/" + Thread.KEY_THREAD + "/" +  keyThread).removeValue();
                            deleteUserInChat(thread);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void deleteUserInChat(final Thread thread){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference.child(Thread.KEY_THREAD + "/" + thread.getThread_id() + "/" + "user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    HashMap mapUsers = (HashMap) dataSnapshot.getValue();
                    Iterator iterator = mapUsers.keySet().iterator();
                    while (iterator.hasNext()){
                        String keyUser = (String) iterator.next();
                        HashMap map = (HashMap) mapUsers.get(keyUser);
                        String userId = (String) map.get("user_id");
                        if(userId.equals(currentUserId)){
                            reference.child(Thread.KEY_THREAD + "/" + thread.getThread_id() + "/" + "user/" + keyUser).removeValue();
                            if(mapUsers.keySet().size() == 1){
                                reference.child(Thread.KEY_THREAD + "/" + thread.getThread_id()).removeValue();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
