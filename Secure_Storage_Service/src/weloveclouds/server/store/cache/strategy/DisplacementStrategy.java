package weloveclouds.server.store.cache.strategy;

import weloveclouds.server.store.cache.IKVStoreNotification;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * Represents a displacement strategy that decides which key shall be removed from the cache.
 * 
 * @author Benedek
 */
public interface DisplacementStrategy extends IKVStoreNotification {

    /**
     * Name of the key that shall be removed from the cache.
     * 
     * @throws StorageException if an error occurs
     */
    public String displaceKey() throws StorageException;

    /**
     * @return name of the strategy
     */
    public String getStrategyName();
}
