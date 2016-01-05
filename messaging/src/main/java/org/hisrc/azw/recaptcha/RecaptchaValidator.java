package org.hisrc.azw.recaptcha;

import java.io.IOException;

public interface RecaptchaValidator {

	public boolean isValid(String token) throws IOException;

}
