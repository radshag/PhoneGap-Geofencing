//
//  DGGeofencingHelper.m
//  Geofencing
//
//  Created by Dov Goldberg on 10/18/12.
//
//

#import "DGGeofencingHelper.h"

static DGGeofencingHelper *sharedGeofencingHelper = nil;

@implementation DGLocationData

@synthesize locationStatus, locationInfo;
@synthesize locationCallbacks;
@synthesize geofenceCallbacks;

-(DGLocationData*) init
{
    self = (DGLocationData*)[super init];
    if (self)
	{
        self.locationInfo = nil;
    }
    return self;
}
-(void) dealloc
{
    self.locationInfo = nil;
    self.locationCallbacks = nil;
    self.geofenceCallbacks = nil;
    [super dealloc];
}

@end

@implementation DGGeofencingHelper

@synthesize webView;
@synthesize locationManager;
@synthesize didLaunchForRegionUpdate;
@synthesize locationData;
@synthesize commandDelegate;

- (void) saveGeofenceCallbackId:(NSString *) callbackId withKey:(NSString *)key{
    NSLog(@"callbackId: %@", callbackId);
    if (!self.locationData) {
        self.locationData = [[[DGLocationData alloc] init] autorelease];
    }
    
    DGLocationData* lData = self.locationData;
    if (!lData.geofenceCallbacks) {
        lData.geofenceCallbacks = [NSMutableDictionary dictionary];//]WithCapacity:1];
    }
    
    [lData.geofenceCallbacks setObject:callbackId forKey:key];
    // add the callbackId into the array so we can call back when get data
    //[lData.geofenceCallbacks enqueue:callbackId];
}

- (void) saveLocationCallbackId:(NSString *) callbackId {
    NSLog(@"callbackId: %@", callbackId);
    if (!self.locationData) {
        self.locationData = [[[DGLocationData alloc] init] autorelease];
    }
    
    DGLocationData* lData = self.locationData;
    if (!lData.locationCallbacks) {
        lData.locationCallbacks = [NSMutableArray array];//]WithCapacity:1];
    }
    
    // add the callbackId into the array so we can call back when get data
    [lData.locationCallbacks enqueue:callbackId];
}

- (void) locationManager:(CLLocationManager *)manager didEnterRegion:(CLRegion *)region
{
    if (self.didLaunchForRegionUpdate) {
        NSString *path = [DGGeofencingHelper applicationDocumentsDirectory];
        NSString *finalPath = [path stringByAppendingPathComponent:@"notifications.dg"];
        NSMutableArray *updates = [NSMutableArray arrayWithContentsOfFile:finalPath];
        
        if (!updates) {
            updates = [NSMutableArray array];
        }
        
        NSMutableDictionary *update = [NSMutableDictionary dictionary];
        
        [update setObject:region.identifier forKey:@"fid"];
        [update setObject:[NSNumber numberWithDouble:[[NSDate date] timeIntervalSince1970]] forKey:@"timestamp"];
        [update setObject:@"enter" forKey:@"status"];
        
        [updates addObject:update];
        
        [updates writeToFile:finalPath atomically:YES];
    } else {
        NSMutableDictionary *dict = [NSMutableDictionary dictionary];
        [dict setObject:@"enter" forKey:@"status"];
        [dict setObject:region.identifier forKey:@"fid"];
        NSString *jsStatement = [NSString stringWithFormat:@"DGGeofencing.regionMonitorUpdate(%@);", [dict JSONString]];
        [self.webView stringByEvaluatingJavaScriptFromString:jsStatement];
    }
}

