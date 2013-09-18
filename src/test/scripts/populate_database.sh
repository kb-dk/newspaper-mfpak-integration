#!/bin/sh
DIR=$(readlink -f $(dirname $(readlink -f $0)))
psql -h achernar -d mfpak-devel -U mfpak -f ${DIR}/../../test/resources/test_data.sql -W