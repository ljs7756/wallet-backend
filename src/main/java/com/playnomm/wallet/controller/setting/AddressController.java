package com.playnomm.wallet.controller.setting;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.setting.request.AddressPatchRequestDTO;
import com.playnomm.wallet.dto.setting.request.AddressRequestDTO;
import com.playnomm.wallet.dto.setting.response.AddressResponseDTO;
import com.playnomm.wallet.dto.setting.response.MainNetResponseDTO;
import com.playnomm.wallet.dto.setting.response.RecentAddressResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.service.setting.AddressService;
import com.playnomm.wallet.util.AuthenticationUtil;
import com.playnomm.wallet.util.BeanValidationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.controller.setting
 * fileName : AddressController
 * author :  ljs7756
 * date : 2022-12-19
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-19                ljs7756             최초 생성
 */
@Tag(name = "주소록", description = "")
@RestController
@RequestMapping("/api/v1/address")
public class AddressController {

    private AddressService addressService;

    public AddressController(AddressService service) {
        this.addressService = service;
    }
    @Operation(summary = "주소록 가져오기", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = AddressResponseDTO.class)))
    @GetMapping("/{userSn}")
    public ResponseEntity<Object> getAddressList(@Parameter(description = "사용자 id", in= ParameterIn.PATH) @PathVariable int userSn) {
        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        return ResponseEntity.ok().body (addressService.getAddressList(userSn));
    }

    @Operation(summary = "네트워크 리스트 가져오기", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = MainNetResponseDTO.class)))
    @GetMapping("/netList")
    public ResponseEntity<Object> getMainNetList() {
        return ResponseEntity.ok().body (addressService.getMainNetList());
    }

    @Operation(summary = "주소록 가져오기", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = AddressResponseDTO.class)))
    @GetMapping("/{userSn}/{symbol}")
    public ResponseEntity<Object> searchAddressBySymbol(@Parameter(description = "사용자 id", in= ParameterIn.PATH) @PathVariable int userSn,
                                                          @Parameter(description = "symbol", in= ParameterIn.PATH) @PathVariable String symbol) {

        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);
        //20220970
        return ResponseEntity.ok().body (addressService.searchAddressBySymbol(userSn, symbol));
    }

    @Operation(summary = "주소록 등록하기", description = "주소록 등록 시 상용되는 파라메터 usePurpsCode의 값은 F(FT), N(NFT)을 사용한다.")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = AddressResponseDTO.class)))
    @PostMapping("")
    public ResponseEntity<Object> postAddress(@RequestBody @Valid AddressRequestDTO params, Errors errors) {
        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(params.getUserSn());

        if (errors.hasErrors ()) {
            return ResponseEntity.ok ().body (new ResultDTO<>(StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
        }

        return ResponseEntity.ok().body (addressService.postAddress(params));
    }

    @Operation(summary = "주소록 삭제하기", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation")
    @DeleteMapping("/{userCxwaletAdbkSn}")
    public ResponseEntity<Object> deleteAddress(@Parameter(description = "삭제 할 주소록 고유번호", in= ParameterIn.PATH) @PathVariable int userCxwaletAdbkSn) {
        return ResponseEntity.ok().body (addressService.deleteAddress(userCxwaletAdbkSn));
    }

    @Operation(summary = "주소록 수정하기", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = AddressResponseDTO.class)))
    @PatchMapping("/{userCxwaletAdbkSn}")
    public ResponseEntity<Object> patchAddress(@Parameter(description = "수정 할 주소록 고유번호", in= ParameterIn.PATH) @PathVariable int userCxwaletAdbkSn,
                                               @RequestBody AddressPatchRequestDTO body) {
        return ResponseEntity.ok().body (addressService.patchAddress(userCxwaletAdbkSn, body));
    }

    @Operation(summary = "토큰 최근 주소록", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = RecentAddressResponseDTO.class)))
    @GetMapping("/token/{userSn}/{blcNtwrkId}/{cxSymbolCode}")
    public ResponseEntity<Object> recentAddressForToken(@Parameter(description = "사용자 id", in=ParameterIn.PATH) @PathVariable Integer userSn,
                                                        @Parameter(description = "메인넷", in=ParameterIn.PATH,schema = @Schema(allowableValues = {"1000","1001","1002"})) @PathVariable String blcNtwrkId,
                                                        @Parameter(description = "심볼 코드", in=ParameterIn.PATH, example = "LM") @PathVariable String cxSymbolCode) {

        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        Map<String, Object> params = new HashMap<>();
        params.put("userSn", userSn);
        params.put("blcNtwrkId", blcNtwrkId);
        params.put("cxSymbolCode", cxSymbolCode);

        return ResponseEntity.ok().body (addressService.getRecentAddressForToken(params));
    }

    @Operation(summary = "NFT 최근 주소록", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = RecentAddressResponseDTO.class)))
    @GetMapping("/nft/{userSn}")
    public ResponseEntity<Object> recentAddressForToken(@Parameter(description = "사용자 id", in=ParameterIn.PATH) @PathVariable Integer userSn) {

        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        return ResponseEntity.ok().body (addressService.getRecentAddressForNft(userSn));
    }
}
