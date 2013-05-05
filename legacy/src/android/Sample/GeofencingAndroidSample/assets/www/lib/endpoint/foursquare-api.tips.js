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

FourSquareClient.prototype.getTipsClient = function()
{
	var client = this;
	return {
		TIPS_URL: "https://api.foursquare.com/v2/tips/{tip_id}",

		ADD_URL: "https://api.foursquare.com/v2/tips/add",

		SEARCH_URL: "https://api.foursquare.com/v2/tips/search",

		MARK_TODO_URL: "https://api.foursquare.com/v2/tips/{tip_id}/marktodo",

		MARK_DONE_URL: "https://api.foursquare.com/v2/tips/{tip_id}/markdone",

		UNMARK_URL: "https://api.foursquare.com/v2/tips/{tip_id}/unmark",
	
		tips: function(tipId, requestCallback)
		{
			var requestUrl = this.TIPS_URL.replace("{tip_id}", tipId) + client.requestQuery();
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		add: function(parameters, requestCallback)
		{
//			var parameters = {
//				venueId: venueId,
//				text: text,
//				url: url
//			}
			
			var requestUrl = this.ADD_URL + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback, "POST");
		},
		
		search: function(parameters, requestCallback)
		{
//			var parameters = {
//				ll,
//				limit,
//				offset,
//				filter,
//				query
//			}
			
			var requestUrl = this.SEARCH_URL + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		marktodo: function(tipId, requestCallback)
		{
			var requestUrl = this.MARK_TODO_URL.replace("{tip_id}", tipId) + client.requestQuery();
			
			FourSquareUtils.doRequest(requestUrl, requestCallback, "POST");
		},
		
		markdone: function(tipId, requestCallback)
		{
			var requestUrl = this.MARK_DONE_URL.replace("{tip_id}", tipId) + client.requestQuery();
			
			FourSquareUtils.doRequest(requestUrl, requestCallback, "POST");
		},
		
		unmark: function(tipId, requestCallback)
		{
			var requestUrl = this.UNMARK_URL.replace("{tip_id}", tipId) + client.requestQuery();
			
			FourSquareUtils.doRequest(requestUrl, requestCallback, "POST");
		}
	};
};