package weloveclouds.server.models.commands;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;

/**
 * Different commands which are handled by
 * {@link weloveclouds.server.models.commands.ServerCommandFactory}.
 *
 * @author Benedek
 */
public enum ServerCommand {
    CACHESIZE("cacheSize"), HELP("help"), LOGLEVEL("logLevel"), PORT("port"), START(
            "start"), STORAGEPATH(
                    "storagePath"), STRATEGY("strategy"), QUIT("quit"), DEFAULT("default");

    private static final Logger LOGGER = Logger.getLogger(ServerCommand.class);

    private String description;

    ServerCommand(String description) {
        this.description = description;
    }

    /**
     * Converts the parameter to a command if its name matches with one of the commands. Otherwise
     * it returns {@link #DEFAULT}
     */
    public static ServerCommand getValueFromDescription(String description) {
        for (ServerCommand command : ServerCommand.values()) {
            if (command.description.equals(description)) {
                return command;
            }
        }

        LOGGER.warn(CustomStringJoiner.join("", "Command (", description, ") is not recognized."));
        return DEFAULT;
    }

}

