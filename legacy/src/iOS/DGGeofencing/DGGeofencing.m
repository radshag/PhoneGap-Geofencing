//
//  Geofencing.m
//  TikalTimeTracker
//
//  Created by Dov Goldberg on 5/3/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "DGGeofencing.h"
#import <Cordova/CDVViewController.h>

@implementation DGGeofencing

- (CDVPlugin*) initWithWebView:(UIWebView*)theWebView
{
    self = (DGGeofencing*)[super initWithWebView:(UIWebView*)theWebView];
    if (self) 
	{
    }
    return self;
}

- (void) dealloc 
{
	//self.locationManager.delegate = nil;
	//self.locationManager = nil;
	[super dealloc];
}

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
	BOOL regionMonitoringAvailableClassPropertyAvailable = [CLLocationManager respondsToSelector:@selector(regionMonitoringAvailable)]; 
    if (regionMonitoringAvailableClassPropertyAvailable)
    {
        BOOL regionMonitoringAvailable = [CLLocationManager regionMonitoringAvailable];
        return  (regionMonitoringAvailable);
    }
    
    // by default, assume NO
    return NO;
}

- (BOOL) isRegionMonitoringEnabled
{
	BOOL regionMonitoringEnabledClassPropertyAvailable = [CLLocationManager respondsToSelector:@selector(regionMonitoringEnabled)]; 
    if (regionMonitoringEnabledClassPropertyAvailable)
    {
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
        return  (authStatus == kCLAuthorizationStatusAuthorized) || (authStatus == kCLAuthorizationStatusNotDetermined);
    }
    
    // by default, assume YES (for iOS < 4.2)
    return YES;
}

- (BOOL) isLocationServicesEnabled
{
	BOOL locationServicesEnabledInstancePropertyAvailable = [[[DGGeofencingHelper sharedGeofencingHelper] locationManager] respondsToSelector:@selector(locationServicesEnabled)]; // iOS 3.x
	BOOL locationServicesEnabledClassPropertyAvailable = [CLLocationManager respondsToSelector:@selector(locationServicesEnabled)]; // iOS 4.x
    
	if (locationServicesEnabledClassPropertyAvailable) 
	{ // iOS 4.x
		return [CLLocationManager locationServicesEnabled];
	} 
	else if (locationServicesEnabledInstancePropertyAvailable) 
	{ // iOS 2.x, iOS 3.x
		return [(id)[[DGGeofencingHelper sharedGeofencingHelper] locationManager] locationServicesEnabled];
	} 
	else 
	{
		return NO;
	}
}

#pragma mark Plugin Functions

