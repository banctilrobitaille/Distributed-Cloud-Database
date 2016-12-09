package weloveclouds.server.requests.kvecs;

import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createErrorKVAdminMessage;
import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createSuccessKVAdminMessage;

import org.apache.log4j.Logger;

import weloveclouds.communication.models.ServerConnectionInfos;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.core.requests.exceptions.IllegalRequestException;
import weloveclouds.server.models.replication.HashRangesWithRoles;
import weloveclouds.server.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.IReplicableDataAccessService;
import weloveclouds.server.services.utils.IReplicationTransferer;
import weloveclouds.server.services.utils.ReplicationTransfererFactory;

/**
 * An update metadata request to the {@link IReplicableDataAccessService}, which defines in what
 * range shall be the keys of the entries which are stored on this server.
 *
 * @author Benedek
 */
public class UpdateRingMetadata implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(UpdateRingMetadata.class);

    private IReplicableDataAccessService dataAccessService;
    private RingMetadata ringMetadata;
    private HashRangesWithRoles rangesManagedByServer;

    private ServerConnectionInfos replicaConnectionInfos;
    private ReplicationTransfererFactory replicationTransfererFactory;

    protected UpdateRingMetadata(Builder builder) {
        this.dataAccessService = builder.dataAccessService;
        this.ringMetadata = builder.ringMetadata;
        this.rangesManagedByServer = builder.rangesManagedByServer;
        this.replicaConnectionInfos = builder.replicaConnectionInfos;
        this.replicationTransfererFactory = builder.replicationTransfererFactory;
    }

    @Override
    public KVAdminMessage execute() {
        LOGGER.debug("Executing update ring metadata request.");
        dataAccessService.setRingMetadata(ringMetadata);
        dataAccessService.setManagedHashRanges(rangesManagedByServer);

        IReplicationTransferer replicationTransferer = null;
        if (replicaConnectionInfos != null) {
            replicationTransferer = replicationTransfererFactory
                    .createReplicationTransferer(replicaConnectionInfos.getServerConnectionInfos());
        }
        dataAccessService.setReplicationTransferer(replicationTransferer);

        LOGGER.debug("Update ring metadata request finished successfully.");
        return createSuccessKVAdminMessage();
    }

    @Override
    public IKVECSRequest validate() throws IllegalArgumentException {
        try {
            if (replicaConnectionInfos != null) {
                KVServerRequestsValidator.validateServerConnectionInfos(replicaConnectionInfos);
            }
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Replica connection infos are invalid.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createErrorKVAdminMessage(errorMessage));
        }
        try {
            KVServerRequestsValidator.validateRingMetadata(ringMetadata);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Ring metadata is invalid.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createErrorKVAdminMessage(errorMessage));
        }
        try {

            KVServerRequestsValidator.validateHashRangesWithRoles(rangesManagedByServer);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Hash range with roles is invalid.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createErrorKVAdminMessage(errorMessage));
        }
        return this;
    }

    /**
     * Builder pattern for creating a {@link UpdateRingMetadata} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private IReplicableDataAccessService dataAccessService;
        private RingMetadata ringMetadata;
        private HashRangesWithRoles rangesManagedByServer;
        private ServerConnectionInfos replicaConnectionInfos;
        private ReplicationTransfererFactory replicationTransfererFactory;

        /**
         * @param dataAccessService which is used for the data access
         */
        public Builder dataAccessService(IReplicableDataAccessService dataAccessService) {
            this.dataAccessService = dataAccessService;
            return this;
        }

        /**
         * @param ringMetadata metadata information about the server ring
         */
        public Builder ringMetadata(RingMetadata ringMetadata) {
            this.ringMetadata = ringMetadata;
            return this;
        }

        /**
         * @param rangesManagedByServer the hash ranges which are managed by this server together
         *        with roles the server has for each range
         */
        public Builder rangesManagedByServer(HashRangesWithRoles rangesManagedByServer) {
            this.rangesManagedByServer = rangesManagedByServer;
            return this;
        }

        /**
         * @param replicaConnectionInfos connection information to the replica nodes
         */
        public Builder replicaConnectionInfos(ServerConnectionInfos replicaConnectionInfos) {
            this.replicaConnectionInfos = replicaConnectionInfos;
            return this;
        }

        /**
         * @param replicationTransfererFactory a factory to create replication transferer instances
         */
        public Builder replicationTransfererFactory(
                ReplicationTransfererFactory replicationTransfererFactory) {
            this.replicationTransfererFactory = replicationTransfererFactory;
            return this;
        }

        public UpdateRingMetadata build() {
            return new UpdateRingMetadata(this);
        }
    }

}
