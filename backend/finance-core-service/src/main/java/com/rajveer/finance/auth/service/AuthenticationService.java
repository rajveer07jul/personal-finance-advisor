package com.rajveer.finance.auth.service;

import com.rajveer.finance.auth.dto.AuthenticationResponse;
import com.rajveer.finance.auth.dto.RegisterRequest;

public interface AuthenticationService {

    AuthenticationResponse register(RegisterRequest request);
}