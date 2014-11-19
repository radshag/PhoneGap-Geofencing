/**
 *
 * Phonegap Geofencing Plugin
 * Copyright (c) Dov Goldberg 2014
 * http://www.ogonium.com
 * dov.goldberg@ogonium.com
 *
 */

#import "DGGeofencing.h"

@implementation DGLocationData

@synthesize locationStatus, geofencingStatus, locationInfo, locationCallbacks, geofencingCallbacks;
- (DGLocationData*)init
{
    self = (DGLocationData*)[super init];
    if (self) {
        self.locationInfo = nil;
        self.locationCallbacks = nil;
        self.geofencingCallbacks = nil;
    }
    return self;
}

@end

@implementation DGGeofencing

@synthesize locationData, locationManager;

- (CDVPlugin*)initWithWebView:(UIWebView*)theWebView
{
    self = (DGGeofencing*)[super initWithWebView:(UIWebView*)theWebView];
    if (self) {
        self.locationManager = [[CLLocationManager alloc] init];
        self.locationManager.delegate = self; // Tells the location manager to send updates to this object
        
        NSString *version = [[UIDevice currentDevice] systemVersion];
        if ([version floatValue] >= 8.0f) //for iOS8
        {
            if ([self.locationManager respondsToSelector:@selector(requestAlwaysAuthorization)]) {
                [self.locationManager requestAlwaysAuthorization];
            }
        }

        __locationStarted = NO;
        __highAccuracyEnabled = NO;
        self.locationData = nil;
    }
    return self;
}

#pragma mark Location and Geofencing Permissions
- (BOOL) isSignificantLocationChangeMonitoringAvailable
{
	BOOL significantLocationChangeMonitoringAvailablelassPropertyAvailable = [CLLocationManager respondsToSelector:@selector(significantLocationChangeMonitoringAvailable)];
    if (significantLocationChangeMonitoringAvailablelassPropertyAvailable)
    {
        BOOL significantLocationChangeMonitoringAvailable = [CLLocationManager significantLocationChangeMonitoringAvailable];
        return  (significantLocationChangeMonitoringAvailable);
    }
    
    // by default, assume NO
    return NO;
}

- (BOOL) isRegionMonitoringAvailable
{
    // iOS 4.x, iOS 5.x, iOS 6.x
    BOOL regionMonitoringAvailableClassPropertyAvailable = [CLLocationManager respondsToSelector:@selector(regionMonitoringAvailable)];
    // iOS 7.0+
    BOOL regionMonitoringAvailableForClassClassPropertyAvailable = [CLLocationManager respondsToSelector:@selector(isMonitoringAvailableForClass:)];
    
    if (regionMonitoringAvailableForClassClassPropertyAvailable)
    { // iOS 7.0+
        BOOL regionMonitoringAvailable = [CLLocationManager isMonitoringAvailableForClass:[CLCircularRegion class]];
        return  (regionMonitoringAvailable);
    } else if (regionMonitoringAvailableClassPropertyAvailable)
    { // iOS 4.x, iOS 5.x, iOS 6.x
        BOOL regionMonitoringAvailable = [CLLocationManager regionMonitoringAvailable];
        return  (regionMonitoringAvailable);
    }
    
    // by default, assume NO
    return NO;
}

- (BOOL) isRegionMonitoringEnabled
{
	BOOL regionMonitoringEnabledClassPropertyAvailable = [CLLocationManager respondsToSelector:@selector(regionMonitoringEnabled)]; // iOS 4.0, 4.1
    BOOL authorizationStatusClassPropertyAvailable = [CLLocationManager respondsToSelector:@selector(authorizationStatus)]; // iOS 4.2+
    if (authorizationStatusClassPropertyAvailable)
    { // iOS 4.2+
        NSUInteger authStatus = [CLLocationManager authorizationStatus];
        NSString *version = [[UIDevice currentDevice] systemVersion];
        if ([version floatValue] >= 8.0f) //for iOS8
            return (authStatus == kCLAuthorizationStatusAuthorizedAlways || authStatus == kCLAuthorizationStatusAuthorizedWhenInUse);
        else // for iOS < 8.0
            return  (authStatus == kCLAuthorizationStatusAuthorized) || (authStatus == kCLAuthorizationStatusNotDetermined);
    } else if (regionMonitoringEnabledClassPropertyAvailable)
    { // iOS 4.0, 4.1
        BOOL regionMonitoringEnabled = [CLLocationManager regionMonitoringEnabled];
        return  (regionMonitoringEnabled);
    }
    
    // by default, assume NO
    return NO;
}

