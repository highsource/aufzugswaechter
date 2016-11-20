package org.hisrc.azw.integration.impl.v_1_0;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.hisrc.azw.integration.FacilityStateReportDataAccess;
import org.hisrc.azw.model.FacilityState;
import org.hisrc.azw.model.FacilityStateReport;
import org.hisrc.azw.model.FacilityType;
import org.hisrc.fasta.client.v1.api.DefaultApi;
import org.hisrc.fasta.client.v1.invoker.ApiException;

public class DefaultApiFacilityStateReportDataAccess implements FacilityStateReportDataAccess {

	private DefaultApiFacilityToFacilityStateReportConverter converter = new DefaultApiFacilityToFacilityStateReportConverter();

	private DefaultApi api;

	@SuppressWarnings("unused")
	private DefaultApiFacilityStateReportDataAccess() {
	}

	public DefaultApiFacilityStateReportDataAccess(DefaultApi api) {
		Validate.notNull(api);
		this.api = api;
	}

	public DefaultApi getApi() {
		return api;
	}

	public void setApi(DefaultApi api) {
		this.api = api;
	}

	@Override
	public Iterable<FacilityStateReport> findAll() throws IOException {
		return findByFacilityTypesAndFacilityStates(null, null);
	}

	@Override
	public Iterable<FacilityStateReport> findByFacilityStates(Iterable<FacilityState> facilityStates)
			throws IOException {
		return findByFacilityTypesAndFacilityStates(null, facilityStates);
	}

	private Iterable<FacilityStateReport> findByFacilityTypesAndFacilityStates(Iterable<FacilityType> facilityTypes,
			Iterable<FacilityState> facilityStates) throws IOException {

		final List<String> types;
		if (facilityTypes == null) {
			types = null;
		} else {
			types = new LinkedList<String>();
			for (FacilityType facilityType : facilityTypes) {
				types.add(facilityType.name());
			}
		}
		final List<String> states;
		if (facilityStates == null) {
			states = null;
		} else {
			states = new LinkedList<String>();
			for (FacilityState facilityState : facilityStates) {
				states.add(facilityState.name());
			}
		}

		try {
			final List<org.hisrc.fasta.client.v1.model.Facility> fs = getApi().findFacilities(types, states, null, null, null);
			final List<FacilityStateReport> facilityStateReports = new ArrayList<>(fs.size());
			for (org.hisrc.fasta.client.v1.model.Facility f : fs) {
				facilityStateReports.add(converter.asFacilityStateReport(f));
			}
			return facilityStateReports;
		} catch (ApiException cause) {
			throw new IOException("Error retrieving facilities.", cause);
		}
	}

	@Override
	public FacilityStateReport findByEquipmentnumber(long equipmentnumber) throws IOException {
		try {
			org.hisrc.fasta.client.v1.model.Facility f = getApi().getFacilityByEquipmentNumber(equipmentnumber);
			return converter.asFacilityStateReport(f);
		} catch (ApiException apiex) {
			// TODO distinguish not found vs. io ex
			return null;
		}
	}

}
