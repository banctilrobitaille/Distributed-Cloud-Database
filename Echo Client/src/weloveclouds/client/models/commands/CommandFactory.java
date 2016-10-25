package weloveclouds.client.models.commands;

import weloveclouds.client.models.UserInput;
import weloveclouds.client.utils.UserInputParser;
import weloveclouds.communication.api.ICommunicationApi;

/**
 * Created by Benoit on 2016-10-25.
 */
public class CommandFactory {
    ICommunicationApi communicationApi;

    public CommandFactory(ICommunicationApi communicationApi) {
        this.communicationApi = communicationApi;
    }

    public ICommand createCommandFromUserInput(UserInput userInput) {
        ICommand command = null;

        switch (userInput.getCommand()) {
            case CONNECT:
                command = new Connect(userInput.getArguments(), communicationApi);
                break;
            case DISCONNECT:
                command = new Disconnect(userInput.getArguments(),communicationApi);
                break;
            case SEND:
                command = new Send(userInput.getArguments(),communicationApi);
                break;
            case HELP:
                command = new Help(userInput.getArguments());
                break;
            case LOGLEVEL:
                command = new LogLevel(userInput.getArguments());
                break;
            case QUIT:
                command = new Quit(userInput.getArguments());
                break;
        }
        return command;
    }
}