- (BOOL) isAuthorized
{
	BOOL authorizationStatusClassPropertyAvailable = [CLLocationManager respondsToSelector:@selector(authorizationStatus)]; // iOS 4.2+
    if (authorizationStatusClassPropertyAvailable)
    {
        NSUInteger authStatus = [CLLocationManager authorizationStatus];
        NSString *version = [[UIDevice currentDevice] systemVersion];
        if ([version floatValue] >= 8.0f) //for iOS8
            return (authStatus == kCLAuthorizationStatusAuthorizedAlways || authStatus == kCLAuthorizationStatusAuthorizedWhenInUse);
        else // for iOS < 8.0
            return  (authStatus == kCLAuthorizationStatusAuthorized) || (authStatus == kCLAuthorizationStatusNotDetermined);
    }
    
    // by default, assume YES (for iOS < 4.2)
    return YES;
}

- (BOOL) isLocationServicesEnabled
{
	BOOL locationServicesEnabledInstancePropertyAvailable = [[self locationManager] respondsToSelector:@selector(locationServicesEnabled)]; // iOS 3.x
	BOOL locationServicesEnabledClassPropertyAvailable = [CLLocationManager respondsToSelector:@selector(locationServicesEnabled)]; // iOS 4.x
    
	if (locationServicesEnabledClassPropertyAvailable)
	{ // iOS 4.x
		return [CLLocationManager locationServicesEnabled];
	}
	else if (locationServicesEnabledInstancePropertyAvailable)
	{ // iOS 2.x, iOS 3.x
		return [(id)[self locationManager] locationServicesEnabled];
	}
	else
	{
		return NO;
	}
}


#pragma mark Plugin Functions

- (void) initCallbackForRegionMonitoring:(CDVInvokedUrlCommand *)command {
    NSString* callbackId = command.callbackId;
    if (!self.locationData) {
        self.locationData = [[DGLocationData alloc] init];
    }
    DGLocationData* lData = self.locationData;
    
    if (!lData.geofencingCallbacks) {
        lData.geofencingCallbacks = [NSMutableArray arrayWithCapacity:1];
    }
    
    [lData.geofencingCallbacks addObject:callbackId];
    // return success to callback
    
    NSMutableDictionary* returnInfo = [NSMutableDictionary dictionaryWithCapacity:2];
    NSNumber* timestamp = [NSNumber numberWithDouble:([[NSDate date] timeIntervalSince1970] * 1000)];
    [returnInfo setObject:timestamp forKey:@"timestamp"];
    [returnInfo setObject:@"Region monitoring callback added" forKey:@"message"];
    [returnInfo setObject:@"initmonitor" forKey:@"callbacktype"];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnInfo];
    [result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
}

