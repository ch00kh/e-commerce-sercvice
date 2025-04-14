package kr.hhplus.be.server.interfaces.coupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.coupon.dto.CouponRequest;
import kr.hhplus.be.server.interfaces.coupon.dto.CouponResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Coupon API", description = "쿠폰 관련 API")
public interface ICouponController {

    @Operation(summary = "쿠폰 발급 API", description = "사용자에게 쿠폰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CouponResponse.class))),
            @ApiResponse(responseCode = "400", description = "쿠폰 수량 부족", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":404,\"message\":\"OUT_OF_STOCK_COUPON\"}"))),
            @ApiResponse(responseCode = "404", description = "사용자 혹은 쿠폰을 찾을 수 없음", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":404,\"message\":\"NOT_FOUND\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":500,\"message\":\"INTERNAL_SERVER_ERROR\"}")))
    })
    ResponseEntity<CouponResponse> issue(@PathVariable Long id, @RequestBody CouponRequest couponRequest);

}
