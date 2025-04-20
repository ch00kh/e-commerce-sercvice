package kr.hhplus.be.server.application.user.dto;

import kr.hhplus.be.server.domain.user.dto.UserCommand;

public record UserCriteria() {

    public record Find(
            Long id
    ){
        public static UserCommand.Find toCommand(Long id) {
            return new UserCommand.Find(id);
        }
    }

    public record Create(
            String name
    ) {
        public UserCommand.Create toCommand() {
            return new UserCommand.Create(name);
        }
    }
}
