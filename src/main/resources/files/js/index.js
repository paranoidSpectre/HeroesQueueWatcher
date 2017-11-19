$(document).ready(function () {

	var timer = undefined;
	var audio = new Audio("/sfx/airhorn.mp3") ;

	$('#toggle').click(function () {
		togglePolling();
	});

	function togglePolling() {
		if (!timer) {
            timer = setInterval(function () {
                $.get('/queuestate', function (data) {
                    data = data == 'true';
                    if (data == true) {
                    	audio.play();
                    	togglePolling();
                    }
                });
            }, 1000);
        } else {
            clearInterval(timer);
            timer = undefined;
        }
	}

});
