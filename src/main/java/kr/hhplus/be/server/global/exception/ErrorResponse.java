package kr.hhplus.be.server.global.exception;

public record ErrorResponse(
        int code,
        String message
) {
}
