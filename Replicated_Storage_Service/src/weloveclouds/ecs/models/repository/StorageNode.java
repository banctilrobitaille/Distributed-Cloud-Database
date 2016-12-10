package weloveclouds.ecs.models.repository;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.core.ExternalConfigurationServiceConstants;
import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.utils.HashingUtil;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.ecs.models.repository.StorageNodeStatus.*;

/**
 * Created by Benoit on 2016-11-16.
 */
public class StorageNode extends AbstractNode {
    private static final int NO_CONNECTION = 0;

    private StorageNodeStatus metadataStatus;
    private StorageNodeStatus status;
    private HashRange previousHashRange;
    private HashRange hashRange;
    private List<StorageNode> replicas;
    private List<HashRange> childHashranges;

    private StorageNode(Builder storageNodeBuilder) {
        this.status = IDLE;
        this.metadataStatus = UNSYNCHRONIZED;
        this.id = storageNodeBuilder.id;
        this.serverConnectionInfo = storageNodeBuilder.serverConnectionInfo;
        this.hashKey = storageNodeBuilder.hashKey;
        this.hashRange = storageNodeBuilder.hashRange;
        this.ecsChannelConnectionInfo = storageNodeBuilder.ecsChannelConnectionInfo;
        this.replicas = storageNodeBuilder.replicas;
        this.childHashranges = storageNodeBuilder.childHashranges;
        this.previousHashRange = storageNodeBuilder.previousHashRange;

        if (storageNodeBuilder.healthInfos == null) {
            this.healthInfos = new NodeHealthInfos.Builder()
                    .serverName(id)
                    .serverConnectionInfo(serverConnectionInfo)
                    .numberOfActiveConnections(NO_CONNECTION)
                    .build();
        } else {
            this.healthInfos = storageNodeBuilder.healthInfos;
        }
    }


    public void updateHealthInfos(NodeHealthInfos healthInfos) {
        this.healthInfos = healthInfos;
    }

    public void setHashRange(HashRange hashRange) {
        this.previousHashRange = hashRange;
        this.hashRange = hashRange;
        this.metadataStatus = SYNCHRONIZED;
    }

    public StorageNodeStatus getMetadataStatus() {
        return metadataStatus;
    }

    public void setMetadataStatus(StorageNodeStatus metadataStatus) {
        this.metadataStatus = metadataStatus;
    }

    public StorageNodeStatus getStatus() {
        return status;
    }

    public void setStatus(StorageNodeStatus status) {
        this.status = status;
    }


    public HashRange getHashRange() {
        return hashRange;
    }

    public List<StorageNode> getReplicas() {
        return replicas;
    }

    public List<HashRange> getChildHashranges() {
        return childHashranges;
    }

    public void addChildHashrange(HashRange childHashRange) {
        this.childHashranges.add(childHashRange);
    }

    public void removeChildHashrange(HashRange childHashRange) {
        this.childHashranges.remove(childHashRange);
    }

    public void clearReplicas() {
        this.replicas.clear();
    }

    public void addReplicas(StorageNode node) {
        replicas.add(node);
    }

    public void removeReplicas(StorageNode node) {
        replicas.remove(node);
    }

    public boolean isReadResponsibleOf(Hash hash) {
        if (isWriteResponsibleOf(hash)) {
            return true;
        }

        for (HashRange hashRange : childHashranges) {
            if (hashRange.contains(hash)) {
                return true;
            }
        }
        return false;
    }

    public boolean isWriteResponsibleOf(Hash hash) {
        return hashRange.contains(hash);
    }

    public String toString() {
        return CustomStringJoiner.join(" ", "Node:" + getIpAddress() + "Status:" + status.name());
    }

    public static class Builder {
        private String id;
        private ServerConnectionInfo serverConnectionInfo;
        private ServerConnectionInfo ecsChannelConnectionInfo;
        private NodeHealthInfos healthInfos;
        private Hash hashKey;
        private HashRange previousHashRange;
        private HashRange hashRange;
        private List<StorageNode> replicas;
        private List<HashRange> childHashranges;

        public Builder() {
            this.replicas = new ArrayList<>();
            this.childHashranges = new ArrayList<>();
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder serverConnectionInfo(ServerConnectionInfo serverConnectionInfo) {
            this.serverConnectionInfo = serverConnectionInfo;
            this.ecsChannelConnectionInfo = new ServerConnectionInfo.Builder()
                    .ipAddress(serverConnectionInfo.getIpAddress())
                    .port(ExternalConfigurationServiceConstants.ECS_REQUESTS_PORT)
                    .build();
            this.hashKey = HashingUtil.getHash(serverConnectionInfo.toString());
            return this;
        }

        public Builder healthInfos(NodeHealthInfos nodeHealthInfos) {
            this.healthInfos = nodeHealthInfos;
            return this;
        }

        public Builder previousHashRange(HashRange previousHashRange) {
            this.previousHashRange = previousHashRange;
            return this;
        }

        public Builder hashRange(HashRange hashRange) {
            this.hashRange = hashRange;
            return this;
        }

        public Builder replicas(List<StorageNode> replicas) {
            this.replicas = replicas;
            return this;
        }

        public Builder childHashranges(List<HashRange> childHashanges) {
            this.childHashranges = childHashanges;
            return this;
        }

        public StorageNode build() {
            return new StorageNode(this);
        }
    }
}
