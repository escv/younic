$(function() {
	$('.navbar .nav-item').each(function() {
		var current = $(this);
		if (current.find('> .dropdown-menu > .dropdown-item').length > 0) {
			current.addClass('dropdown');
			current.find('> .nav-link').attr('data-toggle','dropdown').addClass('dropdown-toggle');
		}
	});
})