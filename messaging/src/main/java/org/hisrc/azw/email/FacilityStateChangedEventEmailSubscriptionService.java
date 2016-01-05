package org.hisrc.azw.email;

public interface FacilityStateChangedEventEmailSubscriptionService {

	public void subscribeForAllFacilities(String email);
	
	public void subscribeForFacility(Long equipmentnumber, String email);
}
