#!/bin/bash

sfctl application delete --application-id CiaoVoteService
sfctl application unprovision --application-type-name CiaoVoteServiceType --application-type-version 1.0.0
sfctl store delete --content-path CiaoVoteService
