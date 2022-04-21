package ru.comgrid.server.security.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.UnknownContentTypeException;

public class UserInfoAuthenticationException extends OAuth2AuthorizationException{
	private static final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";

	public UserInfoAuthenticationException(OAuth2Error error, String message, Throwable cause){
		super(error, message, cause);
	}

	public static UserInfoAuthenticationException of(OAuth2AuthorizationException ex, String userInfoEndpoint){
		OAuth2Error oauth2Error = ex.getError();
		StringBuilder errorDetails = new StringBuilder();
		errorDetails.append("Error details: [");
		errorDetails.append("UserInfo Uri: ")
			.append(userInfoEndpoint);
		errorDetails.append(", Error Code: ").append(oauth2Error.getErrorCode());
		if(oauth2Error.getDescription() != null){
			errorDetails.append(", Error Description: ").append(oauth2Error.getDescription());
		}
		errorDetails.append("]");
		oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
			"An error occurred while attempting to retrieve the UserInfo Resource: " + errorDetails,
			null);
		return new UserInfoAuthenticationException(oauth2Error, oauth2Error.toString(), ex);
	}

	public static OAuth2AuthenticationException of(UnknownContentTypeException ex, String userInfoEndpoint, String registrationId){
		String errorMessage = "An error occurred while attempting to retrieve the UserInfo Resource from '"
			+ userInfoEndpoint
			+ "': response contains invalid content type '" + ex.getContentType() + "'. "
			+ "The UserInfo Response should return a JSON object (content type 'application/json') "
			+ "that contains a collection of name and value pairs of the claims about the authenticated End-User. "
			+ "Please ensure the UserInfo Uri in UserInfoEndpoint for Client Registration '"
			+ registrationId + "' conforms to the UserInfo Endpoint, "
			+ "as defined in OpenID Connect 1.0: 'https://openid.net/specs/openid-connect-core-1_0.html#UserInfo'";
		OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE, errorMessage, null);
		return new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
	}

	public static OAuth2AuthenticationException of(RestClientException ex){
		OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
			"An error occurred while attempting to retrieve the UserInfo Resource: " + ex.getMessage(), null);
		throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
	}
}
