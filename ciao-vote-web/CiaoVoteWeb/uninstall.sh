#!/bin/bash

sfctl application delete --application-id CiaoVoteWeb
sfctl application unprovision --application-type-name CiaoVoteWebType --application-type-version 1.0.0
sfctl store delete --content-path CiaoVoteWeb
