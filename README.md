rdap-conformance
================

[![Build Status](https://travis-ci.org/APNIC-net/rdap-conformance.png)](https://travis-ci.org/APNIC-net/rdap-conformance)

Tests an RDAP server for conformance with the published standards.
See [[RFC7480](http://tools.ietf.org/html/rfc7480)] and
http://datatracker.ietf.org/wg/weirds.

Synopsis
--------

    [user@host]$ java -jar ./rdap-conformance.jar {configuration-path}

Each line printed to stdout is a comma-separated test result. The
elements are as follows:

+ Request type

  The object class (e.g. 'ip') followed by a period and the type of
  the request (e.g. 'not-found'). 

+ URL

  The URL for the request, including any query component.

+ Status

  Describes the outcome of the test. One of 'success', 'notification',
  'warning' and 'failure'.

+ Code

  Describes the particular aspect of the response that is being tested
  by this entry. One of 'response' (general), 'status-code',
  'content-type' and 'content'. 'content' is the most common test
  code, unsurprisingly.

+ Information

  Contains more detail about the test and its result. For example, if
  an array were expected, but not found, this element would be 'is not
  an array' or similar.

+ Node

  A colon-separated list of values that describes the location within
  the response with which the test is concerned, if applicable.

+ Document
+ Reference

  If there is specific text concerning this test, then the final two
  fields will contain the document name (e.g. 'rfc7480') and the
  paragraph reference for the text.

The output format and elements are subject to change in later
releases. Result ordering is not guaranteed to be consistent.

Configuration
-------------

The {configuration-path} from the synopsis points to a JSON file with
the following structure:

    {
        "baseUrl": "{base-url}",
        "requestsPerSecond": "{requests-per-second}",
        "objectClasses": {
            "{object-class}": {
                "supported": {boolean},
                "exists":    [ "{path1}", "{path2}", ..., "{pathN}" ],
                "notExists": [ "{path1}", "{path2}", ..., "{pathN}" ],
                "redirects": [ "{path1}", "{path2}", ..., "{pathN}" ],
                "search": {
                    "supported": {boolean},
                    "values": {
                        "{parameter1}": [ "{value1}", "{value2}", ...,
                                          "{valueN}" ],
                        "{parameter2}": [ "{value1}", "{value2}", ...,
                                          "{valueN}" ],
                        ...
                    }
                }
            },
            ...
        }
    }

`requestsPerSecond` is an optional configuration entry. By default,
there is no limit on the number of requests issued per second.

Redirects are not applicable for the entity object class. Searches are
only applicable for entities, domains and nameservers.

If an object class is omitted from the configuration file, it is
treated as unimplemented.

For example:

    { 
        "baseUrl": "http://testrdap.apnic.net",
        "objectClasses": {
            "ip": {
                "supported": true,
                "exists":    ["203.119.0.42/23"],
                "redirects": ["200.3.14.10"]
            },
            "domain": {
                "supported": true,
                "exists":    ["203.in-addr.arpa"],
                "notExists": ["google.com"]
            },
            "nameserver": {
                "supported": false
            },
            "entity": {
                "supported": true,
                "exists":    ["TP137-AP"],
                "notExists": ["@@@@@@@@"],
                "search": {
                    "supported": true,
                    "values": {
                        "fn":     [ "Test*" ],
                        "handle": [ "TP*" ]
                    }
                }
            },
            "autnum": {
                "supported": true,
                "exists":    ["4608"]
            }
        } 
    }

License
-------

See LICENSE.txt.

Acknowledgments
---------------

Viagénie (http://viagenie.ca) has conducted several live
interoperability tests for the protocol, which were helpful in
identifying specific behaviours that need to be tested.
