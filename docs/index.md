---
layout: home
title:  "Home"
section: "home"
position: 1
---

[![Build Status](https://travis-ci.com/plmuninn/hocones.svg?branch=master)](https://travis-ci.com/plmuninn/hocones)

# What is hocones?

Is CLI application and sbt-plugin for managing [hocon](https://github.com/lightbend/config) format configuration in business environment.

It help's with:
 * creating, managing documentation for configurations
 * tracking changes in environments defined in configurations
 * understanding application configuration

## What exactly it does?

It loads your configuration, analyse it and generate 4 files:
 * [environment file](https://docs.docker.com/compose/env-file/) with all application environments
 * [yaml](https://en.wikipedia.org/wiki/YAML) meta file with flat structure of configuration, where you can describe 
 configuration, add some more context and possible values
 * [Markdown](https://en.wikipedia.org/wiki/Markdown) file with environment files merged with values from meta file
 * [Markdown](https://en.wikipedia.org/wiki/Markdown) file with documentation for whole configuration merged 
 from meta file - it also includes information like environment files, default values etc.
 
## Why?

Because configurations are often very complex and fragile parts of software - many times not managed properly, what can
lead to many problems. 

[hocones](https://plmuninn.github.io/hocones/) is also big help for DevOps and QA Engineers

## Limitations

* describing elements in array
* scraping descriptions from hocon file comments