- (void) startMonitoringRegion:(CDVInvokedUrlCommand*)command {
    NSString* regionId = [command.arguments objectAtIndex:0];
    NSString *latitude = [command.arguments objectAtIndex:1];
    NSString *longitude = [command.arguments objectAtIndex:2];
    double radius = [[command.arguments objectAtIndex:3] doubleValue];
    //CLLocationAccuracy accuracy = [[command.arguments objectAtIndex:4] floatValue];
    
    DGLocationData* lData = self.locationData;
    NSString *callbackId = [lData.geofencingCallbacks objectAtIndex:0];
    
    if ([self isLocationServicesEnabled] == NO) {
        lData.locationStatus = PERMISSIONDENIED;
        NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
        [posError setObject:[NSNumber numberWithInt:PERMISSIONDENIED] forKey:@"code"];
        [posError setObject:@"Location services are disabled." forKey:@"message"];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:posError];
        [result setKeepCallbackAsBool:YES];
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
    } else if ([self isAuthorized] == NO) {
        lData.locationStatus = PERMISSIONDENIED;
        NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
        [posError setObject:[NSNumber numberWithInt:PERMISSIONDENIED] forKey:@"code"];
        [posError setObject:@"Location services are not authorized." forKey:@"message"];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:posError];
        [result setKeepCallbackAsBool:YES];
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
    } else if ([self isRegionMonitoringAvailable] == NO) {
        lData.geofencingStatus = GEOFENCINGUNAVAILABLE;
        NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
        [posError setObject:[NSNumber numberWithInt:GEOFENCINGUNAVAILABLE] forKey:@"code"];
        [posError setObject:@"Geofencing services are disabled." forKey:@"message"];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:posError];
        [result setKeepCallbackAsBool:YES];
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
    } else if ([self isRegionMonitoringEnabled] == NO) {
        lData.geofencingStatus = GEOFENCINGPERMISSIONDENIED;
        NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
        [posError setObject:[NSNumber numberWithInt:GEOFENCINGPERMISSIONDENIED] forKey:@"code"];
        [posError setObject:@"Geofencing services are not authorized." forKey:@"message"];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:posError];
        [result setKeepCallbackAsBool:YES];
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
    } else {
        CLLocationCoordinate2D coord = CLLocationCoordinate2DMake([latitude doubleValue], [longitude doubleValue]);
        if(radius > locationManager.maximumRegionMonitoringDistance)
        {
            radius = locationManager.maximumRegionMonitoringDistance;
        }
        NSString *version = [[UIDevice currentDevice] systemVersion];
        CLRegion *region = nil;
 
        if ([version floatValue] >= 7.0f) //for iOS7
        {
            region =  [[CLCircularRegion alloc] initWithCenter:coord radius:radius identifier:regionId];
        } else // iOS 7 below
        {
            region = [[CLRegion alloc] initCircularRegionWithCenter:coord radius:radius identifier:regionId];
        }
        [self.locationManager startMonitoringForRegion:region];
    }
}

- (void) stopMonitoringRegion:(CDVInvokedUrlCommand*)command {
    DGLocationData* lData = self.locationData;
    NSString* callbackId = [lData.geofencingCallbacks objectAtIndex:0];
    // Parse Incoming Params
    NSString* regionId = [command.arguments objectAtIndex:0];
    NSString *latitude = [command.arguments objectAtIndex:1];
    NSString *longitude = [command.arguments objectAtIndex:2];
    
    CLLocationCoordinate2D coord = CLLocationCoordinate2DMake([latitude doubleValue], [longitude doubleValue]);
    NSString *version = [[UIDevice currentDevice] systemVersion];
    CLRegion *region = nil;
    if ([version floatValue] >= 7.0f) //for iOS7
    {
        region =  [[CLCircularRegion alloc] initWithCenter:coord radius:10.0 identifier:regionId];
    } else // iOS 7 below
    {
        region = [[CLRegion alloc] initCircularRegionWithCenter:coord radius:10.0 identifier:regionId];
    }
    [[self locationManager] stopMonitoringForRegion:region];
    
    // return success to callback
    
    NSMutableDictionary* returnInfo = [NSMutableDictionary dictionaryWithCapacity:2];
    NSNumber* timestamp = [NSNumber numberWithDouble:([[NSDate date] timeIntervalSince1970] * 1000)];
    [returnInfo setObject:timestamp forKey:@"timestamp"];
    [returnInfo setObject:@"Region was removed successfully" forKey:@"message"];
    [returnInfo setObject:regionId forKey:@"regionId"];
    [returnInfo setObject:@"monitorremoved" forKey:@"callbacktype"];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnInfo];
    [result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
}