- (void) startMonitoringSignificantLocationChanges:(CDVInvokedUrlCommand*)command {
    NSString* callbackId = command.callbackId;
    
    [[DGGeofencingHelper sharedGeofencingHelper] saveLocationCallbackId:callbackId];
    
    [[DGGeofencingHelper sharedGeofencingHelper] setCommandDelegate:self.commandDelegate];
    
    if (![self isLocationServicesEnabled])
	{
		BOOL forcePrompt = NO;
		if (!forcePrompt)
		{
            [[DGGeofencingHelper sharedGeofencingHelper] returnLocationError:PERMISSIONDENIED withMessage: nil];
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
        [[DGGeofencingHelper sharedGeofencingHelper] returnLocationError:PERMISSIONDENIED withMessage: message];
        
        return;
    }
    
    if (![self isSignificantLocationChangeMonitoringAvailable])
	{
		[[DGGeofencingHelper sharedGeofencingHelper] returnLocationError:SIGNIFICANTLOCATIONMONITORINGUNAVAILABLE withMessage: @"Significant location monitoring is unavailable"];
        return;
    }
    
    [[[DGGeofencingHelper sharedGeofencingHelper] locationManager] startMonitoringSignificantLocationChanges];
}

- (void) stopMonitoringSignificantLocationChanges:(CDVInvokedUrlCommand*)command {
    NSString* callbackId = command.callbackId;
    
    [[DGGeofencingHelper sharedGeofencingHelper] saveLocationCallbackId:callbackId];
    
    [[DGGeofencingHelper sharedGeofencingHelper] setCommandDelegate:self.commandDelegate];
    
    if (![self isLocationServicesEnabled])
	{
		BOOL forcePrompt = NO;
		if (!forcePrompt)
		{
            [[DGGeofencingHelper sharedGeofencingHelper] returnLocationError:PERMISSIONDENIED withMessage: nil];
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
        [[DGGeofencingHelper sharedGeofencingHelper] returnLocationError:PERMISSIONDENIED withMessage: message];
        
        return;
    }
    
    if (![self isSignificantLocationChangeMonitoringAvailable])
	{
		[[DGGeofencingHelper sharedGeofencingHelper] returnLocationError:SIGNIFICANTLOCATIONMONITORINGUNAVAILABLE withMessage: @"Significant location monitoring is unavailable"];
        return;
    }
    
    [[[DGGeofencingHelper sharedGeofencingHelper] locationManager] stopMonitoringSignificantLocationChanges];
    
    [[DGGeofencingHelper sharedGeofencingHelper] returnLocationSuccess];
}


- (void)addRegion:(CDVInvokedUrlCommand*)command {
    
    NSString* callbackId = command.callbackId;
    
    [[DGGeofencingHelper sharedGeofencingHelper] saveGeofenceCallbackId:callbackId];
    
    [[DGGeofencingHelper sharedGeofencingHelper] setCommandDelegate:self.commandDelegate];
    
    if (![self isLocationServicesEnabled])
	{
		BOOL forcePrompt = NO;
		if (!forcePrompt)
		{
            [[DGGeofencingHelper sharedGeofencingHelper] returnGeofenceError:PERMISSIONDENIED withMessage: nil];
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
        [[DGGeofencingHelper sharedGeofencingHelper] returnGeofenceError:PERMISSIONDENIED withMessage: message];
        
        return;
    } 
    
    if (![self isRegionMonitoringAvailable])
	{
		[[DGGeofencingHelper sharedGeofencingHelper] returnGeofenceError:REGIONMONITORINGUNAVAILABLE withMessage: @"Region monitoring is unavailable"];
        return;
    }
    
    if (![self isRegionMonitoringEnabled])
	{
		[[DGGeofencingHelper sharedGeofencingHelper] returnGeofenceError:REGIONMONITORINGPERMISSIONDENIED withMessage: @"User has restricted the use of region monitoring"];
        return;
    }
    NSMutableDictionary *options;
    [command legacyArguments:nil andDict:&options];
    [self addRegionToMonitor:options];
    
    [[DGGeofencingHelper sharedGeofencingHelper] returnRegionSuccess];
}

- (void) getPendingRegionUpdates:(CDVInvokedUrlCommand*)command {
    NSString* callbackId = command.callbackId;
    
    NSString *path = [DGGeofencingHelper applicationDocumentsDirectory];
    NSString *finalPath = [path stringByAppendingPathComponent:@"notifications.dg"];
    NSMutableArray *updates = [NSMutableArray arrayWithContentsOfFile:finalPath];
    
    if (updates) {
        NSError *error;
        [[NSFileManager defaultManager] removeItemAtPath:finalPath error:&error];
    } else {
        updates = [NSMutableArray array];
    }
    
    NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:3];
    [posError setObject: [NSNumber numberWithInt: CDVCommandStatus_OK] forKey:@"code"];
    [posError setObject: @"Region Success" forKey: @"message"];
    [posError setObject: updates forKey: @"pendingupdates"];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:posError];
    if (callbackId) {
        [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    }
    NSLog(@"pendingupdates: %@", updates);
}

- (void) addRegionToMonitor:(NSMutableDictionary *)params {
    // Parse Incoming Params
    NSString *regionId = [params objectForKey:KEY_REGION_ID];
    NSString *latitude = [params objectForKey:KEY_REGION_LAT];
    NSString *longitude = [params objectForKey:KEY_REGION_LNG];
    double radius = [[params objectForKey:KEY_REGION_RADIUS] doubleValue];
    
    CLLocationCoordinate2D coord = CLLocationCoordinate2DMake([latitude doubleValue], [longitude doubleValue]);
    CLRegion *region = [[CLRegion alloc] initCircularRegionWithCenter:coord radius:radius identifier:regionId];
    [[[DGGeofencingHelper sharedGeofencingHelper] locationManager] startMonitoringForRegion:region desiredAccuracy:kCLLocationAccuracyBestForNavigation];
    [region release];
}

- (void) removeRegionToMonitor:(NSMutableDictionary *)params {
    // Parse Incoming Params
    NSString *regionId = [params objectForKey:KEY_REGION_ID];
    NSString *latitude = [params objectForKey:KEY_REGION_LAT];
    NSString *longitude = [params objectForKey:KEY_REGION_LNG];
    
    CLLocationCoordinate2D coord = CLLocationCoordinate2DMake([latitude doubleValue], [longitude doubleValue]);
    CLRegion *region = [[CLRegion alloc] initCircularRegionWithCenter:coord radius:10.0 identifier:regionId];
    [[[DGGeofencingHelper sharedGeofencingHelper] locationManager] stopMonitoringForRegion:region];
    [region release];
}

- (void)removeRegion:(CDVInvokedUrlCommand*)command {
    
    NSString* callbackId = command.callbackId;
    
    [[DGGeofencingHelper sharedGeofencingHelper] saveGeofenceCallbackId:callbackId];
    
    [[DGGeofencingHelper sharedGeofencingHelper] setCommandDelegate:self.commandDelegate];
    
    if (![self isLocationServicesEnabled])
	{
		BOOL forcePrompt = NO;
		if (!forcePrompt)
		{
            [[DGGeofencingHelper sharedGeofencingHelper] returnGeofenceError:PERMISSIONDENIED withMessage: nil];
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
        [[DGGeofencingHelper sharedGeofencingHelper] returnGeofenceError:PERMISSIONDENIED withMessage: message];
        
        return;
    }  
    
    if (![self isRegionMonitoringAvailable])
	{
		[[DGGeofencingHelper sharedGeofencingHelper] returnGeofenceError:REGIONMONITORINGUNAVAILABLE withMessage: @"Region monitoring is unavailable"];
        return;
    }
    
    if (![self isRegionMonitoringEnabled])
	{
		[[DGGeofencingHelper sharedGeofencingHelper] returnGeofenceError:REGIONMONITORINGPERMISSIONDENIED withMessage: @"User has restricted the use of region monitoring"];
        return;
    }
    NSMutableDictionary *options;
    [command legacyArguments:nil andDict:&options];
    [self removeRegionToMonitor:options];
    
    [[DGGeofencingHelper sharedGeofencingHelper] returnRegionSuccess];
}

- (void)getWatchedRegionIds:(CDVInvokedUrlCommand*)command {
    NSString* callbackId = command.callbackId;
    
    NSSet *regions = [[DGGeofencingHelper sharedGeofencingHelper] locationManager].monitoredRegions;
    NSMutableArray *watchedRegions = [NSMutableArray array];
    for (CLRegion *region in regions) {
        [watchedRegions addObject:region.identifier];
    }
    NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:3];
    [posError setObject: [NSNumber numberWithInt: CDVCommandStatus_OK] forKey:@"code"];
    [posError setObject: @"Region Success" forKey: @"message"];
    [posError setObject: watchedRegions forKey: @"regionids"];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:posError];
    if (callbackId) {
        [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    }
    NSLog(@"watchedRegions: %@", watchedRegions);
}

#pragma mark Core Location Delegates

@end
