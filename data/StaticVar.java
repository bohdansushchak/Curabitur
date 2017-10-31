package sushchak.bohdan.curabitur.data;

import android.os.Environment;

import java.io.File;

public class StaticVar {

    public static String STR_APP_DIRECTORY = Environment.getExternalStorageState() +
            File.separator + "Curabitur";

    public static String STR_DEFAULT_AVATAR = "default";
    public static String STR_EXTRA_CHAT_ID = "idChat";
    public static String STR_EXTRA_CONTACT_ID = "idContact";
    public static String STR_EXTRA_CONTACT_NAME = "contactName";

}
