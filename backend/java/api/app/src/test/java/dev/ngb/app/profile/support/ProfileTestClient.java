package dev.ngb.app.profile.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileRequest;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileResponse;
import dev.ngb.app.support.AbstractIntegrationTest;
import dev.ngb.app.support.RequestJsonClient;
import dev.ngb.web.ErrorResponse;
import io.vavr.control.Either;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

/**
 * Composable HTTP client for {@code /api/profiles}. {@code Left} = {@link ErrorResponse}.
 */
public final class ProfileTestClient {

    private static final String PROFILES_ENDPOINT = "/api/profiles";

    private final RequestJsonClient json;

    public ProfileTestClient(ObjectMapper objectMapper, RestTemplate restTemplate, String baseUrl) {
        this.json = new RequestJsonClient(objectMapper, baseUrl, restTemplate);
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
