package dev.ngb.app.profile.support;

import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileRequest;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileResponse;
import dev.ngb.app.support.HttpJsonClient;
import dev.ngb.web.ErrorResponse;
import io.vavr.control.Either;
import org.springframework.http.HttpHeaders;

/**
 * Composable HTTP client for {@code /api/profiles}. {@code Left} = {@link ErrorResponse}.
 */
public final class ProfileApiClient {

    private static final String PROFILES_ENDPOINT = "/profiles";

    private final HttpJsonClient json;

    public ProfileApiClient(HttpJsonClient json) {
        this.json = json;
    }

    public Either<ErrorResponse, CreateProfileResponse> createProfile(CreateProfileRequest body) {
        return json.post(PROFILES_ENDPOINT, body, CreateProfileResponse.class);
    }

    public Either<ErrorResponse, CreateProfileResponse> createProfile(
            CreateProfileRequest body,
            HttpHeaders headers
    ) {
        return json.post(PROFILES_ENDPOINT, body, headers, CreateProfileResponse.class);
    }
}
