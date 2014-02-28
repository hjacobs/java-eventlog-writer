#!/usr/bin/env python
# -*- coding: utf-8 -*-

# This setup.py is utilized only to run tests, since there is not yet a requirement to install this package to Disutils

import argparse
import nose
import os

parser = argparse.ArgumentParser(description='RQM tool to report and display test and environment related information')

group = parser.add_mutually_exclusive_group(required=True)
group.add_argument('-t', '--test', action='store_true', help='Runs tests')
# To this group install / deploy actions should be added

args = parser.parse_args()

if args.test:
    argv = ['nosetests']
    argv.append('--with-coverage')
    argv.append('--cover-xml')
    argv.append('--cover-xml-file=' + os.getcwd() + '/coverage.xml')
    argv.append('--with-xunit')
    argv.append('--xunit-file=' + os.getcwd() + '/nosetests.xml')
    nose.run(argv=argv)
