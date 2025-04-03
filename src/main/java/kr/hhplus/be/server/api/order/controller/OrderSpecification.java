package kr.hhplus.be.server.api.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.api.order.dto.OrderRequest;
import kr.hhplus.be.server.api.order.dto.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Order API", description = "주문 관련 API")
public interface OrderSpecification {

    @Operation(summary = "Order API", description = "주문 관련 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문을 요청합니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":400,\"message\":\"BAD_REQUEST\"}"))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품입니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":404,\"message\":\"NOT_FOUND\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":500,\"message\":\"INTERNAL_SERVER_ERROR\"}")))
    })
    ResponseEntity<OrderResponse> order(@RequestBody OrderRequest request);

}
