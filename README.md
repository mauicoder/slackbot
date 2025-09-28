# SlackBot
This project is a slack-bot used to listen for slack messages in a channel and perform
some actions

Based on spring boot, it uses the slack-client library and perform a polling with 
a configurable interval time

# TODO
- find a way to override the base slack-client api URLs.
Look at 
```java
package com.slack.api;

class SlackConfig {
    ... 
    private String auditEndpointUrlPrefix = AuditClient.ENDPOINT_URL_PREFIX;

    private String methodsEndpointUrlPrefix = MethodsClient.ENDPOINT_URL_PREFIX;

    private String scimEndpointUrlPrefix = SCIMClient.ENDPOINT_URL_PREFIX;

    private String scim2EndpointUrlPrefix = SCIM2Client.ENDPOINT_URL_PREFIX;

    private String statusEndpointUrlPrefix = StatusClient.ENDPOINT_URL_PREFIX;

    private String legacyStatusEndpointUrlPrefix = LegacyStatusClient.ENDPOINT_URL_PREFIX;
    ...
}
```

- implement ack-functionality for messages already digested/or answer to the messages (thread?)
