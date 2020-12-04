package com.venesa.gateway.controller;

import com.venesa.common.DTO.ResponseData;
import com.venesa.common.Utils.ConstantsUtil;
import com.venesa.gateway.component.WrapperResponseData;
import com.venesa.gateway.dto.JwtResponse;
import com.venesa.gateway.dto.UserDTO;
import com.venesa.gateway.service.JwtUserDetailsService;
import com.venesa.gateway.utils.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Date;

@RestController
@CrossOrigin
public class JwtAuthenticationController {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationController.class);

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    private final JwtUserDetailsService jwtUserDetailsService;

    private final WrapperResponseData wrapperResponse;

    @Value("${jwt.timetoken}")
    private String jwt_token_validity;

    @Autowired
    public JwtAuthenticationController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, JwtUserDetailsService jwtUserDetailsService, WrapperResponseData wrapperResponse) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.wrapperResponse = wrapperResponse;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> generateAuthenticationToken(@RequestBody UserDTO rq, HttpServletRequest request)
            throws Exception {
        ResponseEntity<?> responseEntity;
        authenticate(rq.getUsername(), rq.getPassword());
        try {
            final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(rq.getUsername());
            Date date = jwtUserDetailsService.getTimeToken(rq.getUsername());
            if (date != null && date.after((new Date()))) {
                responseEntity = wrapperResponse.error(
                        new ResponseData<>(ConstantsUtil.ERROR, ConstantsUtil.LOGGIN, null), HttpStatus.BAD_REQUEST);
            } else {
                long timeToken = new Date().getTime() + Long.parseLong(jwt_token_validity) * 1000;
                jwtUserDetailsService.updateTimeTokenByUsername(rq.getUsername(), new Date(timeToken));
                final String token = jwtTokenUtil.generateToken(userDetails);
                responseEntity = wrapperResponse
                        .success(new ResponseData<>(ConstantsUtil.SUCCSESS, ConstantsUtil.SUCCSESS_MESS, new JwtResponse(token)));
            }
        } catch (Exception e) {
            responseEntity = wrapperResponse.error(
                    new ResponseData<>(ConstantsUtil.ERROR, e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(Principal principal) throws Exception {
        ResponseEntity<?> responseEntity;
        try {
            Date date = jwtUserDetailsService.getTimeToken(principal.getName());
            if (date != null) {
                jwtUserDetailsService.updateTimeTokenByUsername(principal.getName(), null);
                responseEntity = new ResponseEntity<>(new ResponseData<>(ConstantsUtil.SUCCSESS, ConstantsUtil.SUCCSESS_MESS, null), HttpStatus.OK);
            } else {
                responseEntity = new ResponseEntity<>(new ResponseData<>(ConstantsUtil.ERROR, ConstantsUtil.ERR_BUSINESS, null), HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            log.info("===== err ==" + e.getMessage());
            responseEntity = new ResponseEntity<>(new ResponseData<>(ConstantsUtil.ERROR, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }

        return responseEntity;
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS" + e.getMessage(), e);
        }
    }
}
