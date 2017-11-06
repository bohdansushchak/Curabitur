package sushchak.bohdan.curabitur.data;


import android.content.Context;
import android.content.SharedPreferences;
import sushchak.bohdan.curabitur.model.User;

public class UserDataSharedPreference {
    private static volatile UserDataSharedPreference instance = null;
    private static SharedPreferences preference;
    private SharedPreferences.Editor editor;

    private static final String SHARE_USER_DATA = "user_data";
    private static final String SHARE_KEY_UID = "uid";
    private static final String SHARE_KEY_NAME = "name";
    private static final String SHARE_KEY_EMAIL = "email";
    private static final String SHARE_KEY_AVATAR = "avatar";
    private static final String SHARE_KEY_PHONE = "phone";
    private static final String SHARE_KEY_PHONE_AVATAR = "phonePathAvatar";


    private UserDataSharedPreference() {
    }

    public static UserDataSharedPreference getInstance(Context context) {
        if (context == null)
            throw new NullPointerException("Context cannot be null");

        if (instance == null) {
            synchronized (UserDataSharedPreference.class) {
                if(instance == null) {
                    instance = new UserDataSharedPreference();
                    preference = context.getSharedPreferences(SHARE_USER_DATA, Context.MODE_PRIVATE);

                }
            }
        }

        return instance;
    }

    public void saveUserData(User user) {
        if (user == null)
            throw new NullPointerException("UserDB cannot be null");

        editor = preference.edit();
        editor.putString(SHARE_KEY_UID, user.getUserId());
        editor.putString(SHARE_KEY_NAME, user.getName());
        editor.putString(SHARE_KEY_EMAIL, user.getEmail());
        editor.putString(SHARE_KEY_AVATAR, user.getAvatar());
        editor.putString(SHARE_KEY_PHONE, user.getPhone());
        editor.apply();
    }

    public void savePhonePathAvatar(String path){
        editor = preference.edit();
        editor.putString(SHARE_KEY_PHONE_AVATAR, path);
        editor.apply();
    }

    public String getPhonePathAvatar(){
        String path = preference.getString(SHARE_KEY_PHONE_AVATAR, "none");
        return path;
    }

    public User getUserData(){
        String userId = preference.getString(SHARE_KEY_UID, null);
        String userName = preference.getString(SHARE_KEY_NAME, null);
        String userEmail = preference.getString(SHARE_KEY_EMAIL, null);
        String userAvatar = preference.getString(SHARE_KEY_AVATAR, null);
        String userPhone = preference.getString(SHARE_KEY_PHONE, null);

        User user = new User(
                userId,
                userName,
                userEmail,
                userAvatar,
                userPhone
        );
        return user;
    }

    public String getUid(){
        return preference.getString(SHARE_KEY_UID, null);
    }
}
