# Matrix Identity Service
**WARNING**: This document is incomplete and can be missleading.

Implementation of the [Unofficial Matrix Identity Service API](https://kamax.io/matrix/api/identity_service/unstable.html).

## Invitation
Resolution can be customized using the following configuration:

`invite.resolution.recursive`  
- Default value: `true`  
- Description: Control if the pending invite resolution should be done recursively or not.  
  **DANGER ZONE:** This setting has the potential to create "an isolated island", which can have unexpected side effects
  and break invites in rooms. This will most likely not have the effect you think it does. Only change the value if you
  understand the consequences.

`invite.resolution.timer`  
- Default value: `1`  
- Description: How often, in minutes, mxisd should try to resolve pending invites.
