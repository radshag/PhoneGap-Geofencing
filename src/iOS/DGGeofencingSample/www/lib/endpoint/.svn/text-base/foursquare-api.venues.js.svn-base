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

FourSquareClient.prototype.getVenuesClient = function()
{
	var client = this;
	return {
		/**
		 * @constant
		 */	
		VENUES_URL: "https://api.foursquare.com/v2/venues/{venue_id}",
		/**
		 * @constant
		 */
		ADD_URL: "https://api.foursquare.com/v2/venues/add",
		/**
		 * @constant
		 */
		CATEGORIES_URL: "https://api.foursquare.com/v2/venues/categories",
		/**
		 * @constant
		 */
		SEARCH_URL: "https://api.foursquare.com/v2/venues/search",
		/**
		 * @constant
		 */
		TRENDING_URL: "https://api.foursquare.com/v2/venues/trending",
		/**
		 * @constant
		 */
		HERENOW_URL: "https://api.foursquare.com/v2/venues/{venue_id}/herenow",
		/**
		 * @constant
		 */
		TIPS_URL: "https://api.foursquare.com/v2/venues/{venue_id}/tips",
		/**
		 * @constant
		 */
		PHOTOS_URL: "https://api.foursquare.com/v2/venues/{venue_id}/photos",
		/**
		 * @constant
		 */
		LINKS_URL: "https://api.foursquare.com/v2/venues/{venue_id}/links",
		/**
		 * @constant
		 */
		MARK_TODO_URL: "https://api.foursquare.com/v2/venues/{venue_id}/marktodo",
		/**
		 * @constant
		 */
		FLAG_URL: "https://api.foursquare.com/v2/venues/{venue_id}/flag",
		/**
		 * @constant
		 */
		PROPOSE_EDIT_URL: "https://api.foursquare.com/v2/venues/{venue_id}/proposeedit",
		
		venues: function(venueId, requestCallback)
		{
			var requestUrl = this.VENUES_URL.replace("{venue_id}", venueId) + client.requestQuery();
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		add: function(parameters, requestCallback)
		{
//			var parameters = {
//				name: null, 
//				address: null,
//				crossStreet: null, 
//				city: null, 
//				state: null, 
//				zip: null,
//				phone: null, 
//				ll: 0.0,0.0
//				primaryCategoryId
//			};
						
			var requestUrl = this.ADD_URL + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback, "POST");
		},
		
		categories: function(requestCallback)
		{
			var requestUrl = this.CATEGORIES_URL + client.requestQuery();
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		search: function(parameters, requestCallback)
		{
//			var parameters = {
//				requestCallback, 
//				ll, 
//				llAcc, 
//				altAcc, 
//				alt, 
//				query, 
//				limit, 
//				intent, 
//				categoryId, 
//				url, 
//				providerId, 
//				linkedId,
//				radius
//			}
			
			var requestUrl = this.SEARCH_URL + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		trending: function(parameters, requestCallback)
		{
//			var parameters = {
//				ll,
//				limit,
//				radius
//			}
			
			var requestUrl = this.TRENDING_URL + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
						
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		herenow: function(venueId, parameters, requestCallback)
		{
//			var parameters = {
//				limit,
//				radius,
//				afterTimestamp
//			}
			
			var requestUrl = this.HERENOW_URL.replace("{venue_id}", venueId) + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&",
								{
									
								});
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},

		tips: function(venueId, parameters, requestCallback)
		{
//			var parameters = {
//				sort,
//				limit,
//				offset
//			}
			
			var requestUrl = this.TIPS_URL.replace("{venue_id}", venueId) + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		photos: function(venueId, parameters, requestCallback)
		{
//			var parameters = {
//				group,
//				limit,
//				offset
//			}
			
			var requestUrl = this.PHOTOS_URL.replace("{venue_id}", venueId) + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		links: function(venueId, requestCallback)
		{
			var requestUrl = this.LINKS_URL.replace("{venue_id}", venueId) + client.requestQuery();
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
	
		marktodo: function(venueId, parameters, requestCallback)
		{
//			var parameters = {
//				text
//			}
			
			var requestUrl = this.MARK_TODO_URL.replace("{venue_id}", venueId) + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback, "POST");
		},
		
		flag: function(venueId, parameters, requestCallback)
		{
//			var parameters = {
//				problem
//			}
			
			var requestUrl = this.FLAG_URL.replace("{venue_id}", venueId) + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback, "POST");
		},
		
		proposeedit: function(venueId, parameters, requestCallback)
		{
//			var parameters = {
//				name, 
//				address, 
//				crossStreet, 
//				city, 
//				state, 
//				zip, 
//				phone, 
//				ll, 
//				primaryCategoryId
//			}
			
			var requestUrl = this.PROPOSE_EDIT_URL.replace("{venue_id}", venueId) + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
						
			FourSquareUtils.doRequest(requestUrl, requestCallback, "POST");
		}
	}
};