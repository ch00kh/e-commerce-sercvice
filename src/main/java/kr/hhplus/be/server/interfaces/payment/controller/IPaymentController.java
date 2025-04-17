package kr.hhplus.be.server.interfaces.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.payment.dto.PaymentRequest;
import kr.hhplus.be.server.interfaces.payment.dto.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Order API", description = "주문 관련 API")
public interface IPaymentController {

    @Operation(summary = "주문 API", description = "사용자의 주문을 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PaymentResponse.class)
            )),
            @ApiResponse(responseCode = "404", description = "사용자 혹은 주문을 찾을 수 없음", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":400,\"message\":\"BAD_REQUEST\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":500,\"message\":\"INTERNAL_SERVER_ERROR\"}")))
    })
    ResponseEntity<PaymentResponse> pay(@RequestBody PaymentRequest request);

}