- (void) startMonitoringSignificantLocationChanges:(CDVInvokedUrlCommand*)command {
    DGLocationData* lData = self.locationData;
    NSString *callbackId = [lData.geofencingCallbacks objectAtIndex:0];
    if (![self isLocationServicesEnabled])
	{
		BOOL forcePrompt = NO;
		if (!forcePrompt)
		{
            lData.locationStatus = GEOFENCINGPERMISSIONDENIED;
            NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
            [posError setObject:[NSNumber numberWithInt:GEOFENCINGPERMISSIONDENIED] forKey:@"code"];
            [posError setObject:@"Location services are not enabled." forKey:@"message"];
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:posError];
            [result setKeepCallbackAsBool:YES];
            [self.commandDelegate sendPluginResult:result callbackId:callbackId];
			return;
		}
    }
    
    if (![self isAuthorized])
    {
        NSString* message = nil;
        BOOL authStatusAvailable = [CLLocationManager respondsToSelector:@selector(authorizationStatus)]; // iOS 4.2+
        if (authStatusAvailable) {
            NSUInteger code = [CLLocationManager authorizationStatus];
            if (code == kCLAuthorizationStatusNotDetermined) {
                // could return POSITION_UNAVAILABLE but need to coordinate with other platforms
                message = @"User undecided on application's use of location services";
            } else if (code == kCLAuthorizationStatusRestricted) {
                message = @"application use of location services is restricted";
            }
        }
        //PERMISSIONDENIED is only PositionError that makes sense when authorization denied
        lData.locationStatus = GEOFENCINGPERMISSIONDENIED;
        NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
        [posError setObject:[NSNumber numberWithInt:GEOFENCINGPERMISSIONDENIED] forKey:@"code"];
        [posError setObject:message forKey:@"message"];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:posError];
        [result setKeepCallbackAsBool:YES];
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
        
        return;
    }
    
    if (![self isSignificantLocationChangeMonitoringAvailable])
	{
        lData.locationStatus = GEOFENCINGUNAVAILABLE;
        NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
        [posError setObject:[NSNumber numberWithInt:GEOFENCINGPERMISSIONDENIED] forKey:@"code"];
        [posError setObject:@"Location services are not available." forKey:@"message"];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:posError];
        [result setKeepCallbackAsBool:YES];
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
        return;
    }
    
    [[self locationManager] startMonitoringSignificantLocationChanges];
}

- (void) stopMonitoringSignificantLocationChanges:(CDVInvokedUrlCommand*)command {
    DGLocationData* lData = self.locationData;
    NSString *callbackId = [lData.geofencingCallbacks objectAtIndex:0];
    if (![self isLocationServicesEnabled])
	{
		BOOL forcePrompt = NO;
		if (!forcePrompt)
		{
            lData.locationStatus = GEOFENCINGPERMISSIONDENIED;
            NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
            [posError setObject:[NSNumber numberWithInt:GEOFENCINGPERMISSIONDENIED] forKey:@"code"];
            [posError setObject:@"Location services are not enabled." forKey:@"message"];
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:posError];
            [result setKeepCallbackAsBool:YES];
            [self.commandDelegate sendPluginResult:result callbackId:callbackId];
			return;
		}
    }
    
    if (![self isAuthorized])
    {
        NSString* message = @"User has explicitly denied authorization for this application, or location services are disabled in Settings.";
        BOOL authStatusAvailable = [CLLocationManager respondsToSelector:@selector(authorizationStatus)]; // iOS 4.2+
        if (authStatusAvailable) {
            NSUInteger code = [CLLocationManager authorizationStatus];
            if (code == kCLAuthorizationStatusNotDetermined) {
                // could return POSITION_UNAVAILABLE but need to coordinate with other platforms
                message = @"User undecided on application's use of location services";
            } else if (code == kCLAuthorizationStatusRestricted) {
                message = @"application use of location services is restricted";
            }
        }
        //PERMISSIONDENIED is only PositionError that makes sense when authorization denied
        lData.locationStatus = GEOFENCINGPERMISSIONDENIED;
        NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
        [posError setObject:[NSNumber numberWithInt:GEOFENCINGPERMISSIONDENIED] forKey:@"code"];
        [posError setObject:message forKey:@"message"];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:posError];
        [result setKeepCallbackAsBool:YES];
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
        
        return;
    }
    
    if (![self isSignificantLocationChangeMonitoringAvailable])
	{
        lData.locationStatus = GEOFENCINGUNAVAILABLE;
        NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
        [posError setObject:[NSNumber numberWithInt:GEOFENCINGPERMISSIONDENIED] forKey:@"code"];
        [posError setObject:@"Location services are not available." forKey:@"message"];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:posError];
        [result setKeepCallbackAsBool:YES];
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
        return;
    }
    
    [[self locationManager] stopMonitoringSignificantLocationChanges];
}

