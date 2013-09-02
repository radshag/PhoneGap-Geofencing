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

var getScriptPath = function() 
{
    var scripts = document.getElementsByTagName('script');
    var path = '';
    
    if(scripts && scripts.length>0) 
    {
        for(var i in scripts) 
        {
            if(scripts[i].src && scripts[i].src.match(/foursquare\-api(.*)$/)) 
            {
                path = scripts[i].src.replace(/foursquare-js-api(.*)\.js$/, '$1');
            }
        }
    }
    
    return path;
};

function require(file)
{
	try
	{
	      // inserting via DOM fails in Safari 2.0, so brute force approach
	      document.write('<script type="text/javascript" src="'+file+'"><\/script>');
	} 
	catch(e) 
	{
	      // for xhtml+xml served content, fall back to DOM methods
	      var script = document.createElement('script');
	      script.type = 'text/javascript';
	      script.src = file;
	      document.getElementsByTagName('head')[0].appendChild(script);
	}
}

//var path = getScriptPath();
require("lib/foursquare-api.core.js");

var types = ["photos", "venues", "settings", "users", "checkins", "tips", "specials"];
for(var type in types)
{
	require("lib/endpoint/foursquare-api."+ types[type] +".js");
}