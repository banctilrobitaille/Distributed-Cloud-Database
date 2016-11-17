package weloveclouds.communication.models;

import java.net.InetAddress;
import java.net.UnknownHostException;

import weloveclouds.client.utils.CustomStringJoiner;

/**
 * Stores the server connection information, the {@link #ipAddress} and the {@link #port}.
 *
 * @author Benoit, Benedek
 */
public class ServerConnectionInfo {

    private InetAddress ipAddress;
    private int port;

    protected ServerConnectionInfo(ServerConnectionInfoBuilder builder) {
        this.ipAddress = builder.ipAddress;
        this.port = builder.port;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
        result = prime * result + port;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ServerConnectionInfo)) {
            return false;
        }
        ServerConnectionInfo other = (ServerConnectionInfo) obj;
        if (ipAddress == null) {
            if (other.ipAddress != null) {
                return false;
            }
        } else if (!ipAddress.equals(other.ipAddress)) {
            return false;
        }
        if (port != other.port) {
            return false;
        }
        return true;
    }

    public String toStringWithDelimiter(String delimiter) {
        return CustomStringJoiner.join(delimiter, ipAddress.getHostAddress(), String.valueOf(port));
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join("", "<", ipAddress.getHostAddress(), " , ",
                String.valueOf(port), ">");
    }

    /**
     * Builder pattern for creating a {@link ServerConnectionInfo} instance.
     *
     * @author Benedek
     */
    public static class ServerConnectionInfoBuilder {
        private InetAddress ipAddress;
        private int port;

        public ServerConnectionInfoBuilder ipAddress(String ipAddress) throws UnknownHostException {
            if (ipAddress == null) {
                throw new UnknownHostException("No host provided.");
            }
            this.ipAddress = InetAddress.getByName(ipAddress);
            return this;
        }

        public ServerConnectionInfoBuilder ipAddress(InetAddress address) {
            this.ipAddress = address;
            return this;
        }

        public ServerConnectionInfoBuilder port(int port) {
            this.port = port;
            return this;
        }

        public ServerConnectionInfo build() {
            return new ServerConnectionInfo(this);
        }
    }
}
