package weloveclouds.server.models.requests;

import weloveclouds.kvstore.KVMessage;
import weloveclouds.server.services.IDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;

import static weloveclouds.kvstore.IKVMessage.StatusType.GET_ERROR;
import static weloveclouds.kvstore.IKVMessage.StatusType.GET_SUCCESS;

/**
 * Created by Benoit on 2016-10-31.
 */
public class Get implements IRequest {
    private IDataAccessService dataAccessService;
    private String key;

    public Get(IDataAccessService dataAccessService, String key) {
        this.dataAccessService = dataAccessService;
        this.key = key;
    }

    @Override
    public KVMessage execute() {
        KVMessage response = null;
        try {
            response = new KVMessage.KVMessageBuilder()
                    .status(GET_SUCCESS)
                    .key(key)
                    .value(dataAccessService.getValue(key))
                    .build();
        } catch (StorageException e) {
            response = new KVMessage.KVMessageBuilder()
                    .status(GET_ERROR)
                    .key(key)
                    .value(null)
                    .build();
        }
        return response;
    }
}
