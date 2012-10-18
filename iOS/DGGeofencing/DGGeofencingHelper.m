//
//  DGGeofencingHelper.m
//  Geofencing
//
//  Created by Dov Goldberg on 10/18/12.
//
//

#import "DGGeofencingHelper.h"
#import <Cordova/CDVCordovaView.h>

static DGGeofencingHelper *sharedGeofencingHelper = nil;

@implementation DGGeofencingHelper

@synthesize webView;
@synthesize locationManager;

- (void) locationManager:(CLLocationManager *)manager didEnterRegion:(CLRegion *)region
{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setObject:@"enter" forKey:@"status"];
    [dict setObject:region.identifier forKey:@"fid"];
    NSString *jsStatement = [NSString stringWithFormat:@"DGGeofencing.regionMonitorUpdate(%@);", [dict cdvjk_JSONString]];
    [self.webView stringByEvaluatingJavaScriptFromString:jsStatement];
}

- (void) locationManager:(CLLocationManager *)manager didExitRegion:(CLRegion *)region
{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setObject:@"left" forKey:@"status"];
    [dict setObject:region.identifier forKey:@"fid"];
    NSString *jsStatement = [NSString stringWithFormat:@"DGGeofencing.regionMonitorUpdate(%@);", [dict cdvjk_JSONString]];
    [self.webView stringByEvaluatingJavaScriptFromString:jsStatement];
}

- (id) init {
    self = [super init];
    if (self) {
        self.locationManager = [[[CLLocationManager alloc] init] autorelease];
        self.locationManager.delegate = self; // Tells the location manager to send updates to this object
    }
    return self;
}

+(DGGeofencingHelper *)sharedGeofencingHelper
{
	//objects using shard instance are responsible for retain/release count
	//retain count must remain 1 to stay in mem
    
	if (!sharedGeofencingHelper)
	{
		sharedGeofencingHelper = [[DGGeofencingHelper alloc] init];
	}
	
	return sharedGeofencingHelper;
}

- (void) dispose {
    locationManager.delegate = nil;
    [locationManager release];
    [sharedGeofencingHelper release];
}

@end
