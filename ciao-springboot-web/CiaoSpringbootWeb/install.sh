#!/bin/bash

sfctl application upload --path CiaoSpringbootWeb --show-progress
sfctl application provision --application-type-build-path CiaoSpringbootWeb
sfctl application create --app-name fabric:/CiaoSpringbootWeb --app-type CiaoSpringbootWebType --app-version 1.0.0
