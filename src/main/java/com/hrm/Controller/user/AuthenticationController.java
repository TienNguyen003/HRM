package com.hrm.Controller.user;

import com.hrm.Service.user.AuthenticationService;
import com.hrm.dto.request.token.IntrospectRequest;
import com.hrm.dto.request.token.RefreshRequest;
import com.hrm.dto.request.user.AuthenticationRequest;
import com.hrm.dto.request.user.LogoutRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.user.AuthenticationResponse;
import com.hrm.dto.response.user.IntrospectResponse;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("${api.prefix}auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authentication(@RequestBody AuthenticationRequest authenticationRequest){
        var result = authenticationService.authentication(authenticationRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest introspectRequest)
            throws ParseException, JOSEException {
        var result = authenticationService.introspectResponse(introspectRequest);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authentication(@RequestBody RefreshRequest request) throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }
}
