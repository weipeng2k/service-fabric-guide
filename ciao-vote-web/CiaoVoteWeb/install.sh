#!/bin/bash

sfctl application upload --path CiaoVoteWeb --show-progress
sfctl application provision --application-type-build-path CiaoVoteWeb
sfctl application create --app-name fabric:/CiaoVoteWeb --app-type CiaoVoteWebType --app-version 1.0.0
