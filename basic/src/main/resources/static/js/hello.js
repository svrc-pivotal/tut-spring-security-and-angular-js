angular.module('hello', []).controller('home', function($http) {
	var self = this;

	$http.get('features/').then(function(response) {
		self.features = response.data;
	})

	$http.get('features/c').then(function(response) {
		self.feature_c = response.data;
	})

	$http.get('resource/').then(function(response) {
		if (self.features.show_greeting == true) {
			self.greeting = response.data;
		}
		else {
			self.greeting = { "id": "default", "content": "((Show Greeting disabled))" };
		}
	})


});
