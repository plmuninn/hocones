---
layout: page
title:  "Meta file"
section: "meta"
position: 2
---

## What is meta file?

Meta file is simple representation of concatenated Hocon configuration that allows add some extra meta information for configuration key.

## How is structured?

File is itself [yaml](https://en.wikipedia.org/wiki/YAML) grouped to three sections:

```yaml
hocones-version: '0.1'
roots:
  root_path:
    (path - root_path):
    - name:
    - meta-information:
orphans:
  - name:
  - meta-information:
```

* `hocones-version` is for migration
* `roots` contains objects and values grouped with long paths
* `orphans` contains objects and values with super (single segment) or none paths

Is structured like that to help finding looked values.

## What are possible meta information to set?

Every value contains:

* `name` - from config file
* `description` - you can give some comment about this value

Then values are treated differently depending of type:

* String literals:
    * `pattern` - string value
    * `min-length` - number value
    * `max-length` - number value
* Numbers:
    * `max-value` - number value
    * `min-value` - number value
* Lists:
    * `can-be-empty` - boolean value
    * `element-type` - description of list elements
* Object:
    * `element-type` - description of object children
    
## Why we need it?

This file is used during generation of documentation