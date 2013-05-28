/*
The MIT License

Copyright (c) 2011 M.F.A. ten Veldhuis

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

FourSquareClient.prototype.getUsersClient = function()
{
	var client = this;
	return {
		USERS_URL: "https://api.foursquare.com/v2/users/{user_id}",	

		SEARCH_URL: "https://api.foursquare.com/v2/users/search",

		REQUESTS_URL: "https://api.foursquare.com/v2/users/requests",	

		BADGES_URL: "https://api.foursquare.com/v2/users/{user_id}/badges",

		CHECKINS_URL: "https://api.foursquare.com/v2/users/{user_id}/checkins",

		FRIENDS_URL: "https://api.foursquare.com/v2/users/{user_id}/friends",

		TIPS_URL: "https://api.foursquare.com/v2/users/{user_id}/tips",

		TODOS_URL: "https://api.foursquare.com/v2/users/{user_id}/todos",

		VENUE_HISTORY_URL: "https://api.foursquare.com/v2/users/{user_id}/venuehistory",	

		REQUEST_URL: "https://api.foursquare.com/v2/users/{user_id}/request",

		UNFRIEND_URL: "https://api.foursquare.com/v2/users/{user_id}/unfriend",

		APPROVE_URL: "https://api.foursquare.com/v2/users/{user_id}/approve",

		DENY_URL: "https://api.foursquare.com/v2/users/{user_id}/deny",

		SET_PINGS_URL: "https://api.foursquare.com/v2/users/{user_id}/setpings",
			
		users: function(userId, requestCallback)
		{
			var requestUrl = this.USERS_URL.replace("{user_id}", userId) + client.requestQuery();
		
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},	
		
		search: function(parameters, requestCallback)
		{
//			var parameters = {
//				phone,
//				email,
//				twitter,
//				twitterSource,
//				fbid,
//				name
//			}
			
			var requestUrl = this.SEARCH_URL + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		requests: function(requestCallback)
		{
			var requestUrl = this.REQUESTS_URL + client.requestQuery();
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		badges: function(userId, parameters, requestCallback)
		{
//			var parameters = {
//				sets,
//				badges
//			}
			
			var requestUrl = this.BADGES_URL.replace("{user_id}", userId) + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		checkins: function(userId, parameters, requestCallback)
		{
//			var parameters = {
//				limit,
//				offset,
//				afterTimestamp,
//				beforeTimestamp
//			}
			
			var requestUrl = this.CHECKINS_URL.replace("{user_id}", userId) + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		friends: function(userId, parameters, requestCallback)
		{
//			var parameters = {
//				limit,
//				offset
//			}
			
			var requestUrl = this.FRIENDS_URL.replace("{user_id}", userId) + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		tips: function(userId, parameters, requestCallback)
		{
//			var parameters = {
//				sort,
//				ll,
//				limit,
//				offset
//			}
			
			var requestUrl = this.TIPS_URL.replace("{user_id}", userId) + client.requestQuery();			
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		todos: function(userId, parameters, requestCallback)
		{
//			var parameters = {
//				sort,
//				ll
//			}
			
			var requestUrl = this.TODOS_URL.replace("{user_id}", userId) + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		venuehistory: function(userId, parameters, requestCallback)
		{
//			var parameters = {
//				afterTimestamp,
//				beforeTimestamp
//			}
			
			var requestUrl = this.VENUE_HISTORY_URL.replace("{user_id}", userId) + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);

			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},	
		
		request: function(userId, requestCallback)
		{
			var requestUrl = this.REQUEST_URL.replace("{user_id}", userId) + client.requestQuery();
			
			FourSquareUtils.doRequest(requestUrl, requestCallback, "POST");
		},
		
		unfriend: function(userId, requestCallback)
		{
			var requestUrl = this.UNFRIEND_URL.replace("{user_id}", userId) + client.requestQuery();
			
			FourSquareUtils.doRequest(requestUrl, requestCallback, "POST");
		},
		
		approve: function(userId, requestCallback)
		{
			var requestUrl = this.APPROVE_URL.replace("{user_id}", userId) + client.requestQuery();
			
			FourSquareUtils.doRequest(requestUrl, requestCallback, "POST");
		},
		
		deny: function(userId, requestCallback)
		{
			var requestUrl = this.DENY_URL.replace("{user_id}", userId) + client.requestQuery();
			
			FourSquareUtils.doRequest(requestUrl, requestCallback, "POST");
		},
		
		setpings: function(userId, parameters, requestCallback)
		{
//			var parameters = {
//				value: value
//			}
			
			var requestUrl = this.DENY_URL.replace("{user_id}", userId) + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback, "POST");
		}
	};
};