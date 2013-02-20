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

FourSquareClient.prototype.getSpecialsClient = function()
{
	var client = this;
	return {
		SPECIAL_URL: "https://api.foursquare.com/v2/specials/{special_id}",

		SEARCH_URL: "https://api.foursquare.com/v2/specials/search",
	
		special: function(specialId, parameters, requestCallback)
		{
//			var parameters = {
//				venueId
//			}
		
			var requestUrl = this.SPECIAL_URL.replace("{special_id}", specialId) + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		search: function(parameters, requestCallback)
		{
//			var parameters = {
//				ll,
//				llAcc,
//				alt,
//				altAcc,
//				limit
//			}
			
			var requestUrl = this.SEARCH_URL + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		}
	};
};