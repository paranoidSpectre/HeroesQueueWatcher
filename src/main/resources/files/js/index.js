$(document).ready(function () {
	
	var pollingtimer = undefined;
    
	var numberOfPeriods = 0;
	
	$('#toggle').click(function () {
		togglePolling();
	});
	
	function togglePolling() {
		if (!pollingtimer) {
			$('#airhorn')[0].muted = true;
			$("#airhorn").trigger('play').trigger('pause').prop("currentTime", 0);
        	$("body").css("background-color", "white");
			pollingtimer = setInterval(function () {
                $.get('/queuestate', function (data) {
                    data = data == 'true';
                    if (data == true) {
            			$('#airhorn')[0].muted = false;
                    	$('#airhorn').trigger('play');
                    	$("body").css("background-color", "lime");
                    	togglePolling();
                    }
                });
                numberOfPeriods++;
                if (numberOfPeriods > 3) {
                	numberOfPeriods = 0;
                }
                var text = "Watching";
                for (var i = 0; i < numberOfPeriods; i++) {
                	text += ".";
                }
                $('#status').text(text);
            }, 1000);
            $('#status').text("Watching");
            $('#toggle').html("Stop<br>Monitoring");
        } else {
            clearInterval(pollingtimer);
            pollingtimer = undefined;
            numberOfPeriods = 0;
            $('#status').text("Sleeping");
            $('#toggle').html("Start<br>Monitoring");
        }
	}
	
});