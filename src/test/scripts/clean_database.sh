#!/bin/sh
DIR=$(readlink -f $(dirname $(readlink -f $0)))
psql -h achernar -d mfpak-devel -U mfpak -f ${DIR}/../../main/resources/mfpak_schema.sql -W