package weloveclouds.client.models.commands;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.communication.api.v1.IKVCommunicationApi;
import weloveclouds.communication.exceptions.ClientSideException;
import weloveclouds.kvstore.models.IKVMessage;

public class Put extends AbstractKVCommunicationApiCommand {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 0;
    private Logger logger;

    public Put(String[] arguments, IKVCommunicationApi communicationApi) {
        super(arguments, communicationApi);
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            logger.info("Executing put command.");
            IKVMessage response =
                    communicationApi.put(arguments[KEY_INDEX], arguments[VALUE_INDEX]);

            logger.debug(response);
            switch (response.getStatus()) {
                case PUT_SUCCESS:
                case PUT_ERROR:
                    userOutputWriter.writeLine(response.getValue());
                    break;
                default:
                    userOutputWriter.writeLine("Unexpected response type.");
                    break;
            }
        } catch (Exception e) {
            logger.error(e);
            throw new ClientSideException(e.getMessage(), e);
        } finally {
            logger.info("Put command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validatePutArguments(arguments);
        return this;
    }

}
