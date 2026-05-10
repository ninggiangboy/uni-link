package dev.ngb.app.profile.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ngb.app.profile.application.dto.ProfileSettingResponse;
import dev.ngb.app.profile.application.dto.ProfileSummary;
import dev.ngb.app.profile.application.usecase.profile.create_profile.dto.CreateProfileRequest;
import dev.ngb.app.profile.application.usecase.profile.create_profile.dto.CreateProfileResponse;
import dev.ngb.app.profile.application.usecase.profile.update_profile.dto.UpdateProfileRequest;
import dev.ngb.app.profile.application.usecase.profile.update_profile_setting.dto.UpdateProfileSettingRequest;
import dev.ngb.app.support.HttpJsonClient;
import dev.ngb.web.ErrorResponse;
import io.vavr.control.Either;
import org.springframework.http.HttpHeaders;

import java.util.Map;

/**
 * Composable HTTP client for {@code /api/app/profiles} and profile GraphQL reads. {@code Left} = {@link ErrorResponse}.
 */
public final class ProfileApiClient {

    private static final String PROFILES_ENDPOINT = "/profiles";
    private static final String GRAPHQL_ENDPOINT = "/graphql";

    private static final String QUERY_MY_PROFILE = """
            query MyProfile {
              myProfile {
                profileUuid
                username
                displayName
                bio
                website
                location
                avatarUrl
                bannerUrl
                visibility
                isVerified
                followerCount
                followingCount
                threadCount
                likeCount
                createdAt
              }
            }
            """;

    private static final String QUERY_MY_SETTINGS = """
            query MySettings {
              mySettings {
                allowMentions
                allowMessages
                allowTagging
                showActivityStatus
              }
            }
            """;

    private final HttpJsonClient json;
    private final ObjectMapper objectMapper;

    public ProfileApiClient(HttpJsonClient json, ObjectMapper objectMapper) {
        this.json = json;
        this.objectMapper = objectMapper;
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

    public Either<ErrorResponse, ProfileSummary> queryMyProfile(HttpHeaders headers) {
        return postGraphQl(QUERY_MY_PROFILE, "myProfile", ProfileSummary.class, headers);
    }

    public Either<ErrorResponse, ProfileSettingResponse> queryMySettings(HttpHeaders headers) {
        return postGraphQl(QUERY_MY_SETTINGS, "mySettings", ProfileSettingResponse.class, headers);
    }

    public Either<ErrorResponse, ProfileSummary> updateProfile(UpdateProfileRequest body, HttpHeaders headers) {
        return json.patch(PROFILES_ENDPOINT + "/me", body, headers, ProfileSummary.class);
    }

    public Either<ErrorResponse, ProfileSettingResponse> updateSettings(
            UpdateProfileSettingRequest body,
            HttpHeaders headers
    ) {
        return json.patch(PROFILES_ENDPOINT + "/me/settings", body, headers, ProfileSettingResponse.class);
    }

    private <T> Either<ErrorResponse, T> postGraphQl(
            String query,
            String dataField,
            Class<T> type,
            HttpHeaders headers
    ) {
        try {
            return json.postJson(GRAPHQL_ENDPOINT, Map.of("query", query), headers)
                    .flatMap(raw -> parseGraphQlResponse(raw.getBody(), dataField, type));
        } catch (RuntimeException e) {
            return Either.left(ErrorResponse.of("GRAPHQL_CLIENT_ERROR", e.getMessage()));
        }
    }

    private <T> Either<ErrorResponse, T> parseGraphQlResponse(String body, String dataField, Class<T> type) {
        if (body == null || body.isBlank()) {
            return Either.left(ErrorResponse.of("EMPTY_GRAPHQL_BODY", "Empty GraphQL response"));
        }
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode errors = root.get("errors");
            if (errors != null && errors.isArray() && errors.size() > 0) {
                JsonNode first = errors.get(0);
                JsonNode ext = first.get("extensions");
                String code = ext != null && ext.hasNonNull("error")
                        ? ext.get("error").asText()
                        : "GRAPHQL_ERROR";
                String message = first.hasNonNull("message")
                        ? first.get("message").asText()
                        : "GraphQL error";
                return Either.left(ErrorResponse.of(code, message));
            }
            JsonNode dataNode = root.get("data");
            if (dataNode == null || !dataNode.has(dataField)) {
                return Either.left(ErrorResponse.of("GRAPHQL_NO_DATA", "Missing data." + dataField));
            }
            return Either.right(objectMapper.treeToValue(dataNode.get(dataField), type));
        } catch (Exception e) {
            return Either.left(ErrorResponse.of(
                    "GRAPHQL_PARSE_ERROR",
                    "Failed to parse GraphQL response: " + e.getMessage()
            ));
        }
    }
}
