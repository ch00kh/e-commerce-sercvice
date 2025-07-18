package kr.hhplus.be.server.interfaces.balance.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.balance.dto.BalanceRequest;
import kr.hhplus.be.server.interfaces.balance.dto.BalanceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "UserBalance API", description = "잔고 관련 API 입니다.")
public interface IBalanceController {

    @Operation(summary = "잔액 충전 API", description = "사용자의 잔액을 충전합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "잔액 충전 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BalanceResponse.class))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 충전 금액", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":400,\"message\":\"BAD_REQUEST\"}"))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 충전 금액", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":400,\"message\":\"INVALID_CHARGE_AMOUNT\"}"))),
            @ApiResponse(responseCode = "400", description = "최대 잔고는 10,000,000을 초과할 수 없습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":400,\"message\":\"BALANCE_EXCEED_MAXIMUM\"}"))),
            @ApiResponse(responseCode = "404", description = "사용자 혹은 잔고를 찾을 수 없습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":404,\"message\":\"NOT_FOUND\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":500,\"message\":\"INTERNAL_SERVER_ERROR\"}")))
    })
    ResponseEntity<BalanceResponse.UserBalance> charge(@PathVariable Long userId, @RequestBody BalanceRequest.Charge request);

    @Operation(summary = "잔액 조회 API", description = "사용자의 잔고를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "잔고 조회 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BalanceResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 혹은 잔고를 찾을 수 없습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":404,\"message\":\"NOT_FOUND\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":500,\"message\":\"INTERNAL_SERVER_ERROR\"}")))
    })
    ResponseEntity<BalanceResponse.UserBalance> findBalance(@PathVariable Long id);

}
