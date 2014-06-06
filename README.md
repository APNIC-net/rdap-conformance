rdap-conformance
================

[![Build Status](https://travis-ci.org/APNIC-net/rdap-conformance.png)](https://travis-ci.org/APNIC-net/rdap-conformance.png)

Tests an RDAP server for conformance with the current drafts. See
http://datatracker.ietf.org/wg/weirds.

This does not currently cover every aspect of the protocol. The issue
tracker contains work items for the extra parts that are required.

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
  fields will contain the document name (e.g.
  'draft-ietf-weirds-using-http-08') and the paragraph reference for
  the text.

Configuration
-------------

The {configuration-path} from the synopsis points to a JSON file with
the following structure:

    {
        "base_url": "{base-url}",
        "object_classes": {
            "{object-class}": {
                "supported":  {boolean},
                "exists":     [ "{path1}", "{path2}", ..., "{pathN}" ],
                "not_exists": [ "{path1}", "{path2}", ..., "{pathN}" ],
            },
            ...
        }
    }

If an object class is omitted from the configuration file, it is
treated as unimplemented.

For example:

    { 
        "base_url": "http://testrdap.apnic.net",
        "object_classes": {
            "ip": {
                "supported": true,
                "exists":    ["203.119.0.42/23"]
            },
            "domain": {
                "supported":  true,
                "exists":     ["203.in-addr.arpa"],
                "not_exists": ["google.com"]
            },
            "nameserver": {
                "supported": false
            },
            "entity": {
                "supported":  true,
                "exists":     ["TP137-AP"],
                "not_exists": ["@@@@@@@@"]
            },
            "autnum": {
                "supported": true,
                "exists":    ["4608"]
            }
        } 
    }
