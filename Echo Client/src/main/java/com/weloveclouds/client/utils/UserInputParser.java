package main.java.com.weloveclouds.client.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.com.weloveclouds.client.models.UserInput;

/**
 * Created by Benoit on 2016-10-21.
 */
public class UserInputParser {
    private static final Pattern INPUT_REGEX = Pattern.compile("(?<command>\\w+) " +
            "(?<payload>[a-zA-Z ]+)");

    public static UserInput parse(String userInput){
        Matcher matcher = INPUT_REGEX.matcher(userInput);
        matcher.find();
        return new UserInput()
                .withCommand(matcher.group("command"))
                .withPayload(matcher.group("payload"));
    }
}