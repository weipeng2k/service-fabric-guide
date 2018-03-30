#!/bin/bash

sfctl application upload --path CiaoVoteService --show-progress
sfctl application provision --application-type-build-path CiaoVoteService
sfctl application create --app-name fabric:/CiaoVoteService --app-type CiaoVoteServiceType --app-version 1.0.0
