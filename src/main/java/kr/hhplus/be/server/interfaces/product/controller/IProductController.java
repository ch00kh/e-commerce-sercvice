package kr.hhplus.be.server.interfaces.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.product.dto.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Product API", description = "상품 관련 API 입니다.")
public interface IProductController {

    @Operation(summary = "상품 목록 조회 API", description = "상품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class)))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":500,\"message\":\"INTERNAL_SERVER_ERROR\"}")))
    })
    ResponseEntity<ProductResponse.ProductList> findAll();

    @Operation(summary = "상품 정보 조회 API", description = "요청한 상품 id의 상품 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 정보 조회 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":404,\"message\":\"NOT_FOUND\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":500,\"message\":\"INTERNAL_SERVER_ERROR\"}")))
    })
    ResponseEntity<ProductResponse.ProductAggregate> findProduct(@PathVariable Long id);

    @Operation(summary = "인기 상품 조회 API", description = "3일간 상위 5개 판매량이 높은 상품 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 정보 조회 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"code\":500,\"message\":\"INTERNAL_SERVER_ERROR\"}")))
    })
    ResponseEntity<ProductResponse.ProductList> findBest(
            @RequestParam(value = "days", defaultValue = "3") Integer days,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit
    );

}
