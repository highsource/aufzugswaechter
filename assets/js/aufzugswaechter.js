var map = L.map('map').setView([51.5, 10.5], 6);

L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1Ijoib3JsZXNzIiwiYSI6ImNpaXg4cGt2cTAwMGh2Mm01ZDlqYnk5N28ifQ.EDKxdytV0xTiHyI16K0zsg', {
	maxZoom: 18,
	attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
		'<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
		'Imagery &copy; <a href="http://mapbox.com">Mapbox</a>',
	id: 'mapbox.streets'
}).addTo(map);

var markers = L.markerClusterGroup();

map.addLayer(markers);

$.getJSON("http://api.aufzugswaechter.org/facilities", function(features) {
	for (var index = 0; index < features.length; index++)
	{
		var feature = features[index];
		if (feature.geometry)
		{
			var since = new Date(feature.properties.facilityStateKnownSince);
			var popupText = "<b>Station:</b> " + feature.properties.stationname + "<br/>" +
					"<b>Facility:</b> " + feature.properties.facilityDescription + "<br/>" + 
					"<b>Facility type:</b> " + feature.properties.facilityType + "<br/>" + 
					"<b>Facility state:</b> " + feature.properties.facilityState + "<br/>" +
					"<b>Facility state known since</b> " + since.toLocaleDateString() + " " + since.toLocaleTimeString() + "<br/>" +
					"<a style=\"cursor:pointer;\" onclick=\"showSubscribeModal(" + feature.properties.facilityEquipmentnumber + ")\"><b>Subscribe to facility state updates</b></a><br/>";
			var icon;
			if (feature.properties.facilityState === 'INACTIVE' || feature.properties.facilityState === 'TEST_INACTIVE') {
				icon = L.AwesomeMarkers.icon({ prefix: 'fa', icon: 'arrows-v', markerColor: 'red'});
			} else if (feature.properties.facilityState === 'ACTIVE' || feature.properties.facilityState === 'TEST_ACTIVE') {
				icon = L.AwesomeMarkers.icon({ prefix: 'fa', icon: 'arrows-v', markerColor: 'green'});
			} else {
				icon = L.AwesomeMarkers.icon({ prefix: 'fa', icon: 'arrows-v', markerColor: 'orange'});
			}
			var marker = L.marker([feature.geometry.coordinates[1], feature.geometry.coordinates[0]], {icon: icon}).bindPopup(popupText);

			markers.addLayer(marker);
		}
	}
});

var showSubscribeModal = function(equipmentnumber)
{
	$.getJSON("http://api.aufzugswaechter.org/facilities/" + equipmentnumber, function(feature) {
		$("#subscribe-stationName").text(feature.properties.stationname);
		$("#subscribe-facilityDescription").text(feature.properties.facilityDescription);
		$("#subscribe-facilityEquipmentnumber").val(feature.properties.facilityEquipmentnumber);
	});

	$("#subscribeModal").modal("show");
	//$(".navbar-collapse.in").collapse("hide");
	return false;
}

var subscribe = function()
{
	var equipmentnumber = $("#subscribe-facilityEquipmentnumber").val();
	var email = $("#subscribe-email").val();
	var grecaptchaResponse = grecaptcha.getResponse();
	grecaptcha.reset();
	$.ajax({
		type: "PUT",
		url: "http://api.aufzugswaechter.org/facilities/" + equipmentnumber + "/subscriptions/email/" + encodeURIComponent(email) + "/?token=" + encodeURIComponent(grecaptchaResponse),
		contentType: "application/json"});
	$("#subscribeModal").modal("hide");
	return false;
}

$("#about-btn").click(function() {
  $("#aboutModal").modal("show");
  $(".navbar-collapse.in").collapse("hide");
  return false;
});
