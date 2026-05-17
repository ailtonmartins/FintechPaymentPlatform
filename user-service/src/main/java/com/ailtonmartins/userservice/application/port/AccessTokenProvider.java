package com.ailtonmartins.userservice.application.port;

import com.ailtonmartins.userservice.domain.model.User;

public interface AccessTokenProvider {

    String generate(User user);
}