#pragma mark Location Delegate Callbacks

/*
 *  locationManager:didStartMonitoringForRegion:
 *
 *  Discussion:
 *    Invoked when a monitoring for a region started successfully.
 */
- (void)locationManager:(CLLocationManager *)manager didStartMonitoringForRegion:(CLRegion *)region {
    NSString *regionId = region.identifier;
    DGLocationData* lData = self.locationData;
    NSString* callbackId = [lData.geofencingCallbacks objectAtIndex:0];
    // return success to callback
    
    NSMutableDictionary* returnInfo = [NSMutableDictionary dictionaryWithCapacity:2];
    NSNumber* timestamp = [NSNumber numberWithDouble:([[NSDate date] timeIntervalSince1970] * 1000)];
    [returnInfo setObject:timestamp forKey:@"timestamp"];
    [returnInfo setObject:@"Region was successfully added for monitoring" forKey:@"message"];
    [returnInfo setObject:regionId forKey:@"regionId"];
    [returnInfo setObject:@"monitorstart" forKey:@"callbacktype"];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnInfo];
    [result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
}

/*
 *  locationManager:monitoringDidFailForRegion:withError:
 *
 *  Discussion:
 *    Invoked when a region monitoring error has occurred. Error types are defined in "CLError.h".
 */
- (void)locationManager:(CLLocationManager *)manager monitoringDidFailForRegion:(CLRegion *)region withError:(NSError *)error {
    NSString *regionId = region.identifier;
    DGLocationData* lData = self.locationData;
    NSString* callbackId = [lData.geofencingCallbacks objectAtIndex:0];
    // return error to callback
    
    NSMutableDictionary* returnInfo = [NSMutableDictionary dictionaryWithCapacity:2];
    NSNumber* timestamp = [NSNumber numberWithDouble:([[NSDate date] timeIntervalSince1970] * 1000)];
    [returnInfo setObject:timestamp forKey:@"timestamp"];
    [returnInfo setObject:error.description forKey:@"message"];
    [returnInfo setObject:regionId forKey:@"regionId"];
    [returnInfo setObject:@"monitorfail" forKey:@"callbacktype"];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnInfo];
    [result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
}

/*
 *  locationManager:didEnterRegion:
 *
 *  Discussion:
 *    Invoked when the user enters a monitored region.  This callback will be invoked for every allocated
 *    CLLocationManager instance with a non-nil delegate that implements this method.
 */
- (void)locationManager:(CLLocationManager *)manager didEnterRegion:(CLRegion *)region {
    NSString *regionId = region.identifier;
    DGLocationData* lData = self.locationData;
    NSString* callbackId = [lData.geofencingCallbacks objectAtIndex:0];
    
    if (callbackId) {
        // return success to callback
        
        NSMutableDictionary* returnInfo = [NSMutableDictionary dictionaryWithCapacity:2];
        NSNumber* timestamp = [NSNumber numberWithDouble:([[NSDate date] timeIntervalSince1970] * 1000)];
        [returnInfo setObject:timestamp forKey:@"timestamp"];
        [returnInfo setObject:@"Region was entered" forKey:@"message"];
        [returnInfo setObject:regionId forKey:@"regionId"];
        [returnInfo setObject:@"enter" forKey:@"callbacktype"];
        
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnInfo];
        [result setKeepCallbackAsBool:YES];
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
    }
}

