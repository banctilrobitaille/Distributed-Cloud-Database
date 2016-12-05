package weloveclouds.server.models.commands;


import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.commons.networking.requests.IValidatable;

/**
 * Represents an object (command) that can be executed.
 *
 * @author Benoit
 */
public interface ICommand extends IValidatable<ICommand> {

    /**
     * Executes the respective command.
     *
     * @throws ServerSideException if any error occurs
     */
    void execute() throws ServerSideException;
}
