#!/usr/bin/python
# -*- coding: utf-8 -*-

import logging
import argparse
import collections
import datetime
import re
import os
from multiprocessing import Pool

FAST_LOCK_CHECK = 'lock'
ACQUIRE_LOCK_REGEX = re.compile(r'.*Acquiring lock on ([^ ]+) for ([^ ]).*')
RELEASE_LOCK_REGEX = re.compile(r'.*Releasing lock on ([^\s]+)')


def group_resources(log_file):
    if not os.path.isfile(log_file):
        raise IOError('File not found'.format(log_file))

    resources = collections.defaultdict(list)
    last_acquired_resource = collections.defaultdict()

    with open(log_file) as file:
        for line in file:
            if FAST_LOCK_CHECK in line:
                match = ACQUIRE_LOCK_REGEX.search(line)
                if match:
                    resource = match.group(1)
                    resource_interval = last_acquired_resource.get(resource)
                    if resource_interval:
                        logging.debug('Lock %s acquired without success: %s', resource, resource_interval)
                    last_acquired_resource[resource] = parse_timestamp(line[:23])
                else:
                    match = RELEASE_LOCK_REGEX.search(line)
                    if match:
                        resource = match.group(1)
                        ts = parse_timestamp(line[:23])
                        resource_interval = last_acquired_resource.pop(resource, None)
                        if resource_interval:
                            resources[resource].append((resource_interval, ts))
                        else:
                            logging.error('Lock %s released without being acquired or just overlaps another one %s',
                                          resource, ts)

    return resources


def parse_timestamp(ts):
    return datetime.datetime.strptime(ts, '%Y-%m-%d %H:%M:%S,%f')


def check_resource_intersections(results):
    results_len = len(results)
    if results_len > 1:
        for i in range(results_len):
            for j in range(i + 1, results_len):
                for key, value in results[i].items():
                    intervals = results[j].get(key)
                    if intervals:
                        check_interval_intersections(key, value, intervals)


def check_interval_intersections(resource, list1, list2):
    for interval1 in list1:
        interval2 = binary_interval_search(list2, interval1)
        if interval2:
            logging.error('Found overlapping resource %s: (%s, %s) -> (%s, %s)', resource, interval1[0], interval1[1],
                          interval2[0], interval2[1])


def binary_interval_search(sorted_list, interval):
    start = 0
    end = len(sorted_list)

    while start < end:
        mid = (start + end) // 2
        mid_val = sorted_list[mid]
        if interval[1] < mid_val[0]:
            end = mid
        elif interval[0] > mid_val[1]:
            start = mid + 1
        else:
            return interval


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Finds overlapping jobs')
    parser.add_argument('log_files', metavar='FILE', nargs='+', help='Log files')
    parser.add_argument('--verbose', action='store_true', help='Log debug configuration')

    args = parser.parse_args()

    loglevel = logging.INFO
    if args.verbose:
        loglevel = logging.DEBUG

    logging.basicConfig(level=loglevel, format='%(levelname)-6s %(message)s')

    try:
        pool = Pool(processes=len(args.log_files))
        check_resource_intersections(pool.map(group_resources, args.log_files))
    except IOError, e:
        parser.error(e)
