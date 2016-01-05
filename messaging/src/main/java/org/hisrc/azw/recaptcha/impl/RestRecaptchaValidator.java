package org.hisrc.azw.recaptcha.impl;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.lang3.Validate;
import org.hisrc.azw.recaptcha.RecaptchaValidator;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RestRecaptchaValidator implements RecaptchaValidator {

	private static class RecaptchaResponse {
		@JsonProperty("success")
		private boolean success;
		@JsonProperty("error-codes")
		private Collection<String> errorCodes;
	}

	private String recaptchaUrl;
	private String recaptchaSiteSecret;
	private RestTemplate restTemplate;

	public String getRecaptchaUrl() {
		return recaptchaUrl;
	}

	public void setRecaptchaUrl(String recaptchaUrl) {
		this.recaptchaUrl = recaptchaUrl;
	}

	public String getRecaptchaSiteSecret() {
		return recaptchaSiteSecret;
	}

	public void setRecaptchaSiteSecret(String recaptchaSiteSecret) {
		this.recaptchaSiteSecret = recaptchaSiteSecret;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public boolean isValid(String token) throws IOException {
		Validate.notNull(token);
		RecaptchaResponse recaptchaResponse;
		try {
			recaptchaResponse = restTemplate
					.postForEntity(recaptchaUrl, createBody(getRecaptchaSiteSecret(), token), RecaptchaResponse.class)
					.getBody();
		} catch (RestClientException rcex) {
			throw new IOException("Recaptcha API not available due to exception", rcex);
		}
		return recaptchaResponse.success;
	}

	private MultiValueMap<String, String> createBody(String secret, String token) {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("secret", secret);
		form.add("response", token);
		return form;
	}

}
