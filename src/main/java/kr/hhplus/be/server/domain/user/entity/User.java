package kr.hhplus.be.server.domain.user.entity;


import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTimeEntity {

    private Long id;
    private String name;
    private String email;
}
