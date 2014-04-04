#Lightning Browser
#![](ic_launcher_small.png)
####Download
* [Download APK from here](https://github.com/anthonycr/Lightning-Browser/blob/master/BrowserActivity.apk?raw=true)

* [Download from Google Play](https://play.google.com/store/apps/details?id=acr.browser.barebones)

* [Download the version with a new interface here](https://github.com/anthonycr/Lightning-Browser/blob/master/MainActivity.apk?raw=true)


####Features
* Bookmarks

* History

* Multiple Search Engines (Google, Bing, Yahoo, StartPage, DuckDuckGo, etc.)

* Incognito Mode

* Flash Support

* Privacy Aware

* HOLOYOLO

####Permissions Requested and Explained

* ````INTERNET````: For accessing the web

* ````WRITE_EXTERNAL_STORAGE````: For downloading files from the browser

* ````READ_EXTERNAL_STORAGE````: For downloading files from the browser

* ````ACCESS_FINE_LOCATION````: For sites like Google Maps, it is disabled by default in settings and displays a pop-up asking if a site may use your location when it is enabled

* ````READ_HISTORY_BOOKMARKS````: To synchronize history and bookmarks between the stock browser and Lightning

* ````WRITE_HISTORY_BOOKMARKS````: To synchronize history and bookmarks between the stock browser and Lightning

####The Code
* Please contribute code back if you can. The code isn't perfect.
* Please add translations/translation fixes as you see need
* Change ````FinalVariables.MAX_TABS```` from 5 to 100 to change the Lightning to the paid version
* Beware when using proguard while compiling. Some methods should not be obfuscated. Use the proguard settings file I provided for best results.

####License
````
Copyright 2014 Anthony Restaino

Lightning Browser

   This Source Code Form is subject to the terms of the 
   Mozilla Public License, v. 2.0. If a copy of the MPL 
   was not distributed with this file, You can obtain one at 
   
   http://mozilla.org/MPL/2.0/
````
This means that you MUST provide attribution in your application to Lightning Browser for the use of this code. The way you can do this is to provide a separate screen in settings showing what open-source libraries and/or apps (this one) you used in your application. You must also publish any changes you make to the code, basically meaning that you must open-source code derived from this one. The purpose of this is to encourage contribution back to the parent repository.
