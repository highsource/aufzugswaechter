package org.hisrc.azw.email.controller;

import java.io.IOException;
import java.text.MessageFormat;

import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.hisrc.azw.email.FacilityStateChangedEventEmailSubscriptionService;
import org.hisrc.azw.recaptcha.RecaptchaValidator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailSubscriptionController {

	private FacilityStateChangedEventEmailSubscriptionService emailSubscriptionService;

	public FacilityStateChangedEventEmailSubscriptionService getEmailSubscriptionService() {
		return emailSubscriptionService;
	}

	@Inject
	public void setEmailSubscriptionService(
			FacilityStateChangedEventEmailSubscriptionService emailSubscriptionService) {
		this.emailSubscriptionService = emailSubscriptionService;
	}

	private RecaptchaValidator recaptchaValidator;

	public RecaptchaValidator getRecaptchaValidator() {
		return recaptchaValidator;
	}

	@Inject
	public void setRecaptchaValidator(RecaptchaValidator recaptchaValidator) {
		this.recaptchaValidator = recaptchaValidator;
	}

	@CrossOrigin(origins = { "*" })
	@RequestMapping(value = "/facilities/subscriptions/email/{email}/", method = RequestMethod.PUT)
	@ResponseBody
	public void subscribe(@PathVariable(value = "email") String email,
			@RequestParam(name = "token", required = true) String token) {
		Validate.notNull(email);
		Validate.notNull(token);
		try {
			if (getRecaptchaValidator().isValid(token)) {
				getEmailSubscriptionService().subscribeForAllFacilities(email);
			} else {
				throw new IllegalArgumentException(MessageFormat.format("Invalid token: [{0}].", token));
			}
		} catch (IOException ioex) {
			throw new IllegalArgumentException(MessageFormat.format("Invalid token: [{0}].", token), ioex);
		}

	}

	@CrossOrigin(origins = { "*" })
	@RequestMapping(value = "/facilities/{equipmentnumber}/subscriptions/email/{email}/", method = RequestMethod.PUT)
	@ResponseBody
	public void subscribe(@PathVariable(value = "equipmentnumber") Long equipmentnumber,
			@PathVariable(value = "email") String email,
			@RequestParam(name = "token", required = true) String token) {
		Validate.notNull(equipmentnumber);
		Validate.notNull(email);
		Validate.notNull(token);
		try {
			if (getRecaptchaValidator().isValid(token)) {
				getEmailSubscriptionService().subscribeForFacility(equipmentnumber, email);
			} else {
				throw new IllegalArgumentException(MessageFormat.format("Invalid token: [{0}].", token));
			}
		} catch (IOException ioex) {
			throw new IllegalArgumentException(MessageFormat.format("Invalid token: [{0}].", token), ioex);
		}
	}
}
