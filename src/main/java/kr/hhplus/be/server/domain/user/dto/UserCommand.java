package kr.hhplus.be.server.domain.user.dto;

public record UserCommand() {

    public record Find(
            Long id
    ){
        public static Find from(Long id) {
            return new Find(id);
        }
    }
}
