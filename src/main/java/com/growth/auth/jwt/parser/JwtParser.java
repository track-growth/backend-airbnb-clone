package com.growth.auth.jwt.parser;

import com.growth.auth.jwt.domain.EncodedToken;
import io.jsonwebtoken.Claims;

public interface JwtParser {
    Claims parseToken(EncodedToken token);
}