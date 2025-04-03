package kr.hhplus.be.server.common.exception;

public record ErrorResponse(
        int code,
        String message
) {
}