- (void) locationManager:(CLLocationManager *)manager didExitRegion:(CLRegion *)region
{
    if (self.didLaunchForRegionUpdate) {
        NSString *path = [DGGeofencingHelper applicationDocumentsDirectory];
        NSString *finalPath = [path stringByAppendingPathComponent:@"notifications.dg"];
        NSMutableArray *updates = [NSMutableArray arrayWithContentsOfFile:finalPath];
        
        if (!updates) {
            updates = [NSMutableArray array];
        }
        
        NSMutableDictionary *update = [NSMutableDictionary dictionary];
        
        [update setObject:region.identifier forKey:@"fid"];
        [update setObject:[NSNumber numberWithDouble:[[NSDate date] timeIntervalSince1970]] forKey:@"timestamp"];
        [update setObject:@"left" forKey:@"status"];
        
        [updates addObject:update];
        
        [updates writeToFile:finalPath atomically:YES];
    } else {
        NSMutableDictionary *dict = [NSMutableDictionary dictionary];
        [dict setObject:@"left" forKey:@"status"];
        [dict setObject:region.identifier forKey:@"fid"];
        NSString *jsStatement = [NSString stringWithFormat:@"DGGeofencing.regionMonitorUpdate(%@);", [dict JSONString]];
        [self.webView stringByEvaluatingJavaScriptFromString:jsStatement];
    }
}

`

- (void)locationManager:(CLLocationManager *)manager monitoringDidFailForRegion:(CLRegion *)region withError:(NSError *)error {
    NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
    [posError setObject: [NSNumber numberWithInt: error.code] forKey:@"code"];
    [posError setObject: region.identifier forKey: @"regionid"];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:posError];
    for (NSString *callbackId in self.locationData.geofenceCallbacks) {
        if (callbackId) {
            [self.commandDelegate sendPluginResult:result callbackId:callbackId];
        }
    }
    self.locationData.geofenceCallbacks = [NSMutableArray array];
}

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error{
    NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
    [posError setObject: [NSNumber numberWithInt: error.code] forKey:@"code"];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:posError];
    for (NSString *callbackId in self.locationData.locationCallbacks) {
        if (callbackId) {
            [self.commandDelegate sendPluginResult:result callbackId:callbackId];
        }
    }
    self.locationData.locationCallbacks = [NSMutableArray array];
}

- (void) returnRegionSuccessForRegion:(NSString *)regionId AndKeepCallbackAsBool:(BOOL)keepcallback {
    NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
    [posError setObject: [NSNumber numberWithInt: CDVCommandStatus_OK] forKey:@"code"];
    [posError setObject: @"Region Success" forKey: @"message"];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:posError];
    NSString *callbackId = [self.locationData.geofenceCallbacks objectForKey:regionId];
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
    [result setKeepCallbackAsBool:keepcallback];
}

- (void) returnLocationSuccess {
    NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
    [posError setObject: [NSNumber numberWithInt: CDVCommandStatus_OK] forKey:@"code"];
    [posError setObject: @"Region Success" forKey: @"message"];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:posError];
    for (NSString *callbackId in self.locationData.locationCallbacks) {
        if (callbackId) {
            [self.commandDelegate sendPluginResult:result callbackId:callbackId];
        }
    }
    self.locationData.locationCallbacks = [NSMutableArray array];
}

- (void) returnLocationError: (NSUInteger) errorCode withMessage: (NSString*) message
{
    NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
    [posError setObject: [NSNumber numberWithInt: errorCode] forKey:@"code"];
    [posError setObject: message ? message : @"" forKey: @"message"];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:posError];
    for (NSString *callbackId in self.locationData.locationCallbacks) {
        if (callbackId) {
            [self.commandDelegate sendPluginResult:result callbackId:callbackId];
        }
    }
    self.locationData.locationCallbacks = [NSMutableArray array];
}

- (void) returnGeofenceError: (NSUInteger) errorCode withMessage: (NSString*) message forRegionId:(NSString *)regionId andKeepCallback:(BOOL)keepcallback
{
    NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
    [posError setObject: [NSNumber numberWithInt: errorCode] forKey:@"code"];
    [posError setObject: message ? message : @"" forKey: @"message"];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:posError];
    NSString *callbackId = [self.locationData.geofenceCallbacks objectForKey:regionId];
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
    [result setKeepCallbackAsBool:keepcallback];
}

- (id) init {
    self = [super init];
    if (self) {
        self.locationManager = [[CLLocationManager alloc] init];
        self.locationManager.delegate = self; // Tells the location manager to send updates to this object
        self.locationData = nil;
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
    self.locationData = nil;
    [locationData release];
    [sharedGeofencingHelper release];
}

+ (NSString*) applicationDocumentsDirectory
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *basePath = ([paths count] > 0) ? [paths objectAtIndex:0] : nil;
    return basePath;
}

@end
