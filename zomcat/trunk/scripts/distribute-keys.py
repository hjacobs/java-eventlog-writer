#!/usr/bin/env python
# -*- coding: utf-8 -*-

'''
Helper script to generate Zomcat/Zompy keypairs for DeployCtl projects ("applications")
and distribute them to config-frontend (public keys) and application instances (private keys).
'''

import argparse
import logging
import glob
import os
import sys
import subprocess
from deployctl.client import DeployCtl
from keyczar import keyczart
from keyczar import keyinfo


def create_directory(target, key_type, environment, project):
    path = os.path.join(target, 'zomcat-keys', key_type, environment, project)
    if not os.path.isdir(path):
        os.makedirs(path)
    return path


def create_private_key(location, name):
    keyczart.Create(location, name, keyinfo.DECRYPT_AND_ENCRYPT, asymmetric='rsa')
    keyczart.AddKey(location, 'PRIMARY')
    logging.debug('Created private key in %s', location)


def export_public_key(private_key_location, target):
    keyczart.PubKey(private_key_location, target)
    logging.debug('Exported public key to %s', target)


def create_key_pair(environment, project, target):
    path = create_directory(target, 'private', environment, project)
    create_private_key(path, '%s-%s' % (project, environment))
    export_public_key(path, create_directory(target, 'public', environment, project))


def generate_key_pairs(base_dir, env, project_filter):
    client = DeployCtl()
    logging.info('Loading %s instances..', env)
    instances = client.get_instances(env=env)

    projects = set()
    for instance in instances:
        if instance.status in ('PROVISIONING', 'ALLOCATED'):
            projects.add(instance.project)

    for project in sorted(projects):
        if project_filter and project_filter not in project:
            logging.debug('Skipping project %s', project)
            continue
        path = os.path.join(base_dir, 'zomcat-keys', 'private', env, project)
        if os.path.isdir(path):
            logging.info('Found existing key pair for %s %s', env, project)
        else:
            logging.info('Creating key pair for %s %s..', env, project)
            create_key_pair(env, project, base_dir)


def distribute_public_keys(base_dir, environment):
    client = DeployCtl()
    instances = client.get_instances(env=environment, project='config-frontend')

    files = glob.glob(os.path.join(base_dir, 'zomcat-keys', 'public'))  # , '*', '*'))
    for fn in files:
        for instance in instances:
            logging.info('Copying %s to %s:%s..', fn, instance.host, instance.instance)
            subprocess.check_call(['scp', '-r', fn,
                                  'root@{host}:/data/zalando/app/p{instance}/zomcat-keys/'.format(host=instance.host,
                                  instance=instance.instance)])


def distribute_private_keys(base_dir, environment, project_filter):
    client = DeployCtl()
    files = glob.glob(os.path.join(base_dir, 'zomcat-keys', 'private', environment, '*'))
    for fn in files:
        parts = fn.split('/')
        env, project = parts[-2:]
        if project_filter and project_filter not in project:
            logging.debug('Skipping project %s', project)
            continue
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
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('environment', help='DeployCtl environment to generate/distribute keypairs for')
    parser.add_argument('directory',
                        help='Directory to store/load keypairs from. Must already contain "zomcat-keys" folder.')
    parser.add_argument('--project',
                        help='Only generate/distribute keypairs for projects matching the given pattern (substring).')
    parser.add_argument('--no-distribute-public', action='store_true', help='Do not distribute public keys')
    parser.add_argument('--no-distribute-private', action='store_true', help='Do not distribute private keys')
    args = parser.parse_args()
    logging.basicConfig(level=logging.INFO)
    if not os.path.isdir(os.path.join(args.directory, 'zomcat-keys')):
        # we will abort for safety reasons (running the script with an empty dir might re-generate and distribute all keys by accident!)
        logging.error('Directory does not contain a "zomcat-keys" folder. Aborting.')
        sys.exit(1)
    generate_key_pairs(args.directory, args.environment, args.project)
    if not args.no_distribute_public:
        distribute_public_keys(args.directory, args.environment)
    if not args.no_distribute_private:
        distribute_private_keys(args.directory, args.environment, args.project)