/*
 *  locationManager:didExitRegion:
 *
 *  Discussion:
 *    Invoked when the user exits a monitored region.  This callback will be invoked for every allocated
 *    CLLocationManager instance with a non-nil delegate that implements this method.
 */
- (void)locationManager:(CLLocationManager *)manager didExitRegion:(CLRegion *)region {
    NSString *regionId = region.identifier;
    DGLocationData* lData = self.locationData;
    NSString* callbackId = [lData.geofencingCallbacks objectAtIndex:0];
    
    if (callbackId) {
        // return success to callback
        
        NSMutableDictionary* returnInfo = [NSMutableDictionary dictionaryWithCapacity:2];
        NSNumber* timestamp = [NSNumber numberWithDouble:([[NSDate date] timeIntervalSince1970] * 1000)];
        [returnInfo setObject:timestamp forKey:@"timestamp"];
        [returnInfo setObject:@"Region was exited" forKey:@"message"];
        [returnInfo setObject:regionId forKey:@"regionId"];
        [returnInfo setObject:@"exit" forKey:@"callbacktype"];
        
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnInfo];
        [result setKeepCallbackAsBool:YES];
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
    }

}

-(void) locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation {
    
    DGLocationData* lData = self.locationData;
    NSString* callbackId = [lData.geofencingCallbacks objectAtIndex:0];
    
    NSMutableDictionary *returnInfo = [NSMutableDictionary dictionary];
    [returnInfo setObject:[NSNumber numberWithDouble:[newLocation.timestamp timeIntervalSince1970]] forKey:@"new_timestamp"];
    [returnInfo setObject:[NSNumber numberWithDouble:newLocation.speed] forKey:@"new_speed"];
    [returnInfo setObject:[NSNumber numberWithDouble:newLocation.course] forKey:@"new_course"];
    [returnInfo setObject:[NSNumber numberWithDouble:newLocation.verticalAccuracy] forKey:@"new_verticalAccuracy"];
    [returnInfo setObject:[NSNumber numberWithDouble:newLocation.horizontalAccuracy] forKey:@"new_horizontalAccuracy"];
    [returnInfo setObject:[NSNumber numberWithDouble:newLocation.altitude] forKey:@"new_altitude"];
    [returnInfo setObject:[NSNumber numberWithDouble:newLocation.coordinate.latitude] forKey:@"new_latitude"];
    [returnInfo setObject:[NSNumber numberWithDouble:newLocation.coordinate.longitude] forKey:@"new_longitude"];
    
    [returnInfo setObject:[NSNumber numberWithDouble:[oldLocation.timestamp timeIntervalSince1970]] forKey:@"old_timestamp"];
    [returnInfo setObject:[NSNumber numberWithDouble:oldLocation.speed] forKey:@"old_speed"];
    [returnInfo setObject:[NSNumber numberWithDouble:oldLocation.course] forKey:@"oldcourse"];
    [returnInfo setObject:[NSNumber numberWithDouble:oldLocation.verticalAccuracy] forKey:@"old_verticalAccuracy"];
    [returnInfo setObject:[NSNumber numberWithDouble:oldLocation.horizontalAccuracy] forKey:@"old_horizontalAccuracy"];
    [returnInfo setObject:[NSNumber numberWithDouble:oldLocation.altitude] forKey:@"old_altitude"];
    [returnInfo setObject:[NSNumber numberWithDouble:oldLocation.coordinate.latitude] forKey:@"old_latitude"];
    [returnInfo setObject:[NSNumber numberWithDouble:oldLocation.coordinate.longitude] forKey:@"old_longitude"];
    
    
    [returnInfo setObject:@"locationupdate" forKey:@"callbacktype"];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnInfo];
    [result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
}

@end
