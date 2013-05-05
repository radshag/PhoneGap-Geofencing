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

FourSquareClient.prototype.getPhotosClient = function()
{
	var client = this;
	return {
		PHOTOS_URL: "https://api.foursquare.com/v2/photos/{photo_id}",
		
		photos: function(photoId, requestCallback)
		{
			var requestUrl = this.PHOTOS_URL.replace("{photo_id}", photoId) + client.requestQuery();
			
			FourSquareUtils.doRequest(requestUrl, requestCallback);
		},
		
		add: function(bytes, parameters, requestCallback)
		{
//			var parameters = {
//				checkinId,
//				tipId,
//				venueId,
//				broadcast,
//				ll,
//				llAcc,
//				alt,
//				altAcc
//			}
			
			var requestUrl = this.ADD_URL + client.requestQuery();
			requestUrl += FourSquareUtils.createQueryString("&", parameters);
			
			FourSquareUtils.doRequest(requestUrl, requestCallback, "POST", bytes);
		}
	};
};