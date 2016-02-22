package org.hisrc.dbeac.v_1_0.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.hisrc.azw.integration.impl.v_1_0.DefaultApiFacilityToFacilityStateReportConverter;
import org.hisrc.azw.model.FacilityStateReport;
import org.hisrc.azw.service.FacilityStateChangedEventService;
import org.hisrc.dbeac.client.v_1_0.model.Facility;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultApiFacilitiesController {

	private DefaultApiFacilityToFacilityStateReportConverter converter = new DefaultApiFacilityToFacilityStateReportConverter();

	private FacilityStateChangedEventService facilityStateChangedEventService;

	public FacilityStateChangedEventService getFacilityStateChangedEventService() {
		return facilityStateChangedEventService;
	}

	@Inject
	public void setFacilityStateChangedEventService(FacilityStateChangedEventService facilityStateChangedEventService) {
		this.facilityStateChangedEventService = facilityStateChangedEventService;
	}

	@CrossOrigin(origins = { "*" })
	@RequestMapping(value = "/api/v1.0/facilities", method = RequestMethod.PUT)
	@ResponseBody
	public void findByEquipmentnumber(@RequestBody Facility[] facilities) throws IOException {
		Validate.noNullElements(facilities);
		final List<FacilityStateReport> facilityStateReports = new ArrayList<>(facilities.length);
		for (Facility facility : facilities) {
			final FacilityStateReport facilityStateReport = this.converter.asFacilityStateReport(facility);
			facilityStateReports.add(facilityStateReport);
		}
		getFacilityStateChangedEventService().processFacilityStateReports(facilityStateReports);
	}

}
