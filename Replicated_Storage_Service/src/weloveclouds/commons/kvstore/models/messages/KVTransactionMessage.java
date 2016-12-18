package weloveclouds.commons.kvstore.models.messages;

import java.util.UUID;

import weloveclouds.commons.kvstore.models.messages.proxy.KVTransactionMessageProxy;
import weloveclouds.commons.utils.StringUtils;

public class KVTransactionMessage implements IKVTransactionMessage {

    private StatusType status;
    private UUID transactionId;
    private IKVTransferMessage transferPayload;

    protected KVTransactionMessage(Builder builder) {
        this.status = builder.status;
        this.transactionId = builder.transactionId;
        this.transferPayload = builder.transferPayload;
    }

    @Override
    public StatusType getStatus() {
        return status;
    }

    @Override
    public UUID getTransactionId() {
        return transactionId;
    }

    @Override
    public IKVTransferMessage getTransferPayload() {
        return transferPayload;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
        result = prime * result + ((transferPayload == null) ? 0 : transferPayload.hashCode());
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
        if (obj instanceof KVTransactionMessageProxy) {
            KVTransactionMessageProxy other = (KVTransactionMessageProxy) obj;
            return other.equals(this);
        }
        if (!(obj instanceof KVTransactionMessage)) {
            return false;
        }
        KVTransactionMessage other = (KVTransactionMessage) obj;
        if (transactionId == null) {
            if (other.transactionId != null) {
                return false;
            }
        } else if (!transactionId.equals(other.transactionId)) {
            return false;
        }
        if (transferPayload == null) {
            if (other.transferPayload != null) {
                return false;
            }
        } else if (!transferPayload.equals(other.transferPayload)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return StringUtils.join(" ", "{ Message status:", status, ", Transaction ID:",
                transactionId, ", Transfer payload: ", transferPayload, "}");
    }

    public static class Builder {
        private StatusType status;
        private UUID transactionId;
        private IKVTransferMessage transferPayload;

        public Builder status(StatusType status) {
            this.status = status;
            return this;
        }

        public Builder transactionId(UUID transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder transferPayload(IKVTransferMessage transferPayload) {
            this.transferPayload = transferPayload;
            return this;
        }

        public KVTransactionMessage build() {
            return new KVTransactionMessage(this);
        }
    }

}
