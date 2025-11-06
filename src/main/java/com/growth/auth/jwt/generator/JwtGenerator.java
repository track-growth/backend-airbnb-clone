package com.growth.auth.jwt.generator;

import com.growth.auth.jwt.domain.UserIdentity;
import com.growth.auth.jwt.domain.EncodedToken;
import com.growth.auth.jwt.domain.TokenType;

public interface JwtGenerator {
    EncodedToken generateToken(UserIdentity userIdentity, TokenType tokenType);
}
