package org.hisrc.azw.controller;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.hisrc.azw.dto.FacilityStateReportFeature;
import org.hisrc.azw.service.FacilityStateReportFeatureService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FacilitiesController {

	private FacilityStateReportFeatureService facilityStateReportFeatureService;

	public FacilityStateReportFeatureService getFacilityStateReportFeatureService() {
		return facilityStateReportFeatureService;
	}

	@Inject
	public void setFacilityStateReportFeatureService(
			FacilityStateReportFeatureService facilityStateReportFeatureService) {
		this.facilityStateReportFeatureService = facilityStateReportFeatureService;
	}

	@CrossOrigin(origins = { "*" })
	@RequestMapping(value = "/facilities", method = RequestMethod.GET)
	@ResponseBody
	public List<FacilityStateReportFeature> findAll(
			@RequestParam(value = "updatedSince", required = false) final Long timestamp) throws IOException {

		if (timestamp == null) {
			return getFacilityStateReportFeatureService().findAll();
		} else {
			return getFacilityStateReportFeatureService().findAllUpdatedSince(timestamp);
		}
	}
}
