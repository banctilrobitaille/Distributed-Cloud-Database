package app_kvServer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import weloveclouds.communication.api.v1.ConcurrentCommunicationApiV1;
import weloveclouds.communication.services.ConcurrentCommunicationService;
import weloveclouds.server.core.Server;
import weloveclouds.server.core.ServerSocketFactory;
import weloveclouds.server.models.RequestFactory;
import weloveclouds.server.models.ResponseFactory;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.cache.strategy.FIFOStrategy;
import weloveclouds.server.store.cache.strategy.LFUStrategy;
import weloveclouds.server.store.cache.strategy.LRUStrategy;

public class KVServer {
    private static int SERVER_PORT;
    private static int CACHE_SIZE;

    public static void main(String[] args) {
        try {
            new Server.ServerBuilder()
                    .port(SERVER_PORT)
                    .cacheSize(CACHE_SIZE)
                    .serverSocketFactory(new ServerSocketFactory())
                    .requestFactory(new RequestFactory())
                    .responseFactory(new ResponseFactory())
                    .communicationApi(new ConcurrentCommunicationApiV1(new ConcurrentCommunicationService()))
                    .build()
                    .start();
        } catch (IOException e) {
            //LOG what's going on
        }
    }

    /**
     * Start KV Server at given port
     *
     * @param port      given port for storage server to operate
     * @param cacheSize specifies how many key-value pairs the server is allowed to keep in-memory
     * @param strategy  specifies the cache replacement strategy in case the cache is full and there
     *                  is a GET- or PUT-request on a key that is currently not contained in the
     *                  cache. Options are "FIFO", "LRU", and "LFU".
     */
    public KVServer(int port, int cacheSize, String strategy) {
        DisplacementStrategy displacementStrategy = null;

        switch (strategy) {
            case "FIFO":
                displacementStrategy = new FIFOStrategy();
                break;
            case "LRU":
                displacementStrategy = new LRUStrategy();
                break;
            case "LFU":
                displacementStrategy = new LFUStrategy();
                break;
            default:
                throw new IllegalArgumentException(
                        "Invalid strategy. Valid values are: FIFO, LRU, LFU");
        }
    }
}
