AUI().ready(function(A) {
	if(productionEnvId)
		showDevEnvPanel();
});

function showDevEnvPanel() {
	if (document.getElementById(productionEnvId).value === "true") {
		document.getElementById(devEnvPrefsId).style.display = 'none';
	} else {
		document.getElementById(devEnvPrefsId).style.display = 'block';
	}
}