/*
 * Helpdesk API
 * ticketing/queuing API
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.openapitools.client.api;

import org.openapitools.client.ApiException;
import org.openapitools.client.model.InlineObject;
import org.junit.Test;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for DisplayTicketsApi
 */
@Ignore
public class DisplayTicketsApiTest {

    private final DisplayTicketsApi api = new DisplayTicketsApi();

    
    /**
     * Display all tickets
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void specificationFromToUserIdPostTest() throws ApiException {
        Long specification = null;
        String from = null;
        String to = null;
        String userId = null;
        InlineObject inlineObject = null;
        api.specificationFromToUserIdPost(specification, from, to, userId, inlineObject);

        // TODO: test validations
    }
    
}
