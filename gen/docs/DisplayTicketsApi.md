# DisplayTicketsApi

All URIs are relative to *http://192.168.4.134:3030/helpdesk-ticketing*

Method | HTTP request | Description
------------- | ------------- | -------------
[**queuingFromToPost**](DisplayTicketsApi.md#queuingFromToPost) | **POST** /queuing/{from}/{to} | Display all tickets for queuing
[**specificationFromToTeamIdPost**](DisplayTicketsApi.md#specificationFromToTeamIdPost) | **POST** /{specification}/{from}/{to}/{teamId} | Display all tickets for Admin and Support
[**specificationFromToTeamIdUserIdPost**](DisplayTicketsApi.md#specificationFromToTeamIdUserIdPost) | **POST** /{specification}/{from}/{to}/{teamId}/{userId} | Display all tickets for specific user


<a name="queuingFromToPost"></a>
# **queuingFromToPost**
> queuingFromToPost(from, to, inlineObject2)

Display all tickets for queuing

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DisplayTicketsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://192.168.4.134:3030/helpdesk-ticketing");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    DisplayTicketsApi apiInstance = new DisplayTicketsApi(defaultClient);
    String from = "from_example"; // String | Parameter for specific from date
    String to = "to_example"; // String | Parameter for specific to date
    InlineObject2 inlineObject2 = new InlineObject2(); // InlineObject2 | 
    try {
      apiInstance.queuingFromToPost(from, to, inlineObject2);
    } catch (ApiException e) {
      System.err.println("Exception when calling DisplayTicketsApi#queuingFromToPost");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **from** | **String**| Parameter for specific from date |
 **to** | **String**| Parameter for specific to date |
 **inlineObject2** | [**InlineObject2**](InlineObject2.md)|  | [optional]

### Return type

null (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**500** | Server Error. |  -  |
**400** | You are not authorized to access the page. |  -  |
**401** | Invalid Key. |  -  |
**402** | System key expired, please contact the administrator. |  -  |
**403** | Invalid Token. |  -  |
**0** | Unexpected error |  -  |

<a name="specificationFromToTeamIdPost"></a>
# **specificationFromToTeamIdPost**
> specificationFromToTeamIdPost(specification, from, to, teamId, inlineObject)

Display all tickets for Admin and Support

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DisplayTicketsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://192.168.4.134:3030/helpdesk-ticketing");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    DisplayTicketsApi apiInstance = new DisplayTicketsApi(defaultClient);
    Long specification = 56L; // Long | Parameter for specific ticket status
    String from = "from_example"; // String | Parameter for specific from date
    String to = "to_example"; // String | Parameter for specific to date
    Long teamId = 56L; // Long | Parameter for specific from date
    InlineObject inlineObject = new InlineObject(); // InlineObject | 
    try {
      apiInstance.specificationFromToTeamIdPost(specification, from, to, teamId, inlineObject);
    } catch (ApiException e) {
      System.err.println("Exception when calling DisplayTicketsApi#specificationFromToTeamIdPost");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **specification** | **Long**| Parameter for specific ticket status |
 **from** | **String**| Parameter for specific from date |
 **to** | **String**| Parameter for specific to date |
 **teamId** | **Long**| Parameter for specific from date |
 **inlineObject** | [**InlineObject**](InlineObject.md)|  | [optional]

### Return type

null (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**500** | Server Error. |  -  |
**400** | You are not authorized to access the page. |  -  |
**401** | Invalid Key. |  -  |
**402** | System key expired, please contact the administrator. |  -  |
**403** | Invalid Token. |  -  |
**0** | Unexpected error |  -  |

<a name="specificationFromToTeamIdUserIdPost"></a>
# **specificationFromToTeamIdUserIdPost**
> specificationFromToTeamIdUserIdPost(specification, from, to, teamId, userId, inlineObject1)

Display all tickets for specific user

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DisplayTicketsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://192.168.4.134:3030/helpdesk-ticketing");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    DisplayTicketsApi apiInstance = new DisplayTicketsApi(defaultClient);
    Long specification = 56L; // Long | Parameter for specific ticket status
    String from = "from_example"; // String | Parameter for specific from date
    String to = "to_example"; // String | Parameter for specific to date
    Long teamId = 56L; // Long | Parameter for specific from date
    String userId = "userId_example"; // String | Parameter for specific from date
    InlineObject1 inlineObject1 = new InlineObject1(); // InlineObject1 | 
    try {
      apiInstance.specificationFromToTeamIdUserIdPost(specification, from, to, teamId, userId, inlineObject1);
    } catch (ApiException e) {
      System.err.println("Exception when calling DisplayTicketsApi#specificationFromToTeamIdUserIdPost");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **specification** | **Long**| Parameter for specific ticket status |
 **from** | **String**| Parameter for specific from date |
 **to** | **String**| Parameter for specific to date |
 **teamId** | **Long**| Parameter for specific from date |
 **userId** | **String**| Parameter for specific from date |
 **inlineObject1** | [**InlineObject1**](InlineObject1.md)|  | [optional]

### Return type

null (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**500** | Server Error. |  -  |
**400** | You are not authorized to access the page. |  -  |
**401** | Invalid Key. |  -  |
**402** | System key expired, please contact the administrator. |  -  |
**403** | Invalid Token. |  -  |
**0** | Unexpected error |  -  |

