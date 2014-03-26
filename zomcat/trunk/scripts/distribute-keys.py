#!/usr/bin/env python
# -*- coding: utf-8 -*-

import argparse
import logging
import glob
import os
import subprocess
from deployctl.client import DeployCtl


def distribute_public_keys(base_dir):
    client = DeployCtl()
    instances = client.get_instances(env='live', project='config-frontend')

    files = glob.glob(os.path.join(base_dir, 'zomcat-keys', 'public'))  # , '*', '*'))
    for fn in files:
        for instance in instances:
            logging.info('Copying %s to %s:%s..', fn, instance.host, instance.instance)
            subprocess.check_call(['scp', '-r', fn,
                                  'root@{host}:/data/zalando/app/p{instance}/zomcat-keys/'.format(host=instance.host,
                                  instance=instance.instance)])


def distribute_private_keys(base_dir):
    client = DeployCtl()
    files = glob.glob(os.path.join(base_dir, 'zomcat-keys', 'private', '*', '*'))
    for fn in files:
        parts = fn.split('/')
        env, project = parts[-2:]
        instances = client.get_instances(env=env, project=project)
        for instance in instances:
            if instance.status not in ('PROVISIONING', 'ALLOCATED'):
                logging.warn('Instance %s:%s has status %s', instance.host, instance.instance, instance.status)
                continue
            logging.info('Copying %s to %s:%s..', fn, instance.host, instance.instance)
            subprocess.check_call(['ssh', 'root@{host}'.format(host=instance.host),
                                  'mkdir -p /data/zalando/app/p{instance}/zomcat-keys/private/{env}/{project}'.format(instance=instance.instance,
                                  env=env, project=project)])
            subprocess.check_call(['scp', '-r', fn,
                                  'root@{host}:/data/zalando/app/p{instance}/zomcat-keys/private/{env}'.format(host=instance.host,
                                  instance=instance.instance, env=env, project=project)])


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('directory')
    args = parser.parse_args()
    logging.basicConfig(level=logging.INFO)
    distribute_public_keys(args.directory)
    distribute_private_keys(args.directory)
