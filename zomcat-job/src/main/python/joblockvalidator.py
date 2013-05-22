#!/usr/bin/env python
# -*- coding: utf-8 -*-

import logging
import argparse
import collections
import datetime
import re
import os
from multiprocessing import Pool

FAST_LOCK_CHECK = 'resource'
ACQUIRE_LOCK_REGEX = re.compile(r'.*acquired resource lock for job; job will start')
RELEASE_LOCK_REGEX = re.compile(r'.*Lock with resource ([^\s]+) and flow id ([^\s]+) released')

LOCK_THRESHOLD = 15


class KeyboardInterruptError(Exception):

    pass


def parse_timestamp(line):
    return datetime.datetime.strptime(line[:23], '%Y-%m-%d %H:%M:%S,%f')


def parse_flowid(line):
    return line[24:46]


def group_resources_with_interrupt(log_file):
    # workaround -> KeyboardInterrupt is never sent.
    try:
        return group_resources(log_file)
    except KeyboardInterrupt:
        raise KeyboardInterruptError()


def group_resources(log_file):
    if not os.path.isfile(log_file):
        raise IOError('File not found'.format(log_file))

    resources = collections.defaultdict(list)
    acquired_locks_flowid = collections.defaultdict()

    with open(log_file) as file:
        for line in file:
            if FAST_LOCK_CHECK in line:
                match = ACQUIRE_LOCK_REGEX.search(line)
                if match:
                    acquired_locks_flowid[parse_flowid(line)] = parse_timestamp(line)
                else:
                    match = RELEASE_LOCK_REGEX.search(line)
                    if match:
                        resource = match.group(1)
                        flowid = match.group(2)
                        ts = parse_timestamp(line)
                        acquire_ts = acquired_locks_flowid.pop(flowid, None)
                        if acquire_ts:
                            resources[resource].append((acquire_ts, ts))

                            delta = (ts - acquire_ts).seconds / 60
                            if delta > LOCK_THRESHOLD:
                                logging.warn('%s: Lock %s with flowid %s acquired at %s until %s (%s minutes)',
                                             log_file, resource, flowid, acquire_ts, ts, delta)
                        else:
                            logging.error('%s: Lock %s released without being acquired or just overlaps another one on file: %s. Time: %s'
                                          , log_file, resource, log_file, ts)

    return log_file, resources


def check_resource_intersections(results):
    results_len = len(results)
    if results_len > 1:
        for i in range(results_len):
            for j in range(i + 1, results_len):
                resources1 = results[i]
                for key, value in resources1[1].items():
                    resources2 = results[j]
                    intervals = resources2[1].get(key)
                    if intervals:
                        check_interval_intersections(key, resources1[0], value, resources2[0], intervals)


def check_interval_intersections(resource, log_file1, list1, log_file2, list2):
    for interval1 in list1:
        interval2 = binary_interval_search(list2, interval1)
        if interval2:
            logging.error(
                'Found overlapping resource %s: (%s: %s, %s) -> (%s: %s, %s)',
                resource,
                log_file1,
                interval1[0],
                interval1[1],
                log_file2,
                interval2[0],
                interval2[1],
            )


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
            return mid_val


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Validates job locking')
    parser.add_argument('log_files', metavar='FILE', nargs='+', help='Log files')
    parser.add_argument('--verbose', action='store_true', help='Log debug configuration')

    args = parser.parse_args()

    loglevel = logging.INFO
    if args.verbose:
        loglevel = logging.DEBUG

    logging.basicConfig(level=loglevel, format='%(levelname)-6s %(message)s')

    try:
        pool = Pool(processes=len(args.log_files))
        check_resource_intersections(pool.map(group_resources_with_interrupt, args.log_files))
    except IOError, e:
        parser.error(e)
