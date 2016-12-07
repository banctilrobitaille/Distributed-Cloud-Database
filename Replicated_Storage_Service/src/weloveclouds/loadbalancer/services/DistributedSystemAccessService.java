package weloveclouds.loadbalancer.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.services.DistributedService;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * Created by Benoit on 2016-12-03.
 */
public class DistributedSystemAccessService {
    private static final int FIRST = 0;
    private DistributedService distributedService;

    public void updateServiceHealthWith(NodeHealthInfos serverHealthInfos) {

    }

    public StorageNode getMasterOf(HashRange hashRange) {
        return null;
    }

    public List<StorageNode> getReplicasOf(HashRange hashRange) {
        return null;
    }

    public StorageNode getReadServerFor(String key) {
        Hash hash = new Hash(key.getBytes());
        return null;
    }

    public StorageNode getWriteServerFor(String key) {
        return null;
    }

    private StorageNode getHealthiestNodeFrom(List<StorageNode> storageNodes) {
        Collections.sort(storageNodes, new Comparator<StorageNode>() {
            @Override
            public int compare(StorageNode node1, StorageNode node2) {
                return node1.getHashKey().compareTo(node2.getHashKey());
            }
        });
        return storageNodes.get(FIRST);
    }
}
