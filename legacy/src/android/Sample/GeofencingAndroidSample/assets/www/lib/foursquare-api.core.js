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

// make sure we can do XMLHttpRequests
if(!window.XMLHttpRequest) 
{
	window.XMLHttpRequest = function()
	{
		try 
		{
			return new ActiveXObject("Msxml2.XMLHTTP");
		} 
		catch(e) 
		{
			try 
			{
				return new ActiveXObject("Microsoft.XMLHTTP");
			} 
			catch(e) 
			{
				return new XMLHttpRequest();
			}
		}
	};
} 

String.prototype.trim = function() 
{
	var	str = this.replace(/^\s\s*/, ''),
		ws = /\s/,
		i = str.length;
	while (ws.test(str.charAt(--i)));
	return str.slice(0, i + 1);
}

/**
 * @class
 */
FourSquareUtils = 
{
	getCookie: function(toGet)
	{
		var cookies = document.cookie.split(";");
		for(var idx = 0; idx < cookies.length; idx++)
		{
			var cookieName = cookies[idx].substr(0, cookies[idx].indexOf("=")).trim();
			var cookieValue = cookies[idx].substr(cookies[idx].indexOf("=") + 1);

			if(cookieName == toGet)
			{
				return unescape(cookieValue);
			}
  		}
  		
  		return null;
	},
		
	setCookie: function(name, value)
	{
		document.cookie = name + "=" + value;
	},
	
	retrieveAccessToken: function()
	{
		//alert("getting token");
		var hash = document.location.hash;
		if(hash.indexOf("#access_token=") != -1)
		{
			FourSquareUtils.setCookie("fs_access_token", hash.replace("#access_token=", ""));
			return hash.replace("#access_token=", "");
		}
		else if(FourSquareUtils.getCookie("fs_access_token") != null)
		{
			//alert(FourSquareUtils.getCookie("fs_access_token"));
			return FourSquareUtils.getCookie("fs_access_token");
		}
		return null;
	},
	
	parseResponse: function(response)
	{
		try
		{
			//return eval("(" + response + ")");
			//alert(response);
			return $.parseJSON(response);
		}
		catch(exception)
		{
			return null;
		}
	},
	
	createQueryString: function(prefix, parameters)
	{
		var query = "";
		for(key in parameters) 
		{
			if(parameters[key] != undefined && parameters[key] != null)
			{
				if(parameters[key].trim() != "")
				{
					query += "&" + key + "=" + parameters[key];
				}
			}
		}
		
		if(query.length > 0)
		{
			prefix = (prefix) ? prefix : "";
			query = prefix + query.substring(1);
		}
		
		return query;
	},
	
	doRequest: function(url, requestCallback, method, body)
	{
		var request = new XMLHttpRequest();
	    var method = (method) ? method : "GET";
		//alert(url);	    
	    request.open(method, url, true);	    
	    request.onreadystatechange = function(event) 
	    {
	    	if(request.readyState == 4) 
	    	{
	    		if(request.status == 200)
	    		{
	    			if(requestCallback.onSuccess)
	    			{
	    				requestCallback.onSuccess(
	    						FourSquareUtils.parseResponse(request.responseText));
	    			}
	    		}
	    		else
		    	{
		    		if(requestCallback.onFailure)
		    		{
		    			requestCallback.onFailure(
		    					FourSquareUtils.parseResponse(request.responseText));
		    		}
		    	}
	    	}
	    };
	    
	    if(body)
	    {
	    	request.setRequestHeader("Content-Length", body.length);
	    	request.send(body);
	    }
	    else
	    {
	    	request.send();
	    }
	}
};

/**
 * @class
 */
FourSquareClient = function(clientId, clientSecret, redirectUri, rememberAppCredentials)
{
	/**
	 * @constant
	 */
	this.AUTHENTICATION_URL = "https://foursquare.com/oauth2/authenticate";
	/**
	 * @constant
	 */
	this.ACCESS_TOKEN_URL = "https://foursquare.com/oauth2/access_token";
	
	this.requestQuery = function()
	{
		if(!this.accessToken)
		{
			return "?v=20111020&client_id=" + this.clientId + "&client_secret=" + this.clientSecret;
		}
		else
		{
			return "?v=20111020&oauth_token=" + this.accessToken;
		}
	};
	
	if(clientId == undefined || redirectUri == undefined)
	{
		if(rememberAppCredentials)
		{
			// see if we can retrieve the cookies
			this.redirectUri = FourSquareUtils.getCookie("fs_redirect_uri");
			this.clientId = FourSquareUtils.getCookie("fs_client_id");
			this.clientSecret = FourSquareUtils.getCookie("fs_client_secret");
		}
	}
	else
	{
		this.redirectUri = redirectUri;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		
		if(rememberAppCredentials)
		{
			FourSquareUtils.setCookie("fs_client_id", this.clientId);
			FourSquareUtils.setCookie("fs_client_secret", this.clientSecret);
			FourSquareUtils.setCookie("fs_redirect_uri", this.redirectUri);
		}
	}
	
	this.accessToken = FourSquareUtils.retrieveAccessToken();
    
	this.authenticate = function()
	{
		var authenticationURL = this.AUTHENTICATION_URL + "?client_id=" + this.clientId; 
		authenticationURL += FourSquareUtils.createQueryString("&",
							 {
							 	 response_type: "token",
								 redirect_uri: this.redirectUri
							 });
		//alert(authenticationURL);
		//$.mobile.changePage(authenticationURL, { transition: "slide"});
		//window.open(authenticationURL);
		
		
		var jqxhr = $.ajax( authenticationURL )
    	.done(function() { alert("success"); })
    	.fail(function() { alert(jqxhr.status); })
    	.always(function() { alert("complete"); });
		
	};
	
	//=================================================
	// The separate clients for each type of endpoint.
	//=================================================

	this.usersClient = this.getUsersClient();	
	
	this.venuesClient = this.getVenuesClient();
	
	this.checkinsClient = this.getCheckinsClient();
		
	this.tipsClient = this.getTipsClient();
	
	this.photosClient = this.getPhotosClient();
	
	this.settingsClient = this.getSettingsClient();
	
	this.specialsClient = this.getSpecialsClient();
	
	//this.updatesClient = this.getUpdatesClient();
};