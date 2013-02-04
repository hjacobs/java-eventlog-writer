#!/usr/bin/python
# -*- coding: utf-8 -*-

import logging
import fnmatch
import os
import re
import argparse
import collections

JOB_FILE_FILTER = '*?Job.java'
IGNORE_JOBS = set(['AbstractJob.java'])

JOB_FILE_REGEX = re.compile(r'String\s+getLockResource()', re.IGNORECASE)
PROJECT_NAME_REGEX = re.compile(r'.*src/de/zalando/([^/]+)/.*', re.IGNORECASE)

QUERY_PREFIX = \
    """
-- project: %s --
  SELECT *
    FROM ${DB_SCHEMA}.app_config
   WHERE ac_key LIKE 'jobConfig%%'
ORDER BY ac_key;

UPDATE ${DB_SCHEMA}.app_config
   SET ac_value = '*'
 WHERE ac_key
    IN ("""
QUERY_SUFIX = '''
       );
'''


class ProjectNotFound(Exception):

    def __init__(self, value):
        self.value = value

    def __str__(self):
        return repr(self.value)


class JobLockFinder(object):

    """Find all jobs using the lock mechanism"""

    def __init__(self, source_dir, output_file, config_file=None):

        self.source_dir = source_dir
        self.output_file = output_file
        self.config_file = config_file

        if not os.path.isdir(source_dir):
            raise IOError('Source folder {0} not found'.format(source_dir))

    def run(self):
        matches = sorted(self.find_files())

        logging.info('Exporting jobs to file: %s', self.output_file)
        with open(self.output_file, 'w') as f:
            for entry in matches:
                f.write(entry + '\n')

        if self.config_file is not None:
            self.build_query(matches, self.config_file)

    def find_files(self):
        logging.info('Searching for jobs on folder: %s', self.source_dir)
        matches = set()
        for root, _, filenames in os.walk(self.source_dir):
            for filename in fnmatch.filter(filenames, JOB_FILE_FILTER):
                if filename not in IGNORE_JOBS:
                    file_path = os.path.join(root, filename)
                    with open(file_path) as f:
                        for l in f:
                            match = JOB_FILE_REGEX.search(l)
                            if match:
                                matches.add(file_path)

        return matches

    def build_config(self, matches):
        logging.debug('Grouping results')
        configs = collections.defaultdict(list)
        for file_path in matches:
            match = PROJECT_NAME_REGEX.search(file_path)
            if not match:
                raise ProjectNotFound('Project not found: {0}'.format(file_path))

            project = match.group(1)
            filename = os.path.splitext(os.path.basename(file_path))[0]
            configs[project].append('jobConfig.' + filename[0].lower() + filename[1:] + '.appInstanceKey')

        for key, value in configs.items():
            logging.debug("Found '%s' jobs on project: '%s'", len(value), key)

        return configs

    def build_query(self, matches, path):
        configs = self.build_config(matches)
        logging.info('Exporting queries to file: %s', path)
        with open(path, 'w') as f:
            for key, value in configs.items():
                query = QUERY_PREFIX % key
                query += "\n        '" + value[0] + "'"
                for i in range(1, len(value)):
                    query += ",\n        '" + value[i] + "'"
                f.write(query + QUERY_SUFIX)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Find jobs with locking')
    parser.add_argument('source_folder', metavar='DIR', help='Source folder')
    parser.add_argument('--output', default='jobs-with-lock.txt', metavar='FILE', help='Name of the output file')
    parser.add_argument('--query', metavar='FILE', help='Generate configurations')
    parser.add_argument('--verbose', action='store_true', help='Log debug configuration')

    args = parser.parse_args()

    loglevel = logging.INFO
    if args.verbose:
        loglevel = logging.DEBUG

    logging.basicConfig(level=loglevel, format='%(levelname)-6s %(message)s')

    try:
        prog = JobLockFinder(args.source_folder, args.output, args.query)
    except IOError, e:
        parser.error(e)

    prog.run()
