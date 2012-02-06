#!/bin/sh

rm -rf /tmp/pagina.tif
scanimage --resolution 150 --mode color --format tiff > /tmp/pagina.tif
convert /tmp/pagina.tif $1
