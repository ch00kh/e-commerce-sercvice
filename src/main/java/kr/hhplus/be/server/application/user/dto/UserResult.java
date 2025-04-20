package kr.hhplus.be.server.application.user.dto;

public record UserResult() {

    public record Create(
            Long id,
            String name,
            Long balance
    ) {}

}
