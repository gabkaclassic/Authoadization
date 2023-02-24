package org.example.configuration.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class AccountValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\w+@\\w+\\.\\w+");
    private static final Pattern PASSWORD_PATTERN_FIRST = Pattern.compile("\\w+");
    private static final Pattern PASSWORD_PATTERN_SECOND = Pattern.compile("\\d+");
    private static final Pattern PASSWORD_PATTERN_THREE = Pattern.compile("[^0-9a-zA-Z]+");
    private static final int MIN_LENGTH_PASSWORD = 8;
    private static final int MAX_LENGTH_PASSWORD = 128;

    public boolean validate(String login,
                           String password,
                            List<String> remarks) {

        return validLogin(login, remarks) && validPassword(password, remarks);
    }

    public boolean validPassword(String password, List<String> remarks) {

        if(Objects.isNull(password)
                || (password = StringUtils.trimAllWhitespace(password)).isEmpty()) {

            remarks.add("Enter your password");

            return false;
        }

        boolean isCorrectPassword = (password.length() >= MIN_LENGTH_PASSWORD)
                && (password.length() <= MAX_LENGTH_PASSWORD)
                && PASSWORD_PATTERN_FIRST.matcher(password).find()
                && PASSWORD_PATTERN_SECOND.matcher(password).find()
                && PASSWORD_PATTERN_THREE.matcher(password).find();

        if(!isCorrectPassword)
            remarks.add(String.format("The password must contain %d-%d characters, of which 1 digit and 1 specified character", MIN_LENGTH_PASSWORD, MAX_LENGTH_PASSWORD));

        return isCorrectPassword;
    }

    public boolean validLogin(String login, List<String> remarks) {

        boolean isCorrectEmailAddress = Objects.nonNull(login)
                && !login.isEmpty()
                && EMAIL_PATTERN.matcher(login).find();

        if (!isCorrectEmailAddress)
            remarks.add("Invalid email address");

        return isCorrectEmailAddress;
    }
}
