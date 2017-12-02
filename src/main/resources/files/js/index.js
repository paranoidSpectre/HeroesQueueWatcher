$(document).ready(function () {
	
	var pollingtimer = undefined;
	var audio = new Audio("/sfx/airhorn.mp3") ;
    
	var numberOfPeriods = 0;
	
	$('#toggle').click(function () {
		togglePolling();
	});
	
	function togglePolling() {
		if (!pollingtimer) {
        	$("body").css("background-color", "white");
			pollingtimer = setInterval(function () {
                $.get('/queuestate', function (data) {
                    data = data == 'true';
                    if (data == true) {
                    	audio.play();
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
                $('#toggle').html("Stop<br>Monitoring");
            }, 1000);
        } else {
            clearInterval(pollingtimer);
            pollingtimer = undefined;
            numberOfPeriods = 0;
            $('#status').text("Sleeping");
            $('#toggle').html("Start<br>Monitoring");
        }
	}
	
});