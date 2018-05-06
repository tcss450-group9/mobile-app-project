package group9.tcss450.uw.edu.chatappgroup9.utils;


import android.text.TextUtils;

import java.util.regex.Pattern;

public class InputVerificationTool {
    /**
     * Regular expression
     **/
    private static final Pattern REG_EX_USERNAME = Pattern.compile("[^a-zA-Z_0-9]");
    private static final Pattern REG_EX_NAME = Pattern.compile("[^a-zA-Z]");
    private static final Pattern REG_EX_PASSWORD =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*(_|[^\\w])).+$");

    public static boolean isUsername(String theString) {
        if (TextUtils.isEmpty(theString)) {
            return false;
        }
        return !REG_EX_USERNAME.matcher(theString).find();
    }

    public static boolean isName(String theString) {
        if (TextUtils.isEmpty(theString)) {
            return false;
        }
        return !REG_EX_NAME.matcher(theString).find();
    }

    public static boolean isPassword(String theString) {
        if (TextUtils.isEmpty(theString)) {
            return false;
        }
        return REG_EX_PASSWORD.matcher(theString).matches();
    }

    public static boolean isEmail (final String theString) {
        if (TextUtils.isEmpty(theString)) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(theString).matches();
    }
}
