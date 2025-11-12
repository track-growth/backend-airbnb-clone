/** 
 * @description JWT 토큰을 저장하는 class
 */

package com.growth.auth.jwt.domain;

import lombok.Value;

@Value
public class EncodedToken {
  String value;

  public static EncodedToken from(String token) {
    return new EncodedToken(token);
  }
}

