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

FourSquareClient.prototype.getCheckinsClient = function()
{
	var client = this;
	return {
		CHECKINS_URL: "https://api.foursquare.com/v2/checkins/{checkin_id}",

		ADD_URL: "https://api.foursquare.com/v2/checkins/add",

		RECENT_URL: "https://api.foursquare.com/v2/checkins/recent",

		ADD_COMMENT_URL: "https://api.foursquare.com/v2/checkins/{checkin_id}/addcomment",

		DELETE_COMMENT_URL: "https://api.foursquare.com/v2/checkins/{checkin_id}/deletecomment",
			
		checkins: function(checkinId, parameters, requestCallback)
		{
//			var parameters = {
//				signature: signature
//			}
		
			var requestUrl = this.CHECKINS_URL.replace("{checkin_id}", checkinId) + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		add: function(parameters, requestCallback)
		{
//			var parameters = {
//				venueId,
//				venue,
//				shout,
//				accuracy,
//				broadcast,
//				ll,
//				llAcc,
//				alt,
//				altAcc
//			}
			
			var requestUrl = this.ADD_URL + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback, "POST");
		},
		
		recent: function(parameters, requestCallback)
		{
//			var parameters = {
//				ll,
//				limit,
//				afterTimestamp
//			}
			
			var requestUrl = this.RECENT_URL + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);

			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		addcomment: function(checkinId, parameters, requestCallback)
		{
//			var parameters = {
//				text
//			}
			
			var requestUrl = this.ADD_COMMENT_URL.replace("{checkin_id}", checkinId) + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback, "POST");
		},
			
		deletecomment: function(checkinId, parameters, requestCallback)
		{
//			var parameters = {
//				commentId
//			}
			
			var requestUrl = this.DELETE_COMMENT_URL.replace("{checkin_id}", checkinId) + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback, "POST");
		}
	};
};