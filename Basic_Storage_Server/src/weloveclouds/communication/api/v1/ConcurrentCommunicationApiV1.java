package weloveclouds.communication.api.v1;

import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.services.IConcurrentCommunicationService;

/**
 * Created by Benoit on 2016-11-01.
 */
public class ConcurrentCommunicationApiV1 implements IConcurrentCommunicationApi{

    private IConcurrentCommunicationService communicationService;

    public ConcurrentCommunicationApiV1(IConcurrentCommunicationService communicationService){
        this.communicationService = communicationService;
    }
}
