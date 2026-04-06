# DefaultApi

All URIs are relative to *http://localhost*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**bookingsCreate**](#bookingscreate) | **POST** /bookings | |
|[**bookingsList**](#bookingslist) | **GET** /bookings | |
|[**eventTypesCreate**](#eventtypescreate) | **POST** /event-types | |
|[**eventTypesList**](#eventtypeslist) | **GET** /event-types | |
|[**slotsList**](#slotslist) | **GET** /slots | |

# **bookingsCreate**
> Booking bookingsCreate(bookingRequest)


### Example

```typescript
import {
    DefaultApi,
    Configuration,
    BookingRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new DefaultApi(configuration);

let bookingRequest: BookingRequest; //

const { status, data } = await apiInstance.bookingsCreate(
    bookingRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **bookingRequest** | **BookingRequest**|  | |


### Return type

**Booking**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, text/plain


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**201** | The request has succeeded and a new resource has been created as a result. |  -  |
|**400** | The server could not understand the request due to invalid syntax. |  -  |
|**409** | The request conflicts with the current state of the server. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **bookingsList**
> Array<Booking> bookingsList()


### Example

```typescript
import {
    DefaultApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new DefaultApi(configuration);

const { status, data } = await apiInstance.bookingsList();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<Booking>**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | The request has succeeded. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **eventTypesCreate**
> EventType eventTypesCreate(eventTypeCreateRequest)


### Example

```typescript
import {
    DefaultApi,
    Configuration,
    EventTypeCreateRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new DefaultApi(configuration);

let eventTypeCreateRequest: EventTypeCreateRequest; //

const { status, data } = await apiInstance.eventTypesCreate(
    eventTypeCreateRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **eventTypeCreateRequest** | **EventTypeCreateRequest**|  | |


### Return type

**EventType**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**201** | The request has succeeded and a new resource has been created as a result. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **eventTypesList**
> Array<EventType> eventTypesList()


### Example

```typescript
import {
    DefaultApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new DefaultApi(configuration);

const { status, data } = await apiInstance.eventTypesList();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<EventType>**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | The request has succeeded. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **slotsList**
> Array<OccupiedSlot> slotsList()


### Example

```typescript
import {
    DefaultApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new DefaultApi(configuration);

let startDate: string; // (default to undefined)
let endDate: string; // (default to undefined)

const { status, data } = await apiInstance.slotsList(
    startDate,
    endDate
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **startDate** | [**string**] |  | defaults to undefined|
| **endDate** | [**string**] |  | defaults to undefined|


### Return type

**Array<OccupiedSlot>**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | The request has succeeded. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

