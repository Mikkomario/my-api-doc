# Tests for the HP API

## Set up admin auth
Makes sure the admin API key is available
```
if adminKey == null
    adminKey = prompt: "Enter the admin API key"
use adminKey
```

## Set prices
Sets the default contact-extraction prices.
```
domainMax = 1
domainStart = 1

PATCH contact-extraction prices
assert: last.status == 200
```

## Verify email state
Checks whether an email address is available. Fails if not.
```
run: set up admin auth

// Requests the email address
email = prompt: "Enter your email address:"

// Assumes that the email address is available
POST email checks
assert: last.status == 200
assert: last.currentlyUsed == false
```

## Register user
Registers a new test user.
```
run: set up admin auth

// Makes sure an email address is available
if email == null
    email = prompt: "Enter your email address"

password = prompt: "Enter your password"
initialCredits = 100
creditsDuration = "1d"
createApiKey = true

POST users
assert: last.status == 200
userId = last.userId
accountId = last.accountId
apiKey = last.apiKey.key
assert: last.balance.usableAmount == 100
```

## Check account credit balance
Checks the available credit balance. Assumes that authorization has been specified. 
Updates the `credits` and `creditsReserved` variables.
```
assert accountId != null
GET API account
assert: last.status == 200
credits = last.balance.usableAmount
creditsReserved = last.balance.reservedAmount
```

## Extract contacts
Runs contact-extraction. Assumes that an API key and credits are available. 
Uses targets from a CSV file.
```
use apiKey

run: check account credit balance
startingCredits = credits

body = CSV: data/targets.csv
POST contact-extraction batches
body = null
assert: last.status == 200
batchId = last.batchId

// Case: Failed to initiate contact-extraction => Fails
if last.executionFailed
    POST close contact-extraction batch
    FAIL
else
    // Makes sure credits were reserved
    run: check account credit balance
    reservedCorrectly = creditsReserved > 0

    // Monitors the batch status
    status = "running"
    while status == "running"
        GET contact-extraction batch status
        if (last.statusCode == 200)
            status = last.status
        else
            status = "failed"
        wait "5 minutes"

    // Once ready, retrieves the results
    GET close contact-extraction batch
    assert: last.status == 200
    
    // Reservation check is delayed in order to close the batch first
    assert: reservedCorrectly
    
    // Checks whether credits were consumed correctly
    run: check account credit balance
    assert: credits < startingCredits
    assert: creditsReserved == 0
```

## Login
Logs the user in. Assumes that the user has been registered.
```
use basic email+password
POST user sessions
assert: last.status == 200
sessionToken = last.key
sessionExpires = last.expires
```

## Delete user
Deletes all data concerning the current user
```
if sessionExpires <= now
    run: login
    
DELETE user
assert: last.status == 202

GET user deletion request
assert: last.status == 200

shouldConfirm = prompt: "Should we confirm user deletion?"
if shouldConfirm
    POST confirm user deletion
    assert: last.status == 200
```