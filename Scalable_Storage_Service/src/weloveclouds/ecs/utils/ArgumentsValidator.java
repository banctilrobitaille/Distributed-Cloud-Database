package weloveclouds.ecs.utils;

import java.util.Arrays;
import java.util.List;

import static weloveclouds.client.utils.CustomStringJoiner.join;


/**
 * Created by Benoit on 2016-11-21.
 */
public class ArgumentsValidator {
    private static final int INIT_SERVICE_NUMBER_OF_ARGUMENTS = 3;
    private static final int INIT_SERVICE_NUMBER_OF_NODE_ARG_INDEX = 0;
    private static final int INIT_SERVICE_CACHE_SIZE_ARG_INDEX = 1;
    private static final int INIT_SERVICE_DISPLACEMENT_STRATEGY_ARG_INDEX = 2;
    private static final int ADD_NODE_NUMBER_OF_ARGUMENTS = 2;
    private static final int ADD_NODE_CACHE_SIZE_ARG_INDEX = 0;
    private static final int ADD_NODE_DISPLACEMENT_STRATEGY__ARG_INDEX = 1;
    private static List<String> validStrategyNames = Arrays.asList("FIFO", "LFU", "LRU");

    public static void validateStartArguments(List<String> arguments) throws
            IllegalArgumentException {
        if (!isNullOrEmpty(arguments)) {
            throw new IllegalArgumentException("Start command doesn't accept any arguments.");
        }
    }

    public static void validateStopArguments(List<String> arguments) throws
            IllegalArgumentException {
        if (!isNullOrEmpty(arguments)) {
            throw new IllegalArgumentException("Stop command doesn't accept any arguments.");
        }
    }

    public static void validateInitServiceArguments(List<String> arguments) throws
            IllegalArgumentException {
        if (arguments.size() != INIT_SERVICE_NUMBER_OF_ARGUMENTS) {
            throw new IllegalArgumentException("InitService command takes " +
                    INIT_SERVICE_NUMBER_OF_ARGUMENTS + "arguments. " + arguments.size() + " " +
                    "arguments provided");
        }
        validateNumberOfNode(arguments.get(INIT_SERVICE_NUMBER_OF_NODE_ARG_INDEX));
        validateDisplacementStrategy(arguments.get(INIT_SERVICE_DISPLACEMENT_STRATEGY_ARG_INDEX));
        validateCacheSize(arguments.get(INIT_SERVICE_CACHE_SIZE_ARG_INDEX));
    }

    public static void validateRemoveNodeArguments(List<String> arguments) throws
            IllegalArgumentException {
        if (!isNullOrEmpty(arguments)) {
            throw new IllegalArgumentException("RemoveNode command doesn't accept any arguments.");
        }
    }

    public static void validateShutdownArguments(List<String> arguments) throws
            IllegalArgumentException {
        if (!isNullOrEmpty(arguments)) {
            throw new IllegalArgumentException("Shutdown command doesn't accept any arguments.");
        }
    }

    public static void validateAddNodeArguments(List<String> arguments) throws
            IllegalArgumentException {
        if (arguments.size() != ADD_NODE_NUMBER_OF_ARGUMENTS) {
            throw new IllegalArgumentException("AddNode command takes " +
                    ADD_NODE_NUMBER_OF_ARGUMENTS + "arguments. " + arguments.size() + " " +
                    "arguments provided");
        }
        validateCacheSize(arguments.get(ADD_NODE_CACHE_SIZE_ARG_INDEX));
        validateDisplacementStrategy(arguments.get(ADD_NODE_DISPLACEMENT_STRATEGY__ARG_INDEX));
    }

    private static void validateNumberOfNode(String argument) throws IllegalArgumentException {
        if (!isInteger(argument)) {
            throw new IllegalArgumentException("The number of node should be an integer.");
        }
    }

    private static void validateCacheSize(String argument) throws IllegalArgumentException {
        if (!isInteger(argument)) {
            throw new IllegalArgumentException("The cache size should be an integer.");
        }
    }

    private static void validateDisplacementStrategy(String argument) throws IllegalArgumentException {
        String message = join(" ",
                "Strategy is not recognized. It should be capitalized and should be one of the followings:",
                join(",", validStrategyNames));
        if (!validStrategyNames.contains(argument)) {
            throw new IllegalArgumentException(message);
        }
    }

    private static boolean isInteger(String argument) {
        boolean isInteger;
        try {
            Integer.parseInt(argument);
            isInteger = true;
        } catch (NumberFormatException ex) {
            isInteger = false;
        }

        return isInteger;
    }

    private static boolean isNullOrEmpty(List<String> arguments) {
        return arguments == null || arguments.isEmpty();
    }
}
