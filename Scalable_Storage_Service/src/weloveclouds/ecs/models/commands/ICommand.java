package weloveclouds.ecs.models.commands;

import weloveclouds.ecs.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-11-18.
 */
public interface ICommand {
    void execute() throws ClientSideException;
}
