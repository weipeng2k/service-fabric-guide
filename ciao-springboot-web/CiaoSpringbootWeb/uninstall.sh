#!/bin/bash

sfctl application delete --application-id CiaoSpringbootWeb
sfctl application unprovision --application-type-name CiaoSpringbootWebType --application-type-version 1.0.0
sfctl store delete --content-path CiaoSpringbootWeb
