package ra.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidate {

    public static boolean checkRegexPassword(String string) {
        String password = RegexConstants.REGEX_PASSWORD;
        Pattern pattern = Pattern.compile(password);
        Matcher matcher = pattern.matcher(string);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static boolean checkRegexEmail(String string) {
        String email = RegexConstants.REGEX_EMAIL;
        Pattern pattern = Pattern.compile(email);
        Matcher matcher = pattern.matcher(string);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static boolean checkRegexPhone(String string) {
        String phone = RegexConstants.REGEX_PHONE;
        Pattern pattern = Pattern.compile(phone);
        Matcher matcher = pattern.matcher(string);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}
