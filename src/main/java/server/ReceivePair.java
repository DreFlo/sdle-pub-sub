package server;

import messages.Message;

public record ReceivePair(byte[] address, Message message) {
}
