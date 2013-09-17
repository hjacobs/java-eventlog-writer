#!/bin/bash
#
#
#

cd documentation/src/asciidoctor/
#echo pwd
unzip asciidoctorResources.zip
asciidoctor -a stylesdir=./themes -a stylesheet=golo.css zomcat-jpa.adoc
