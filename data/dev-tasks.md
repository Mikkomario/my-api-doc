# Development tasks on MyApiDoc

## Bugs
Some log entries are unnecessarily long (e.g. in for loops and assignments)

```
Next instruction: print "asd".length
Failure: Expected end of line, got .length; print "asd".length
```

## Features
We need a command for checking the current auth method

Add support for messages in assert statements

Store API (doc) directories so that updates can be easier

Add support for string interpolation

## Other
Add request rate limiting

When printing models, don't reorder properties