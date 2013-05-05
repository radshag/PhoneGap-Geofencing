
#import <CoreLocation/CoreLocation.h>
#import <Cordova/CDVPlugin.h>

enum DGLocationStatus {
    PERMISSIONDENIED = 1,
    POSITIONUNAVAILABLE,
    TIMEOUT
};
typedef NSUInteger DGLocationStatus;

enum DGGeofencingStatus {
    GEOFENCINGPERMISSIONDENIED = 4,
    GEOFENCINGUNAVAILABLE=5,
    GEOFENCINGTIMEOUT=6
};
typedef NSUInteger DGGeofencingStatus;


// simple object to keep track of location information
@interface DGLocationData : NSObject {
    DGLocationStatus locationStatus;
    DGGeofencingStatus geofencingStatus;
    NSMutableArray* locationCallbacks;
    NSMutableArray* geofencingCallbacks;
    CLLocation* locationInfo;
}

@property (nonatomic, assign) DGLocationStatus locationStatus;
@property (nonatomic, assign) DGGeofencingStatus geofencingStatus;
@property (nonatomic, strong) CLLocation* locationInfo;
@property (nonatomic, strong) NSMutableArray* locationCallbacks;
@property (nonatomic, strong) NSMutableArray* geofencingCallbacks;

@end

//=====================================================
// DGGeofencing
//=====================================================

@interface DGGeofencing : CDVPlugin <CLLocationManagerDelegate> {
    @private BOOL __locationStarted;
    @private BOOL __highAccuracyEnabled;
    DGLocationData* locationData;
}

@property (nonatomic, strong) CLLocationManager* locationManager;
@property (nonatomic, strong) DGLocationData* locationData;

- (BOOL) isLocationServicesEnabled;
- (BOOL) isAuthorized;
- (BOOL) isRegionMonitoringAvailable;
- (BOOL) isRegionMonitoringEnabled;
- (BOOL) isSignificantLocationChangeMonitoringAvailable;

#pragma mark Plugin Functions
- (void) initCallbackForRegionMonitoring:(CDVInvokedUrlCommand*)command;
- (void) startMonitoringRegion:(CDVInvokedUrlCommand*)command;
- (void) stopMonitoringRegion:(CDVInvokedUrlCommand*)command;
- (void) getMonitoredRegionIds:(CDVInvokedUrlCommand*)command;
- (void) getPendingRegionUpdates:(CDVInvokedUrlCommand*)command;
- (void) startMonitoringSignificantLocationChanges:(CDVInvokedUrlCommand*)command;
- (void) stopMonitoringSignificantLocationChanges:(CDVInvokedUrlCommand*)command;

@end
